package com.claymus;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.claymus.xml.ClaymusHome;
import com.claymus.xml.ClaymusWebApp;
import com.claymus.xml.Forward;
import com.google.appengine.api.NamespaceManager;

/*
 * Thread-Safe: Synchronized wherever required.
 */
@SuppressWarnings("serial")
public class ClaymusMain extends HttpServlet {

	private static final Map<String, HostMapping> hostMappings = new Hashtable<String, HostMapping>();
	private static final HostMapping catchAllHostMapping;

	static {
		ClaymusWebApp claymusWebApp = null;
		try {
			JAXBContext jc = JAXBContext.newInstance("com.claymus.xml");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			claymusWebApp = (ClaymusWebApp) unmarshaller.unmarshal(new File(System.getProperty("user.dir") + "/WEB-INF/claymus-web.xml"));
		} catch (JAXBException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}

		Iterator<ClaymusHome> homeItr = claymusWebApp.getClaymusHomes().getClaymusHome().iterator();
		while(homeItr.hasNext()) {
			ClaymusHome claymusHome = homeItr.next();
			String namespace = claymusHome.getNamespace();
			NamespaceManager.set(namespace);
			try {
				HttpServlet servlet = (HttpServlet) Class.forName(claymusHome.getServletClass()).newInstance();
				Iterator<String> itr = claymusHome.getHost().iterator();
				while(itr.hasNext()) {
					ClaymusMain.hostMappings.put(itr.next(), new HostMapping(namespace, servlet));
				}
			} catch(Exception ex) {
				Logger.get().log(Level.SEVERE, null, ex);
			}
		}

		if(claymusWebApp.getUrlForwarding() != null && claymusWebApp.getUrlForwarding().getForward() != null) {
			Iterator<Forward> forwardItr = claymusWebApp.getUrlForwarding().getForward().iterator();
			while(forwardItr.hasNext()) {
				Forward forward = forwardItr.next();
				ClaymusMain.hostMappings.put(forward.getHost(), new HostMapping(forward.getTo()));
			}
		}

		String action = claymusWebApp.getCatchAll() == null ? null : claymusWebApp.getCatchAll().getAction();
		String host = claymusWebApp.getCatchAll() == null ? null : claymusWebApp.getCatchAll().getHost();
		if(action == null || host == null) {
			catchAllHostMapping = null;
		} else if(action.equals("redirect")) {
			catchAllHostMapping = new HostMapping(host);
		} else if(action.equals("transfer") && ClaymusMain.hostMappings.get(host) != null) {
			catchAllHostMapping = ClaymusMain.hostMappings.get(host);
		} else {
			catchAllHostMapping = null;
		}
	}


	private static final ThreadLocal<HttpServletRequest> threadRequest = new ThreadLocal<HttpServletRequest>();

	private static final ThreadLocal<HttpServletResponse> threadResponse = new ThreadLocal<HttpServletResponse>();

	public static HttpServletRequest getRequest() {
		synchronized(ClaymusMain.threadRequest) {
			return ClaymusMain.threadRequest.get();
		}
	}

	private static void setRequest(HttpServletRequest req) {
		synchronized(ClaymusMain.threadRequest) {
			ClaymusMain.threadRequest.set(req);
		}
	}

	public static HttpServletResponse getResponse() {
		synchronized(ClaymusMain.threadResponse) {
			return ClaymusMain.threadResponse.get();
		}
	}

	private static void setResponse(HttpServletResponse resp) {
		synchronized(ClaymusMain.threadResponse) {
			ClaymusMain.threadResponse.set(resp);
		}
	}


	@Override
	protected void service(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		HostMapping hostMapping = ClaymusMain.hostMappings.get(req.getHeader("host"));

		if(hostMapping == null) {
			if(ClaymusMain.catchAllHostMapping == null) {
				resp.sendError(404);
				return;
			} else {
				hostMapping = ClaymusMain.catchAllHostMapping;
			}
		}

		if(hostMapping.getType() == HostMapping.Type.FORWARD) {
			resp.sendRedirect("http://" + hostMapping.getFwdURL());
			return;
		}

		NamespaceManager.set(hostMapping.getNamespace());

		HttpServlet servlet = hostMapping.getServlet();
		if(servlet.getServletConfig() == null)
			servlet.init(this.getServletConfig());

		HttpServletRequest request = new HttpServletRequestWrapper(req) {
			@Override
			public String getRequestURI() {
				String uri = super.getRequestURI();
				if (uri.length() != 1 && uri.charAt(uri.length() - 1) == '/')
					uri = uri.substring(0, uri.length() - 1); // Removing trailing "/".
				return uri;
			}
		};

		HttpServletResponse response = new HttpServletResponseWrapper(resp) {
			@Override
			public PrintWriter getWriter() throws IOException {
				return new PrintWriter(resp.getWriter()) {
					@Override
					public void close() {
						// DO NOTHING
					}
				};
			}
		};

		ClaymusMain.setRequest(request);
		ClaymusMain.setResponse(response);

		servlet.service(request, response);

		try {
			resp.getWriter().close();
		} catch(IllegalStateException ex) {
			Logger.get().log(Level.WARNING, null, ex);
		}

	}

}
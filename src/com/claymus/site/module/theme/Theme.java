package com.claymus.site.module.theme;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletException;

import com.claymus.site.module.block.Block;
import com.claymus.site.module.content.Content;
import com.claymus.site.module.page.PageLayout;

public abstract class Theme implements Serializable {

	private static final long serialVersionUID = 7890111319479940967L;

	protected static final String GWT_Standad = "<link type='text/css' rel='stylesheet' href='/com.claymus/gwt.standard/standard.css'>";
	protected static final String GWT_Chorme  = "<link type='text/css' rel='stylesheet' href='/com.claymus/gwt.chrome/chrome.css'>";
	protected static final String GWT_Dark    = "<link type='text/css' rel='stylesheet' href='/com.claymus/gwt.dark/dark.css'>";
	protected static final String GWT_Clean   = "<link type='text/css' rel='stylesheet' href='/com.claymus/gwt.clean/clean.css'>";

	protected static int LEFT_SIDEBAR 	= 0;
	protected static int RIGHT_SIDEBAR 	= 1;
	protected static int BEFORE_CONTENT = 2;
	protected static int AFTER_CONTENT 	= 3;
	protected static int HEADER 		= 4;
	protected static int FOOTER			= 5;

	/*
	 * Inherited Methods
	 */

	@Override
	public final boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj instanceof Theme)
			return obj.getClass().equals(this.getClass());
		else
			return false;
	}

	/*
	 * Helper Methods : To be over-ridden by subclasses
	 */

	public abstract String getName();

	public String getFavicon() {
		return "/com.claymus.site/theme/favicon.ico";
	}

	public String getCSS() {
		return "<link type='text/css' rel='stylesheet' href='/com.claymus.site/theme/style.css'>";
	}

//	public String getCKEditorConfiguration () {
//		return "<script type='text/javascript' src='/com.claymus.site/theme/ckeditor_config.js'></script>";
//		return "<script type='text/javascript'>CKEDITOR.replace( 'cke_editor1', { customConfig : '/com.claymus.site/theme/ckeditor_config.js'});</script>";
//	}

	public String[][] getLocations() {
		return new String[][] {
				{ "Left Sidebar", 	"LEFT_SIDEBAR"	 },
				{ "Right Sidebar", 	"RIGHT_SIDEBAR"  },
				{ "Before Content", "BEFORE_CONTENT" },
				{ "After Sidebar", 	"AFTER_CONTENT"  },
				{ "Header", 		"HEADER" 		 },
				{ "Footer", 		"FOOTER" 		 } };
	}

	public Object[][][] getLayout() {
		return new Object[][][] {
				{ { 3, 1, "HEADER" } },
				{ { 1, 3, "LEFT_SIDEBAR" }, { 1, 1, "BEFORE_CONTENT" }, { 1, 3, "RIGHT_SIDEBAR" } },
				{ { 1, 1, null } },
				{ { 1, 1, "AFTER_CONTENT" } },
				{ { 3, 1, "FOOTER" } }, };
	}

	protected void printBlocks(List<Block> blocks, PrintWriter out) throws IOException {
		for (int i = 0; i < blocks.size(); i++) {
			Block block = blocks.get(i);

			out.print("<div class='claymus-block'>");

			out.print(block.getHTML());

//			String blockTitle = block.getTitle();
//			if(blockTitle != null) {
//				out.print("<div class='claymus-h2'>");
//				out.print(blockTitle);
//				out.print("</div>");
//			}
//
//			out.print("<div class='claymus-t2'>");
//			out.print(block.getContent());
//			out.print("</div>");

			out.print("</div>");
		}
	}

	public void printContent(List<Content> contents, PrintWriter out) throws IOException {
		for (int i = 0; i < contents.size(); i++) {
			Content content = contents.get(i);

			out.print("<div class='claymus-content'>");

			out.print(content.getHTML());

//			if (aContent.getTitle() != null) {
//				out.print("<div class='claymus-h1'>");
//				out.print(content.getTitle());
//				out.print("</div>");
//			}
//
//			out.print("<div class='claymus-t1'>");
//			out.print(content.getContent());
//			out.print("</div>");

			out.print("</div>");
		}
	}

	public void generateLayout(List<List<Content>> contents, List<List<Block>> blocks, PageLayout layout, PrintWriter out) throws ServletException, IOException {
		List<Block> headerBlocks 		= blocks.get(Theme.HEADER);
		List<Block> footerBlocks 		= blocks.get(Theme.FOOTER);
		List<Block> leftSidebarBlocks 	= blocks.get(Theme.LEFT_SIDEBAR);
		List<Block> rightSidebarBlocks 	= blocks.get(Theme.RIGHT_SIDEBAR);
		List<Block> beforeContentBlocks = blocks.get(Theme.BEFORE_CONTENT);
		List<Block> afterContentBlocks 	= blocks.get(Theme.AFTER_CONTENT);

		// HEADER
		if(headerBlocks.size() > 0) {
			out.print("<div id='claymus-header'>");
			printBlocks(headerBlocks, out);
			out.print("</div>");
		}

		out.print("<table id='claymus-container'>");
		out.print("<tr>");

		// LEFT SIDEBAR
		if(leftSidebarBlocks.size() > 0) {
			out.print("<td id='claymus-leftsidebar'>");
			printBlocks(leftSidebarBlocks, out);
			out.print("</td>");
		}

		out.print("<td id='claymus-middle'>");

		// BEFORE CONTENT
		if(beforeContentBlocks.size() > 0) {
			out.print("<div id='claymus-beforecontent'>");
			printBlocks(beforeContentBlocks, out);
			out.print("</div>");
		}

		// CONTENT
		out.print("<div id='claymus-pagecontent'>");
			layout.generate(contents, this, out);
		out.print("</div>");


		// AFTER CONTENT
		if (afterContentBlocks.size() > 0) {
			out.print("<div id='claymus-aftercontent'>");
			printBlocks(afterContentBlocks, out);
			out.print("</div>");
		}

		out.print("</td>");

		// RIGHT SIDEBAR
		if(rightSidebarBlocks.size() > 0) {
			out.print("<td id='claymus-rightsidebar'>");
			printBlocks(rightSidebarBlocks, out);
			out.print("</td>");
		}

		out.print("</tr>");
		out.print("</table>");

		// FOOTER
		if(footerBlocks.size() > 0) {
			out.print("<div id='claymus-footer'>");
			printBlocks(footerBlocks, out);
			out.print("</div>");
		}

	}

}

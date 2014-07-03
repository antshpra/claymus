package com.claymus;

import java.util.logging.Level;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.PresenceShow;
import com.google.appengine.api.xmpp.PresenceType;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.apphosting.api.ApiProxy.OverQuotaException;

/*
 * Quota Details: http://code.google.com/appengine/docs/quotas.html#XMPP
 * .---------------------.-------------------------.
 * |					 | Free & Billing Enabled  |
 * | Resource			 |------------.------------|
 * |					 | Daily	  | Per Minute |
 * |---------------------|------------|------------|
 * | XMPP API Calls		 | 46,310,400 |	   257,280 |
 * | Recipients Messaged | 46,310,400 |	   257,280 |
 * | Invitations Sent	 |	  100,000 |		 2,000 |
 * '---------------------'------------'------------'
 *
 * ASSUMPTION : "XMPP API Calls" and "Recipients Messaged" will never reach its
 * 			    quota limit. Hence, no need to handle "OverQuotaException".
 */

/*
 * Thread-Safe: No Global Data
 */
public class XMPP {

	public static final XMPPService xmppService = XMPPServiceFactory.getXMPPService();


	// SENDING INVITATION

	public static boolean sendInvite(JID jid) {
		try {
			XMPP.xmppService.sendInvitation(jid);
			return true;
		} catch (OverQuotaException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return false;
		}
	}

	public static boolean sendInvite(String id) {
		return XMPP.sendInvite(new JID(id));
	}


	// CHECKING PRESENCE

	public static boolean getPresence(JID jid) {
		return XMPP.xmppService.getPresence(jid).isAvailable();
	}

	public static boolean getPresence(String id) {
		return XMPP.getPresence(new JID(id));
	}


	// SENDING PRESENCE

	public static void sendPresence(JID to, PresenceType type, PresenceShow show, String status) {
		XMPP.xmppService.sendPresence(to, type, show, status);
	}

	public static void sendPresence(String to, PresenceType type, PresenceShow show, String status) {
		XMPP.xmppService.sendPresence(new JID(to), type, show, status);
	}

	public static void sendPresence(JID to, PresenceShow show, String status) {
		XMPP.xmppService.sendPresence(to, PresenceType.AVAILABLE, show, status);
	}

	public static void sendPresence(String to, PresenceShow show, String status) {
		XMPP.xmppService.sendPresence(new JID(to), PresenceType.AVAILABLE, show, status);
	}

	public static void sendPresence(JID to, String status) {
		XMPP.xmppService.sendPresence(to, PresenceType.AVAILABLE, PresenceShow.NONE, status);
	}

	public static void sendPresence(String to, String status) {
		XMPP.xmppService.sendPresence(new JID(to), PresenceType.AVAILABLE, PresenceShow.NONE, status);
	}

	// SENDING INSTANT MESSAGE

	public static SendResponse.Status sendIM(JID to, String msg) {
		return XMPP.xmppService.sendMessage(
					new MessageBuilder()
						.withRecipientJids(to)
						.withBody(msg)
						.build()
				).getStatusMap()
					.get(to);
	}

	public static SendResponse.Status sendIM(String to, String msg) {
		return XMPP.sendIM(new JID(to), msg);
	}

}

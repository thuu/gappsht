package com.thudesign.gappsht;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;



@SuppressWarnings("serial")
public class SearchServlet extends AbstractAppEngineAuthorizationCodeServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String keyword = req.getParameter("keyword");
		AuthorizationCodeFlow authFlow = initializeFlow();
	    Credential credential = authFlow.loadCredential(getUserId(req));
	    
	    Gmail gmail = new Gmail.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(Utils.APPLICATION_NAME).build();
		ListMessagesResponse messageResponse = gmail.users().messages().list(Utils.USER).setQ(keyword).execute();
		List<Message> messages = messageResponse.getMessages();
		
		if(messages != null){
			for(Message message: messages){
				String messageId = message.getId();
				Message messageData = gmail.users().messages().get(Utils.USER, messageId).setFormat("raw").execute();
				byte[] emailBytes = Base64.decodeBase64(messageData.getRaw());
			    Properties props = new Properties();
			    Session session = Session.getDefaultInstance(props, null);
			    
			    try {
			    	MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
					String sender = (email.getFrom()[0].toString() instanceof String) ? (email.getFrom()[0].toString()).replace("<", "&lt;").replace(">", "&gt;") : "";
					String snippet = (messageData.getSnippet() instanceof String) ? messageData.getSnippet() : "";
					
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Entity mail = new Entity("Mail");
					mail.setProperty("userId", getUserId(req));
					mail.setProperty("messageId", messageId);
					mail.setProperty("sender", sender);
					mail.setProperty("snippet", snippet);
					datastore.put(mail);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		
		resp.sendRedirect("/");
	}
	
	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
	  return Utils.getRedirectUri(req);
	}
	
	@Override
	protected AuthorizationCodeFlow initializeFlow() throws IOException {
	  return Utils.newFlow();
	}
}

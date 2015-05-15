package com.thudesign.gappsht;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DbViewServlet extends HttpServlet{
	private static final long serialVersionUID = -4717919604148110873L;
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setCharacterEncoding("UTF-8");
		Writer writer = resp.getWriter();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Mail");
		PreparedQuery pq = datastore.prepare(query);
		
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		String currentUserId = currentUser.getUserId();
		
		for(Entity result: pq.asIterable()){
			String userId = (String) result.getProperty("userId");
			String messageId = (String) result.getProperty("messageId");
			String sender = (String) result.getProperty("sender");
			String snippet = (String) result.getProperty("snippet");
			
			if(currentUserId.compareTo(userId) == 0){
				writer.write("Message id: " + messageId + "<br>");
				writer.write("Sender: " + sender + "<br>");
				writer.write("Snippet: " + snippet + "<br>");
				writer.write("<br>");
				writer.write("<br>");
			}
		}
	}
}

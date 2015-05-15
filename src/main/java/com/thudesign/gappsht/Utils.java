package com.thudesign.gappsht;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.*;


class Utils {
	private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
	static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
	static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	static final String OAUTH_CALLBACK_PATH = "/oauth2callback";
	private static GoogleClientSecrets clientSecrets = null;
	static final String APPLICATION_NAME = "gappsht";
	static final String USER = "me";
	
	
	static GoogleClientSecrets getClientCredential() throws IOException {
		if (clientSecrets == null) {
		  clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
		      new InputStreamReader(Utils.class.getResourceAsStream("/client_secret.json")));
		}
		return clientSecrets;
	}
	
	  static String getRedirectUri(HttpServletRequest req) {
	    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
	    url.setRawPath(OAUTH_CALLBACK_PATH);
	    return url.build();
	  }

	  static GoogleAuthorizationCodeFlow newFlow() throws IOException {
	    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
	        getClientCredential(), Collections.singleton(GmailScopes.GMAIL_READONLY)).setDataStoreFactory(
	        DATA_STORE_FACTORY).setAccessType("offline").build();
	  }
}

/**
 * 
 */
package com.socialfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.socialapplibrary.endpoints.EndpointConstants;

/**
 * @author Cameron
 *
 */
public class BackgroundWorker {

	public static void startCoreControllerPing() {
		Thread thread = new Thread(new Runnable() {

		     public void run() {
		    	 int i = 0;
		    	 while (i < 3) {
			          try {
				      		String finalData = "";
				    		String output;
				    		String endpoint;
				    		URL url;
				    		HttpURLConnection connection;
					        endpoint = SocialFeed.TARGET_HOST + ":" + EndpointConstants.CORE_PORT;
							String pingMessage = "Pinging the Core Controller at " + endpoint;
							System.out.println(pingMessage);
							finalData = "";
							url = new URL(endpoint);
							connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							connection.setRequestProperty("Accept", "application/json");
							int status = connection.getResponseCode();
							if (status == 200) {
								BufferedReader br = new BufferedReader(new InputStreamReader(
										(connection.getInputStream())));
								while ((output = br.readLine()) != null) {
									finalData += output;
								}

								System.out.println("200 OK. Server response: " + finalData);
							} else {
								System.out.println("Server status code: " + status);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						i++;
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		    	 }
		     }
		});

		thread.start();
	}
}

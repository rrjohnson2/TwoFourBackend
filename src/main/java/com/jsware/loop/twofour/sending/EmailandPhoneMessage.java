package com.jsware.loop.twofour.sending;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
public class EmailandPhoneMessage {

	private final String sms_server = "https://loopsmsemail.net/";

	public void sendEmail(Email em) throws IllegalAccessException, ClientProtocolException, IOException {
		send("sendEmail", em);
	}

	public void sendText(Text text) throws IllegalAccessException, ClientProtocolException, IOException {
		send("send", text);
	}

	private void send(String path, Object obj) throws IllegalAccessException, ClientProtocolException, IOException {
		CloseableHttpClient http_client = HttpClients.createDefault();
		HttpPost post = new HttpPost(sms_server + path);

		post.setEntity(new StringEntity(stripTojsonField(obj)));
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");

		http_client.execute(post);
		http_client.close();
	}

	private String stripTojsonField(Object obj) throws IllegalAccessException {
		StringBuilder json = new StringBuilder();
		json.append("{");
		for (Field field : obj.getClass().getFields()) {
			Object val = field.get(obj);
			if (val != null)
				json.append("\"" + field.getName() + "\":\"" + val + "\",");
		}
		json.deleteCharAt(json.length() - 1);
		json.append("}");

		return json.toString();
	}

}

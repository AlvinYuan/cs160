package edu.berkeley.cs160.gsale;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;

public class Message {
	public static final String HAS_MESSAGE_ID_KEY = "HAS_MESSAGE_ID_KEY";
	public static final String MESSAGE_ID_KEY = "MESSAGE_ID_KEY";
	
	public static final int INVALID_INT = -1;
	public static final int BROADCAST = -2;
	
	public static HashMap<Integer, Message> allMessages;
	public static boolean messagesLoaded = false;
	
	/* Fields */
	public int id = INVALID_INT; //Unique Identifier
	public int saleId = INVALID_INT;
	public int senderId = INVALID_INT;
	public int receiverId = INVALID_INT;
	public String subject;
	public String content;
	public int respondedToMessageId = INVALID_INT; // The message this message is a response to

	public Message() {
		
	}
	
	public Message(JSONArray JSONmessage) {
		this();
		try {
			int i = 0;
			id = JSONmessage.getInt(i++);
			saleId = JSONmessage.getInt(i++);
			senderId = JSONmessage.getInt(i++);
			receiverId = JSONmessage.getInt(i++);
			subject = JSONmessage.getString(i++);
			content = JSONmessage.getString(i++);
			respondedToMessageId = JSONmessage.getInt(i++);			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
	public UrlEncodedFormEntity HttpPostEntity() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("saleId", ""+saleId));
		postParameters.add(new BasicNameValuePair("senderId", ""+senderId));
		postParameters.add(new BasicNameValuePair("receiverId", ""+receiverId));
		postParameters.add(new BasicNameValuePair("subject", ""+subject));
		postParameters.add(new BasicNameValuePair("content", ""+content));
		postParameters.add(new BasicNameValuePair("respondedToMessageId", ""+respondedToMessageId));
		try {
			return new UrlEncodedFormEntity(postParameters);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void startMessagesActivity(Context context) {
		Intent intent = new Intent(context, MessagesActivity.class);
		if (context instanceof DetailsActivity) {
			DetailsActivity detailsActivity = (DetailsActivity) context;
			intent.putExtra(GarageSale.HAS_SALE_ID_KEY, true);
			intent.putExtra(GarageSale.SALE_ID_KEY, detailsActivity.sale.id);
		} else if (context instanceof CreateEditActivity) {
			/* 
			 * Should be able to view messages of a sale from here
			 * But not currently supported
			 */
		} else {
			/* HomeActivity most likely */
			intent.putExtra(GarageSale.HAS_SALE_ID_KEY, false);
		}
		context.startActivity(intent);
	}
	
	public static ArrayList<Message> getMessages(boolean forSpecificSale, GarageSale sale) {
		ArrayList<Message> messages = new ArrayList<Message>();
		HashSet<Integer> followedSalesIds = new HashSet<Integer>();
		for (GarageSale followedSale : User.currentUser.followedSales) {
			followedSalesIds.add(followedSale.id);
		}
		for (Message message : Message.allMessages.values()) {
			if (   forSpecificSale
				&& message.saleId == sale.id
				&& (   message.receiverId == User.currentUser.id 
		            || message.receiverId == Message.BROADCAST)) {
				messages.add(message);
			}
			if (   !forSpecificSale
				&& (   message.receiverId == User.currentUser.id
					|| (   followedSalesIds.contains(message.saleId)
				        && message.receiverId == Message.BROADCAST))) {
				messages.add(message);
			}
		}
		return messages;
	}
}

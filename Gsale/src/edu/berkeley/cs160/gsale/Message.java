package edu.berkeley.cs160.gsale;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;

public class Message {
	public static final String HAS_MESSAGE_ID_KEY = "HAS_MESSAGE_ID_KEY";
	public static final String MESSAGE_ID_KEY = "MESSAGE_ID_KEY";
	
	public static HashMap<Integer, Message> allMessages;
	
	public int id; //Unique Identifier
	public GarageSale sale;
	public User sender;
	public User receiver;
	public String subject;
	public String content;
	public Message respondedToMessage; // The message this message is a response to
	
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
}

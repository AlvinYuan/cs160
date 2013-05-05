package edu.berkeley.cs160.gsale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class SendMessageActivity extends Activity {
	public Message message;
	public boolean currentlyPublishing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView toTextView = (TextView) findViewById(R.id.ToTextView);
		message = new Message();
		message.senderId = User.currentUser.id;
		Bundle extras = this.getIntent().getExtras();
		if (extras.getBoolean(Message.HAS_MESSAGE_ID_KEY)) {
			message.respondedToMessageId = extras.getInt(Message.MESSAGE_ID_KEY);
			message.saleId = Message.allMessages.get(message.respondedToMessageId).saleId;
			message.receiverId = Message.allMessages.get(message.respondedToMessageId).senderId;
			toTextView.setText("To sender of \"" + Message.allMessages.get(message.respondedToMessageId).subject + "\"");
		} else if (extras.getBoolean(GarageSale.HAS_SALE_ID_KEY)) {
			message.respondedToMessageId = Message.INVALID_INT;
			message.saleId = extras.getInt(GarageSale.SALE_ID_KEY);
			int plannerId = GarageSale.allSales.get(message.saleId).plannerId;
			if (User.currentUser.id == plannerId) {
				message.receiverId = Message.BROADCAST;
				toTextView.setText("To all potential attendees of \"" + GarageSale.allSales.get(message.saleId).title + "\"");
			} else {
				message.receiverId = plannerId;
				toTextView.setText("To planner of \"" + GarageSale.allSales.get(message.saleId).title + "\"");
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_send_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void CancelButtonOnClick(View view) {
		finish();
	}
	
	public void SendButtonOnClick(View view) {
		if (!currentlyPublishing) {
			currentlyPublishing = true;
			message.subject = ((EditText) findViewById(R.id.SubjectEditText)).getText().toString();
			message.content = ((EditText) findViewById(R.id.ContentEditText)).getText().toString();
			PostMessageAsyncTask postMessageTask = new PostMessageAsyncTask(this,message);
			postMessageTask.execute();
		}
	}

}

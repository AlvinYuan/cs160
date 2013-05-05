package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class MessagesActivity extends Activity implements OnItemClickListener {
	public boolean forSpecificSale;
	public GarageSale sale;
	public MessageAdapter messageAdapter;
	public ArrayList<Message> messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = this.getIntent().getExtras();
		forSpecificSale = extras.getBoolean(GarageSale.HAS_SALE_ID_KEY);
		if (forSpecificSale) {
			int saleid = extras.getInt(GarageSale.SALE_ID_KEY);
			sale = GarageSale.allSales.get(saleid);
		}

		ListView l = (ListView) findViewById(R.id.MessagesListView);

		messages = Message.getMessages(forSpecificSale, sale);
		messageAdapter = new MessageAdapter(this, android.R.layout.simple_list_item_1, messages, true);
		l.setAdapter(messageAdapter);
		l.setOnItemClickListener(this);
	}

	/*
	 * Method: SendNewMessageButtonOnClick
	 */
	public void SendNewMessageButtonOnClick(View view) {
		SendMessage(false,null);
	}
	
	public void SendMessage(boolean isResponse, Message respondedMessage) {
		Intent intent = new Intent(this, SendMessageActivity.class);
		intent.putExtra(Message.HAS_MESSAGE_ID_KEY, isResponse);
		intent.putExtra(GarageSale.HAS_SALE_ID_KEY, forSpecificSale);
		if (isResponse) {
			intent.putExtra(Message.MESSAGE_ID_KEY, respondedMessage.id);
		}
		if (forSpecificSale) {
			intent.putExtra(GarageSale.SALE_ID_KEY, sale.id);
		}
		startActivity(intent);
	}
	/*
	 * AdapterView.OnItemClickListener Interface
	 * Specifically for @+id/MySalesListView
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Message message = (Message) messageAdapter.getItem(position);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_messages, menu);
		
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

	/*
	 * Activity Override
	 */
	public void onResume() {
		super.onResume();
		messageAdapter.notifyDataSetChanged();
	}
}
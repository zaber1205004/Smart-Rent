package com.frrahat.smartrent;

import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.FileHandler;
import com.frrahat.smartrent.utils.Message;
import com.frrahat.smartrent.utils.MessageThread;
import com.frrahat.smartrent.utils.MessageTypes;
import com.frrahat.smartrent.utils.Passenger;
import com.frrahat.smartrent.utils.TaxiRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DestinationInputActivity extends Activity {
	
	EditText addressEditText;
	Button btnPointDestInMap;
	Button btnSend;
	
	Passenger passenger;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_input);
		
		addressEditText=(EditText) findViewById(R.id.editTextAddress);
		btnPointDestInMap=(Button) findViewById(R.id.buttonPointDestInMap);
		btnSend=(Button) findViewById(R.id.buttonSend);
		
		btnPointDestInMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pointDestInMap();
			}
		});
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performActionOnSendButtonClick();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_input, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		if (id == R.id.action_resetLocalPassengerInfo) {
			resetLocalPassengerInfo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void pointDestInMap() {
		System.out.println("TODO : need to be implemented");
	}
	
	private void performActionOnSendButtonClick(){
		passenger = FileHandler.getPassengerFromLocalStorage(getApplicationContext());
		
		//setup passenger ID
		if(passenger.getPassengerID()==null){//new ID
			passenger.setPassengerID(
					DatabaseHandler.getNewID(DatabaseHandler.getPassengersRef(getApplicationContext()),
							"passengerID", passenger));
			
			FileHandler.savePassengerToLocalStorage(getApplicationContext(), passenger);
		}else{
			//TODO
		}
		
		TaxiRequest taxiRequest=new TaxiRequest(addressEditText.getText().toString(), passenger.getPassengerID(), System.currentTimeMillis());
		//pushing new request
		String requestID=DatabaseHandler.getNewID(DatabaseHandler.getRequestsRef(getApplicationContext()), "requestID", taxiRequest);
		
		//MessageThread thread=new MessageThread(requestID, System.currentTimeMillis(), true);
		//pushing new thread
		//String threadID=DatabaseHandler.getNewID(DatabaseHandler.getThreadsRef(), "threadID", thread);
		
		showToast("Sending Request...");
		//advance to Message Thread
		Intent intent=new Intent(DestinationInputActivity.this, MessageThreadActivity.class);
		intent.putExtra("locationString", addressEditText.getText().toString());
		intent.putExtra("clientType", "passenger");
		intent.putExtra("requestID", requestID);
		intent.putExtra("clientID", passenger.getPassengerID());
		//intent.putExtra("threadID", threadID);
		
		//pushing a request message
		Message message =new Message(requestID, MessageTypes.TEXT, "Anyone ready to take me to "+addressEditText.getText(), 
				passenger.getPassengerID(), System.currentTimeMillis());
		DatabaseHandler.getNewID(DatabaseHandler.getMessagesRef(getApplicationContext()), null, message);
		
		startActivity(intent);
	}
	
	private void resetLocalPassengerInfo(){
		passenger=new Passenger();
		FileHandler.savePassengerToLocalStorage(getApplicationContext(), passenger);
		
		showToast("Deleted");
	}
}

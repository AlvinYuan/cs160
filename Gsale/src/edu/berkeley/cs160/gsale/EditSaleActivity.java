package edu.berkeley.cs160.gsale;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditSaleActivity extends FragmentActivity implements OnSeekBarChangeListener  {
	public boolean isEditing;
	public int editingSaleId;
	public GarageSale editingSale;
	public int step;
	public View editBasicInfoView;
	public View editDescriptionView;
	public View editPhotosView;
	public View editReviewPublishView;
	public View detailsView;
	public View visibleEditView;
	public SeekBar editProgressBar;
	public Button backButton;
	public Button nextButton;

	public ListView editPhotosListView;
	public PhotoAdapter photoAdapter;
	public boolean photoAdded = false;
	
	/* Camera Stuff */
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	

	public boolean selectingStart; //false = selectingEnd (for date/time)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sale);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		/*
		 * EditViews
		 */
		RelativeLayout editLayout = (RelativeLayout) findViewById(R.id.EditLayout);
		LayoutInflater inflater = getLayoutInflater();
		visibleEditView = null;

		/* Basic Info */
		editBasicInfoView = inflater.inflate(R.layout.edit_basic_info_view, null);
		editLayout.addView(editBasicInfoView);
		editBasicInfoView.setVisibility(View.INVISIBLE);
		
		/* Description */
		editDescriptionView = inflater.inflate(R.layout.edit_description_view, null);
		editLayout.addView(editDescriptionView);
		editDescriptionView.setVisibility(View.INVISIBLE);
		
		/* Photos */
		editPhotosView = inflater.inflate(R.layout.edit_photos_view, null);
		editLayout.addView(editPhotosView);
		editPhotosView.setVisibility(View.INVISIBLE);
		editPhotosListView = (ListView) editPhotosView.findViewById(R.id.EditPhotosListView);

		/* Review/Publish */
		editReviewPublishView = inflater.inflate(R.layout.edit_review_publish_view, null);
		editLayout.addView(editReviewPublishView);
		editReviewPublishView.setVisibility(View.INVISIBLE);
		RelativeLayout editDetailsLayout = (RelativeLayout) editReviewPublishView.findViewById(R.id.EditDetailsLayout);
		detailsView = inflater.inflate(R.layout.garage_sale_details_view, null);
		editDetailsLayout.addView(detailsView);
		
		Bundle extras = this.getIntent().getExtras();
		isEditing = extras.getBoolean(GarageSale.HAS_SALE_ID_KEY);
		if (isEditing) {
			editingSaleId = extras.getInt(GarageSale.SALE_ID_KEY);
			editingSale = GarageSale.allSales.get(editingSaleId);
			/* Populate fields with existing information */
			if (editingSale.title != null) {
				EditText titleField = (EditText) editBasicInfoView.findViewById(R.id.TitleField);
				titleField.setText(editingSale.title);
			}
			if (editingSale.location != null) {
				EditText locationField = (EditText) editBasicInfoView.findViewById(R.id.LocationField);
				locationField.setText(editingSale.location);				
			}
			if (editingSale.description != null) {
				EditText descriptionField = (EditText) editDescriptionView.findViewById(R.id.DescriptionField);
				descriptionField.setText(editingSale.description);				
			}
			if (editingSale.dateTime(true, false) != null) {
				EditText startTimeField = (EditText) editBasicInfoView.findViewById(R.id.StartTimeField);
				startTimeField.setText(editingSale.timeString(true));
			}
			if (editingSale.dateTime(false, false) != null) {
				EditText endTimeField = (EditText) editBasicInfoView.findViewById(R.id.EndTimeField);
				endTimeField.setText(editingSale.timeString(false));
			}
			if (editingSale.dateTime(true, true) != null) {
				EditText startDateField = (EditText) editBasicInfoView.findViewById(R.id.StartDateField);
				startDateField.setText(editingSale.dateString(true));
			}
			if (editingSale.dateTime(false, true) != null) {
				EditText endDateField = (EditText) editBasicInfoView.findViewById(R.id.EndDateField);
				endDateField.setText(editingSale.dateString(false));
			}
			
		} else {
			editingSale = new GarageSale();
			editingSale.plannerId = User.currentUser.id;
		}
		photoAdapter = new PhotoAdapter(this, android.R.layout.simple_list_item_1, editingSale.photos);
		editPhotosListView.setAdapter(photoAdapter);


		backButton = (Button) findViewById(R.id.BackButton);
		nextButton = (Button) findViewById(R.id.NextButton);
		
		editProgressBar = (SeekBar) findViewById(R.id.EditProgressBar);
		editProgressBar.setOnSeekBarChangeListener(this);
		onProgressChanged(editProgressBar, 0, false); // Trigger on activity creation
		/* Fix Layout to look nice with text */
		int padding = editProgressBar.getPaddingLeft();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int margin = width * 1/8 - padding;
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) editProgressBar.getLayoutParams();
		params.setMargins(margin, 0, margin, 0);
		editProgressBar.setLayoutParams(params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_sale, menu);
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

	public void updateEditView() {
		switch(step) {
		case 0:
			loadBasicInfoView();
			break;
		case 1:
			loadDescriptionView();
			break;
		case 2:
			loadPhotosView();
			break;
		case 3:
			loadReviewPublishView();
			break;
		}
		
	}
	
	public void loadBasicInfoView() {
		editBasicInfoView.setVisibility(View.VISIBLE);
		visibleEditView = editBasicInfoView;
	}
	
	public void loadDescriptionView() {
		editDescriptionView.setVisibility(View.VISIBLE);
		visibleEditView = editDescriptionView;
		
	}
	
	public void loadPhotosView() {
		editPhotosView.setVisibility(View.VISIBLE);
		visibleEditView = editPhotosView;
		
	}
	
	public void loadReviewPublishView() {
		editingSale.loadDetailsIntoView(detailsView);
		editReviewPublishView.setVisibility(View.VISIBLE);
		visibleEditView = editReviewPublishView;
		
	}
	
	/*
	 * OnSeekBarChangeListener Methods
	 */
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		if (visibleEditView != null) {
			if (visibleEditView.equals(editBasicInfoView)) {
				EditText titleField = (EditText) editBasicInfoView.findViewById(R.id.TitleField);
				EditText locationField = (EditText) editBasicInfoView.findViewById(R.id.LocationField);
				editingSale.title = titleField.getText().toString();
				editingSale.location = locationField.getText().toString();
			}
			if (visibleEditView.equals(editDescriptionView)) {
				EditText descriptionField = (EditText) editDescriptionView.findViewById(R.id.DescriptionField);
				editingSale.description = descriptionField.getText().toString();
			}
			if (visibleEditView.equals(editPhotosView)) {
				
			}
			if (visibleEditView.equals(editReviewPublishView)) {
				
			}
			visibleEditView.setVisibility(View.INVISIBLE);
		}
		step = progress;
		if (step == 0) {
			backButton.setText("Cancel");
		} else {
			backButton.setText("Back");
		}
		if (step == editProgressBar.getMax()) {
			nextButton.setText("Publish");
		} else {
			nextButton.setText("Next");
		}
		updateEditView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * OnClick Methods
	 */
	
	/*
	 * Method: BackButtonOnClick
	 */
	public void BackButtonOnClick(View view) {
		if(step == 0) {
			finish();
		} else {
			editProgressBar.setProgress(editProgressBar.getProgress() - 1);
		}
	}

	/*
	 * Method: NextButtonOnClick
	 */
	public void NextButtonOnClick(View view) {
		if (step == editProgressBar.getMax()) {
			//Storage store = new Storage(this);
			//store.storeSale(editingSale, 13376);
			//store.storeId(13376, Storage.PLANNED_SALES);
			if (User.currentUser.plannedSales.size() == 0) {
				PostSaleAsyncTask postTask = new PostSaleAsyncTask(this, editingSale);
				postTask.execute();
				// TODO (Mon): this doesn't work yet...
				GetLatLngFromAddressAsyncTask markerTask = new GetLatLngFromAddressAsyncTask(this, editingSale);
				markerTask.execute();
				User.currentUser.plannedSales.add(editingSale);
			}
			/*
			System.out.println("Storing test sale:");
			System.out.println("The title of the sale is: " + editingSale.title);
			System.out.println("The location of the sale is: " + editingSale.location);
			System.out.println("The description of the sale is: " + editingSale.description);
			*/
			finish();
		} else {
			editProgressBar.setProgress(editProgressBar.getProgress() + 1);
		}
	}
	
	/*
	 * ProgressBarText OnClick Methods
	 */
    public void ProgressBarTextDetailsOnClick(View view) {
		editProgressBar.setProgress(0);
    }
    public void ProgressBarTextDescriptionOnClick(View view) {
		editProgressBar.setProgress(1);    	
    }
    public void ProgressBarTextPhotosOnClick(View view) {
		editProgressBar.setProgress(2);    	
    }
    public void ProgressBarTextReviewOnClick(View view) {
		editProgressBar.setProgress(3);    	
    }
	
	/*
	 * Date and Time Field OnClick Methods
	 */
	public void StartDateFieldOnClick(View view) {
		selectingStart = true;
		createDatePickerDialog();
	}
	
	public void EndDateFieldOnClick(View view) {
		selectingStart = false;
		createDatePickerDialog();
	}

	public void StartTimeFieldOnClick(View view) {
		selectingStart = true;
		createTimePickerDialog();
	}
	
	public void EndTimeFieldOnClick(View view) {
		selectingStart = false;
		createTimePickerDialog();
	}

	/*
	 * Method: AddNewPhotoButtonOnClick
	 */
	public void AddNewPhotoButtonOnClick(View view) {
	    // create Intent to take a picture and return control to the calling application
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // start the image capture Intent
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	/*
	 * Method: ViewOnMapButtonOnClick
	 * TODO: Have something happen. Maybe say this is disabled with Toast or go to Map View.
	 */
	public void ViewOnMapButtonOnClick(View view) {
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Image Captured!", Toast.LENGTH_LONG).show();
	    	    Bundle extras = data.getExtras();
	    	    Bitmap mImageBitmap = (Bitmap) extras.get("data");
	    	    Photo p = new Photo();
	    	    p.bitmap = mImageBitmap;
	    	    p.description = "";
	    	    
	    	    editingSale.mainPhoto = p;
	    	    editingSale.photos.add(p);
	    	    
	    	    /*
	    	     * Can't start Fragment in this method. Known bug. Start in onResume
	    	     * http://stackoverflow.com/questions/10114324/show-dialogfragment-from-onactivityresult
	    	     */
	    	    photoAdded = true;

	    	    photoAdapter.notifyDataSetChanged();

	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	
	/*
	 * Activity.onResume
	 */
	public void onResume() {
		super.onResume();
		if (photoAdded) {
			createDescriptionDialog();
		}
		photoAdded = false;
	}
	
	/*
	 * CreateDialog Methods
	 */

	public void createDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");		
	}

	public void createTimePickerDialog() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "timePicker");		
	}
	
	public void createDescriptionDialog() {
		DescriptionDialogFragment newFragment = new DescriptionDialogFragment();
		newFragment.show(getSupportFragmentManager(), "photoDescription");
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(newFragment.input, InputMethodManager.SHOW_IMPLICIT);
	}

	/*
	 * DatePickerFragment Class
	 * encapsulates a DatePickerDialog with Fragment support.
	 * Follows android guide on pickers
	 */
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Calendar today = Calendar.getInstance();
			DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
			return datePickerDialog;
		}
		
		/*
		 * DatePickerDialog.OnDateSetListener Methods
		 */

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			EditSaleActivity activity = (EditSaleActivity) getActivity();
			EditText dateField;
			if (activity.selectingStart) {
				activity.editingSale.startYear = year;
				activity.editingSale.startMonth = monthOfYear;
				activity.editingSale.startDay = dayOfMonth;
				dateField = (EditText) activity.editBasicInfoView.findViewById(R.id.StartDateField);
				if (activity.editingSale.endYear == GarageSale.INVALID_INT) {
					activity.editingSale.endYear = year;
					activity.editingSale.endMonth = monthOfYear;
					activity.editingSale.endDay = dayOfMonth;
					((EditText) activity.editBasicInfoView.findViewById(R.id.EndDateField)).setText(activity.editingSale.dateString(false));
				}
			} else {
				activity.editingSale.endYear = year;
				activity.editingSale.endMonth = monthOfYear;
				activity.editingSale.endDay = dayOfMonth;
				dateField = (EditText) activity.editBasicInfoView.findViewById(R.id.EndDateField);
			}
			dateField.setText(activity.editingSale.dateString(activity.selectingStart));
		}
		
	}

	/*
	 * TimePickerFragment Class
	 * encapsulates a TimePickerDialog with Fragment support.
	 * Follows android guide on pickers
	 */
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Calendar today = Calendar.getInstance();
			TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
			return timePickerDialog;
		}
		
		/*
		 * TimePickerDialog.OnTimeSetListener Methods
		 */

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			EditSaleActivity activity = (EditSaleActivity) getActivity();
			EditText timeField;
			if (activity.selectingStart) {
				activity.editingSale.startHour = hourOfDay;
				activity.editingSale.startMinute = minute;
				timeField = (EditText) activity.editBasicInfoView.findViewById(R.id.StartTimeField);
			} else {
				activity.editingSale.endHour = hourOfDay;
				activity.editingSale.endMinute = minute;
				timeField = (EditText) activity.editBasicInfoView.findViewById(R.id.EndTimeField);
			}
			timeField.setText(activity.editingSale.timeString(activity.selectingStart));
		}
		
	}
	
	/*
	 * DescriptionDialogFragment Class
	 * For photo descriptions
	 */

	public static class DescriptionDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
		public EditText input;
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			//Alert Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Photo Description");
			builder.setMessage("Add a description or leave blank");
			input = new EditText(getActivity());

			builder.setView(input);
			builder.setPositiveButton("Ok", this);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}				
			});
			/* http://stackoverflow.com/questions/2403632/android-show-soft-keyboard-automatically-when-focus-is-on-an-edittext */
			final AlertDialog dialog = builder.create();
			input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			    @Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    }
			});
			return dialog;
		}

		@Override
		public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			EditSaleActivity activity = (EditSaleActivity) getActivity();
			Photo p = activity.editingSale.photos.get(activity.editingSale.photos.size() - 1);
			p.description = input.getText().toString();
			activity.photoAdapter.notifyDataSetChanged();
		}
	}
	
}

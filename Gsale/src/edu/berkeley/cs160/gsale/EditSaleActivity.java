package edu.berkeley.cs160.gsale;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditSaleActivity extends FragmentActivity implements OnSeekBarChangeListener, OnItemClickListener {
	public boolean isEditing;
	public int editingSaleId;
	public GarageSale editingSale;
	public int step;
	public TextView progressBarTextViews[];
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
	public int selectedPhoto;
	
	public boolean currentlyPublishing = false;
	
	public Photo newlyAddedPhoto;

	/* Camera Stuff */
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 101;	

	public boolean selectingStart; //false = selectingEnd (for date/time)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sale);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		/*
		 * ProgressBarTextViews
		 */
		progressBarTextViews = new TextView[4];
		progressBarTextViews[0] = (TextView) findViewById(R.id.ProgressBarTextDetails);
		progressBarTextViews[1] = (TextView) findViewById(R.id.ProgressBarTextDescription);
		progressBarTextViews[2] = (TextView) findViewById(R.id.ProgressBarTextPhotos);
		progressBarTextViews[3] = (TextView) findViewById(R.id.ProgressBarTextReview);
		
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
		final EditText descriptionField = (EditText) editDescriptionView.findViewById(R.id.DescriptionField);
		descriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(descriptionField, InputMethodManager.SHOW_IMPLICIT);
		        }
		    }
		});

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
				//EditText descriptionField = (EditText) editDescriptionView.findViewById(R.id.DescriptionField);
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
		
		// What I am attempting to add
		View header = (View)getLayoutInflater().inflate(R.layout.listview_header_createphotos, null);
        editPhotosListView.addHeaderView(header);
     
        //End of what I am attempting to add for header

		editPhotosListView.setAdapter(photoAdapter);
		
		editPhotosListView.setOnItemClickListener(this);


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
		editingSale.loadDetailsIntoView(detailsView, this);
		editReviewPublishView.setVisibility(View.VISIBLE);
		visibleEditView = editReviewPublishView;
		
	}
	
	/*
	 * OnSeekBarChangeListener Methods
	 */
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		EditText descriptionField = (EditText) editDescriptionView.findViewById(R.id.DescriptionField);
		
		if (visibleEditView != null) {
			if (visibleEditView.equals(editBasicInfoView)) {
				EditText titleField = (EditText) editBasicInfoView.findViewById(R.id.TitleField);
				EditText locationField = (EditText) editBasicInfoView.findViewById(R.id.LocationField);
				editingSale.title = titleField.getText().toString();
				editingSale.location = locationField.getText().toString();
				// converts address to latlng coords
				GetLatLngFromAddressAsyncTask markerTask = new GetLatLngFromAddressAsyncTask(this, editingSale);
				markerTask.execute();
			}
			if (visibleEditView.equals(editDescriptionView)) {
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
		if (step == 2) {
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(descriptionField.getWindowToken(), 0);
		}
		if (step == editProgressBar.getMax()) {
			nextButton.setText("Publish");
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(descriptionField.getWindowToken(), 0);
		} else {
			nextButton.setText("Next");
		}
		for (int i = 0; i < progressBarTextViews.length; i++) {
			progressBarTextViews[i].setTypeface(null, Typeface.NORMAL);
		}
		progressBarTextViews[step].setTypeface(null, Typeface.BOLD);
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
			//store.storeSale(editingSale, 13376);
			if (!currentlyPublishing) {
				currentlyPublishing = true;
				PostSaleAsyncTask postTask = new PostSaleAsyncTask(this, editingSale);
				postTask.execute();
			}
			
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
	    
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			// set title
			alertDialogBuilder.setTitle("Add New Photo");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Choose Photo Option")
				.setCancelable(true)
				.setPositiveButton("Camera",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// create Intent to take a picture and return control to the calling application
					    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					    // start the image capture Intent
					    startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				  })
				.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent pickPhoto = new Intent(Intent.ACTION_PICK,
						           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(pickPhoto , GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				});
				// Create Alert Dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// Show Dialog
				alertDialog.show();
	}
	
	/*
	 * Method: ViewOnMapButtonOnClick
	 * TODO: Have something happen. Maybe say this is disabled with Toast or go to Map View.
	 */
	public void ViewOnMapButtonOnClick(View view) {
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap newBitmap = null;
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Image Captured!", Toast.LENGTH_LONG).show();
	    	    Bundle extras = data.getExtras();
	    	    newBitmap = (Bitmap) extras.get("data");
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	    if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
	    	if (resultCode == RESULT_OK){
	    		Toast.makeText(this, "Image Selected!", Toast.LENGTH_SHORT).show();
	    		Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };

	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);

	            cursor.close();
	            newBitmap = BitmapFactory.decodeFile(picturePath);
	    	} else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	    if (newBitmap != null) {
		    newlyAddedPhoto = new Photo();
		    newlyAddedPhoto.bitmap = newBitmap;
		    newlyAddedPhoto.description = "";

		    /*
		     * Can't start Fragment in this method. Known bug. Start in onResume
		     * http://stackoverflow.com/questions/10114324/show-dialogfragment-from-onactivityresult
		     */
		    photoAdded = true;
		    
		    photoAdapter.add(newlyAddedPhoto);
		    photoAdapter.notifyDataSetChanged();	    	
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
		public EditSaleActivity activity;
		public EditText input;
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			activity = (EditSaleActivity) getActivity();
			//Alert Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Photo Description");
			builder.setMessage("Add a description or leave blank");
			input = new EditText(getActivity());

			builder.setView(input);
			builder.setPositiveButton("Ok", this);
			builder.setNegativeButton("Cancel", this);
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
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				activity.newlyAddedPhoto.description = input.getText().toString();
				activity.photoAdapter.notifyDataSetChanged();
			}
			/* 
    	     * For now just publish photo to server here. 
    	     * Probably should be done elsewhere though (like on publish)
    	     * This task also updates editingSale.mainPhotoId and photoIds
    	     */
    	    PostPhotoAsyncTask postPhotoTask = new PostPhotoAsyncTask(getActivity(), activity.newlyAddedPhoto);
    	    postPhotoTask.execute();
		}
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Add Another Photo?");
			builder.setMessage("Would you like to add another photo?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.AddNewPhotoButtonOnClick(null);
				}
			});
			builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}				
			});
			builder.show();
		}
	}
	
	public static class EditDescriptionDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
		public EditSaleActivity activity;
		public EditText input;
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			activity = (EditSaleActivity) getActivity();
			//Alert Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Photo Description");
			builder.setMessage("Edit description or leave blank");
			input = new EditText(getActivity());

			builder.setView(input);
			builder.setPositiveButton("Ok", this);
			builder.setNegativeButton("Cancel", this);
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
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				Photo p = activity.photoAdapter.getItem(activity.selectedPhoto);
				p.description = input.getText().toString();
				activity.photoAdapter.notifyDataSetChanged();
			}
		}
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
		}
	}
	
	public class PhotoOptionsDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction	        
	        CharSequence options[] = new CharSequence[] {"Preview", "Set as main photo", "Edit description", "Delete"};
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Photo options");
	        builder.setItems(options, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // the user clicked on options[which]
	            	Photo pic = (Photo) photoAdapter.getItem(selectedPhoto);
	            	switch (which) {
	     	       case 0:
	     	           System.out.println("Preview");
	     	           Intent i = new Intent(getActivity(), PhotoPreviewActivity.class);
	     	           i.putExtra("image", pic.bitmap);
	     	           i.putExtra("description", pic.description);
	     	           startActivity(i);
	     	           break;
	     	       case 1:
	     	    	   System.out.println("Set as main photo");
	     	    	   editingSale.mainPhotoId = pic.id;
	     	    	   Toast.makeText(getActivity(), "Set as main photo", Toast.LENGTH_SHORT).show();
	     	           break;
	     	       case 2:
	     	    	   System.out.println("Edit description");
	     	    	   EditDescriptionDialogFragment newFragment = new EditDescriptionDialogFragment();
	     	    	   newFragment.show(getSupportFragmentManager(), "photoDescription");
	     	           break;
	     	       case 3:
	     	    	   System.out.println("Delete");
	     	    	   photoAdapter.remove(pic);
	     	    	   break;
	     	       }
	            }
	        });
	        return builder.create();
	    }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		System.out.println("Got here!");
		selectedPhoto = position;
		PhotoOptionsDialogFragment newFragment = new PhotoOptionsDialogFragment();
		newFragment.show(getSupportFragmentManager(), "photoOptions");
		//popupMenu.show();
	}
}

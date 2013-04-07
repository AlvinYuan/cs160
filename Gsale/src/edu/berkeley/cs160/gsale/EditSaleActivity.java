package edu.berkeley.cs160.gsale;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class EditSaleActivity extends FragmentActivity implements OnSeekBarChangeListener  {
	public boolean isNewSale;
	public int editingSaleId;
	public GarageSale editingSale;
	public int step;
	public View editBasicInfoView;
	public View editDescriptionView;
	public View editPhotosView;
	public View editReviewPublishView;
	public View visibleEditView;
	public SeekBar editProgressBar;
	public Button backButton;
	public Button nextButton;
	
	public boolean selectingStart; //false = selectingEnd (for date/time)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sale);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle extras = this.getIntent().getExtras();
		isNewSale = extras.getBoolean(CreateEditActivity.IS_NEW_SALE_KEY);
		if (!isNewSale) {
			editingSaleId = extras.getInt(GarageSale.SALE_ID_KEY);
			editingSale = GarageSale.mapIdToSale.get(editingSaleId);
		} else {
			editingSale = new GarageSale();
		}
		
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

		/* Review/Publish */
		editReviewPublishView = inflater.inflate(R.layout.edit_review_publish_view, null);
		editLayout.addView(editReviewPublishView);
		editReviewPublishView.setVisibility(View.INVISIBLE);

		
		backButton = (Button) findViewById(R.id.BackButton);
		nextButton = (Button) findViewById(R.id.NextButton);
		
		editProgressBar = (SeekBar) findViewById(R.id.EditProgressBar);
		editProgressBar.setOnSeekBarChangeListener(this);
		onProgressChanged(editProgressBar, 0, false); // Trigger on activity creation
		
		
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
	 * Button OnClick Methods
	 */
	public void BackButtonOnClick(View view) {
		if(step == 0) {
			
		} else {
			editProgressBar.setProgress(editProgressBar.getProgress() - 1);
		}
	}
	
	public void NextButtonOnClick(View view) {
		if (step == editProgressBar.getMax()) {
			
		} else {
			editProgressBar.setProgress(editProgressBar.getProgress() + 1);
		}
	}
	
	public void AddNewPhotoButtonOnClick(View view) {
		
	}
	
	public void StartDateFieldOnClick(View view) {
		selectingStart = true;
		createDatePickerDialog();
	}
	
	public void EndDateFieldOnClick(View view) {
		selectingStart = false;
		createDatePickerDialog();
	}
	
	public void createDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");		
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
			Calendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			String monthString = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
			String weekdayString = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
			String dateString = weekdayString + ", " + monthString + " " + dayOfMonth + ", " + year;
			EditText dateField;
			if (activity.selectingStart) {
				activity.editingSale.startDate = date;
				dateField = (EditText) activity.editBasicInfoView.findViewById(R.id.StartDateField);
			} else {
				activity.editingSale.endDate = date;
				dateField = (EditText) activity.editBasicInfoView.findViewById(R.id.EndDateField);
			}
			dateField.setText(dateString);
		}
		
	}
}

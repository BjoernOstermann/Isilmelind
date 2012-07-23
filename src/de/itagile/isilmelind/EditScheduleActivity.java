package de.itagile.isilmelind;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.OccupationTable;
import de.itagile.isilmelind.TimePickerFragment.TimePickerDialogListener;

public class EditScheduleActivity extends FragmentActivity implements
		TimePickerDialogListener, OnItemSelectedListener {

	private IsilmelindDbOpenHelper dbHelper;
	public static final String START = "start";
	public static final String END = "end";

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void ok(View view) {
		setResult(RESULT_OK, getIntent());
		finish();
	}

	public void editScheduleTimeStart(View view) {
		editScheduleTime(START);
	}

	public void editScheduleTimeEnd(View view) {
		editScheduleTime(END);
	}

	private void editScheduleTime(String buttonKey) {
		TimePickerFragment timePickerFragment = new TimePickerFragment();
		Bundle args = new Bundle();
		Time time = getTime(getIntent(), buttonKey);
		args.putInt("hour", time.hour);
		args.putInt("minute", time.minute);
		args.putString("buttonId", buttonKey);
		timePickerFragment.setArguments(args);
		timePickerFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = IsilmelindDbOpenHelper.getInstance(this);
		setContentView(R.layout.edit_schedule);
		prefillViews(getIntent());
	}

	private void prefillViews(Intent intent) {
		prefillButton(intent, R.id.edit_schedule_start, START);
		prefillButton(intent, R.id.edit_schedule_end, END);
		prefillOccupation(intent);
	}

	private void prefillOccupation(Intent intent) {
		Spinner occupations = (Spinner) findViewById(R.id.edit_schedule_occupation_name);
		SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, dbHelper.selectAll(),
				new String[] { OccupationTable.COLUMN_NAME, },
				new int[] { android.R.id.text1 }, 0);
		scAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		occupations.setAdapter(scAdapter);
		occupations.setOnItemSelectedListener(this);
		int occupationId = intent.getIntExtra("occupationId", -1);
		if (occupationId > -1) {
			for (int position = 0; position < occupations.getCount(); position++) {
				Cursor cursor = (Cursor) occupations.getItemAtPosition(position);
				if (cursor.getInt(0) == occupationId) {
					occupations.setSelection(position);
					break;
				}
			}
		}
	}

	private void prefillButton(Intent intent, int id, String key) {
		Button timeButton = (Button) findViewById(id);
		Time time = getTime(intent, key);
		timeButton.setText(formatForButton(time));
	}

	private String formatForButton(Time time) {
		return time.format("%H:%M");
	}

	private Time getTime(Intent intent, String buttonKey) {
		Time time = new Time();
		String timeStr = intent.getStringExtra(buttonKey);
		if (timeStr != null) {
			time.parse(timeStr);
		} else {
			time.setToNow();
			intent.putExtra(buttonKey, time.format2445());
		}
		return time;
	}

	@Override
	public void onFinishDialog(String buttonId, int hour, int minute) {
		int id = R.id.edit_schedule_start;
		if (buttonId.equals(END)) {
			id = R.id.edit_schedule_end;
		}
		Button timeButton = (Button) findViewById(id);
		final Intent intent = getIntent();
		Time time = getTime(intent, buttonId);
		time.hour = hour;
		time.minute = minute;
		timeButton.setText(formatForButton(time));
		intent.putExtra(buttonId, time.format2445());
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		Cursor cursor = (Cursor) parent.getItemAtPosition(pos);
		int rowId = cursor.getInt(cursor.getColumnIndex(OccupationTable._ID));
		Intent intent = getIntent();
		intent.putExtra("occupationId", rowId);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
package de.itagile.isilmelind;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	public interface TimePickerDialogListener {
		void onFinishDialog(String buttonId, int hour, int minute);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = getArguments().getInt("hour");
		int minute = getArguments().getInt("minute");
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		TimePickerDialogListener activity = (TimePickerDialogListener) getActivity();
		Bundle arguments = getArguments();
		Log.v("Hmmm...", arguments.getString("buttonId"));
		activity.onFinishDialog(arguments.getString("buttonId"), hourOfDay, minute);
		this.dismiss();
	}
}
package de.itagile.isilmelind;

import de.itagile.isilmelind.IsilmelindDbOpenHelper.OccupationTable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;

public class EditOccupationActivity extends Activity {

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void ok(View view) {
		EditText editName = (EditText) findViewById(R.id.edit_occupation_name);
		EditText editRating = (EditText) findViewById(R.id.edit_occupation_rating);
		Intent intent = getIntent();
		intent.putExtra(OccupationTable.COLUMN_NAME, editName.getText().toString());
		intent.putExtra(OccupationTable.COLUMN_RATING, Integer.parseInt(editRating.getText().toString()));
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_occupation);
		prefillEditFields();
	}
	
	private void prefillEditFields() {
		Intent intent = getIntent();
		prefillName(intent);
		prefillRating(intent);
	}

	private void prefillName(Intent intent) {
		EditText editName = (EditText) findViewById(R.id.edit_occupation_name);
		String name = intent.getStringExtra(OccupationTable.COLUMN_NAME);
		if (name == null) {
			name = "";
		}
		editName.setText(name);
	}
	
	private void prefillRating(Intent intent) {
		EditText editRating = (EditText) findViewById(R.id.edit_occupation_rating);
		int rating = intent.getIntExtra(OccupationTable.COLUMN_RATING, 0);
		editRating.setText(Integer.toString(rating));
	}
}

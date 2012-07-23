package de.itagile.isilmelind;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.OccupationTable;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.ScheduleTable;

public class ScheduleActivity extends ListActivity {

	private static final int REQUEST_CREATE = 1;
	private static final int REQUEST_EDIT = 2;
	private IsilmelindDbOpenHelper dbHelper;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_show_schedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menue_show_schedule_create:
			startActivityForResult(createEditIntent(), REQUEST_CREATE);
			return true;
		case R.id.menue_show_schedule_goto_show_occupations:
			startActivity(new Intent(this, OccupationActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (intent == null)
			return;
		Time startTime = new Time();
		startTime.parse(intent.getStringExtra(EditScheduleActivity.START));
		Time endTime = new Time();
		endTime.parse(intent.getStringExtra(EditScheduleActivity.END));
		int occupationId = intent.getIntExtra("occupationId", 0);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ScheduleTable.COLUMN_START, startTime.format2445());
		values.put(ScheduleTable.COLUMN_END, endTime.format2445());
		values.put(ScheduleTable.COLUMN_OCCUPATION_ID, occupationId);
		if (requestCode == REQUEST_EDIT) {
			updateRecord(intent, db, values);
		} else if (requestCode == REQUEST_CREATE) {
			insertRecord(db, values);
		}
		refreshAdapter(db);
		db.close();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = IsilmelindDbOpenHelper.getInstance(this);
		View header = getLayoutInflater().inflate(R.layout.schedule_date, null);
		getListView().addHeaderView(header);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = selectAll(db);
		SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this,
				R.layout.show_schedule, cursor, new String[] {
						ScheduleTable.COLUMN_START, ScheduleTable.COLUMN_END,
						OccupationTable.COLUMN_NAME,
						OccupationTable.COLUMN_RATING }, new int[] {
						R.id.show_schedule_start, R.id.show_schedule_end,
						R.id.show_schedule_occupation_name,
						R.id.show_schedule_occupation_rating }, 0);
		setListAdapter(scAdapter);
		db.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		position--;
		if (position < 0) {
			return;
		}
		Cursor cursor = (Cursor) getListAdapter().getItem(position);
		String startTime = cursor.getString(ScheduleTable.COLUMN_START_INDEX);
		String endTime = cursor.getString(ScheduleTable.COLUMN_END_INDEX);
		int columnIndex = cursor
				.getColumnIndex(ScheduleTable.COLUMN_OCCUPATION_ID);
		int occupationId = cursor.getInt(columnIndex);
		Intent intent = createEditIntent();
		intent.putExtra(ScheduleTable._ID, id);
		intent.putExtra(EditScheduleActivity.START, startTime);
		intent.putExtra(EditScheduleActivity.END, endTime);
		intent.putExtra("occupationId", occupationId);
		startActivityForResult(intent, REQUEST_EDIT);
	}

	private Intent createEditIntent() {
		return new Intent(this, EditScheduleActivity.class);
	}

	private void insertRecord(SQLiteDatabase db, ContentValues values) {
		db.insert(ScheduleTable.NAME, null, values);
	}

	private void refreshAdapter(SQLiteDatabase db) {
		SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) getListAdapter();
		Cursor cursor = selectAll(db);
		listAdapter.changeCursor(cursor);
	}

	private Cursor selectAll(SQLiteDatabase db) {
		return db.rawQuery("SELECT " + ScheduleTable.NAME + "."
				+ ScheduleTable._ID + ", " + ScheduleTable.COLUMN_START + ", "
				+ ScheduleTable.COLUMN_END + ", " + OccupationTable.COLUMN_NAME
				+ ", " + OccupationTable.COLUMN_RATING + ", "
				+ ScheduleTable.COLUMN_OCCUPATION_ID
				+ " FROM " + ScheduleTable.NAME + " join "
				+ OccupationTable.NAME + " on "
				+ ScheduleTable.COLUMN_OCCUPATION_ID + " = "
				+ OccupationTable.NAME + "." + OccupationTable._ID, null);
	}

	private void updateRecord(Intent intent, SQLiteDatabase db,
			ContentValues values) {
		String where = ScheduleTable._ID + "=?";
		String[] whereArgs = { String.valueOf(intent.getLongExtra(
				ScheduleTable._ID, 0)) };
		db.update(ScheduleTable.NAME, values, where, whereArgs);
	}
}
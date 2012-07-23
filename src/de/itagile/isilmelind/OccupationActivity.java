package de.itagile.isilmelind;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.OccupationTable;

public class OccupationActivity extends ListActivity {

	private static final int REQUEST_CREATE = 1;
	private static final int REQUEST_EDIT = 2;
	private IsilmelindDbOpenHelper dbHelper;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_occupation_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_menu_create:
			startActivityForResult(createEditIntent(), REQUEST_CREATE);
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
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(OccupationTable.COLUMN_NAME,
				intent.getStringExtra(OccupationTable.COLUMN_NAME));
		values.put(OccupationTable.COLUMN_RATING,
				intent.getIntExtra(OccupationTable.COLUMN_RATING, 0));
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
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this,
				R.layout.occupation_list_row, dbHelper.selectAll(db), new String[] {
						OccupationTable.COLUMN_NAME,
						OccupationTable.COLUMN_RATING }, new int[] {
						R.id.occupation_name, R.id.occupation_rating }, 0);
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
		Cursor cursor = (Cursor) getListAdapter().getItem(position);
		String name = cursor.getString(OccupationTable.COLUMN_NAME_INDEX);
		int rating = cursor.getInt(OccupationTable.COLUMN_RATING_INDEX);
		Intent intent = createEditIntent();
		intent.putExtra(OccupationTable._ID, id);
		intent.putExtra(OccupationTable.COLUMN_NAME, name);
		intent.putExtra(OccupationTable.COLUMN_RATING, rating);
		startActivityForResult(intent, REQUEST_EDIT);
	}

	private Intent createEditIntent() {
		return new Intent(this, EditOccupationActivity.class);
	}

	private void insertRecord(SQLiteDatabase db, ContentValues values) {
		db.insert(OccupationTable.NAME, null, values);
	}

	private void refreshAdapter(SQLiteDatabase db) {
		SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) getListAdapter();
		Cursor cursor = dbHelper.selectAll(db);
		listAdapter.changeCursor(cursor);
	}

	private void updateRecord(Intent intent, SQLiteDatabase db,
			ContentValues values) {
		String where = OccupationTable._ID + "=?";
		String[] whereArgs = { String.valueOf(intent.getLongExtra(
				OccupationTable._ID, 0)) };
		db.update(OccupationTable.NAME, values, where, whereArgs);
	}
}
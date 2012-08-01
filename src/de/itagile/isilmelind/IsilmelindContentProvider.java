package de.itagile.isilmelind;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.OccupationTable;
import de.itagile.isilmelind.IsilmelindDbOpenHelper.ScheduleTable;

public class IsilmelindContentProvider extends ContentProvider {

	// database
	private IsilmelindDbOpenHelper database;

	// Used for the UriMacher
	private static final int OCCUPATIONS = 10;
	private static final int OCCUPATION_ID = 20;
	
	private static final int SCHEDULES = 30;
	private static final int SCHEDULE_ID = 40;

	private static final String AUTHORITY = "de.itagile.isilmelind.contentprovider";

	private static final String OCCUPATIONS_BASE_PATH = "occupations";
	private static final String SCHEDULES_BASE_PATH = "schedules";
	
	public static final Uri OCCUPATIONS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + OCCUPATIONS_BASE_PATH);
	public static final Uri SCHEDULES_CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + SCHEDULES_BASE_PATH);

//	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
//			+ "/occupations";
//	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
//			+ "/occupation";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, OCCUPATIONS_BASE_PATH, OCCUPATIONS);
		sURIMatcher.addURI(AUTHORITY, OCCUPATIONS_BASE_PATH + "/#", OCCUPATION_ID);
		sURIMatcher.addURI(AUTHORITY, SCHEDULES_BASE_PATH, SCHEDULES);
		sURIMatcher.addURI(AUTHORITY, SCHEDULES_BASE_PATH + "/#", SCHEDULE_ID);
	}

	@Override
	public boolean onCreate() {
		database = IsilmelindDbOpenHelper.getInstance(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case OCCUPATIONS:
			queryBuilder.setTables(OccupationTable.NAME);
			break;
		case SCHEDULES:
			queryBuilder.setTables(ScheduleTable.NAME);
			break;
		case OCCUPATION_ID:
			// Set the table
			queryBuilder.setTables(OccupationTable.NAME);
			// Adding the ID to the original query
			queryBuilder.appendWhere(OccupationTable._ID + "="
					+ uri.getLastPathSegment());
			break;
		case SCHEDULE_ID:
			// Set the table
			queryBuilder.setTables(ScheduleTable.NAME);
			// Adding the ID to the original query
			queryBuilder.appendWhere(ScheduleTable._ID + "="
					+ uri.getLastPathSegment());
			break;	
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case OCCUPATIONS:
			id = sqlDB.insert(OccupationTable.NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(OCCUPATIONS_BASE_PATH + "/" + id);
		case SCHEDULES:
			id = sqlDB.insert(ScheduleTable.NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(SCHEDULES_BASE_PATH + "/" + id);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case OCCUPATIONS:
			rowsDeleted = sqlDB.delete(OccupationTable.NAME, selection,
					selectionArgs);
			break;
		case SCHEDULES:
			rowsDeleted = sqlDB.delete(ScheduleTable.NAME, selection,
					selectionArgs);
			break;	
		case OCCUPATION_ID:
			String occupationId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(OccupationTable.NAME,
						OccupationTable._ID + "=" + occupationId, null);
			} else {
				rowsDeleted = sqlDB.delete(OccupationTable.NAME,
						OccupationTable._ID + "=" + occupationId + " and " + selection,
						selectionArgs);
			}
			break;
		case SCHEDULE_ID:
			String scheduleId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ScheduleTable.NAME,
						ScheduleTable._ID + "=" + scheduleId, null);
			} else {
				rowsDeleted = sqlDB.delete(ScheduleTable.NAME,
						ScheduleTable._ID + "=" + scheduleId + " and " + selection,
						selectionArgs);
			}
			break;	
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case OCCUPATIONS:
			rowsUpdated = sqlDB.update(OccupationTable.NAME, values, selection,
					selectionArgs);
			break;
		case SCHEDULES:
			rowsUpdated = sqlDB.update(ScheduleTable.NAME, values, selection,
					selectionArgs);
			break;	
		case OCCUPATION_ID:
			String occupationId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(OccupationTable.NAME, values,
						OccupationTable._ID + "=" + occupationId, null);
			} else {
				rowsUpdated = sqlDB.update(OccupationTable.NAME, values,
						OccupationTable._ID + "=" + occupationId + " and " + selection,
						selectionArgs);
			}
			break;
		case SCHEDULE_ID:
			String scheduleId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(OccupationTable.NAME, values,
						OccupationTable._ID + "=" + scheduleId, null);
			} else {
				rowsUpdated = sqlDB.update(OccupationTable.NAME, values,
						OccupationTable._ID + "=" + scheduleId + " and " + selection,
						selectionArgs);
			}
			break;	
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}

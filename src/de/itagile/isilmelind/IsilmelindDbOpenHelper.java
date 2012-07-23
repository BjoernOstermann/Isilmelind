package de.itagile.isilmelind;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class IsilmelindDbOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "isilmelind.db";
	private static IsilmelindDbOpenHelper instance;

	private IsilmelindDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static IsilmelindDbOpenHelper getInstance(Context ctx) {
		if (instance == null) {
			instance = new IsilmelindDbOpenHelper(ctx.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + OccupationTable.NAME + " ("
				+ OccupationTable._ID + " INTEGER PRIMARY KEY,"
				+ OccupationTable.COLUMN_NAME + " TEXT,"
				+ OccupationTable.COLUMN_RATING + " INTEGER" + ");");
		db.execSQL("CREATE TABLE " + ScheduleTable.NAME + " ("
				+ ScheduleTable._ID + " INTEGER PRIMARY KEY,"
				+ ScheduleTable.COLUMN_START + " TEXT,"
				+ ScheduleTable.COLUMN_END + " TEXT,"
				+ ScheduleTable.COLUMN_OCCUPATION_ID + " INTEGER"
				+ ", FOREIGN KEY (" + ScheduleTable.COLUMN_OCCUPATION_ID
				+ ") REFERENCES " + OccupationTable.NAME + " ("
				+ OccupationTable._ID + "));");
	}

	public static final class OccupationTable implements BaseColumns {
		private OccupationTable() {
		}

		public static final String NAME = "occupation";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_RATING = "rating";
		public static final int COLUMN_NAME_INDEX = 1;
		public static final int COLUMN_RATING_INDEX = 2;
	}

	public static final class ScheduleTable implements BaseColumns {
		private ScheduleTable() {
		}

		public static final String NAME = "schedule";
		public static final String COLUMN_START = "start";
		public static final String COLUMN_END = "end";
		public static final String COLUMN_OCCUPATION_ID = "occupation_id";
		public static final int COLUMN_START_INDEX = 1;
		public static final int COLUMN_END_INDEX = 2;
		public static final int COLUMN_OCCUPATION_ID_INDEX = 3;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Kills the table and existing data
		db.execSQL("DROP TABLE IF EXISTS " + OccupationTable.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ScheduleTable.NAME);
		// Recreates the database with a new version
		onCreate(db);
	}

	public Cursor selectAll(SQLiteDatabase db) {
		return db.rawQuery("SELECT  * FROM " + OccupationTable.NAME, null);
	}

	public Cursor selectAll() {
		return selectAll(getReadableDatabase());
	}
}
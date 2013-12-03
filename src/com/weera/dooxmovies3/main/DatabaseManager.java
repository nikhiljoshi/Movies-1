package com.weera.dooxmovies3.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

	Context context;

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "dooxmovies.db";

	// Table Name
	private static final String TABLE_MOVIE = "movies";

	// -------VERSION 1-------------
	// ---bookmarks---
	private static final String COL_TITLE = "title";
	private static final String COL_DESC = "desc";
	private static final String COL_IMG = "img";
	private static final String COL_URL = "url";
	private static final String COL_VCODE = "vcode";
	// -------------------------------

	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("------ onCreate --------");
		try {
			db.execSQL("CREATE TABLE " + TABLE_MOVIE
					+ "("
					// BookmarkID INTEGER PRIMARY KEY,"
					+ " " + COL_TITLE + " TEXT(100)," + " " + COL_DESC
					+ " TEXT(100)," + " " + COL_IMG + " TEXT(100)," + " "
					+ COL_URL + " TEXT(100))" + COL_VCODE + " TEXT(100));");

			Log.d("CREATE TABLE", "Create Table Successfully.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("DB ERROR: " + e);
		}

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		System.out.println("------ onOpen --------");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("------ onUpgrade --------");
		System.out.println("------ oldVersion : " + oldVersion);
		System.out.println("------ newVersion : " + newVersion);

		if (oldVersion == 1 && newVersion == 2) {
//			createTableRead(db);
		}
	}

	// Insert Data
	public boolean insertOrUpdateMovie(String title, String desc,
			String img, String url, String vcode) {
		// TODO Auto-generated method stub

		if (getMovieByVcode(vcode) == null) {
			try {
				SQLiteDatabase db = this.getWritableDatabase();
				/**
				 * for API 11 and above SQLiteStatement insertCmd; String strSQL
				 * = "INSERT INTO " + TABLE_MEMBER +
				 * "(MemberID,Name,Tel) VALUES (?,?,?)";
				 * 
				 * insertCmd = db.compileStatement(strSQL);
				 * insertCmd.bindString(1, strMemberID); insertCmd.bindString(2,
				 * strName); insertCmd.bindString(3, strTel); return
				 * insertCmd.executeInsert();
				 */

				ContentValues Val = new ContentValues();
				Val.put(COL_TITLE, title);
				Val.put(COL_DESC, desc);
				Val.put(COL_IMG, img);
				Val.put(COL_URL, url);
				Val.put(COL_VCODE, vcode);

				db.insert(TABLE_MOVIE, null, Val);

				if (db != null) {
					db.close();
				}

				return true; // return rows inserted.

			} catch (Exception e) {
				Log.i("insertOrUpdateMovie", e.getMessage());
				return false;
			}
		} else {
			return true;
		}

	}

	public HashMap<String, String> getMovieByVcode(String vcode) {
		// TODO Auto-generated method stub
		HashMap<String, String> hash = null;
		try {
			// SQLiteDatabase db;
			SQLiteDatabase db = this.getWritableDatabase(); // Read Data

			String strSQL = "SELECT * FROM " + TABLE_MOVIE + " WHERE "
					+ COL_VCODE + " = '" + vcode + "'";
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					hash = new HashMap<String, String>();
					hash.put(COL_TITLE, cursor.getString(0));
					hash.put(COL_DESC, cursor.getString(1));
					hash.put(COL_IMG, cursor.getString(2));
					hash.put(COL_URL, cursor.getString(3));
					hash.put(COL_VCODE, cursor.getString(4));
				}
			}
			cursor.close();
			if (db != null) {
				db.close();
			}
			return hash;

		} catch (Exception e) {
			Log.i("getBookmarkByTitle", e.getMessage());
			return null;
		}

	}
	
	public ArrayList<HashMap<String, String>> getAllMovies() {
		// TODO Auto-generated method stub

		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

			SQLiteDatabase db = this.getWritableDatabase(); // Read Data

			String strSQL = "SELECT * FROM " + TABLE_MOVIE;
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						HashMap<String, String> hash = new HashMap<String, String>();
						hash.put(COL_TITLE, cursor.getString(0));
						hash.put(COL_DESC, cursor.getString(1));
						hash.put(COL_IMG, cursor.getString(2));
						hash.put(COL_URL, cursor.getString(3));
						hash.put(COL_VCODE, cursor.getString(4));
						list.add(hash);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			if (db != null) {
				db.close();
			}
			return list;

		} catch (Exception e) {
			Log.i("getAllBookmark", e.getMessage());
			// System.out.println("ERROR getAllBookmark: "+e);
			return null;
		}

	}

	// Delete Data
	public boolean deleteMovieByVcode(String vcode) {
		// TODO Auto-generated method stub

		try {

			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data

			/**
			 * for API 11 and above SQLiteStatement insertCmd; String strSQL =
			 * "DELETE FROM " + TABLE_MEMBER + " WHERE MemberID = ? ";
			 * 
			 * insertCmd = db.compileStatement(strSQL); insertCmd.bindString(1,
			 * strMemberID);
			 * 
			 * return insertCmd.executeUpdateDelete();
			 * 
			 */

			db.delete(TABLE_MOVIE, COL_VCODE + " = ?",
					new String[] { String.valueOf(vcode) });

			db.close();
			return true; // return rows delete.

		} catch (Exception e) {
			return false;
		}

	}
	
}

package com.kenny.LyricPlayer.xwg;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 */
public class LyricDbAdapter
{
        public static final String  KEY_ROWID          = "_id";
        public static final String  KEY_MUSIC_URL      = "music_url";
        public static final String  KEY_MUSIC_TITLE    = "music_title";
        public static final String  KEY_MUSIC_ARTIST   = "music_artist";
        public static final String  KEY_MUSIC_DURATION = "music_duration";
        public static final String  KEY_LYRIC_TITLE    = "lyric_title";
        public static final String  KEY_LYRIC_URL      = "lyric_url";
        public static final String  KEY_LYRIC_ENCODING = "lyric_encoding";
        public static final String  KEY_LAST_PLAY      = "last_play";
        
        // private static final String TAG = "LyricDbAdapter";
        private DatabaseHelper      mDbHelper;
        private SQLiteDatabase      mDb;
        private static final String DATABASE_NAME      = "LyricPlayerDb";
        private static final String LYRIC_TABLE        = "LyricInfo";
        private static final int    DATABASE_VERSION   = 2;
        
        private final String[]      mLyricInfoColumns  = new String[]
	                                             { KEY_ROWID,
	              KEY_MUSIC_URL, KEY_MUSIC_TITLE, KEY_MUSIC_ARTIST,
	              KEY_MUSIC_DURATION, KEY_LYRIC_URL, KEY_LYRIC_TITLE,
	              KEY_LYRIC_ENCODING, KEY_LAST_PLAY };
        
        /**
         * Database creation sql statement
         */
        private static final String DATABASE_CREATE    = "create table "
	                                                             + LYRIC_TABLE
	                                                             + " ("
	                                                             + KEY_ROWID
	                                                             + " integer primary key autoincrement, "
	                                                             + KEY_MUSIC_URL
	                                                             + " text not null,"
	                                                             + KEY_MUSIC_TITLE
	                                                             + " text not null, "
	                                                             + KEY_MUSIC_ARTIST
	                                                             + " text not null,"
	                                                             + KEY_MUSIC_DURATION
	                                                             + " text not null,"
	                                                             + KEY_LYRIC_URL
	                                                             + " text not null,"
	                                                             + KEY_LYRIC_TITLE
	                                                             + " text not null,"
	                                                             + KEY_LYRIC_ENCODING
	                                                             + " text not null,"
	                                                             + KEY_LAST_PLAY
	                                                             + " datetime);";
        
        private final Context       mCtx;
        
        private static class DatabaseHelper extends SQLiteOpenHelper
        {
	      
	      DatabaseHelper(Context context)
	      {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
	      
	      @Override
	      public void onCreate(SQLiteDatabase db)
	      {
		    
		    db.execSQL(DATABASE_CREATE);
	      }
	      
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion,
		            int newVersion)
	      {
		    // Log.w(TAG, "Upgrading database from version " +
		    // oldVersion + " to "
		    // + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + LYRIC_TABLE);
		    onCreate(db);
	      }
        }
        
        /**
         * Constructor - takes the context to allow the database to be
         * opened/created
         * 
         * @param ctx
         *                the Context within which to work
         */
        public LyricDbAdapter(Context ctx)
        {
	      this.mCtx = ctx;
        }
        
        /**
         * Open the notes database. If it cannot be opened, try to create a new
         * instance of the database. If it cannot be created, throw an exception
         * to signal the failure
         * 
         * @return this (self reference, allowing this to be chained in an
         *         initialization call)
         * @throws SQLException
         *                 if the database could be neither opened or created
         */
        public LyricDbAdapter open() throws SQLException
        {
	      mDbHelper = new DatabaseHelper(mCtx);
	      mDb = mDbHelper.getWritableDatabase();
	      return this;
        }
        
        public void close()
        {
	      mDbHelper.close();
        }
        
        public int getColumnIndex(String columnName)
        {
	      for (int i = 0; i < mLyricInfoColumns.length; ++i)
	      {
		    if (columnName.compareTo(mLyricInfoColumns[i]) == 0) { return i; }
	      }
	      return -1;
        }
        
        public int getColumnCount()
        {
	      return mLyricInfoColumns.length;
        }
        
        public String getColumnName(int columnIndex)
        {
	      if (columnIndex >= 0 && columnIndex < getColumnCount())
	      {
		    return mLyricInfoColumns[columnIndex];
	      }
	      else
	      {
		    return null;
	      }
        }
        
        public void updateMediaFiles()
        {
	      Cursor music_cursor = mCtx.getContentResolver().query(
		            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		            null, null, null,
		            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	      if (music_cursor.getCount() == 0)
	      {
		    Toast toast = Toast.makeText(mCtx,
			          "No music has been found",
			          Toast.LENGTH_LONG);
		    toast.show();
		    return;
	      }
	      music_cursor.moveToFirst();
	      while (!music_cursor.isLast())
	      {
		    String music_url = music_cursor
			          .getString(music_cursor
			                          .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
		    Cursor lyric_info_cursor = fetchMusic(music_url);
		    if (lyric_info_cursor.getCount() == 0)
		    {
			  int duration = music_cursor
				        .getInt(music_cursor
				                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000;
			  String duration_str = String.format("%2d:%02d",
				        duration / 60, duration % 60);
			  createNote(music_url,
				        music_cursor.getString(music_cursor
				                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
				        music_cursor.getString(music_cursor
				                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
				        duration_str);
		    }
		    lyric_info_cursor.close();
		    lyric_info_cursor = null;
		    music_cursor.moveToNext();
	      }
	      music_cursor.close();
	      music_cursor = null;
        }
        
        /**
         * Create a new note using the title and body provided. If the note is
         * successfully created return the new rowId for that note, otherwise
         * return a -1 to indicate failure.
         * 
         * @param music_url
         *                the title of the note
         * @param title
         *                the body of the note
         * @return rowId or -1 if failed
         */
        public long createNote(String music_url, String music_title,
	              String music_artist, String music_duration)
        {
	      ContentValues initialValues = new ContentValues();
	      initialValues.put(KEY_MUSIC_URL, music_url);
	      initialValues.put(KEY_MUSIC_TITLE, music_title);
	      initialValues.put(KEY_MUSIC_ARTIST, music_artist);
	      initialValues.put(KEY_MUSIC_DURATION, music_duration);
	      String lyricUrl = music_url.substring(0,
		            music_url.lastIndexOf('.'))
		            + ".lrc";
	      File file = new File(lyricUrl);
	      if (file.exists())
	      {
		    initialValues.put(KEY_LYRIC_URL, lyricUrl);
		    initialValues.put(KEY_LYRIC_TITLE,
			          lyricUrl.substring(lyricUrl
			                          .lastIndexOf('/') + 1));
	      }
	      else
	      {
		    initialValues.put(KEY_LYRIC_URL, new String(""));
		    initialValues.put(KEY_LYRIC_TITLE, new String(""));
	      }
	      file = null;
	      
	      initialValues.put(KEY_LYRIC_ENCODING, new String("GB2312"));
	      return mDb.insert(LYRIC_TABLE, null, initialValues);
        }
        
        /**
         * Delete the note with the given rowId
         * 
         * @param rowId
         *                id of note to delete
         * @return true if deleted, false otherwise
         */
        public boolean deleteNote(String music_url)
        {
	      
	      return mDb.delete(LYRIC_TABLE, KEY_MUSIC_URL + "=\""
		            + music_url + "\"", null) > 0;
        }
        
        /**
         * Return a Cursor over the list of all notes in the database
         * 
         * @return Cursor over all notes
         */
        public Cursor fetchAllNotes()
        {
	      try
	      {
		    return mDb.query(LYRIC_TABLE, mLyricInfoColumns, null,
			          null, null, null, null);
	      }
	      catch (SQLiteException e)
	      {
		    return null;
	      }
        }
        
        public Cursor fetchMusic(int id) throws SQLException
        {
	      
	      Cursor mCursor = mDb.query(true, LYRIC_TABLE,
		            mLyricInfoColumns, KEY_ROWID + "=" + id, null,
		            null, null, null, null);
	      if (mCursor != null)
	      {
		    mCursor.moveToFirst();
	      }
	      return mCursor;
	      
        }
        
        public Cursor fetchRecentPlayAfter(Date time) throws SQLException
        {
	      SimpleDateFormat format = new SimpleDateFormat(
		            "yyyy-MM-dd HH:mm:ss");
	      Cursor mCursor = mDb.query(LYRIC_TABLE, mLyricInfoColumns,
		            KEY_LAST_PLAY + ">\"" + format.format(time)
		                            + "\"", null, null, null,
		            KEY_LAST_PLAY + " DESC", "20");
	      if (mCursor != null)
	      {
		    mCursor.moveToFirst();
	      }
	      return mCursor;
	      
        }
        
        public Cursor fetchMusic(String music_url) throws SQLException
        {
	      
	      Cursor mCursor =
	      
	      mDb.query(true, LYRIC_TABLE, mLyricInfoColumns, KEY_MUSIC_URL
		            + "=\"" + music_url + "\"", null, null, null,
		            null, null);
	      if (mCursor != null)
	      {
		    mCursor.moveToFirst();
	      }
	      return mCursor;
	      
        }
        
        public boolean updateMusicInfo(String music_url, String music_title,
	              String music_artist, String music_duration,
	              String lyric_url, String lyric_title,
	              String lyric_encoding, String last_play)
        {
	      // Toast toast = Toast.makeText(mCtx.getApplicationContext(),
	      // "Seek media file from storage..." + music_title,
	      // Toast.LENGTH_SHORT);
	      // toast.show();
	      
	      ContentValues values = new ContentValues();
	      values.put(KEY_MUSIC_URL, music_url);
	      values.put(KEY_MUSIC_TITLE, music_title);
	      values.put(KEY_MUSIC_ARTIST, music_artist);
	      values.put(KEY_MUSIC_DURATION, music_duration);
	      values.put(KEY_LYRIC_URL, lyric_url);
	      values.put(KEY_LYRIC_TITLE, lyric_title);
	      values.put(KEY_LYRIC_ENCODING, lyric_encoding);
	      values.put(KEY_LAST_PLAY, last_play);
	      return mDb.update(LYRIC_TABLE, values, KEY_MUSIC_URL + "=\""
		            + music_url + "\"", null) > 0;
        }
        
        public boolean updateLyricInfo(String music_url, String lyric_url,
	              String lyric_encoding)
        {
	      boolean result = false;
	      Cursor cursor = fetchMusic(music_url);
	      cursor.moveToLast();
	      if (cursor.isFirst())
	      {
		    String url = cursor
			          .getString(cursor
			                          .getColumnIndex(LyricDbAdapter.KEY_LYRIC_URL));
		    String encoding = cursor
			          .getString(cursor
			                          .getColumnIndex(LyricDbAdapter.KEY_LYRIC_ENCODING));
		    if (url.compareTo(lyric_url) != 0
			          || encoding.compareTo(lyric_encoding) != 0)
		    {
			  ContentValues values = new ContentValues();
			  values.put(KEY_LYRIC_URL, lyric_url);
			  values.put(KEY_LYRIC_TITLE,
				        lyric_url.substring(lyric_url
				                        .lastIndexOf('/') + 1));
			  values.put(KEY_LYRIC_ENCODING, lyric_encoding);
			  result = mDb.update(LYRIC_TABLE, values,
				        KEY_MUSIC_URL + "=\""
				                        + music_url
				                        + "\"", null) > 0;
		    }
		    else
		    {
			  result = true;
		    }
	      }
	      else
	      {
		    Toast.makeText(mCtx.getApplicationContext(),
			          "Can't find information of "
			                          + music_url + ".",
			          Toast.LENGTH_SHORT).show();
		    result = false;
	      }
	      cursor.close();
	      return result;
        }
        
        public boolean updatePlaytime(String music_url)
        {
	      boolean result = false;
	      Cursor cursor = fetchMusic(music_url);
	      cursor.moveToLast();
	      if (cursor.isFirst())
	      {
		    ContentValues values = new ContentValues();
		    Calendar rightNow = Calendar.getInstance();
		    SimpleDateFormat format = new SimpleDateFormat(
			          "yyyy-MM-dd HH:mm:ss");
		    values.put(KEY_LAST_PLAY,
			          format.format(rightNow.getTime()));
		    result = mDb.update(LYRIC_TABLE, values, KEY_MUSIC_URL
			          + "=\"" + music_url + "\"", null) > 0;
	      }
	      else
	      {
		    Toast.makeText(mCtx.getApplicationContext(),
			          "Can't find information of "
			                          + music_url + ".",
			          Toast.LENGTH_SHORT).show();
		    result = false;
	      }
	      cursor.close();
	      return result;
        }
}

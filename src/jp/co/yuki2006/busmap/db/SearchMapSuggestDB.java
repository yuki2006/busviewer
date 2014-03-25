package jp.co.yuki2006.busmap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchMapSuggestDB extends SQLiteOpenHelper {

    private static final String COLUMN_KEYWORD = "keyword";
    private static final String COLUMN_ID = "_id";
    private static final String SUGGEST_TABLE = "suggest_tbl";
    private static final int VERSION = 1;
    private static final String DB_NAME = "suggest.db";

    public SearchMapSuggestDB(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public void addKeyWord(String keyword) {
        if (keyword.equals("")) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEYWORD, keyword);
            db.delete(SUGGEST_TABLE, COLUMN_KEYWORD + " = ?", new String[]{keyword});
            db.insert(SUGGEST_TABLE, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<String> getAllData() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> strings = new ArrayList<String>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT " + COLUMN_KEYWORD + ",_id " +
                    " FROM " + SUGGEST_TABLE + " ORDER BY " + COLUMN_ID + " DESC;",
                    null);
            boolean isNext = c.moveToFirst();
            while (isNext) {
                int columnIndex = c.getColumnIndex(COLUMN_KEYWORD);
                int int1 = c.getInt(1);
                strings.add(c.getString(columnIndex));
                isNext = c.moveToNext();
            }
        } finally {
            c.close();
            db.close();
        }
        return strings;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE " + SUGGEST_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    COLUMN_KEYWORD + " varchar(255) UNIQUE) ");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void truncate() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SUGGEST_TABLE, null, null);
        db.close();
    }
}

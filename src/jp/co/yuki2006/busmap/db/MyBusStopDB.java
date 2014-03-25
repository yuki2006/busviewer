/**
 *
 */
package jp.co.yuki2006.busmap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

/**
 * @author yuki
 */
public class MyBusStopDB extends DataBaseInterface {
    /**
     * マイバス停のテーブル名です。
     */
    private static final String MYBUSSTOP_DB = "mybusstop";
    /**
     * データベースのID。
     */
    private static final String ID = "ID";

    private static final String BUSSTOP_ID = "busstopid";
    private static final String LOADING = "loading";
    private static final String LOADING_ALIAS = "loading_alias";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String REGION = "region";
    private static final String BUSSTOP_NAME = "busstopname";
    /**
     * マイバス停のデータベース作成SQLです。
     */
    private static final String BUSSTOP_CREATE_DB_COMMAND = "CREATE TABLE "
            + MYBUSSTOP_DB + "(" + ID + " integer primary key," + BUSSTOP_ID
            + " integer, " + BUSSTOP_NAME + " text not null, " + LOADING
            + " text , " + LOADING_ALIAS + " text ," + LATITUDE + " integer ,"
            + LONGITUDE + " integer, " + REGION + " text" + ")";
    /**
     * マイバス停取得のフィールドリスト。
     */
    private static final String[] GET_MYBUSSTOP_LIST = new String[]{
            BUSSTOP_NAME, BUSSTOP_ID, LOADING, LOADING_ALIAS, LATITUDE,
            LONGITUDE, REGION};

    public static void create(final SQLiteDatabase db) {
        db.execSQL(BUSSTOP_CREATE_DB_COMMAND);
    }

    public static void upgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
        if (i < VERSION && j == VERSION) {
            sqlitedatabase.execSQL("ALTER TABLE " + MYBUSSTOP_DB + " ADD " + REGION + " text ;");
        }
    }

    /**
     * @param context
     */
    public MyBusStopDB(Context context) {
        super(context);
    }

    public boolean checkAlreadyData(final BusStop data) {
        SQLiteDatabase db = getReadableDatabase();
        // すでにある項目かどうかチェック
        StringBuilder selection = new StringBuilder();
        selection.append(BUSSTOP_ID);
        selection.append("='");
        selection.append(data.getBusStopID());
        selection.append("'");
        selection.append(" And ");
        selection.append(LOADING);
        selection.append("='");
        selection.append(data.getLoading().getLoadingID());
        selection.append("'");
        Cursor c = db.query(MYBUSSTOP_DB, GET_MYBUSSTOP_LIST,
                selection.toString(), null, null, null, null);
        c.moveToFirst();
        boolean result = c.getCount() > 0;
        c.close();
        db.close();
        return result;
    }

    /**
     * データベースからマイバス停のデータを取得します。
     *
     * @return マイバス停のデータ
     */
    public List<BusStop> getBusStopData() {
        List<BusStop> result = new ArrayList<BusStop>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(MYBUSSTOP_DB, GET_MYBUSSTOP_LIST, null, null, null,
                null, ID + " ASC");
        if (c.moveToFirst()) {
            do {
                String loading = null;
                String loadingAlias = null;
                int latitudeE6 = 0;
                int longitudeE6 = 0;
                BusStop.Builder builder = new BusStop.Builder();
                for (int cursor = 0; cursor < c.getColumnCount(); cursor++) {

                    String tmpString = c.getColumnName(cursor);
                    if (tmpString.equals(BUSSTOP_NAME)) {
                        builder.setTitle(c.getString(cursor));
                    } else if (tmpString.equals(LOADING)) {
                        loading = c.getString(cursor);
                    } else if (tmpString.equals(LOADING_ALIAS)) {
                        loadingAlias = c.getString(cursor);
                    } else if (tmpString.equals(LATITUDE)) {
                        latitudeE6 = c.getInt(cursor);
                    } else if (tmpString.equals(LONGITUDE)) {
                        longitudeE6 = c.getInt(cursor);
                    } else if (tmpString.equals(BUSSTOP_ID)) {
                        builder.setBusStopID(c.getInt(cursor));
                    } else if (tmpString.equals(REGION)) {
                        builder.setRegion(c.getString(cursor));
                    }
                }

                builder.setPoint(new LatLng(latitudeE6/1E6, longitudeE6/1E6));
                builder.setLoadingZone(new LoadingZone(loading, loadingAlias));

                result.add(builder.create());

            } while (c.moveToNext());
        }
        c.close();
        db.close();

        // 万が一こういうことがあればチェック

        Iterator<BusStop> iterator = result.iterator();
        while (iterator.hasNext()) {
            BusStop busStop = (BusStop) iterator.next();
            if (busStop.isValid() == false) {
                removeBusStop(busStop);
                iterator.remove();
                Toast.makeText(context, R.string.found_invalid_data,
                        Toast.LENGTH_LONG).show();
            }
        }

        return result;

    }

    private ContentValues getContentValues(final BusStop data) {
        ContentValues values = new ContentValues();
        values.put(BUSSTOP_NAME, data.getBusStopName());
        values.put(BUSSTOP_ID, data.getBusStopID());
        values.put(REGION, data.getRegion());
        if (data.getLoading() != null) {
            values.put(LOADING, data.getLoading().getLoadingID());
            values.put(LOADING_ALIAS, data.getLoading().getAliasName());
            if (data.getPoint() != null) {
                values.put(LATITUDE, data.getPoint().latitude * 1E6);
                values.put(LONGITUDE, data.getPoint().longitude * 1E6);
            }
        } else {
            values.put(LOADING, "");
        }
        return values;
    }

    /**
     * マイバス停用のデータ追加メソッドです。
     *
     * @param data BusStopのデータを指定してください。
     * @return
     */
    public boolean insertData(final BusStop data) {
        boolean checkAlreadyData = checkAlreadyData(data);
        if (checkAlreadyData) {
            return false;
        }
        ContentValues values = getContentValues(data);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(MYBUSSTOP_DB, null, values);
        db.close();

        return true;
    }

    public void removeBusStop(BusStop busStop) {
        List<BusStop> busStopData = getBusStopData();
        busStopData.remove(busStop);
        // db.delete(MYBUSSTOP_DB, ID + "=" + (index + 1), null);
        // String sql = "update " + MYBUSSTOP_DB + " set " + ID + "=" + ID +
        // "-1 where " + ID + ">" + (index + 1);
        // db.rawQuery(sql, null);
        // db.close();
        truncateBusStop();
        for (int i = 0; i < busStopData.size(); i++) {
            insertData(busStopData.get(i));
        }
    }

    public void truncateBusStop() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MYBUSSTOP_DB, null, null);
        db.close();

    }
}

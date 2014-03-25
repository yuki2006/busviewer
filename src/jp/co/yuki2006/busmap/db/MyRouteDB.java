package jp.co.yuki2006.busmap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class MyRouteDB extends DataBaseInterface {

    enum KEY_INDEX {
        BUSSTOP_DB_ID, BUSSTOP_NAME, BUSSTOP_ALIAS, BUSSTOP_LOADING_ID, BUSSTOP_ID, BUSSTOP_REGION
    }

    /**
     * マイルートのテーブル名です。
     */
    private static final String MYROOT_DB = "myrooting";
    private static final String DEP_BUSSTOP_DB_ID = "dep_busstop_db_id";
    private static final String DEP_BUSSTOP_NAME = "dep_busstop_name";
    private static final String DEP_BUSSTOP_ALIAS = "dep_bus_alias";
    private static final String DEP_BUSSTOP_ID = "dep_busstop_id";
    private static final String ARR_BUSSTOP_DB_ID = "arr_busstop_db_id";
    private static final String ARR_BUSSTOP_NAME = "arr_busstop_name";
    private static final String ARR_BUSSTOP_ALIAS = "arr_bus_alias";
    private static final String ARR_BUSSTOP_ID = "arr_busstop_id";
    private static final String DEP_BUSSTOP_REGION = "dep_busstop_region";
    private static final String ARR_BUSSTOP_REGION = "arr_busstop_region";
    private static final String DATA_UNIQUE_WHERE_QUERY = DEP_BUSSTOP_ID
            + "=? And " + ARR_BUSSTOP_ID + "= ? And " + DEP_BUSSTOP_DB_ID
            + "=? And " + ARR_BUSSTOP_DB_ID + "= ?";
    private static final String ID = "_id";
    private static final String DEP_BUSSTOP_LOADING_ID = "dep_busstop_loading_id";

    private static final String ARR_BUSSTOP_LOADING_ID = "arr_busstop_loading_id";

    /**
     * マイルートのデータベース作成SQLです。
     */

    private static final String MYROOT_CREATE_DB_COMMAND = "CREATE TABLE "
            + MYROOT_DB + "(" + ID + " integer primary key ,"
            + DEP_BUSSTOP_DB_ID + " integer, " + DEP_BUSSTOP_NAME
            + " text not null, " + DEP_BUSSTOP_ALIAS + " text not null,"
            + DEP_BUSSTOP_ID + " integer, " + DEP_BUSSTOP_LOADING_ID
            + " text ," + ARR_BUSSTOP_DB_ID + " integer, " + ARR_BUSSTOP_NAME
            + " text not null, " + ARR_BUSSTOP_ALIAS + " text not null, "
            + ARR_BUSSTOP_ID + " integer, " + ARR_BUSSTOP_LOADING_ID + " text,"
            + DEP_BUSSTOP_REGION + " string, " + ARR_BUSSTOP_REGION + " string )";

    private static final String[] GET_MYROOT_LIST = new String[]{
            DEP_BUSSTOP_DB_ID, DEP_BUSSTOP_NAME, DEP_BUSSTOP_ALIAS,
            DEP_BUSSTOP_LOADING_ID, DEP_BUSSTOP_ID,
            DEP_BUSSTOP_REGION,
            ARR_BUSSTOP_DB_ID,
            ARR_BUSSTOP_NAME, ARR_BUSSTOP_ALIAS, ARR_BUSSTOP_LOADING_ID,
            ARR_BUSSTOP_ID, ARR_BUSSTOP_REGION};

    public static void create(final SQLiteDatabase db) {
        db.execSQL(MYROOT_CREATE_DB_COMMAND);
    }

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == VERSION) {
            db.execSQL(MYROOT_CREATE_DB_COMMAND);
        } else if (oldVersion < VERSION && newVersion == VERSION) {
            db.execSQL("ALTER TABLE " + MYROOT_DB + " ADD " + DEP_BUSSTOP_REGION + " string;");
            db.execSQL("ALTER TABLE " + MYROOT_DB + " ADD " + ARR_BUSSTOP_REGION + " string;");

        }
    }

    public MyRouteDB(Context context) {
        super(context);
    }

    private int getColumnIndex(Cursor c, KEY_INDEX key, boolean isDeparture) {

        return c.getColumnIndex(getKey(key, isDeparture));

    }

    private String getKey(KEY_INDEX key, boolean isDeparture) {
        String columnName = "";
        switch (key) {
            case BUSSTOP_ALIAS:
                columnName = isDeparture ? DEP_BUSSTOP_ALIAS : ARR_BUSSTOP_ALIAS;
                break;
            case BUSSTOP_DB_ID:
                columnName = isDeparture ? DEP_BUSSTOP_DB_ID : ARR_BUSSTOP_DB_ID;
                break;
            case BUSSTOP_LOADING_ID:
                columnName = isDeparture ? DEP_BUSSTOP_LOADING_ID
                        : ARR_BUSSTOP_LOADING_ID;
                break;
            case BUSSTOP_ID:
                columnName = isDeparture ? DEP_BUSSTOP_ID : ARR_BUSSTOP_ID;
                break;
            case BUSSTOP_NAME:
                columnName = isDeparture ? DEP_BUSSTOP_NAME : ARR_BUSSTOP_NAME;
                break;
            case BUSSTOP_REGION:
                columnName = isDeparture ? DEP_BUSSTOP_REGION : ARR_BUSSTOP_REGION;
                break;
            default:
                break;
        }

        return columnName;
    }

    public RouteData[] getMyRootData() {
        ArrayList<RouteData> resultList = new ArrayList<RouteData>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        c = db.query(MYROOT_DB, GET_MYROOT_LIST, null, null, null, null, ID
                + " ASC");
        if (c.moveToFirst()) {
            do {
                RouteData data = new RouteData();
                resultList.add(data);
                boolean bool = false;
                do {
                    BusStop.Builder builder = new BusStop.Builder();
                    final LoadingZone loadingZone = new LoadingZone(
                            c.getString(getColumnIndex(c,
                                    KEY_INDEX.BUSSTOP_LOADING_ID, bool)),
                            c.getString(getColumnIndex(c,
                                    KEY_INDEX.BUSSTOP_ALIAS, bool)),
                            c.getInt((getColumnIndex(c,
                                    KEY_INDEX.BUSSTOP_DB_ID, bool))));
                    builder.setRegion(c.getString(getColumnIndex(c, KEY_INDEX.BUSSTOP_REGION, bool)));
                    builder.setBusStopID(c.getInt(getColumnIndex(c,
                            KEY_INDEX.BUSSTOP_ID, bool)));
                    builder.setLoadingZone(loadingZone);
                    builder.setTitle(c.getString(getColumnIndex(c,
                            KEY_INDEX.BUSSTOP_NAME, bool)));
                    data.setBusStop(bool, builder.create());

                    bool = !bool;
                } while (bool);
            } while (c.moveToNext());

        }
        c.close();
        db.close();
        // 万が一こういうことがあればチェック
        Iterator<RouteData> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            RouteData data = (RouteData) iterator.next();
            if (data.isValid() == false) {
                remove(data);
                iterator.remove();
                Toast.makeText(context, R.string.found_invalid_data,
                        Toast.LENGTH_LONG).show();
            }
        }
        return resultList.toArray(new RouteData[0]);
    }

    private String[] getUniqeQuerySelection(RouteData rootingData) {
        String[] selectionArgs = {
                String.valueOf(rootingData.getBusStop(true).getBusStopID()),
                String.valueOf(rootingData.getBusStop(false).getBusStopID()),
                String.valueOf(rootingData.getBusStop(true).getLoading()
                        .getDbId()),
                String.valueOf(rootingData.getBusStop(false).getLoading()
                        .getDbId())};
        return selectionArgs;
    }


    public boolean insertData(RouteData data) {
        if (isDataExist(data)) {
            return false;
        }

        ContentValues values = new ContentValues();
        boolean bool = false;
        do {
            BusStop busData = data.getBusStop(bool);
            if (busData == null) {
                return false;
            }
            values.put(getKey(KEY_INDEX.BUSSTOP_DB_ID, bool), busData
                    .getLoading().getDbId());
            values.put(getKey(KEY_INDEX.BUSSTOP_NAME, bool),
                    busData.getBusStopName());
            values.put(getKey(KEY_INDEX.BUSSTOP_ALIAS, bool), busData
                    .getLoading().getAliasName());
            values.put(getKey(KEY_INDEX.BUSSTOP_ID, bool),
                    busData.getBusStopID());
            values.put(getKey(KEY_INDEX.BUSSTOP_LOADING_ID, bool), busData
                    .getLoading().getLoadingID());
            values.put(getKey(KEY_INDEX.BUSSTOP_REGION, bool), busData
                    .getRegion());
            bool = !bool;
        } while (bool);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(MYROOT_DB, null, values);
        db.close();
        return true;
    }

    public boolean isDataExist(RouteData routeData) {
        if (routeData.getBusStop(true) == null
                || routeData.getBusStop(false) == null) {
            return false;
        }
        String[] selectionArgs = getUniqeQuerySelection(routeData);
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DEP_BUSSTOP_DB_ID};
        Cursor c = db.query(MYROOT_DB, columns, DATA_UNIQUE_WHERE_QUERY,
                selectionArgs, null, null, null);
        c.moveToFirst();
        boolean result = c.getCount() > 0;
        c.close();
        db.close();
        return result;

    }

    public void remove(RouteData routeData) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selection = getUniqeQuerySelection(routeData);
        db.delete(MYROOT_DB, DATA_UNIQUE_WHERE_QUERY, selection);
        db.close();
    }

    public void truncateMyRoot() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MYROOT_DB, null, null);
        db.close();
    }

    /**
     * データがなければ挿入　あれば削除します。
     *
     * @param routeData
     * @return　挿入したらtrue,削除したらfalseが返ります。
     */
    public boolean insertOrDeleteData(RouteData routeData) {
        if (isDataExist(routeData)) {
            remove(routeData);
            return false;
        } else {
            insertData(routeData);
            return true;
        }
    }
}

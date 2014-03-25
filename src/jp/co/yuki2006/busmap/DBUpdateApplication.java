/**
 *
 */
package jp.co.yuki2006.busmap;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import jp.co.yuki2006.busmap.db.AdvancedSearchHistoryDB;
import jp.co.yuki2006.busmap.db.MyBusStopDB;
import jp.co.yuki2006.busmap.db.MyRouteDB;

/**
 * @author yuki
 */
public class DBUpdateApplication extends Application {
    /*
     * (非 Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // データベースアップデート用
        // これで出来るのかは不明
        {
            MyBusStopDB busStopDB = new MyBusStopDB(this);
            SQLiteDatabase database = busStopDB.getWritableDatabase();
            database.close();
        }
        {
            MyRouteDB busStopDB = new MyRouteDB(this);
            SQLiteDatabase database = busStopDB.getWritableDatabase();
            database.close();
        }
        {
            AdvancedSearchHistoryDB busStopDB = new AdvancedSearchHistoryDB(this);
            SQLiteDatabase database = busStopDB.getWritableDatabase();
            database.close();
        }
    }
}

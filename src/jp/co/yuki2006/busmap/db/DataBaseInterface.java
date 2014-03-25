package jp.co.yuki2006.busmap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * データベース接続用のインターフェイスです。
 *
 * @author ono
 */
class DataBaseInterface extends SQLiteOpenHelper {

    /**
     * データベースファイル名です。
     */
    private static final String MYBUSSTOP = "busmap.db";
    /**
     * データベースの現在のバージョンです.
     */
    protected static final int VERSION = 4;
    protected final Context context;

    /**
     * コンストラクタ。
     *
     * @param context アクティビティのコンテキストです。
     */
    public DataBaseInterface(final Context context) {

        super(context, MYBUSSTOP, null, VERSION);
        this.context = context;

    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // すべて一つのデータベースファイルで扱っているため
        // データベース更新で、すべての更新コードを呼ばないといけない。
        // 今後、別々に分離する対応する予定。。
        MyBusStopDB.create(db);
        // MyDirectionDB.create(db);
        MyRouteDB.create(db);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // すべて一つのデータベースファイルで扱っているため
        // データベース更新で、すべての更新コードを呼ばないといけない。
        // 今後、別々に分離する対応する予定。。
        MyBusStopDB.upgrade(db, oldVersion, newVersion);
        // MyDirectionDB.upgrade(db, oldVersion, newVersion);
        MyRouteDB.upgrade(db, oldVersion, newVersion);
    }
}

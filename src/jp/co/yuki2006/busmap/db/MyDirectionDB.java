/**
 *
 */
package jp.co.yuki2006.busmap.db;


/**
 * @author yuki
 *
 */
//@Deprecated
//public class MyDirectionDB extends DataBaseInterface {
//	/**
//	 * データベースのID。
//	 */
//	private static final String ID = "ID";
//	/**
//	 * マイ行き先のテーブル名です。
//	 */
//	private static final String MYDIRECTION_DB = "myroot";
//	private static final String BUSSTOP_ID = "busstopid";
//	private static final String BUSSTOP_NAME = "busstopname";
//	private static final String DESTINATION = "destination";
//	private static final String VIA_BUSSTOP = "via_busstop";
//	/**
//	 *
//	 */
//	private static final String LOADING_ZONE = "LoadingZone";
//
//	/**
//	 * マイ行き先のデータベース作成SQLです。
//	 */
//	private static final String DIRECTION_CREATE_DB_COMMAND = "CREATE TABLE " + MYDIRECTION_DB + "(" + ID
//			+ " integer primary key ," + BUSSTOP_ID + " text not null, " + BUSSTOP_NAME + " text not null, "
//			+ DESTINATION + " text not null," + VIA_BUSSTOP + " text , " + LOADING_ZONE + " text not null" + ")";
//	/**
//	 * マイ行き先取得のフィールドリスト。
//	 */
//	private static final String[] GET_MY_DIRECTION_LIST = new String[] {
//			BUSSTOP_NAME, BUSSTOP_ID, DESTINATION, VIA_BUSSTOP, LOADING_ZONE };
//
//	public static void create(final SQLiteDatabase db) {
//
//		db.execSQL(DIRECTION_CREATE_DB_COMMAND);
//	}
//
//	public static void upgrade(SQLiteDatabase sqlitedatabase, int oldVersion, int newVersion) {
//		if (oldVersion < 3 && newVersion == 3) {
//			sqlitedatabase.execSQL("ALTER TABLE " + MYDIRECTION_DB + " ADD COLUMN " + LOADING_ZONE
//					+ " text DEFAULT \"\";");
//			// sqlitedatabase.execSQL("update " + MYDIRECTION_DB + " set " +
//			// LOADING_ZONE + "=''");
//			// sqlitedatabase.execSQL("alter table " + MYDIRECTION_DB +
//			// " ALTER  COLUMN  " + LOADING_ZONE + " text not null;");
//		}
//	}
//
//	/**
//	 * @param context
//	 */
//	public MyDirectionDB(Context context) {
//		super(context);
//		// TODO 自動生成されたコンストラクター・スタブ
//	}
//
//	/**
//	 * マイ行き先に追加します。
//	 *
//	 * @param destinationStopName
//	 *            目的地のバス停名
//	 * @param viaStopName
//	 *            経由のバス停名です。
//	 * @param loadingZone
//	 *            乗り場IDです。
//	 */
//	public void addMyDirection(BusStop busStop, final String destinationStopName,
//			final String viaStopName, LoadingZone loadingZone) {
//		DirectionData data = new DirectionData();
//		data.busStop = busStop;
//		data.destination = destinationStopName;
//		data.via = viaStopName;
//		data.busStop.setLoading(loadingZone);
//		if (insertData(data)) {
//			Toast.makeText(context, "マイ行き先に追加しました。", Toast.LENGTH_SHORT).show();
//		} else {
//			Toast.makeText(context, data + "\nはすでに登録されているようです。", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * データベースから行き先のデータを取得します。
//	 *
//	 * @return 行き先のデータ
//	 */
//	public List<DirectionData> getMyDirectionList() {
//		List<DirectionData> data = new ArrayList<DirectionData>();
//		SQLiteDatabase db = getReadableDatabase();
//		Cursor c = db.query(MYDIRECTION_DB, GET_MY_DIRECTION_LIST, null, null, null, null, ID + " ASC");
//		if (c.moveToFirst()) {
//			do {
//
//				DirectionData rootdata = new DirectionData();
//				rootdata.busStop = new BusStop(null, c.getString(c.getColumnIndex(BUSSTOP_NAME)));
//				rootdata.busStop.setBusStopID(c.getInt(c.getColumnIndex(BUSSTOP_ID)));
//				rootdata.destination = c.getString(c.getColumnIndex(DESTINATION));
//				rootdata.via = c.getString(c.getColumnIndex(VIA_BUSSTOP));
//
//				String loadingID = c.getString(c.getColumnIndex(LOADING_ZONE));
//				rootdata.busStop.setLoading(new LoadingZone(loadingID, ""));
//				data.add(rootdata);
//
//			} while (c.moveToNext());
//		}
//		c.close();
//		db.close();
//
//		return data;
//
//	}
//
//	/**
//	 * マイ行き先用のデータ追加メソッドです。
//	 *
//	 * @param data
//	 *            BusStopのデータを指定してください。
//	 * @return すでにある項目ならfalseを返します。正常に追加されたらtrueが表示されます。
//	 */
//	public boolean insertData(final DirectionData data) {
//		ContentValues values = new ContentValues();
//		values.put(BUSSTOP_NAME, data.busStop.getBusStopName());
//		values.put(DESTINATION, data.destination);
//		values.put(BUSSTOP_ID, data.busStop.getBusStopID());
//		values.put(LOADING_ZONE, data.getLoadingZone().getLoadingID());
//		if (data.via != null) {
//			values.put(VIA_BUSSTOP, data.via);
//		} else {
//			values.put(VIA_BUSSTOP, "");
//		}
//
//		// すでにある項目かどうかチェック
//		SQLiteDatabase db = getReadableDatabase();
//		StringBuilder selection = new StringBuilder();
//		for (Entry<String, Object> k : values.valueSet()) {
//			if (selection.length() > 0) {
//				selection.append(" And ");
//			}
//			selection.append(k.getKey()).append("=").append("'").append(k.getValue()).append("'");
//		}
//
//		Cursor c = db.query(MYDIRECTION_DB, GET_MY_DIRECTION_LIST, selection.toString(), null, null, null, null);
//		if (0 < c.getCount()) {
//			return false;
//		}
//		c.close();
//
//		db = getWritableDatabase();
//		db.insert(MYDIRECTION_DB, null, values);
//		db.close();
//		return true;
//	}
//
//	/**
//	 * マイ行き先のデータを空にします。
//	 */
//	public void truncateMyDirection() {
//		SQLiteDatabase db = getWritableDatabase();
//		db.delete(MYDIRECTION_DB, null, null);
//		db.close();
//
//	}

//}

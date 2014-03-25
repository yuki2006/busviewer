/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.app.Activity;
import android.content.Context;

import java.io.InputStream;

import jp.co.yuki2006.busmap.parser.NewBusListXMLParser;
import jp.co.yuki2006.busmap.route.store.NewAdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * 詳細検索の条件にあったバスを検索します。
 * public 付けなくてもいい方法があったら改良(widgetパッケージでしよう）
 *
 * @author yuki
 */
public class NewSearchLoader extends WebPortal<RouteData[], Integer, NewAdvancedSearchResult[]> {

	/**
     *
     */
	public static final int LIMIT = 15;
	private boolean singleResult = false;
	private boolean byWidget = false;
	private int count;
	private RouteData[] datas;

	public NewSearchLoader(Activity activity, boolean showDialog,
			IWebPostRunnable<NewAdvancedSearchResult[]> postRunnable) {
		super(activity, showDialog, postRunnable, true);
	}

	public NewSearchLoader(Context context, IWebPostRunnable<NewAdvancedSearchResult[]> postRunnable) {
		super(context, postRunnable);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
	 */
	@Override
	protected NewAdvancedSearchResult[] onBackGroundCore(InputStream is) {
		return NewBusListXMLParser.getBusListDatas(is, this.context, count, datas);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
	 */
	@Override
	protected String onParamParser(RouteData[] datas) {
		this.datas = datas;
		StringBuilder urlPath = new StringBuilder("route/getTransferSearch.php?");
		boolean bool = true;
		if (byWidget) {
			urlPath.append("byWidget=1");
		}

		RouteData data = null;
		count = datas.length;
		if (count == 0) {
			return "";
		}
		for (int i = 0; i < datas.length; i++) {
			data = datas[i];
			do {
				final String id = "" + i;
				if (bool) {
					urlPath.append("&dpBusStopID" + id + "=");
				} else {
					urlPath.append("&arBusStopID" + id + "=");
				}
				urlPath.append(data.getBusStop(bool).getBusStopID());

				bool = !bool;
			} while (!bool);
		}
		if (data.isFromDeparture()) {
			urlPath.append("&departuretime=");
		} else {
			urlPath.append("&arrivetime=");
		}
		long currenttime = data.getCurrentMiliTime();
		urlPath.append(currenttime / 1000);
		if (byWidget) {
			urlPath.append("&limit=10");
		} else if (singleResult) {
			urlPath.append("&limit=1");
		} else {
			// 通常は１５件に設定
			urlPath.append("&limit=" + LIMIT);
		}
		if (data.isFirstOrLast()) {
			urlPath.append("&isFirstOrLast=1");
		} else {
			urlPath.append("&isFirstOrLast=0");
		}
		urlPath.append("&transferTime="+data.getTransferTime());
		return urlPath.toString();
	}

	public void setByWidget(boolean byWidget) {
		this.byWidget = byWidget;
		singleResult = true;
	}

	/**
	 * 結果が1件返ってくるだけでいいようなクエリーを送信します。
	 *
	 * @param b
	 */
	public void setSingleResult(boolean b) {
		singleResult = b;
	}

}
/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.app.Activity;
import android.content.Context;

import java.io.InputStream;

import jp.co.yuki2006.busmap.parser.BusListXMLParser;
import jp.co.yuki2006.busmap.route.store.AdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * 詳細検索の条件にあったバスを検索します。
 * public 付けなくてもいい方法があったら改良(widgetパッケージでしよう）
 *
 * @author yuki
 */
public class SearchLoader extends WebPortal<RouteData[], Integer, AdvancedSearchResult[]> {

    /**
     *
     */
    public static final int LIMIT = 15;
    private boolean singleResult = false;
    private boolean byWidget = false;
    private int count;
    private RouteData[] datas;

    public SearchLoader(Activity activity, boolean showDialog, IWebPostRunnable<AdvancedSearchResult[]> postRunnable) {
        super(activity, showDialog, postRunnable, true);
    }

    public SearchLoader(Context context, IWebPostRunnable<AdvancedSearchResult[]> postRunnable) {
        super(context, postRunnable);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected AdvancedSearchResult[] onBackGroundCore(InputStream is) {
        return BusListXMLParser.getBusListDatas(is, this.context, count, datas);
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(RouteData[] datas) {
        this.datas = datas;
        StringBuilder urlPath = new StringBuilder("route/searchBusList.php?");
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
                    urlPath.append("&departureID" + id + "=");
                } else {
                    urlPath.append("&arriveID" + id + "=");
                }
                urlPath.append(String.format("%06d", data.getBusStop(bool).getLoading().getDbId()));
                if (data.getBusStop(bool).getLoading().getDbId() == -1) {
                    // すべて検索のとき
                    if (bool) {
                        urlPath.append("&depBusStopID" + id + "=");
                    } else {
                        urlPath.append("&arrBusStopID" + id + "=");
                    }

                    urlPath.append(data.getBusStop(bool).getBusStopID());
                    // urlPath.append(0);
                }

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
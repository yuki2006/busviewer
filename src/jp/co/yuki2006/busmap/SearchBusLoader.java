/**
 *
 */
package jp.co.yuki2006.busmap;

import android.app.Activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.parser.BusStopListXMLParser;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

public class SearchBusLoader extends WebPortal<String, Integer, ResultSearchBusList> {

    public SearchBusLoader(Activity activity, boolean showDialog, IWebPostRunnable<ResultSearchBusList> postRunnable) {
        super(activity, showDialog, postRunnable, true);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected ResultSearchBusList onBackGroundCore(InputStream is) {
        return BusStopListXMLParser.setBusListView(is);
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(String param) {
        try {
            return "search_busstop.php?search_name=" +
                    URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
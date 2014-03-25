package jp.co.yuki2006.busmap.bustimeline;

import android.app.Activity;

import java.io.InputStream;

import jp.co.yuki2006.busmap.direction.DirectionExpandAdapater;
import jp.co.yuki2006.busmap.parser.BusTimelineXMLParser;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

public class TimeLineReLoader extends WebPortal<Integer, Integer, Boolean> {

    private final DirectionExpandAdapater timeLineExpandAdapter;

    public TimeLineReLoader(Activity activity, IWebPostRunnable<Boolean> postRunnable, DirectionExpandAdapater timeLineExpandAdapter) {
        super(activity, postRunnable);
        this.timeLineExpandAdapter = timeLineExpandAdapter;
    }


    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected Boolean onBackGroundCore(InputStream is) {
        BusTimelineXMLParser.resetTimelineView(timeLineExpandAdapter, is);
        return true;
    }

    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(Integer currentbusstopID) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("get_station_timelineByID.php?BusStopID=");
        urlString.append(currentbusstopID);
        urlString.append("&prevtime=0");
        return urlString.toString();
    }

}

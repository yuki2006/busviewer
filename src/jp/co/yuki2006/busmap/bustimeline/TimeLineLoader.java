package jp.co.yuki2006.busmap.bustimeline;

import java.io.InputStream;

import jp.co.yuki2006.busmap.bustimeline.BusTimeTableTimerBasisActivity.DateType;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity.TimeLineActivityData;
import jp.co.yuki2006.busmap.parser.BusTimelineXMLParser;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

public class TimeLineLoader extends WebPortal<TimeLineActivityData, Integer, BusTimeTableDataResult> {

    private final int busStopID;

    public TimeLineLoader(TimeLineActivity activity,
                          IWebPostRunnable<BusTimeTableDataResult> postRunnable,
                          int busStopID) {
        super(activity, true, postRunnable, true);
        this.busStopID = busStopID;

    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected BusTimeTableDataResult onBackGroundCore(InputStream is) {
        BusTimeTableDataResult timeLineData = new BusTimeTableDataResult(activity);
        BusTimelineXMLParser.setTimelineView(is, activity, busStopID, timeLineData);
        return timeLineData;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(TimeLineActivityData timeLineActivityData) {
        StringBuilder urlString = new StringBuilder("get_station_timelineByID.php?BusStopID=");
        urlString.append(busStopID);
        if (timeLineActivityData.checkDate.day == null) {

            urlString.append("&time=" + timeLineActivityData.checkTime);

        } else if (timeLineActivityData.checkDate.day == DateType.TODAY) {

            urlString.append("&prevtime=0");

        } else if (timeLineActivityData.checkDate.day == DateType.WEEKDAY) {

            urlString.append("&day=1");

        } else if (timeLineActivityData.checkDate.day == DateType.SATURDAY) {

            urlString.append("&day=6");

        } else if (timeLineActivityData.checkDate.day == DateType.HOLIDAY) {

            urlString.append("&day=0");

        }

        return urlString.toString();
    }
}

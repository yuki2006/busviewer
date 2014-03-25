package jp.co.yuki2006.busmap.parser;

import android.content.Context;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.yuki2006.busmap.bustimeline.BusTimeTableDataResult;
import jp.co.yuki2006.busmap.bustimeline.BusTimeTableListAdapter;
import jp.co.yuki2006.busmap.bustimeline.BusTimeTableTimerBasisActivity.DateType;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity.CheckDate;
import jp.co.yuki2006.busmap.direction.DirectionExpandAdapater;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.DirectionData;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.store.TimeTableElement;
import jp.co.yuki2006.busmap.values.CharctorValues;

public class BusTimelineXMLParser {
    public static final String DESTINATION_TEXT = "d_t";
    public static final String NEXT_TEXT_STRING = "next_text";
    public static final String LOADING_STRING = "ls";
    public static final String PREV_TEXT_STRING = "prev_text";
    public static final String LINENUMBER = "ln";
    public static final String LOADING_ZONE = "lz";
    public static final String NEXT_BUS_TIME = "n";
    public static final String NEXT_NEXT_BUS_TIME = "nn";
    public static final String VIA_STATION_NAME = "v";
    public static final String DESTINATION = "d";
    public static final String TIME_LABEL = "L";
    public static final String TIME_DETAIL = "D";
    public static final String PREV_BUS_TIME = "P";
    public static final String NEXT_NEXT_BUS_REMARK = "nr";
    public static final String NEXT_BUS_REMARK = "nb";
    public static final String LOADING_ZONE_ALIAS = "lza";

    protected static CheckDate getCheckTimeParser(
            final XmlPullParser xmlPullParser) {
        CheckDate checkDate = new CheckDate();
        checkDate.month = Integer.valueOf(xmlPullParser.getAttributeValue("",
                "month"));
        checkDate.date = Integer.valueOf(xmlPullParser.getAttributeValue("",
                "date"));
        int day = Integer.valueOf(xmlPullParser.getAttributeValue("",
                "day"));
        switch (day) {
            case 0:
                checkDate.day = DateType.HOLIDAY;
                break;
            case 6:
                checkDate.day = DateType.SATURDAY;
                break;
            default:
                checkDate.day = DateType.WEEKDAY;

                break;
        }

        return checkDate;
    }

    /**
     * Director (行き先別時刻表）のXMLを解析します。 ウィジェット用の機能です。
     * MapからMyDirectionDataへの移行途中のコードです。
     *
     * @param xmlPullParser 解析するxmlPullParserオブジェクト
     * @throws IOException            なんかの例外らしい・・
     * @throws XmlPullParserException XMLの解析例外らしい
     */
    public static DirectionData parserDirectorElements(
            final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        DirectionData.Builder builder = new DirectionData.Builder();
        // // 先に要素を挿入しておく
        // List<Map<String, String>> children = new ArrayList<Map<String,
        // String>>();
        // childData.add(children);

        // 空タグだった場合
        if (xmlPullParser.isEmptyElementTag()) {
            builder.setEmptyBus(true);
            return builder.create();
        }
        String dst = xmlPullParser.getAttributeValue("", "dst");
        String via = xmlPullParser.getAttributeValue("", "via");

        builder.setDestination(dst);
        builder.setVia(via);

        String loadingZone = xmlPullParser.getAttributeValue("", "LoadingZone");
        String loadingZoneAlias = xmlPullParser.getAttributeValue("",
                "LoadingZoneAlias");
        String linenumber = xmlPullParser.getAttributeValue("", "LineNumber");
        builder.setLineNumber(linenumber);
        builder.setLoadingZone(new LoadingZone(loadingZone, loadingZoneAlias));

        String attributeNextBus = xmlPullParser.getAttributeValue("",
                "next_bus_time");
        String attributeNextBusRemark = xmlPullParser.getAttributeValue("",
                "next_bus_remark");
        String attributeNextNextBus = xmlPullParser.getAttributeValue("",
                "next_next_bus_time");
        String attributeNextNextBusRemark = xmlPullParser.getAttributeValue("",
                "next_next_bus_remark");
        String attributePrevBus = xmlPullParser.getAttributeValue("",
                "prev_bus_time");
        DirectionData directionData = builder.create();
        if (attributeNextBus != null) {

            directionData.getBusNextTime().addTime(attributeNextBus,
                    attributeNextBusRemark);
            if (attributeNextNextBus != null) {
                directionData.getBusNextTime().addTime(attributeNextNextBus,
                        attributeNextNextBusRemark);
            }
        }
        if (attributePrevBus != null) {
            directionData.getBusPrevTime().addTime(attributePrevBus, "");
        }

        int e = xmlPullParser.next();
        StringBuilder addChildData = new StringBuilder();

        for (e = xmlPullParser.getEventType(); ; e = xmlPullParser.next()) {
            if (e == XmlPullParser.START_TAG) {
                if (xmlPullParser.getName().equals("timetable")) {

                    String time = xmlPullParser.getAttributeValue("", "time");
                    e = xmlPullParser.next();
                    if (addChildData.length() > 0) {
                        addChildData.append("\n");
                    }
                    addChildData.append(time + "  :  "
                            + xmlPullParser.getText());

                    // Map<String, String> object = new HashMap<String,
                    // String>();
                    // object.put(TIME_LABEL, time + ":");
                    // object.put(TIME_DETAIL, xmlPullParser.getText());
                    TimeTableElement object = new TimeTableElement();
                    object.hour = Integer.valueOf(time);
                    object.data = xmlPullParser.getText();
                    directionData.getTimeTableElementList().add(object);
                    // children.add(object);
                }
            } else if (e == XmlPullParser.END_TAG) {
                if (xmlPullParser.getName().equals("director")) {
                    break;
                }
            }

        }
        return directionData;

        // setNextTimeText(groupElementData);
    }

    public static void resetTimelineView(DirectionExpandAdapater adapter,
                                         InputStream is) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, CharctorValues.UTF_8);
            xmlPullParser.next();
            int count = 0;

            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                    .next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("director")) {
                            DirectionData parentData = parserDirectorElements(xmlPullParser);
                            adapter.setRawGroup(count, parentData);
                            count++;
                        }
                        break;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    public static void setTimelineView(InputStream is, Context context,
                                       int busStopID, BusTimeTableDataResult timeLineDataResult) {
        List<DirectionData> directionDatas = new ArrayList<DirectionData>();

        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, CharctorValues.UTF_8);
            xmlPullParser.next();
            BusStop.Builder builder = new BusStop.Builder();
            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                    .next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("busstop")) {
                            int versioncode = Integer.parseInt(xmlPullParser
                                    .getAttributeValue("", "version"));
                            // Etc.checkVersion(versioncode);
                            builder.setTitle(xmlPullParser
                                    .getAttributeValue("", "busname"));
                            builder.setRegion(xmlPullParser
                                    .getAttributeValue("", "Region"));
                        } else if (tagName.equals("checktime")) {

                            timeLineDataResult.checkDate = getCheckTimeParser(xmlPullParser);
                        } else if (tagName.equals("zone")) {
                            String loadingID = xmlPullParser.getAttributeValue("",
                                    "id");
                            String alias = xmlPullParser.getAttributeValue("",
                                    "alias");
                            String lat = xmlPullParser.getAttributeValue("", "lat");
                            String lng = xmlPullParser.getAttributeValue("", "lng");
                            LatLng geoPoint = null;
                            if (!lat.equals("0")) {
                                geoPoint = new LatLng(Integer.valueOf(lat)/1E6,
                                        Integer.valueOf(lng)/1E6);
                            }
                            builder.setLoadingZone(new LoadingZone(loadingID, alias));
                            builder.setPoint(geoPoint);
                            builder.setBusStopID(busStopID);

                            timeLineDataResult.loadingListAdapter.add(builder.create());
                        } else if (tagName.equals("director")) {
                            directionDatas.add(parserDirectorElements(xmlPullParser));
                        } else if (tagName.equals("remark")) {
                            // 備考の読み込み
                            String text = "";
                            // <remark />ではないとき　（現在この場合はない）
                            if (xmlPullParser.isEmptyElementTag() == false) {
                                xmlPullParser.next();
                                // <remark></remark>
                                if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
                                    text = "";
                                } else {
                                    text = xmlPullParser.getText();
                                }
                            }

                            timeLineDataResult.remarks = text.split("   ");
                        }
                        break;
                    }
                    default:
                        break;

                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        // for (Map<String, String> nextdata : groupData) {
        // setNextTimeText(nextdata);
        // }

        BusTimeTableListAdapter adapter = new BusTimeTableListAdapter(context,
                directionDatas);

        timeLineDataResult.timelineExpandAdapter = adapter;
    }
}

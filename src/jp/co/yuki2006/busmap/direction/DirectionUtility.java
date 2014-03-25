package jp.co.yuki2006.busmap.direction;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.yuki2006.busmap.bustimeline.BusTimeTableTimerBasisActivity.DateType;
import jp.co.yuki2006.busmap.parser.BusTimelineXMLParser;
import jp.co.yuki2006.busmap.parser.MyDirectorXMLParser;
import jp.co.yuki2006.busmap.store.DirectionData;
import jp.co.yuki2006.busmap.store.TimeTableElement;

public class DirectionUtility {
    public static ArrayList<Map<String, String>> getDirectionMapList(List<DirectionData> myDirectionList) {
        ArrayList<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        for (DirectionData data : myDirectionList) {
            Map<String, String> root = new HashMap<String, String>();
//			root.put(MyDirectorXMLParser.BUSSTOPNAME, data.busStop.getBusStopName());
//			root.put(MyDirectorXMLParser.BUSSTOPID, String.valueOf(data.busStop.getBusStopID()));
            root.put(MyDirectorXMLParser.DESTINATION, data.destination);
            root.put(MyDirectorXMLParser.VIA_STATION_NAME, data.via);
            root.put(MyDirectorXMLParser.LOADING_ZONE, data.getLoadingZone().toString());
            StringBuilder string = new StringBuilder();
            string.append(data.toDestinationString());
            root.put(MyDirectorXMLParser.DESTINATION_TEXT, string.toString());
            groupData.add(root);
        }
        return groupData;
    }

    @Deprecated
    public static String getDirectionURL(Context context, DateType currentDate,
                                         DirectionData[] myDirectionDataArray, boolean isByWidget) {
        StringBuilder urlString = new StringBuilder("get_station_timelineByIDSub.php?");
        if (isByWidget) {
            urlString.append("byWidget=1");
        }

        switch (currentDate) {
            case WEEKDAY:
                urlString.append("&day=1");

                break;
            case SATURDAY:
                urlString.append("&day=6");
                break;
            case HOLIDAY:
                urlString.append("&day=0");
                break;

            default:
                urlString.append("&prevtime=0");
                break;
        }
        int i = 0;
        for (DirectionData directionData : myDirectionDataArray) {
            HashMap<String, String> keyToParam = new HashMap<String, String>();
//			keyToParam.put("getId", "" + directionData.busStop.getBusStopID());
            keyToParam.put("dest", directionData.destination);
            keyToParam.put("via", directionData.via);
            keyToParam.put("loadingZone", directionData.getLoadingZone().getLoadingID());

            for (Entry<String, String> data : keyToParam.entrySet()) {
                urlString.append("&");
                urlString.append(data.getKey()).append(i).append("=").append(data.getValue());
            }
            i++;
        }
        return urlString.toString();
    }

    public static String getDirectionURL(Context context, DateType currentDate,
                                         List<Map<String, String>> groupData, boolean isByWidget) {

        StringBuilder urlString = new StringBuilder("get_station_timelineByIDSub.php?");
        if (isByWidget) {
            urlString.append("byWidget=1");
        }

        switch (currentDate) {
            case WEEKDAY:
                urlString.append("&day=1");

                break;
            case SATURDAY:
                urlString.append("&day=6");
                break;
            case HOLIDAY:
                urlString.append("&day=0");
                break;

            default:
                urlString.append("&prevtime=0");
                break;
        }
        HashMap<String, String> keyToParam = new HashMap<String, String>();
        keyToParam.put(MyDirectorXMLParser.BUSSTOPID, "getId");
        keyToParam.put(MyDirectorXMLParser.DESTINATION, "dest");
        keyToParam.put(MyDirectorXMLParser.VIA_STATION_NAME, "via");
        keyToParam.put(MyDirectorXMLParser.LOADING_ZONE, "loadingZone");
        for (int i = 0; i < groupData.size(); i++) {
            for (Entry<String, String> data : groupData.get(i).entrySet()) {
                String paramValue = keyToParam.get(data.getKey());
                if (paramValue != null) {
                    urlString.append("&");
                    urlString.append(paramValue).append(i).append("=").append(data.getValue());
                }
            }
        }
        return urlString.toString();
    }

    public static void inflateDirectionMapList(List<DirectionData> myDirectionList,
                                               List<Map<String, String>> groupData,
                                               List<List<Map<String, String>>> childData) {

        for (DirectionData data : myDirectionList) {
            Map<String, String> root = new HashMap<String, String>();
//			root.put(MyDirectorXMLParser.BUSSTOPNAME, data.busStop.getBusStopName());
//			root.put(MyDirectorXMLParser.BUSSTOPID, String.valueOf(data.busStop.getBusStopID()));
            root.put(MyDirectorXMLParser.DESTINATION, data.destination);
            root.put(MyDirectorXMLParser.VIA_STATION_NAME, data.via);
            root.put(MyDirectorXMLParser.LOADING_ZONE, data.getLoadingZone().toString());

            root.put(MyDirectorXMLParser.PREV_TEXT_STRING, data.getBusPrevTime().toString());
            root.put(MyDirectorXMLParser.NEXT_TEXT_STRING, data.getBusNextTime().toString());
            StringBuilder string = new StringBuilder();
            string.append(data.toDestinationString());
            root.put(MyDirectorXMLParser.DESTINATION_TEXT, string.toString());
            groupData.add(root);

            List<Map<String, String>> child = new ArrayList<Map<String, String>>();
            for (TimeTableElement timeTableElement : data.getTimeTableElementList()) {
                Map<String, String> object = new HashMap<String, String>();
                object.put(BusTimelineXMLParser.TIME_LABEL, timeTableElement.hour + " : ");
                object.put(BusTimelineXMLParser.TIME_DETAIL, timeTableElement.data);
                child.add(object);
            }
            childData.add(child);
        }
    }

    private DirectionUtility() {

    }
}

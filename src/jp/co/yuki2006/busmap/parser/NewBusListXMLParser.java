package jp.co.yuki2006.busmap.parser;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import jp.co.yuki2006.busmap.route.store.AdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.BusSearch;
import jp.co.yuki2006.busmap.route.store.BusTransferSearch;
import jp.co.yuki2006.busmap.route.store.NewAdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.NewBusSearch;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class NewBusListXMLParser {
    public static AdvancedSearchResult getBusList(InputStream is, Context context) {
        AdvancedSearchResult.Builder advancedSearchbuilder = new AdvancedSearchResult.Builder(context);
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();
            BusSearch searchElement = null;
            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser.next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();

                        if (tagName.equals("root")) {

                            // バーションチェック
                            int versioncode = Integer.parseInt(xmlPullParser.getAttributeValue("", "version"));
                            // Etc.checkVersion(versioncode);
                        } else if (tagName.equals("fare")) {
                            xmlPullParser.next();
                            advancedSearchbuilder.fare = Integer.valueOf(xmlPullParser.getText());
                        } else if (tagName.equals("bus")) {
                            searchElement = new BusSearch(
                                    xmlPullParser.getAttributeValue("", "departureTime"),
                                    xmlPullParser.getAttributeValue("", "arriveTime"),
                                    xmlPullParser.getAttributeValue("", "comment"),
                                    xmlPullParser.getAttributeValue("", "LastBusStopName")
                            );
                            searchElement.busID = Integer.valueOf(xmlPullParser.getAttributeValue("", "busID"));
                            searchElement.isNonStep = Boolean.parseBoolean(xmlPullParser.getAttributeValue("", "NonStep"));
                            try {
                                searchElement.lineNumber = Integer.valueOf(xmlPullParser
                                        .getAttributeValue("", "LineNumber"));

                            } catch (NumberFormatException e2) {
                                searchElement.lineNumber = null;
                            }

                            advancedSearchbuilder.busSearchs.add(searchElement);

                            e = xmlPullParser.next();
                            for (e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                                    .next()) {
                                if (e == XmlPullParser.START_TAG && xmlPullParser.getName().equals("BusStopData")) {
                                    BusStop.Builder builder = new BusStop.Builder();

                                    Integer busStopID = Integer.valueOf(xmlPullParser
                                            .getAttributeValue("", "BusStopID"));
                                    builder.setBusStopID(busStopID);
                                    builder.setLoadingZone(new LoadingZone(xmlPullParser.getAttributeValue("",
                                            "LoadingZone")
                                            , xmlPullParser.getAttributeValue("", "LoadingAlias")));
                                    if (xmlPullParser.getAttributeValue("", "class").equals("departure")) {
                                        searchElement.setDepartureBusStop(builder.create());
                                    } else {
                                        searchElement.setArriveBusStop(builder.create());
                                    }
                                } else if (e == XmlPullParser.END_TAG && xmlPullParser.getName().equals("bus")) {
                                    break;
                                }
                            }

                        }
                    }

                }
            }
            return advancedSearchbuilder.build();
        } catch (XmlPullParserException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NewAdvancedSearchResult[] getBusListDatas(InputStream is, Context context, int count,
                                                            RouteData[] datas) {
        NewAdvancedSearchResult[] advancedSearchResults = new NewAdvancedSearchResult[count];
        NewAdvancedSearchResult.Builder advancedSearchBuilder = null;
        BusTransferSearch busTransferSearch = null;
        int point = 0;
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();

            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser.next()) {

                switch (e) {
                    case XmlPullParser.END_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("result")) {
                            advancedSearchResults[point] = advancedSearchBuilder.build();
                            point++;
                        } else if (tagName.equals("BusList")) {
                            advancedSearchBuilder.busSearchs.add(busTransferSearch);
                        }
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();

                        if (tagName.equals("root")) {

                            // バーションチェック
                            // int versioncode =
                            // Integer.parseInt(xmlPullParser.getAttributeValue("",
                            // "version"));
                            // Etc.checkVersion(versioncode);
                        } else if (tagName.equals("result")) {
                            advancedSearchBuilder = new NewAdvancedSearchResult.Builder(context);
                        } else if (tagName.equals("fare")) {
                            xmlPullParser.next();
                            advancedSearchBuilder.fare = Integer.valueOf(xmlPullParser.getText());
                        } else if (tagName.equals("BusList")) {
                            busTransferSearch = new BusTransferSearch();
                        } else if (tagName.equals("Bus")) {
                            NewBusSearch searchElement = new NewBusSearch(
                                    xmlPullParser.getAttributeValue("", "Remark"),
                                    xmlPullParser.getAttributeValue("", "LastBusStopName")
                            );
                            busTransferSearch.add(searchElement);
                            searchElement.busID = Integer.valueOf(xmlPullParser.getAttributeValue("", "BusID"));
                            // searchElement.isNonStep =
                            // Boolean.parseBoolean(xmlPullParser.getAttributeValue("",
                            // "NonStep"));
                            searchElement.setLineNumber(xmlPullParser
                                    .getAttributeValue("", "LineNumber"));
                            // busTransferSearch.add(searchElement);

                            e = xmlPullParser.next();
                            for (e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                                    .next()) {
                                if (e == XmlPullParser.START_TAG && xmlPullParser.getName().equals("BusStopData")) {
                                    boolean isDeparture = xmlPullParser.getAttributeValue("", "class").equals("dp");
                                    BusStop.Builder builder = new BusStop.Builder();
                                    Integer busStopID = Integer.valueOf(xmlPullParser
                                            .getAttributeValue("", "BusStopID"));
                                    builder.setBusStopID(busStopID);
                                    builder.setTitle(xmlPullParser.getAttributeValue("", "BusStopName"));
                                    builder.setLoadingZone(new LoadingZone(xmlPullParser.getAttributeValue("",
                                            "LoadingZone")
                                            , xmlPullParser.getAttributeValue("", "LoadingZoneAlias")));
                                    BusStop busStop = builder.create();
                                    String time = xmlPullParser.getAttributeValue("", "Time");
                                    searchElement.setBusStop(busStop, time, isDeparture);
                                } else if (e == XmlPullParser.END_TAG && xmlPullParser.getName().equals("Bus")) {
                                    break;
                                }
                            }

                        }
                    }
                }
            }
            for (NewAdvancedSearchResult tmp : advancedSearchResults) {
                tmp.busRouteElements.refleshMode();
            }

            return advancedSearchResults;
        } catch (XmlPullParserException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

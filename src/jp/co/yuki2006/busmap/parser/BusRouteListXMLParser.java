package jp.co.yuki2006.busmap.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.route.store.BusRouteElement;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class BusRouteListXMLParser extends BusStopBaseXMLParser {

    public static ArrayList<BusRouteElement> getBusDetailList(InputStream is) {
        ArrayList<BusRouteElement> busRouteElements = new ArrayList<BusRouteElement>();
        BusRouteElement searchElement = null;
        BusStop.Builder builder = null;
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();

            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                    .next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("root")) {

                            // バーションチェック
                            int versioncode = Integer.parseInt(xmlPullParser
                                    .getAttributeValue("", "version"));
                            // Etc.checkVersion(versioncode);
                        } else if (tagName.equals("item")) {
                            builder = new BusStop.Builder();
                            searchElement = new BusRouteElement();
                            busRouteElements.add(searchElement);

                            searchElement.arriveTime = xmlPullParser
                                    .getAttributeValue("", "time");
                            String busID = xmlPullParser.getAttributeValue("",
                                    "BusStopID");

                            String tmpLoadingZone = xmlPullParser
                                    .getAttributeValue("", "loading_zone");
                            String tmpLoadingZoneAlias = xmlPullParser
                                    .getAttributeValue("", "LoadingZoneAlias");
                            int busstopID = 0;
                            if (busID != null) {
                                busstopID = Integer.parseInt(busID);
                            } else {
                                busstopID = 0;
                            }
                            builder.setRegion(xmlPullParser
                                    .getAttributeValue("", "Region"));
                            builder.setBusStopID(busstopID);
                            LoadingZone loadingZone = new LoadingZone(
                                    tmpLoadingZone, tmpLoadingZoneAlias);

                            if (tmpLoadingZoneAlias == null) {
                                tmpLoadingZoneAlias = "";
                            }
                            builder.setLoadingZone(loadingZone);
                        } else if (tagName.equals("title")) {
                            xmlPullParser.next();
                            builder.setTitle(xmlPullParser.getText());

                        } else if (tagName.equals("point")) {
                            xmlPullParser.next();
                            builder.setPoint(parsePoint(xmlPullParser.getText()));
                        }
                        break;

                    }
                    case XmlPullParser.END_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("item")) {
                            searchElement.setBusStop(builder.create());
                        }
                    }
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return busRouteElements;
    }
}

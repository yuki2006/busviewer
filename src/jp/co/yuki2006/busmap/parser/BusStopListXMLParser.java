package jp.co.yuki2006.busmap.parser;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class BusStopListXMLParser {
    public static ResultSearchBusList setBusListView(InputStream is) {
        ResultSearchBusList result = new ResultSearchBusList();
        BusStop.Builder builder = new BusStop.Builder();
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();
            int dbid = 0;
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
                            String tmpBusstopID = xmlPullParser.getAttributeValue(
                                    "", "BusStopID");
                            String aliasName = xmlPullParser.getAttributeValue("",
                                    "LoadingZoneAlias");
                            String loadingZone = xmlPullParser.getAttributeValue("",
                                    "LoadingZone");
                            builder.setLoadingZone(new LoadingZone(loadingZone, aliasName));
                            String region = xmlPullParser.getAttributeValue("", "Region");
                            if (xmlPullParser.getAttributeValue("", "DBId") != null) {
                                dbid = Integer.valueOf(xmlPullParser
                                        .getAttributeValue("", "DBId"));
                            }
                            int busStopID = 0;
                            if (tmpBusstopID != null) {
                                busStopID = Integer.parseInt(tmpBusstopID);
                            }
                            if (xmlPullParser.getAttributeValue("", "Latitude") != null) {
                                double latitude = Double.valueOf(xmlPullParser.getAttributeValue("", "Latitude"));
                                double longitude = Double.valueOf(xmlPullParser.getAttributeValue("", "Longitude"));
                                builder.setPoint(new LatLng(latitude, longitude));
                            }
                            builder.setRegion(region);
                            builder.setBusStopID(busStopID);
                        } else if (tagName.equals("title")) {
                            xmlPullParser.next();
                            String title = xmlPullParser.getText();
                            builder.setTitle(title);
                        } else if (tagName.equals("suggest")) {
                            // サジェスト（もしかして？機能）
                            xmlPullParser.next();
                            result.suggestString = xmlPullParser.getText();

                        } else if (tagName.equals("point")) {
                            xmlPullParser.next();

                            String text = xmlPullParser.getText();
                            if (text != null) {
                                text = text.trim();
                                String[] pos = text.split(" ");

                                String float_value_x = pos[0].replace(".", "");

//							int size = Math.min(float_value_x.length(), 8);
//							float_value_x = float_value_x.substring(0, size);

                                String float_value_y = pos[1].replace(".", "");

//							size = Math.min(float_value_y.length(), 9);
//							float_value_y = float_value_y.substring(0, size);
                                LatLng point = new LatLng(
                                        Float.parseFloat(float_value_x),
                                        Float.parseFloat(float_value_y));
                                builder.setPoint(point);
                            }

                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("item")) {
                            result.busStopList.add(builder.create());

                        }
                    }

                }
            }
            return result;
        } catch (XmlPullParserException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return null;

    }

}

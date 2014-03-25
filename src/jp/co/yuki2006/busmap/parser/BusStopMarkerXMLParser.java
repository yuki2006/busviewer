package jp.co.yuki2006.busmap.parser;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class BusStopMarkerXMLParser extends BusStopBaseXMLParser {
    /**
     * XMLデータの解析を行います。
     *
     * @param is
     * @param ret 　格納するオブジェクト
     * @return　途中でうち切ったらfalseを返す
     */
    public static boolean parse(InputStream is, ArrayList<BusStop> ret,
                                int busStopMarkerLimit) {

        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();
            BusStop.Builder builder = new BusStop.Builder();
            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser
                    .next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("channel")) {
                            // バーションチェック
                            int versioncode = Integer.parseInt(xmlPullParser
                                    .getAttributeValue("", "version"));
                            // Etc.checkVersion(versioncode);
                        } else if (tagName.equals("item")) {
                            String busID = xmlPullParser.getAttributeValue("",
                                    "BusStopID");

                            String loadingZone = xmlPullParser.getAttributeValue("",
                                    "loading_zone");
                            String loadingZoneAlias = xmlPullParser.getAttributeValue("",
                                    "LoadingZoneAlias");
                            int busStopID = 0;
                            if (busID != null) {
                                busStopID = Integer.parseInt(busID);
                            }
                            if (loadingZone == null) {
                                loadingZone = "";
                            }
                            builder.setRegion(xmlPullParser.getAttributeValue("", "Region"));
                            builder.setBusStopID(busStopID);
                            builder.setLoadingZone(new LoadingZone(loadingZone, loadingZoneAlias));
                        } else if (tagName.equals("title")) {
                            xmlPullParser.next();

                            String tmpTitle = xmlPullParser.getText();
                            builder.setTitle(tmpTitle);
                        } else if (tagName.equals("point")) {
                            xmlPullParser.next();

                            LatLng tmpPoint = parsePoint(xmlPullParser.getText());
                            builder.setPoint(tmpPoint);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("item")) {
                            ret.add(builder.create());
                            if (ret.size() > 10 + busStopMarkerLimit) {
                                return false;
                            }
                        }
                    }
                    default:
                        break;

                }
            }

        } catch (XmlPullParserException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return true;

    }
}

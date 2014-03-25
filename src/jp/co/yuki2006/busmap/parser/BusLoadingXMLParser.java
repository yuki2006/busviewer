package jp.co.yuki2006.busmap.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.store.LoadingZone;

/**
 * 詳細検索の画面の乗り場を指定してするXMLを解析します。
 *
 * @author yuki
 */
public final class BusLoadingXMLParser {
    public static LoadingZone[] getLoading(InputStream is) {
        ArrayList<LoadingZone> list = new ArrayList<LoadingZone>();
        // すべての乗り場のの項目を追加する。

        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();
            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser.next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("busstop")) {
                            int versioncode = Integer.parseInt(xmlPullParser.getAttributeValue("", "version"));
//						Etc.checkVersion(versioncode);

                        } else if (tagName.equals("zone")) {
                            String loadingID = xmlPullParser.getAttributeValue("", "id");
                            String alias = xmlPullParser.getAttributeValue("", "alias");
                            String lat = xmlPullParser.getAttributeValue("", "lat");
                            String lng = xmlPullParser.getAttributeValue("", "lng");
                            int dbid = Integer.valueOf(xmlPullParser.getAttributeValue("", "DBId"));
                            LoadingZone loading = new LoadingZone(loadingID, alias, dbid);
                            list.add(loading);

                        }

                    }
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

        // 乗り場が２つ以上ならすべての乗り場を追加
        if (list.size() > 1) {
            list.add(0, new LoadingZone("", "すべての乗り場", -1));
        }
        LoadingZone[] listArray = new LoadingZone[list.size()];
        list.toArray(listArray);
        return listArray;
    }

    /**
     * ユーティリティクラスのための。
     */
    private BusLoadingXMLParser() {
    }

}

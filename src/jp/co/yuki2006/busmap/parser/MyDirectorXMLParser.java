package jp.co.yuki2006.busmap.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jp.co.yuki2006.busmap.direction.MyDirectionParseResult;
import jp.co.yuki2006.busmap.store.DirectionData;

public class MyDirectorXMLParser extends BusTimelineXMLParser {
    public static final String BUSSTOPNAME = "a";
    public static final String BUSSTOPID = "b";

    public static MyDirectionParseResult setDirectionTimeline(InputStream is, List<DirectionData> directionData) {
        MyDirectionParseResult myDirectionParseResult = new MyDirectionParseResult();
        myDirectionParseResult.directionData = directionData;
        int directorCount = 0;
        try {
            final XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
            xmlPullParser.next();
            for (int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser.next()) {

                switch (e) {
                    case XmlPullParser.START_TAG: {
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("busstop")) {
                            int versioncode =
                                    Integer.parseInt(xmlPullParser.getAttributeValue("",
                                            "version"));
                            // Etc.checkVersion(versioncode);
                        } else if (tagName.equals("director")) {
                            directionData.set(directorCount, BusTimelineXMLParser.parserDirectorElements(xmlPullParser));
                            directorCount++;
                        } else if (tagName.equals("checktime")) {
                            myDirectionParseResult.checkTime = getCheckTimeParser(xmlPullParser);
                        }

                    }
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return myDirectionParseResult;
    }

}

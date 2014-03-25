package jp.co.yuki2006.busmap.parser;

import com.google.android.gms.maps.model.LatLng;

public class BusStopBaseXMLParser {
    protected static LatLng parsePoint(String parseValue) {
        String text = parseValue.trim();
        String[] pos = text.split(" ");

        // String tmpValueX = pos[0].replace(".", "");
        // String tmpValueY = pos[1].replace(".", "");

        // int size = Math.min(tmpValueX.length(), 8);
        // StringBuilder floatValueX = new StringBuilder(tmpValueX.substring(0,
        // size));
        // for (int k = size; k < 8; k++) {
        // floatValueX.append(0);
        // }
        // size = Math.min(tmpValueY.length(), 9);
        // StringBuilder floatValueY = new StringBuilder(tmpValueY.substring(0,
        // size));
        //
        // floatValueY = new StringBuilder(floatValueY.substring(0, size));
        // for (int k = size; k < 9; k++) {
        // floatValueY.append(0);
        // }
        return new LatLng(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]));
    }
}

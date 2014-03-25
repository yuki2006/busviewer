/**
 *
 */
package jp.co.yuki2006.busmap.custom;

import android.app.Activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

class CustomAliasNameSetter extends WebPortal<String, Integer, Boolean> {

    private final String currentLoadingZone;
    private final int busStopID;

    /**
     * @param currentLoadingZone
     * @param postRunnable
     * @param timeLineActivity   TODO
     */
    public CustomAliasNameSetter(Activity activity,
                                 int busStopID,
                                 String currentLoadingZone,
                                 IWebPostRunnable<Boolean> postRunnable) {
        super(activity, true, postRunnable, false);
        this.busStopID = busStopID;
        this.currentLoadingZone = currentLoadingZone;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected Boolean onBackGroundCore(InputStream is) {
        return true;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(String param) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("etc/set_custom_alias.php?BusStopID=");
        stringBuilder.append(busStopID);
        stringBuilder.append("&zone=");
        stringBuilder.append(currentLoadingZone);
        stringBuilder.append("&name=");
        try {
            stringBuilder.append(URLEncoder.encode(param, "UTF-8"));
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
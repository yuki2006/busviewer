/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 *
 */
public class GeoCodingLoader extends WebPortal<CharSequence, Integer, List<Address>> {

    /**
     * @param activity
     * @param showDialog
     * @param postRunnable
     */
    public GeoCodingLoader(Activity activity, IWebPostRunnable<List<Address>> postRunnable) {
        super(activity, true, postRunnable, false);
    }

    /*
     * (非 Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected List<Address> doInBackground(CharSequence... params) {
        Geocoder geocoder = new Geocoder(activity, Locale.JAPAN);

        try {
            List<Address> locationList = geocoder.getFromLocationName(params[0].toString(), 10);
            publishProgress(50);
            return locationList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected List<Address> onBackGroundCore(InputStream is) {
        return null;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(CharSequence param) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(List<Address> result) {
        if (result == null) {
            new Builder(activity)
                    .setTitle("住所検索ができませんでした")
                    .setMessage("何らかの理由で、住所検索ができませんでした。\n住所検索に対応してない機種もあるようです。。")
                    .show();
        }
        super.onPostExecute(result);
    }
}

/**
 *
 */
package jp.co.yuki2006.busmap.custom;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.parser.BusStopListXMLParser;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.web.Web;

public class MixBusStopSearchLoader extends AsyncTaskLoader<ArrayList<BusStop>> {
    private final String searchName;
    private final Context context;

    /**
     * @param searchName
     * @param arg0
     */
    public MixBusStopSearchLoader(Context context, String searchName) {
        super(context);
        this.context = context;
        this.searchName = searchName;
    }

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
     */
    @Override
    public ArrayList<BusStop> loadInBackground() {
        try {
            URL url = new URL(Web.getHostURL(context)
                    + "search_busstop.php?search_name=" + URLEncoder.encode(searchName, "UTF-8"));
            InputStream content = (InputStream) url.getContent();
            if (content != null) {
                ResultSearchBusList setBusListView = BusStopListXMLParser.setBusListView(content);
                return setBusListView.busStopList;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (context instanceof Activity) {
                Runnable action = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.connection_terminated, Toast.LENGTH_LONG).show();
                    }
                };
                ((Activity) context).runOnUiThread(action);

            }

            e.printStackTrace();
        }

        return null;
    }
}
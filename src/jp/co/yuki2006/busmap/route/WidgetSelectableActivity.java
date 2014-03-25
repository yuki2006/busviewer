package jp.co.yuki2006.busmap.route;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.route.MyRouteActivity.MyRouteListAdapter;

/**
 * @author yuki
 */
public class WidgetSelectableActivity extends SherlockListActivity implements OnItemClickListener {
    private MyRouteListAdapter adapter;
    private int widgetID;

    /*
     * (非 Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_select_activity);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int tmpWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, tmpWidgetID);
        }

        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED);
            finish();
        }

        ListView listView = (ListView) findViewById(android.R.id.list);
        adapter = new MyRouteListAdapter(this, null);
        listView.setAdapter(adapter);
        adapter.refleshList(false);
        listView.setOnItemClickListener(this);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        TransitionManager.setWidgetPreference(this, adapter.getItem(position).routeData, widgetID);


        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(this);
        //	AppWidget.onUpdate(this, appWidgetManager, widgetID);
        ComponentName provider = AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetID).provider;
        setResult(RESULT_OK, intent);

        try {
            Intent broadcast = new Intent(this, Class.forName(provider.getClassName()));
            broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] appWidgetIds = {widgetID};
            broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(broadcast);
        } catch (ClassNotFoundException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }


        finish();
    }

}

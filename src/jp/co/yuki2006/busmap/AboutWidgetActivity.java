package jp.co.yuki2006.busmap;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.web.Web;

public class AboutWidgetActivity extends SherlockActivity implements OnClickListener {
    public void onClick(View v) {
        if (v.getId() == R.id.go_android_setting_view) {
            Etc.showAppDetail(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_widget);
        findViewById(R.id.go_android_setting_view).setOnClickListener(this);

        WebView webView = ((WebView) findViewById(R.id.about_widget_webview));
        webView.loadUrl(Web.getHostURL(this) + "appli/widget.html");
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(com.
     * actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActionBarUPWrapper.doActionUpNavigation(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
            ((View) findViewById(R.id.go_android_setting_view).getParent()).setVisibility(View.GONE);
        } else {
            ((View) findViewById(R.id.go_android_setting_view).getParent()).setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

}

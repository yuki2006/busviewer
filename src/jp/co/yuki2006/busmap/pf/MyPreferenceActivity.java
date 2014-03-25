package jp.co.yuki2006.busmap.pf;

import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.AboutWidgetActivity;
import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.db.AdvancedSearchHistoryDB;
import jp.co.yuki2006.busmap.db.SearchMapSuggestDB;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.values.PreferenceValues;
import jp.co.yuki2006.busmap.values.ProviderValues;

public class MyPreferenceActivity extends SherlockPreferenceActivity implements OnClickListener {
    public void onClick(View v) {
        if (v.getId() == R.id.go_about_widget) {
            Intent intent = new Intent(this, AboutWidgetActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_layout);

        addPreferencesFromResource(R.xml.preference);
        findPreference(PreferenceValues.APPLICATION_VERSION).setTitle(
                Etc.getApplicationName(this) + " Ver "
                        + Etc.getVersionName(this));
        findViewById(R.id.go_about_widget).setOnClickListener(this);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockPreferenceActivity#onOptionsItemSelected
     * (com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActionBarUPWrapper.doActionUpNavigation(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        if (preference.getKey().equals(PreferenceValues.KEY_GO_ANDROID_MARKET)) {

            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (preference.getKey().equals(PreferenceValues.KEY_GO_ENQUETE)) {
            Etc.goToEnqueteForm(this);
        } else if (preference.getKey().equals(PreferenceValues.APPLICATION_VERSION)
                || preference.getKey().equals(getString(R.string.key_go_support_twitter))) {
            // Uri uri = Uri
            // .parse(WebPortal.getHostURL(getApplicationContext()) +
            // "appli_new/");
            Uri uri = Uri
                    .parse("https://twitter.com/#!/BusViewerTeam");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (preference.getKey().equals(PreferenceValues.KEY_SUPPORT_MAIL)) {
            String mailTo = "bus.viewer.team@gmail.com";
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_SENDTO);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setData(Uri.parse("mailto:" + mailTo));
            startActivity(intent);
        } else if (preference.getKey().equals(PreferenceValues.KEY_GO_FACEBOOK)) {
            Intent intent;
            try {
                getPackageManager().getPackageInfo("com.facebook.katana", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/260821420706209"));
            } catch (PackageManager.NameNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/bus.viewer"));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (preference.getKey().equals(PreferenceValues.KEY_RESET_SETTINGS)) {
            Etc.showAppDetail(this);
        } else if (preference.getKey().equals(PreferenceValues.PF_KEY_GOTO_TIMELINE_SETTING)) {
            Intent intent = new Intent(this, MainTimeLinePreferenceActivity.class);
            startActivity(intent);
            // } else if
            // (preference.getKey().equals(PreferenceValues.KEY_REPORT_PROBLEM))
            // {
            // Uri uri = Uri
            // .parse("https://docs.google.com/spreadsheet/viewform?formkey=dGc1cWQxM2FrNlg2RGRqTVZ1UEpwTWc6MQ#gid=0");
            // Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // startActivity(intent);
        } else if (preference.getKey().equals(PreferenceValues.PF_RECENT_SEARCH_CLEAR)) {
            {
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        ProviderValues.BUS_STOP_SUGGESTION_PROVIDER,
                        SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
                suggestions.clearHistory();
            }
            {
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        ProviderValues.MAP_SUGGESTION_PROVIDER,
                        SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
                suggestions.clearHistory();
            }
            {
                SearchMapSuggestDB db = new SearchMapSuggestDB(this);
                db.truncate();
                db.close();
            }
            {
                AdvancedSearchHistoryDB db = new AdvancedSearchHistoryDB(this);
                db.truncate();
                db.close();
            }

            Toast.makeText(this, "履歴が削除されました。", Toast.LENGTH_LONG).show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}

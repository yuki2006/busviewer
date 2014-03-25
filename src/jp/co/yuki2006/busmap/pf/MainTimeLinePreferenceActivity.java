/**
 *
 */
package jp.co.yuki2006.busmap.pf;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;

/**
 * @author yuki
 */
public class MainTimeLinePreferenceActivity extends SherlockPreferenceActivity {
    /*
     * (非 Javadoc)
     *
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_timeline_pf);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /* (非 Javadoc)
     * @see com.actionbarsherlock.app.SherlockPreferenceActivity#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            ActionBarUPWrapper.doActionUpNavigation(this);
        }
        return super.onOptionsItemSelected(item);
    }
}

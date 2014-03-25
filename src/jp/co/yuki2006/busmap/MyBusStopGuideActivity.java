/**
 *
 */
package jp.co.yuki2006.busmap;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;

/**
 * @author yuki
 */
public class MyBusStopGuideActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(arg0);
        setContentView(R.layout.my_busstop_guide_activity);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*
     */
    @Override
    public boolean onOptionsItemSelected(
            com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                ActionBarUPWrapper.doActionUpNavigation(this);
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

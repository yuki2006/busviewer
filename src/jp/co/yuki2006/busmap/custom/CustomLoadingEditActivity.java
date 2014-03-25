/**
 *
 */
package jp.co.yuki2006.busmap.custom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.map.IFragmentToBusStop;
import jp.co.yuki2006.busmap.store.BusStop;

/**
 * @author yuki
 */
public class CustomLoadingEditActivity extends SherlockFragmentActivity
        implements IFragmentToBusStop {


    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(arg0);

        setContentView(R.layout.custom_loading_edit_activity);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("fragment");
        if (fragment == null) {
            CustomLoadingEditFragment editFragment = new CustomLoadingEditFragment();
            Bundle extras = getIntent().getExtras();
            editFragment.setArguments(extras);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(android.R.id.content, editFragment, "fragment");
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActionBarUPWrapper.doActionUpNavigation(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void selectBusStop(BusStop busStop) {
        CustomLoadingEditFragment fragment = (CustomLoadingEditFragment) getSupportFragmentManager().findFragmentByTag(
                "fragment");
        fragment.selectBusStop(busStop);
    }
}

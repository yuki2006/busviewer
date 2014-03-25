package jp.co.yuki2006.busmap.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.route.fragment.AdvancedHistoryFragment;
import jp.co.yuki2006.busmap.route.fragment.AdvancedSearchFormFragment;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * 詳細検索のアクティビティです。
 *
 * @author yuki
 */
public class AdvancedSearchConditionActivity extends AdActivity implements IAdvancedSearch {

    /**
     *
     */
    private static final String FORM_FRAGMENT_TAG = "form";
    private ViewPager viewpager;
    public static int REQUEST_RESULT_ACTIVITY = 4;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_search_condition_activity);
        Intent intent = getIntent();
        RouteData tmp = (RouteData) intent.getSerializableExtra(IntentValues.TRANSITION_ADVANCED_SEARCH);
        if (tmp == null && intent.getExtras() != null) {
            Boolean isDeparture = TransitionManager.isDepatureByIntent(intent.getExtras());
            if (isDeparture != null) {
                BusStop busStop = TransitionManager.getBusStopByIndent(this);
                tmp = new RouteData();
                tmp.setBusStop(isDeparture, busStop);
            }
        }
        final RouteData routeData = tmp;
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int i) {
                if (i == 0) {
                    AdvancedSearchFormFragment advancedSearchFormFragment = new AdvancedSearchFormFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentValues.TRANSITION_ADVANCED_SEARCH, routeData);
                    advancedSearchFormFragment.setArguments(bundle);
                    return advancedSearchFormFragment;
                } else {
                    return new AdvancedHistoryFragment();
                }
            }

            /*
             * (非 Javadoc)
             *
             * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
             */
            @Override
            public CharSequence getPageTitle(int i) {
                if (i == 0) {
                    return getString(R.string.search_input);
                } else {
                    return getString(R.string.search_history);
                }
            }

        };
        viewpager.setAdapter(pagerAdapter);
        TitlePageIndicator titles = (TitlePageIndicator) findViewById(R.id.titles);
        titles.setViewPager(viewpager);
        OnPageChangeListener listener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag("menu");
                if (fragment != null) {
                    fragment.setMenuVisibility(i == 0);
                    // あんまりスマートではない。
                    // UI上の問題
                    ((AdvancedSearchFormFragment) viewpager.getAdapter().instantiateItem(viewpager, 0))
                            .setMenuVisibility(i == 0);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO 自動生成されたメソッド・スタブ

            }
        };
        titles.setOnPageChangeListener(listener);
        // if (data.getResult() != null) {
        // setResultAdapter(data.getResult());
        // }

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.route.IAdvancedSearch#searchBus()
     */
    @Override
    public void searchBus() {
        ((AdvancedSearchFormFragment) viewpager.getAdapter().instantiateItem(viewpager, 0)).searchBus();
    }

    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.route.IAdvancedSearch#getRouteData()
     */
    @Override
    public RouteData getRouteData() {
        return ((AdvancedSearchFormFragment) viewpager.getAdapter().instantiateItem(viewpager, 0)).getRouteData();
    }

}

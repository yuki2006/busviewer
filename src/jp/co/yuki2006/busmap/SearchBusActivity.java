package jp.co.yuki2006.busmap;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.BusListActivity;
import jp.co.yuki2006.busmap.etc.BusStopAdapter;
import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.map.MapSuggestionProvider;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.ProviderValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;

public class SearchBusActivity extends BusListActivity implements IWebPostRunnable<ResultSearchBusList> {

    private static class TmpSearchBusData {
        public SearchBusLoader loader;
        public String suggestString;
        public BusStopAdapter busStopAdapter;
        public boolean isLoaded = false;
    }

    /**
     *
     */

    private SearchBusLoader loader;
    private String suggestString;

    private boolean isLoaded = false;
    private String busStopName;

    private void getSearch(final String busStopName, boolean showDialog) {
        loader = new SearchBusLoader(this, showDialog, this);
        loader.execute(busStopName);
        // キーボードを隠す
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        busStopName = intent.getStringExtra(IntentValues.TRANSITION_BUS_STOP_NAME);
        // setTitle(busStopName);
        setEnableSelectForMenu(intent.getBooleanExtra(IntentValues.EQUIRE_SELECT_MENU, false));
        TmpSearchBusData data = (TmpSearchBusData) getLastCustomNonConfigurationInstance();

        if (data == null) {
            getSearch(busStopName, true);
        } else {
            isLoaded = data.isLoaded;
            loader = data.loader;
            if (isLoaded) {
                int count = data.busStopAdapter.getCount();

                for (int i = 0; i < count; i++) {
                    arrayAdapter.add(data.busStopAdapter.getItem(i));
                }

                suggestString = data.suggestString;
                showSuggest();
            } else if (loader != null) {
                loader.resetActivity(this);
                loader.resetPostRunnable(this);
            }

        }
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.etc.BusListActivity#onCreateOptionsMenu(com.
     * actionbarsherlock.view.Menu)
     */
    @Override
    @TargetApi(Build.VERSION_CODES.FROYO)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            MenuItem item = menu.findItem(R.id.menu_search);
            item.setVisible(true);
            SearchView searchView = (SearchView) item.getActionView();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
            searchView.setSearchableInfo(searchableInfo);
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(false);
            searchView.setQuery(busStopName, false);
            searchView.setOnQueryTextListener(new OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String busStopName) {
                    getSearch(busStopName, false);
                    return false;
                }
            });
        } else {
            menu.findItem(R.id.search_button).setVisible(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loader != null) {
            loader.onDestroy();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions =
                    new SearchRecentSuggestions(this, ProviderValues.BUS_STOP_SUGGESTION_PROVIDER,
                            MapSuggestionProvider.DATABASE_MODE_QUERIES);
            suggestions.saveRecentQuery(query, null);
            setTitle(query);
            getSearch(query, true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                Intent intent = getIntent();
                String busStopName = intent.getStringExtra(IntentValues.TRANSITION_BUS_STOP_NAME);
                startSearch(busStopName, false, null, false);
                break;
            case android.R.id.home: {
                ActionBarUPWrapper.doActionUpNavigation(this);

            }
            default:
                break;
        }
        return true;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.IWebPostRunnable#onPostRunnable(java.lang.Object
     * )
     */
    @Override
    public void onPostRunnable(ResultSearchBusList result) {
        setBusList(result.busStopList);

        isLoaded = true;
        suggestString = result.suggestString;
        showSuggest();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        TmpSearchBusData data = new TmpSearchBusData();
        data.loader = loader;
        data.busStopAdapter = this.arrayAdapter;
        data.isLoaded = isLoaded;
        data.suggestString = suggestString;
        return data;
    }

    private void showSuggest() {
        int visible = (suggestString.length() > 0) ? View.VISIBLE : View.GONE;
        // もしかして？文字列 表示
        ((View) findViewById(R.id.suggest_text_view).getParent()).setVisibility(visible);
        // もしかして？文字列追加
        ((TextView) findViewById(R.id.suggest_text_view)).setText(suggestString);
        // もしかしてがある場合　絞り込みの文字を消す
        // if (visible == View.VISIBLE) {
        // search_text_box.setText("");
        // }
    }

}

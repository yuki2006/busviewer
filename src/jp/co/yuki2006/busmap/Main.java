package jp.co.yuki2006.busmap;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.custom.CustomLoadingEditActivity;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.map.BusMapActivity;
import jp.co.yuki2006.busmap.map.MapSuggestionProvider;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.route.MyRouteActivity;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.ProviderValues;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * メインのアクティビティです. マップや各種画面への遷移するためのアクティビティ
 *
 * @author ono
 */
public class Main extends AdActivity implements OnClickListener {

    /**
     * 遷移後のアクティビティに渡すリクエストコードです.
     */
    private static final int REQUEST_CODE = 1;

    /*
     * (非 Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_map_button) {
            Intent intent = new Intent(Main.this, BusMapActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.go_advanced_search) {
            Intent intent = new Intent(Main.this,
                    AdvancedSearchConditionActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.my_busstop_button) {
            Intent intent = new Intent(this,
                    jp.co.yuki2006.busmap.MyBusStopActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (v.getId() == R.id.my_root_button) {
            Intent intent = new Intent(this, MyRouteActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (v.getId() == R.id.search_button) {
            onSearchRequested();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);
        // getApplicationContext().set
        setContentView(R.layout.main);
        if (BuildConfig.DEBUG) {
            Log.e("TAG", "DEBUG = true");
        } else {
            Log.e("TAG", "DEBUG = false");
        }
        Intent intent = getIntent();
        // ショートカットから起動した場合
        // ただし画面回転前のデータはない
        if (savedInstanceState == null && intent.getExtras() != null) {
            BusStop busStop = null;
            // ショートカットからの起動
            if (intent.getAction().equals(Intent.ACTION_MAIN)) {

                if (intent.hasExtra(IntentValues.TRANSITION_BUS_STOP_ID)) {
                    int currentBusStopID = intent.getIntExtra(
                            IntentValues.TRANSITION_BUS_STOP_ID, 0);
                    String currentLoadingZone = intent
                            .getStringExtra(IntentValues.TRANSITION_BUS_STOP_LOADING);
                    String name = intent
                            .getStringExtra(IntentValues.TRANSITION_BUS_NAME);
                    if (name == null) {
                        name = "";
                    }
                    BusStop.Builder builder = new BusStop.Builder();
                    builder.setTitle(name);
                    builder.setBusStopID(currentBusStopID);
                    builder.setLoadingZone(new LoadingZone(currentLoadingZone, ""));
                    busStop = builder.create();
                } else {
                    busStop = TransitionManager.getBusStopByIndent(this);
                }

                // 時刻表のアクティビティに移動。
                TransitionManager transitionManager = new TransitionManager(
                        this, TimeLineActivity.class, busStop);

                this.startActivityForResult(transitionManager, 1);
            } else {
                // 他のアクティビティから呼び出し
                busStop = TransitionManager.getBusStopByIndent(this);
                // インテントで飛ばす。
                // map.setMapPosition(busStop.getPoint());
            }
        } else {
            // 通常起動時
            // map.enableRunOnFirstFix();
        }

        findViewById(R.id.go_map_button).setOnClickListener(this);
        findViewById(R.id.go_advanced_search).setOnClickListener(this);

        findViewById(R.id.my_busstop_button).setOnClickListener(this);
        findViewById(R.id.my_root_button).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);


        int latestVersion = Etc.getVersionCode(this);
        // プリファレンスの準備
        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(this);

//        if (preference.getInt("version", 0) < latestVersion) {
        	if (true){
            // 初回起動時の処理
            AssetManager as = getResources().getAssets();
            InputStream in = null;
            try {
                in = as.open("startup.txt");

                InputStreamReader reader = new InputStreamReader(in);

                BufferedReader buffer = new BufferedReader(reader);

                String tmpData = null;
                StringBuilder data = new StringBuilder();

                while ((tmpData = buffer.readLine()) != null) {
                    data.append(tmpData).append("\n");
                }
                reader.close();
                buffer.close();

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setPositiveButton("OK", null);
                dialog.setNegativeButton("詳しくはこちら",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Uri uri = Uri
                                        .parse("https://sites.google.com/site/busviewer/important");
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        uri);
                                startActivity(intent);
                            }
                        });

                dialog.setTitle("重要なお知らせ").setMessage(data).show();
                // プリファレンスの書き変え
                Editor editor = preference.edit();
                editor.putInt("version", latestVersion).commit();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "更新情報がありません",
                        Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                }
            }

        }

        // 新機能　ユーザー数を知るための機能
        // このバージョンだけ(Ver2.4)
        if (Etc.getMyID(this) == 0) {

            WebPortal<String, Integer, Void> webPortal = new WebPortal<String, Integer, Void>(
                    this, null) {
                @Override
                protected Void onBackGroundCore(InputStream is) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));
                    try {
                        Etc.setMyID(Main.this,
                                Integer.valueOf(reader.readLine()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected String onParamParser(String param) {
                    return "registerUser.php";
                }
            };
            webPortal.execute("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        // メニューインフレーターを取得
        MenuInflater inflater = getSupportMenuInflater();

        // xmlのリソースファイルを使用してメニューにアイテムを追加
        inflater.inflate(R.menu.main_view_optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Etc.checkAndClearCache(this);
    }

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int i, KeyEvent keyevent) {
        if (keyevent.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            onSearchRequested();
        }
        return super.onKeyDown(i, keyevent);
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            // インテント作成
            Etc.callVoiceSearch(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_setting:
                // 設定
                intent = new Intent(this,
                        jp.co.yuki2006.busmap.pf.MyPreferenceActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_about_me:
                intent = new Intent(this,
                        jp.co.yuki2006.busmap.AboutMeActivity.class);
                startActivity(intent);

                break;
            case R.id.go_to_custom_alias_menu: {
                intent = new Intent(this,
                        CustomLoadingEditActivity.class);
                startActivity(intent);

                break;
            }
            // case R.id.goto_advanced_search:
            // intent = new Intent(this, AdvancedSearchConditionActivity.class);
            // startActivity(intent);

            default:
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this, ProviderValues.MAP_SUGGESTION_PROVIDER,
                    MapSuggestionProvider.DATABASE_MODE_QUERIES);
            suggestions.saveRecentQuery(query, null);

            searchBusStop(query);

        }
    }

    /**
     * 検索ボタンを押されたときのメソッドです。
     */
    private void searchBusStop(String searchText) {
        Intent intent = new Intent(Main.this, SearchBusActivity.class);
        intent.putExtra(IntentValues.TRANSITION_BUS_STOP_NAME, searchText);
        intent.setAction(Intent.ACTION_VIEW);
        Main.this.startActivityForResult(intent, REQUEST_CODE);
    }

}

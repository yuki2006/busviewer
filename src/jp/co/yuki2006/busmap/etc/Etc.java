package jp.co.yuki2006.busmap.etc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yuki2006.busmap.BuildConfig;
import jp.co.yuki2006.busmap.Main;
import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.db.MyBusStopDB;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * 汎用的に使えるユーティリティクラスです。
 *
 * @author yuki
 */
public final class Etc {
    public static abstract class OnNextKeyDownListener implements
            View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                onNextKeyDown();
                return true;
            }
            return false;
        }

        public abstract void onNextKeyDown();
    }

    public interface onSelectVoiceResult {
        public void onSelect(String result);
    }

    /**
     * 音声検索結果コードです。
     */
    public static final int REQUEST_VOICE_SEARCH_CODE = 3;
    private static boolean checkedversion = false;

    private static Runnable versionUpDialog;

    private static final String INCREMENTAL_BUS_STOP = "IncrementalBusStop";

    private static final String MAX_STOP_MARKER = "MAXStopmarker";

    private static final String LIST_DEFAULt_ACTION = "ListDefaultAction";

    private static final String MY_ID = "MyID";

    /**
     * ショートカットを作成ユーティリティーメソッド。
     *
     * @param context
     */
    public static void addBusStopShortCut(Context context, BusStop busstop) {
        // ショートカット作成
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(context, Main.class.getName());

        // 作成したショートカットを設定するIntent。ここでショートカット名とアイコンも設定。
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(
                context, R.drawable.ic_launcher);

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, busstop.toString());

        shortcutIntent.putExtra(IntentValues.TRANSITION_BUS_STOP_ID,
                busstop.getBusStopID());
        shortcutIntent.putExtra(IntentValues.TRANSITION_BUS_STOP_LOADING,
                busstop.getLoading().getLoadingID());
        shortcutIntent.putExtra(IntentValues.TRANSITION_BUS_NAME,
                busstop.getBusStopName());
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // ショートカット設定
        context.sendBroadcast(intent);
        Toast.makeText(context, "ホームにショートカットを作成しました。", Toast.LENGTH_LONG)
                .show();
    }

    /**
     * マイバス停にデータを追加するユーティリティーメソッド。
     */
    public static void addMyBusStop(Context context, BusStop data) {
        MyBusStopDB db = new MyBusStopDB(context);

        if (db.insertData(data)) {
            Toast.makeText(context, "マイバス停に追加しました。", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, data + "はすでに登録されているようです。",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void callVoiceSearch(Activity activity) {
        // インテント作成
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "バス停音声検索"); // お好きな文字に変更できます
        try {
            activity.startActivityForResult(intent, REQUEST_VOICE_SEARCH_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Uri uri = Uri
                    .parse("https://market.android.com/details?id=com.google.android.voicesearch");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(marketIntent);
        }
    }

    /**
     * @param main
     */
    public static void checkAndClearCache(Context context) {
        // ファイルサイズ数を取得
        long totalSize = getDirSize(context, context.getCacheDir());
        // 5MB以上になったらキャッシュクリア
        if (totalSize > 1024 * 1024 * 5) {
            Log.d("Bus", "Clear Cache");
            deleteDir(context, context.getCacheDir());
            WebView webView = new WebView(context);
            webView.clearCache(true);
        }
    }

    public static boolean checkVersion(Context context, int dataVersion) {
        // 取得したデータとバーション不整合の場合　最新バーションをすすめる。
        if (checkedversion == false && getVersionCode(context) < dataVersion) {
            checkedversion = true;
            // mHandler.post(versionUpDialog);
        }
        return true;
    }

    public static String command() {

        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec("/system/bin/netcfg");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            // process.getOutputStream().
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteDir(Context context, File file) {
        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                deleteDir(context, tmp);
            }
        } else {
            file.delete();
        }
    }

    public static String getApplicationName(Context context) {
        PackageManager pm = context.getPackageManager();

        try {

            return (String) pm.getApplicationLabel(pm.getApplicationInfo(
                    context.getPackageName(), 0));
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    private static long getDirSize(Context context, File file) {
        if (file.isDirectory()) {
            long size = 0;
            for (File tmp : file.listFiles()) {
                size += getDirSize(context, tmp);
            }
            return size;
        }
        return file.length();
    }

    public static int getIncrementalLevel(Context context) {
        SharedPreferences mSP = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return mSP.getInt(INCREMENTAL_BUS_STOP, 16);
    }

    /**
     * リストタッチをした時のデフォルトのアクションを返します。
     *
     * @return trueなら通常タップでメニュー、falseならロングタップでメニュー
     */
    public static boolean getListDefaultAction(Context context) {
        SharedPreferences mSP = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());

        return mSP.getBoolean(LIST_DEFAULt_ACTION, true);
    }

    public static int getMAXStopMarker(Context context) {
        SharedPreferences mSP = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());

        return mSP.getInt(MAX_STOP_MARKER, 10);
    }

    public static int getMyID(Context context) {
        SharedPreferences mSP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return mSP.getInt(MY_ID, 0);
    }

    @Deprecated
    public static int getPrevTime() {
        // SharedPreferences mSP =
        // PreferenceManager.getDefaultSharedPreferences(context);
        //
        // return mSP.getInt(PrevTime, 3) * 60 * 60;
        return 0;
    }

    public static int getVersionCode(Context context) {
        int ver;
        try {
            ver = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 1).versionCode;
        } catch (NameNotFoundException e) {
            ver = 0;
        }
        return ver;
    }

    public static String getVersionName(Context context) {
        String ver;
        try {
            ver = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 1).versionName;
        } catch (NameNotFoundException e) {
            ver = "";
        }
        return ver;
    }

    /**
     * @param activity
     * @param item
     */
    public static void goToBusCool(FragmentActivity activity, RouteData item) {
        goToBusCool(activity, item.getBusStop(true).getBusStopName(), item
                .getBusStop(false).getBusStopName());

    }

    public static void goToBusCool(FragmentActivity activity,
                                   String departureBusStopName, String arrivalBusStopName) {

        StringBuilder stringBuilder = new StringBuilder();


        BusCoolDialog busCoolDialog = new BusCoolDialog();
        Bundle args = new Bundle();
        args.putString(BusCoolDialog.URL, stringBuilder.toString());
        busCoolDialog.setArguments(args);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(busCoolDialog, "BusCool");
        fragmentTransaction.commit();

    }

    public static class BusCoolDialog extends DialogFragment {
        public static final String URL = "URL";


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ブラウザが起動します");
            builder.setMessage("「バスく～る」は北陸鉄道株式会社様のサービスのため、そちらにはお問い合わせなどはなさらないようよろしくお願いします。\n（このアプリの開発者にお願いします）");
            builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle arguments = getArguments();
                    String url = arguments.getString(URL);
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(intent);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            return builder.create();
        }
    }

    public static void goToEnqueteForm(Activity activity) {

        Uri uri = null;
        try {
            uri = Uri
                    .parse("https://spreadsheets.google.com/spreadsheet/viewform?hl=ja&formkey=dENDLVZiTlFoSER3OEtuMmsxMldKbmc6MQ&entry_10="
                            + URLEncoder.encode(Build.MODEL, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public static boolean isDebugMode(Context mycontext) {
        return BuildConfig.DEBUG;
        // PackageManager pm = mycontext.getPackageManager();
        // ApplicationInfo ai = null;
        // try {
        // ai = pm.getApplicationInfo(mycontext.getPackageName(), 0);
        // } catch (NameNotFoundException e) {
        // ai = null;
        // return false;
        // }
        //
        // if ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) ==
        // ApplicationInfo.FLAG_DEBUGGABLE) {
        // return true;
        // }
        // return false;
    }

    public static void resultVoice(Context context, Intent data,
                                   final onSelectVoiceResult selectVoiceResult) {
        // 結果文字列リスト
        final ArrayList<String> results = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        Builder builder = new Builder(context);
        builder.setTitle(R.string.did_you_mean);
        builder.setItems(results.toArray(new String[0]),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectVoiceResult.onSelect(results.get(which));

                    }
                });

        builder.show();
    }

    public static void setInitialize(final Context context) {

        // mHandler = new Handler();

        versionUpDialog = new Runnable() {
            public void run() {
                Builder dialog = new AlertDialog.Builder(context);
                dialog.setPositiveButton("最新バーションをダウンロードする",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Uri uri = Uri.parse("market://details?id="
                                        + context.getPackageName());
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        uri);
                                context.startActivity(intent);
                            }
                        });
                dialog.setNegativeButton("今はこのまま使用する", null);
                dialog.setTitle("新しいバージョンがあります")
                        .setMessage(
                                "アプリの新しいバージョンがリリースされています。\nこのまま使い続ける事もできますが,正常に読み込まれない可能性があります。")
                        .show();

            }

        };

    }

    public static void setMyID(Context context, int id) {
        SharedPreferences mSP = PreferenceManager
                .getDefaultSharedPreferences(context);

        mSP.edit().putInt(MY_ID, id).commit();
    }

    public static void setNoSearchKeyCancel(Dialog dialog) {
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                }
                return false;
            }
        };
        dialog.setOnKeyListener(onKeyListener);

    }

    /**
     * 一分ごとにタスクを設定するラッパーメソッドです。
     *
     * @param timer
     * @param task
     */
    public static void setMinutesIntervalTimer(Timer timer, TimerTask task) {
        Calendar now = Calendar.getInstance();
        timer.schedule(task, 1000 * (60 - now.get(Calendar.SECOND)), 1000 * 60);
    }

    /**
     * アプリの詳細画面を表示します。
     *
     * @param context
     */
    public static void showAppDetail(final Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        // Android 2.2以外に処理する
        if (Build.VERSION.SDK_INT != 8) {

            Uri uri = Uri.fromParts("package", context.getPackageName(), null);

            // アプリケーションのパッケージ名を登録する
            intent.setData(uri);
            // アプリケーション管理の詳細画面を登録する
            ComponentName cn = new ComponentName("com.android.settings",
                    "com.android.settings.applications.InstalledAppDetails");
            intent.setComponent(cn);

            // intent.setClassName("com.android.settings",
            // "com.android.settings.applications.InstalledAppDetails");
            // intent.putExtra("package", context.getPackageName());
        } else {
            // Android 2.2のみ処理する
            intent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            intent.putExtra("pkg", context.getPackageName());
        }
        context.startActivity(intent);
    }
}

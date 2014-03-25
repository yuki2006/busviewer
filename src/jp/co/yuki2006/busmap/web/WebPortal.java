package jp.co.yuki2006.busmap.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import jp.co.yuki2006.busmap.R;

/**
 * AsyncTask+ProgressDialogを組み合わせた抽象クラスです。
 * 基本的にはwebの接続はこれ実装したクラスを経由して行ってください。
 * 抽象した理由は、webデータの取得とその解析までをdoInBackgroundで行わせたかったからです。
 * （うまい方法があれば教えて下さい）
 *
 * @author yuki
 */
public abstract class WebPortal<Params, Progress, Result> extends WebPortalBase<Params, Integer, Result> {
    private ProgressDialog dialog;
    protected Activity activity;
    private final boolean showDialog;
    private final String loadingText;
    private boolean isShown;
    private static final HashMap<Class<?>, Integer> EXCEPTION_MAP = new HashMap<Class<?>, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(SocketTimeoutException.class, R.string.connection_terminated);
            put(NoContentException.class, R.string.request_problem);
            put(ServiceUnavailableException.class, R.string.connection_concentration);
            put(IOException.class, R.string.not_connect_server);
        }
    };
    private final boolean isCancelActivityFinish;

    public WebPortal(Activity activity, boolean showDialog, IWebPostRunnable<Result> postRunnable,
                     boolean isCancelActivityFinish) {
        this(activity, showDialog, postRunnable, activity.getString(R.string.loading_data), isCancelActivityFinish);
    }

    public WebPortal(Activity activity, boolean showDialog, IWebPostRunnable<Result> postRunnable,
                     String loadingText,
                     boolean isCancelActivityFinish) {
        super(activity, postRunnable);
        this.activity = activity;
        this.showDialog = showDialog;
        this.loadingText = loadingText;
        this.isCancelActivityFinish = isCancelActivityFinish;

    }

    public WebPortal(Activity activity, IWebPostRunnable<Result> postRunnable) {
        this(activity, false, postRunnable, "", false);
    }

    public WebPortal(Context context, IWebPostRunnable<Result> postRunnable) {
        super(context, postRunnable);
        this.showDialog = false;
        this.loadingText = "";
        this.isCancelActivityFinish = false;
    }

    private void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            // データが取得出来なかったら抜ける。
            isShown = false;
        } else {
            // ウィジェットの読み込み対応
            if (activity != null) {
                activity.setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    protected Result doInBackground(Params... param) {
        Result tmp = null;
        String onParamParser = param.length > 0 ? onParamParser(param[0]) :
                onParamParser(null);
        InputStream is = null;
        try {

            is = getMethod(onParamParser);
            publishProgress(50);
            tmp = onBackGroundCore(is);
            publishProgress(100);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    protected InputStream getMethod(String urlString)
            throws IOException {
        InputStream is = null;
        try {
            is = super.getMethod(urlString);
        } catch (final IOException e) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Class<? extends IOException> class1 = e.getClass();
                        if (EXCEPTION_MAP.containsKey(class1) == false) {
                            class1 = IOException.class;
                        }
                        Toast.makeText(activity, EXCEPTION_MAP.get(class1), Toast.LENGTH_LONG).show();
                    }
                });
            }
            throw e;
        }
        return is;
    }

    abstract protected Result onBackGroundCore(InputStream is);

    protected void onCancelled(Result result) {
        closeDialog();
    }

    /**
     * アクティビティが終了したら呼び出す。
     */
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    abstract protected String onParamParser(Params param);

    @Override
    protected void onPostExecute(final Result result) {
        super.onPostExecute(result);

        closeDialog();

    }

    /*
     * (非 Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        if (showDialog) {
            showDialog();
        } else {
            // ウィジェットの読み込み対応
            if (activity != null) {
                activity.setProgressBarIndeterminateVisibility(true);
            }
        }

    }

    /*
     * (非 Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(final Integer... values) {

        super.onProgressUpdate(values);
        if (dialog != null) {
            dialog.setProgress(values[0]);
        }
    }

    /**
     * 画面回転などでレビュームを行うときの再設定アクティビティ。
     *
     * @param activity
     */
    public void resetActivity(Activity activity) {
        this.activity = activity;
        if (showDialog && isShown) {
            showDialog();
        }
    }

    private void showDialog() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(loadingText);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        dialog.setCancelable(false);
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    cancel(false);
                    if (isCancelActivityFinish) {
                        activity.finish();
                    }
                    closeDialog();
                    return true;
                }
                return false;
            }
        };
        dialog.setOnKeyListener(onKeyListener);
        isShown = true;
    }
}

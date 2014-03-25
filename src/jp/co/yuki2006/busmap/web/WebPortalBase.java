package jp.co.yuki2006.busmap.web;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;

import jp.co.yuki2006.busmap.etc.Etc;

class NoContentException extends IOException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

}

class ServiceUnavailableException extends IOException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

}


public abstract class WebPortalBase<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final String USERAGENT;
    private IWebPostRunnable<Result> postRunnable;

    protected final Context context;

    public WebPortalBase(Context context, IWebPostRunnable<Result> postRunnable) {
        this.context = context;
        this.postRunnable = postRunnable;
        // USERAGENT生成
        USERAGENT = "AndroidBusMap/" + Etc.getVersionName(context) + " @ " + Build.MODEL;
    }

    @Override
    protected abstract Result doInBackground(Params... params);

    ;

    protected InputStream getMethod(String urlString)
            throws IOException, ServiceUnavailableException {

        HttpGet httpGetObj;
        HttpEntity httpEntityObj = null;
        InputStream inputStreamObj = null;

        try {
            httpGetObj = new HttpGet(Web.getHostURL(context) + urlString);
            HttpClient httpClientObj = new DefaultHttpClient();
            HttpParams httpParamsObj = httpClientObj.getParams();
            // 接続のタイムアウト（単位：ms）
            HttpConnectionParams.setConnectionTimeout(httpParamsObj, 15000);
            // データ取得のタイムアウト（単位：ms）サーバ側のプログラム(phpとか)でsleepなどを使えばテストできる
            HttpConnectionParams.setSoTimeout(httpParamsObj, 20000);
            // user-agent
            httpParamsObj.setParameter("http.useragent", USERAGENT);
            // httpリクエスト（時間切れなどサーバへのリクエスト時に問題があると例外が発生する）
            HttpResponse httpResponseObj = httpClientObj.execute(httpGetObj);
            // httpレスポンスの400番台以降はエラーだから
            int statusCode = httpResponseObj.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                httpEntityObj = httpResponseObj.getEntity();
                // レスポンス本体を取得
                inputStreamObj = httpEntityObj.getContent();
            } else if (statusCode == 204) {
                throw new NoContentException();
            } else if (statusCode == 503) {
                throw new ServiceUnavailableException();
            } else {
                throw new ClientProtocolException();
            }
        } finally {

        }
        return inputStreamObj;
    }

    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (result == null) {
            if (postRunnable instanceof IWebPostRunnableAndErrors) {
                ((IWebPostRunnableAndErrors<Result>) postRunnable).onErrorRunnable();
            }
            return;
        }
        if (postRunnable != null) {
            postRunnable.onPostRunnable(result);
        }
    }

    /**
     * 処理後実行コードの再設定を行います
     *
     * @param postRunnable
     */
    public void resetPostRunnable(IWebPostRunnable<Result> postRunnable) {
        this.postRunnable = postRunnable;
    }

}
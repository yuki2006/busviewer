package jp.co.yuki2006.busmap.web;


/**
 * WebPortal(AsyncTask+ProgressDialog)の非同期処理終了後に実行するコードを定義するためのインターフェイス
 * postRunnableメソッドが終了するまで　ProgressDialogが出続けます。
 * （他にうまい方法があれば教えて下さい）
 *
 * @author yuki
 */
public interface IWebPostRunnableAndErrors<T> extends IWebPostRunnable<T> {
    void onErrorRunnable();
}

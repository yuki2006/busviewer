/**
 *
 */
package jp.co.yuki2006.busmap.bustimeline.fragment;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * @author yuki
 */
public class CustomAliasThanksDialogFragment extends DialogFragment {
    /*
     * (非 Javadoc)
     *
     * @see
     * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle arg0) {
        Builder dialog = new Builder(getActivity())
                .setTitle("新しい乗り場名を送信しました。")
                .setMessage("ご協力ありがとうございます。\n開発者側で確認後、全ユーザーに反映されます。")
                .setPositiveButton(android.R.string.ok, null);
        return dialog.create();
    }
}

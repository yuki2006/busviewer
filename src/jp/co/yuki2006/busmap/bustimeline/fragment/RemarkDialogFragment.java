/**
 *
 */
package jp.co.yuki2006.busmap.bustimeline.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.Etc;

/**
 * @author yuki
 */
public class RemarkDialogFragment extends DialogFragment {

    /**
     *
     */
    public static final String REMARK_KEY = "remark_key";

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        Builder builder = new Builder(getActivity());
        builder.setItems(getArguments().getStringArray(REMARK_KEY), null);
        builder.setTitle(R.string.remark);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        Etc.setNoSearchKeyCancel(dialog);
        return dialog;
    }
}

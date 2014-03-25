package jp.co.yuki2006.busmap.etc;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public abstract class SearchButtonSwitcher {
    private final ImageButton voiceSearchButton;
    private final ImageButton searchButton;

    public SearchButtonSwitcher(
            final Activity activity,
            final ViewSwitcher viewSwitcher,
            final TextView busStopTextView,
            boolean isDefaultNormalButton

    ) {

        voiceSearchButton = (ImageButton) viewSwitcher.getChildAt(0);

        voiceSearchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Etc.callVoiceSearch(activity);
            }
        });
        // trueなら普通のボタンがデフォルト
        if (isDefaultNormalButton) {
            viewSwitcher.showNext();
        }
        searchButton = (ImageButton) viewSwitcher.getChildAt(1);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchButton(busStopTextView.getText());
            }
        });
        busStopTextView.setOnKeyListener(
                new Etc.OnNextKeyDownListener() {
                    @Override
                    public void onNextKeyDown() {
                        onSearchButton(busStopTextView.getText());

                    }
                });
        busStopTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (viewSwitcher.getDisplayedChild() == 1 && s.length() == 0) {
                    viewSwitcher.showNext();
                } else if (viewSwitcher.getDisplayedChild() == 0 && s.length() > 0) {
                    viewSwitcher.showNext();
                }
            }
        });
    }

    public abstract void onSearchButton(CharSequence charSequence);
}

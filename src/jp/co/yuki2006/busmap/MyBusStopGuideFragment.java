/**
 *
 */
package jp.co.yuki2006.busmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.CirclePageIndicator;

public class MyBusStopGuideFragment extends SherlockFragment {
    static final int[] GUIDE_IMAGE_MAP = {
            R.drawable.guide_mybusstop_1,
            R.drawable.guide_mybusstop_5
            ,
            R.drawable.guide_mybusstop_2, R.drawable.guide_mybusstop_3, R.drawable.guide_mybusstop_4
    };
    static final String PAGE_INDEX = "PAGE_INDEX";

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setRetainInstance(true);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
     * , android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle arg2) {
        View view = inflater.inflate(R.layout.guide_fragment, group, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return GUIDE_IMAGE_MAP.length;
            }

            @Override
            public Fragment getItem(int i) {

                MyBusStopGuidePageFragment myBusStopGuidePageFragment = new MyBusStopGuidePageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(PAGE_INDEX, i);
                myBusStopGuidePageFragment.setArguments(bundle);
                return myBusStopGuidePageFragment;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        return view;
    }

    public static class MyBusStopGuidePageFragment extends SherlockFragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle arg2) {
            Bundle arguments = getArguments();
            ImageView imageView = new ImageView(getActivity());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT);
            imageView.setLayoutParams(params);
            int index = arguments.getInt(PAGE_INDEX);
            imageView.setImageResource(GUIDE_IMAGE_MAP[index]);
            return imageView;
        }
    }
}
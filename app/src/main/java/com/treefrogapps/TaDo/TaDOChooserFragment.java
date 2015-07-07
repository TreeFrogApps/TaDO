package com.treefrogapps.TaDo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treefrogapps.TaDo.slidingtabs.SlidingTabLayout;


public class TaDOChooserFragment extends Fragment {

    private View rootView;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    public TaDOChooserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tado_chooser, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseTabs();
    }

    private void initialiseTabs() {

        mViewPager = (ViewPager) rootView.findViewById(R.id.chooserFragmentViewPager);
        mViewPager.setAdapter(new TaDOChooserPagerAdapter(getActivity(), getFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.chooserFragmentSlidingTabsLayout);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.grey_light);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);
    }
}

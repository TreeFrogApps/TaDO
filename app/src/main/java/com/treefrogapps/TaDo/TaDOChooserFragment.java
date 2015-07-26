package com.treefrogapps.TaDo;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TaDOChooserFragment extends Fragment {

    private View rootView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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
        mViewPager.setAdapter(new TaDOChooserPagerAdapter(getActivity(), getChildFragmentManager()));
        // new design library tab layout
        mTabLayout = (TabLayout) rootView.findViewById(R.id.chooserFragmentSlidingTabsLayout);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        mTabLayout.setTabTextColors(getResources().getColor(R.color.grey_light), getResources().getColor(R.color.white));
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // cast pagerAdapter to the view pager to get access to created method to manually update fragment
                // get fragment 1 (queued items) and use onResume()
                Fragment fragment = ((TaDOChooserPagerAdapter) mViewPager.getAdapter()).getFragment(position);
                if (fragment != null && position == 0){
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}

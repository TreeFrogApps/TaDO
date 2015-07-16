package com.treefrogapps.TaDo;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

public class TaDOChooserPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitlesArray;
    private Context context;
    private FragmentManager fragmentManager;
    private HashMap<Integer, String> fragmentTags;


    public TaDOChooserPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        tabTitlesArray = context.getResources().getStringArray(R.array.chooser_framgent_tab_title_array);

        this.context = context;
        this.fragmentManager = fm;
        this.fragmentTags = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new TaDOChooserTabFragment1();
            case 1: return new TaDOChooserTabFragment2();
            default: return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitlesArray[position];
    }

    @Override
    public int getCount() {
        return tabTitlesArray.length;
    }

    // used for refreshing tabs afetr they have been loaded - uses onResume method
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);

        if (object instanceof Fragment){
            //record the fragment tag
            Fragment fragment = (Fragment) object;
            fragmentTags.put(position, fragment.getTag());
        }

        return object;
    }

    public Fragment getFragment(int position){

        if(fragmentTags.get(position) == null) return null;
        return fragmentManager.findFragmentByTag(fragmentTags.get(position));
    }
}

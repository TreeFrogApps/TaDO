package com.treefrogapps.TaDo;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TaDOChooserPagerAdapter extends FragmentStatePagerAdapter {

    String[] tabTitlesArray;
    Context context;


    public TaDOChooserPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        tabTitlesArray = context.getResources().getStringArray(R.array.chooser_framgent_tab_title_array);
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
}

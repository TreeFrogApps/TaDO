package com.treefrogapps.TaDo;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        return super.getCount();
        // make the adapter 1 smaller than count so the hint is not showing when spinner list is viewable
        // hint only to show as set selection when spinner list not viewable
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }
}

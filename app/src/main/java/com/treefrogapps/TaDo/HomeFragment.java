package com.treefrogapps.TaDo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private DBHelper dbHelper;
    private View rootView;
    private Spinner mTitlesSpinner;
    private List<String> mTitlesSpinnerItemList;

    private SpinnerAdapter mSpinnerAdapter;
    private ArrayList<TitlesListData> mTitlesArrayList;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DBHelper(getActivity());

        initialiseTitlesSpinner();
    }

    public void initialiseTitlesSpinner() {

        // initialise spinner
        mTitlesSpinner = (Spinner) rootView.findViewById(R.id.homeFragmentTitlesSpinner);
        // initialise Titles List Data
        mTitlesArrayList = new ArrayList<>();
        // Query database get all return TitleListData objects in ArrayList
        mTitlesArrayList = dbHelper.getTitles();

        // initialise spinnerArrayList
        mTitlesSpinnerItemList = new ArrayList<>();

        // add the spinnerItem hint
        SpinnerItem spinnerItem =
                new SpinnerItem(getResources().getString(R.string.fragment_home_pick_title));
        mTitlesSpinnerItemList.add(spinnerItem.getSpinnerItem());

        for (int i = 0; i < mTitlesArrayList.size(); i++) {

            spinnerItem = new SpinnerItem(mTitlesArrayList.get(i).getTitle());
            mTitlesSpinnerItemList.add(spinnerItem.getSpinnerItem());
            Log.v("Spinner Item From DB = ", spinnerItem.getSpinnerItem());
        }

        Log.v("Total = ", String.valueOf(mTitlesSpinnerItemList.size()));
        // initialise SpinnerAdapter using mTitlesSpinnerItemList as Data source
        mSpinnerAdapter = new SpinnerAdapter(getActivity(),
                R.layout.spinner_titles_view, R.id.spinner_list_item, mTitlesSpinnerItemList);
        mTitlesSpinner.setAdapter(mSpinnerAdapter);
    }


}

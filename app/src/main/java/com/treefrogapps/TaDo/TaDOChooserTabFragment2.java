package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TaDOChooserTabFragment2 extends Fragment {

    private View mRootView;
    private DBHelper dbHelper;
    private SharedPreferences mSharedPreferences;
    private RecyclerView mTaDOChooserFragment2RecyclerView;
    public static TaDOChooserFragmentRecyclerAdapter mTaDOChooserFragmentRecyclerAdapter;
    private ArrayList<QueuedItemListData> mQueuedItemListDataArrayList;

    public TaDOChooserTabFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_tado_chooser_tab_fragment2, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DBHelper(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);
        mQueuedItemListDataArrayList = new ArrayList<>();

        initialiseRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateRecyclerViewArrayList();
    }

    private void initialiseRecyclerView() {

        mTaDOChooserFragment2RecyclerView = (RecyclerView) mRootView.findViewById(R.id.taDOChooserFragment2RecyclerView);
        mTaDOChooserFragment2RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTaDOChooserFragment2RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void populateRecyclerViewArrayList() {

        mQueuedItemListDataArrayList = dbHelper.getQueuedItems();

        // check if user has manually updated item to be done.
        for (int i = 0; i < mQueuedItemListDataArrayList.size(); i++){

            ItemsListData itemsListData = dbHelper.getSingleItem(mQueuedItemListDataArrayList.get(i).getItemId());
            if (itemsListData.getItemDone().equals("Y")){
                mQueuedItemListDataArrayList.remove(i);
            }
        }
        mTaDOChooserFragmentRecyclerAdapter = new TaDOChooserFragmentRecyclerAdapter(getActivity(), mQueuedItemListDataArrayList);
        mTaDOChooserFragment2RecyclerView.setAdapter(mTaDOChooserFragmentRecyclerAdapter);
    }


}

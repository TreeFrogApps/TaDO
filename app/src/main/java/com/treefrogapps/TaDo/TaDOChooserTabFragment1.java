package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;


public class TaDOChooserTabFragment1 extends Fragment implements View.OnClickListener, TaDOChooserFragment1Dialog.OnItemChosenCallback {

    private View mRootView;
    private DBHelper dbHelper;
    private SharedPreferences mSharedPreferences;
    private RecyclerView mTaDOChooserFragment1RecyclerView;
    public static TaDOChooserFragmentRecyclerAdapter mTaDOChooserFragmentRecyclerAdapter;
    private FloatingActionButton mTaDoChooserFragment1FAB;
    private ArrayList<QueuedItemListData> mQueuedItemListDataArrayList;
    private TaDOChooserFragment1Dialog mTaDOChooserFragment1Dialog;

    public TaDOChooserTabFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_tado_chooser_tab_fragment1, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DBHelper(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);
        mQueuedItemListDataArrayList = new ArrayList<>();

        initialiseRecyclerView();
        initialiseFAB();
    }

    @Override
    public void onResume() {
        super.onResume();
        // always called in the lifecycle stack
        populateRecyclerViewArrayList();

        // reset callback on screen rotation for dialog
        mTaDOChooserFragment1Dialog = (TaDOChooserFragment1Dialog) getFragmentManager().findFragmentByTag("Dialog04");
        if (mTaDOChooserFragment1Dialog != null) {
            mTaDOChooserFragment1Dialog.setCallBack(TaDOChooserTabFragment1.this);
        }
    }

    private void initialiseRecyclerView() {

        mTaDOChooserFragment1RecyclerView = (RecyclerView) mRootView.findViewById(R.id.taDOChooserFragment1RecyclerView);
        mTaDOChooserFragment1RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTaDOChooserFragment1RecyclerView.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(mTaDOChooserFragment1RecyclerView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()){

            case R.id.taDOChooserFragment1RecyclerView :
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.menu_main, menu);

                // TODO - create context menu ?
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemIdPosition = menuInfo.position;
        return super.onContextItemSelected(item);
    }

    private void populateRecyclerViewArrayList() {

        // any previously queued items that have bee marked as done will be removed from queued items (updated when swiped to mark as done)
        mQueuedItemListDataArrayList = dbHelper.getQueuedItems();
        mTaDOChooserFragmentRecyclerAdapter = new TaDOChooserFragmentRecyclerAdapter(getActivity(), mQueuedItemListDataArrayList);
        mTaDOChooserFragment1RecyclerView.setAdapter(mTaDOChooserFragmentRecyclerAdapter);
    }

    private void initialiseFAB() {

        mTaDoChooserFragment1FAB = (FloatingActionButton) mRootView.findViewById(R.id.taDoChooserFragment1FAB);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaDoChooserFragment1FAB.setAnimation(Animations.floatingActionButtonAnim(getActivity()));
                mTaDoChooserFragment1FAB.setVisibility(View.VISIBLE);
                mTaDoChooserFragment1FAB.setOnClickListener(TaDOChooserTabFragment1.this);
            }
        }, 200);
     }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.taDoChooserFragment1FAB :
                chooseItem();

        }
    }

    private void chooseItem() {

        mTaDOChooserFragment1Dialog = new TaDOChooserFragment1Dialog();
        mTaDOChooserFragment1Dialog.mOnItemChosenCallback = TaDOChooserTabFragment1.this;
        mTaDOChooserFragment1Dialog.show(getFragmentManager(), "Dialog04");
    }


    @Override
    public void itemChosenCallBack() {
        populateRecyclerViewArrayList();
    }
}

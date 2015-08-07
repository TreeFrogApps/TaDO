package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TaDOChooserTabFragment1 extends Fragment implements View.OnClickListener, TaDOChooserFragment1Dialog.OnItemChosenCallback {

    private View mRootView;
    private DBHelper dbHelper;
    private SharedPreferences mSharedPreferences;
    public static RecyclerView mTaDOChooserFragment1RecyclerView;
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

        Log.e("onResume - f1", "CALLED");
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

    // recycler context menu - on long click listener and position setter and getter added to recycle adapter
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {

            case R.id.taDOChooserFragment1RecyclerView:
                menu.setHeaderTitle(getActivity().getString(R.string.tado_chooser_fragment_context_menu_header));
                menu.setHeaderIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_nav_sync, null));
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.fragment_tado_chooser_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int adapterPosition;
        String itemId;

        switch (item.getItemId()) {
            case R.id.chooserFragmentContextMenuSetAsCurrent:
                // get adapter position
                adapterPosition = mTaDOChooserFragmentRecyclerAdapter.getPosition();
                itemId = mQueuedItemListDataArrayList.get(adapterPosition).getItemId();
                // get current item and delete
                CurrentItemListData currentItemListData = dbHelper.getCurrentItem();
                if (currentItemListData.getItemId() != null) dbHelper.deleteCurrentItem(currentItemListData);
                // add as current item
                addAsCurrentItem(itemId);
                deleteQueuedItem(adapterPosition, itemId);
                // remove any shared prefs timer object
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove(TaDOChooserTabFragment2.TIMER_OBJECT).apply();
                TaDOChooserTabFragment2.isActive = false;
                TaDOChooserTabFragment2.isPaused = true;
                break;

            case R.id.chooserFragmentContextMenuRemove:
                // get adapter position
                adapterPosition = mTaDOChooserFragmentRecyclerAdapter.getPosition();
                itemId = mQueuedItemListDataArrayList.get(adapterPosition).getItemId();
                deleteQueuedItem(adapterPosition, itemId);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void addAsCurrentItem(String itemId) {

        CurrentItemListData currentItemListData = new CurrentItemListData();
        currentItemListData.setItemId(itemId);
        dbHelper.insertIntoCurrentItemTable(currentItemListData);
    }

    public void deleteQueuedItem(int adapterPosition, String itemId) {

        QueuedItemListData queuedItemListData = new QueuedItemListData();
        queuedItemListData.setItemId(itemId);
        dbHelper.deleteQueuedItem(queuedItemListData);

        mQueuedItemListDataArrayList.remove(adapterPosition);
        mTaDOChooserFragmentRecyclerAdapter.notifyItemRemoved(adapterPosition);
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

        switch (v.getId()) {
            case R.id.taDoChooserFragment1FAB:
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

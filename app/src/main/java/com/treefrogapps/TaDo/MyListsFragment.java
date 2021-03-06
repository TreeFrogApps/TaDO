package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MyListsFragment extends Fragment implements View.OnClickListener, MyListsFragmentDialog.onDialogDonePressedCallBack {

    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private View rootView;

    private FloatingActionButton mFAB;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private TitlesRecyclerAdapter mTitlesRecyclerAdapter;
    private ArrayList<TitlesListData> mTitlesListDataArrayList;
    private RestoreRecyclerPosition mRestoreRecyclerPosition;

    private LinearLayout mHomeFragmentSplashScreen;

    private MyListsFragmentDialog myListsFragmentDialog;

    public MyListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_lists, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);
        checkSplashScreenVisibility();
        dbHelper = new DBHelper(getActivity());

        mFAB = (FloatingActionButton) rootView.findViewById(R.id.homeFragmentFAB);
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFAB.setVisibility(View.VISIBLE);
                mFAB.startAnimation(Animations.floatingActionButtonAnim(getActivity()));
                mFAB.setOnClickListener(MyListsFragment.this);
            }
        }, 200);

        initialiseRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_my_lists_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.homeFragmentSearch) {

            // TODO - database search
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkSplashScreenVisibility() {

        int splashScreenVisible = sharedPreferences.getInt(Constants.SPLASH_SCREEN_VISIBILITY, 1);
        mHomeFragmentSplashScreen = (LinearLayout) rootView.findViewById(R.id.homeFragmentSplashScreen);

        if (splashScreenVisible == 0) {
            mHomeFragmentSplashScreen.setVisibility(View.GONE);
        }
    }

    public void initialiseRecyclerView() {

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.homeFragmentRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mTitlesListDataArrayList = dbHelper.getTitles();
        mTitlesRecyclerAdapter = new TitlesRecyclerAdapter(getActivity(), mTitlesListDataArrayList);
        mRecyclerView.setAdapter(mTitlesRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getMovementFlags(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    deleteTitle(viewHolder.getLayoutPosition());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void deleteTitle(int positionToDelete) {

        String titleId = mTitlesListDataArrayList.get(positionToDelete).getTitle_id();

        dbHelper.deleteTitle(titleId);
        mTitlesRecyclerAdapter.notifyItemRemoved(positionToDelete);
        mTitlesListDataArrayList.remove(positionToDelete);
    }

    public void populateRecyclerView() {

        mTitlesListDataArrayList.clear();
        mTitlesListDataArrayList = dbHelper.getTitles();
        mTitlesRecyclerAdapter = new TitlesRecyclerAdapter(getActivity(), mTitlesListDataArrayList);
        mRecyclerView.setAdapter(mTitlesRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mFAB.getId()) {


            if (mHomeFragmentSplashScreen.getVisibility() == View.VISIBLE) {
                mHomeFragmentSplashScreen.startAnimation(Animations.moveOut(getActivity()));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeSplashScreen();
                        // show dialog fragment
                        // custom dialog fragment initialised
                        myListsFragmentDialog = new MyListsFragmentDialog();
                        myListsFragmentDialog.mOnDialogDonePressedCallBack = MyListsFragment.this;
                        myListsFragmentDialog.show(getFragmentManager(), "Dialog01");
                    }
                }, 400);
            } else {
                // show dialog fragment
                // custom dialog fragment initialised
                myListsFragmentDialog = new MyListsFragmentDialog();
                myListsFragmentDialog.mOnDialogDonePressedCallBack = MyListsFragment.this;
                myListsFragmentDialog.show(getFragmentManager(), "Dialog01");
            }
        }
    }

    // dialog fragment interface callback
    @Override
    public void updateRecyclerViewCallBack() {

        if (mTitlesListDataArrayList.size() > 1){
            // use helper class to get current position of recyclerView
            mRestoreRecyclerPosition = new RestoreRecyclerPosition(mRecyclerView, mLinearLayoutManager);
            RestoreRecyclerPosition.RecyclerPositionValues recyclerPositionValues = mRestoreRecyclerPosition.getCurrentPosition();
            populateRecyclerView();
            mRestoreRecyclerPosition.setCurrentPosition(recyclerPositionValues);
        } else {
            populateRecyclerView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // required to set the dialog callback listener after screen rotation it is lost
        myListsFragmentDialog = (MyListsFragmentDialog) getFragmentManager().findFragmentByTag("Dialog");
        if(myListsFragmentDialog  != null){
            myListsFragmentDialog.setCallBack(MyListsFragment.this);
        }
    }

    public void removeSplashScreen() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.SPLASH_SCREEN_VISIBILITY, Constants.SPLASH_SCREEN_OFF);
        editor.apply();
        mHomeFragmentSplashScreen.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("CALLED", "OnActivity Result");

        // if requestCode matches from CreateItemsActivity
        if (requestCode == Constants.NEW_ITEMS_REQUEST_CODE) {

            Log.e("RESULT CODE", String.valueOf(requestCode));
            populateRecyclerView();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

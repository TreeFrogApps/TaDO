package com.treefrogapps.TaDo;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MyListsFragment extends Fragment implements View.OnClickListener {

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

            // TODO
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

            final Intent intent = new Intent(getActivity(), CreateItemsActivity.class);

            if (mHomeFragmentSplashScreen.getVisibility() == View.VISIBLE) {
                mHomeFragmentSplashScreen.startAnimation(Animations.moveOut(getActivity()));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeSplashScreen();
                        newTitleDialog();
                    }
                }, 400);
            } else {
                newTitleDialog();
            }
        }
    }

    public void removeSplashScreen() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.SPLASH_SCREEN_VISIBILITY, Constants.SPLASH_SCREEN_OFF);
        editor.apply();
        mHomeFragmentSplashScreen.setVisibility(View.GONE);
    }

    public void newTitleDialog(){

        final Dialog titleDialog = new Dialog(getActivity());
                titleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                titleDialog.setContentView(R.layout.fragment_my_lists_dialog);
                titleDialog.setCancelable(false);

        final EditText dialogTitleEditText = (EditText) titleDialog.findViewById(R.id.dialogTitleEditText);

        Button dialogTitleCancelButton = (Button) titleDialog.findViewById(R.id.dialogTitleCancelButton);
        dialogTitleCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleDialog.dismiss();
            }
        });

        Button dialogTitleDoneButton = (Button) titleDialog.findViewById(R.id.dialogTitleDoneButton);
        dialogTitleDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleName = dialogTitleEditText.getText().toString().trim();

                TitlesListData titlesListData = new TitlesListData();
                String checkIfTitleExists = dbHelper.checkTitleNameExists(titleName);

                // return null means no title already exists
                if (checkIfTitleExists == null && !titleName.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                    String date = sdf.format(new Date());

                    titlesListData.setTitle(titleName);
                    titlesListData.setDateTime(date);
                    dbHelper.insertIntoTitlesTable(titlesListData);

                    // use helper class to get current position of recyclerView
                    mRestoreRecyclerPosition = new RestoreRecyclerPosition(mRecyclerView, mLinearLayoutManager);
                    RestoreRecyclerPosition.RecyclerPositionValues recyclerPositionValues = mRestoreRecyclerPosition.getCurrentPosition();

                    populateRecyclerView();
                    mRestoreRecyclerPosition.setCurrentPosition(recyclerPositionValues);
                    titleDialog.dismiss();

                } else {
                    CustomToasts.Toast(getActivity(), "Title already exists");
                }
            }
        });

        titleDialog.show();
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


    /*
    public void getTotalItemsAndTime() {

        String[] totals = new String[3];

        // get total items from array list size
        totals[0] = String.valueOf(mItemsArrayList.size());

        ArrayList<ItemsListData> itemsNotDoneArrayList =
                dbHelper.getItemsForTitleNotDone(mTitlesSpinner.getSelectedItem().toString());

        // get total items from array list size of NOT DONE items
        totals[1] = String.valueOf(itemsNotDoneArrayList.size());


        int hours = 0;
        int mins = 0;

        // get total hours mins - iterate through array list durations
        // adding total hours and total mins
        for (int i = 0; i < itemsNotDoneArrayList.size(); i++) {

            String itemDuration = itemsNotDoneArrayList.get(i).getDuration().substring(0,
                    (itemsNotDoneArrayList.get(i).getDuration().length() - 3));

            String[] durationArray = itemDuration.split(":");
            hours += Integer.valueOf(durationArray[0]);
            mins += Integer.valueOf(durationArray[1]);
        }

        int totalMins = (hours * 60) + mins;
        int finalHours = totalMins / 60;
        // modulus to get remainder minutes
        int finalMins = totalMins % 60;

        totals[2] = String.valueOf(finalHours) + "hrs " + String.valueOf(finalMins) + "mins";

        mHomeFragmentTotalItemsTextView.setText(totals[0]);
        mHomeFragmentTotalItemsTadoTextView.setText(totals[1]);
        mHomeFragmentTotalTimeLeftTextView.setText(totals[2]);
        mHomeFragmentListSpecCardView.setVisibility(View.VISIBLE);
        mHomeFragmentListSpecCardView.startAnimation(Animations.alphaMoveInAnim(getActivity()));
        mHomeFragmentItemsTextView.startAnimation(Animations.alphaFadeOutAndIn(getActivity()));
    }

    */

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}

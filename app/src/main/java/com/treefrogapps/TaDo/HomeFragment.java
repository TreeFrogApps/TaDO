package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private View rootView;

    private Button mRemoveListButton;
    private FloatingActionButton mFAB;
    private AlertDialog mAlertDialog;

    private Spinner mTitlesSpinner;
    private List<String> mTitlesSpinnerItemList; // just for holding title names
    private SpinnerAdapter mSpinnerAdapter;
    private ArrayList<TitlesListData> mTitlesArrayList;

    private RecyclerView mRecyclerView;
    private ItemRecyclerAdapter mRecyclerAdapter;
    private ArrayList<ItemsListData> mItemsArrayList;
    private LinearLayout mCreateListButtonLayout;

    private CardView mHomeFragmentListSpecCardView;
    private TextView mHomeFragmentTotalItemsTextView;
    private TextView mHomeFragmentTotalTimeTextView;
    private TextView mHomeFragmentItemsTextView;

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

        checkSplashScreenVisibility();

        dbHelper = new DBHelper(getActivity());

        mFAB = (FloatingActionButton) rootView.findViewById(R.id.homeFragmentFAB);
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFAB.setVisibility(View.VISIBLE);
                mFAB.startAnimation(Animations.floatingActionButtonAnim(getActivity()));
                mFAB.setOnClickListener(HomeFragment.this);
            }
        }, 200);

        mRemoveListButton = (Button) rootView.findViewById(R.id.homeFragmentRemoveListButton);
        mRemoveListButton.setOnClickListener(HomeFragment.this);

        initialiseRecyclerView();
        initialiseTitlesSpinner();

        mCreateListButtonLayout = (LinearLayout) rootView.findViewById(R.id.createListButtonLayout);
        mHomeFragmentListSpecCardView = (CardView) rootView.findViewById(R.id.homeFragmentListSpecCardView);
        mHomeFragmentTotalItemsTextView = (TextView) rootView.findViewById(R.id.homeFragmentTotalItemsTextView);
        mHomeFragmentTotalTimeTextView = (TextView) rootView.findViewById(R.id.homeFragmentTotalTimeTextView);
        mHomeFragmentItemsTextView = (TextView) rootView.findViewById(R.id.homeFragmentItemsTextView);

        if (mTitlesSpinner.getSelectedItemPosition() != 0){
            getTotalItemsAndTime();
        }
    }

    public void checkSplashScreenVisibility(){

        sharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);
        int splashScreenVisible = sharedPreferences.getInt(Constants.SPLASH_SCREEN_VISIBILITY, 1);

        if (splashScreenVisible == 1){


        }

    }

    public void initialiseRecyclerView(){

        mItemsArrayList = new ArrayList<>();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.homeFragmentRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ItemRecyclerAdapter(getActivity(), mItemsArrayList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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

        // add the spinnerTitle hint
        SpinnerTitle spinnerTitle =
                new SpinnerTitle(getResources().getString(R.string.fragment_home_pick_title));
        mTitlesSpinnerItemList.add(spinnerTitle.getSpinnerItem());

        for (int i = 0; i < mTitlesArrayList.size(); i++) {

            spinnerTitle = new SpinnerTitle(mTitlesArrayList.get(i).getTitle());
            mTitlesSpinnerItemList.add(spinnerTitle.getSpinnerItem());
            Log.v("Spinner Item From DB = ", spinnerTitle.getSpinnerItem());
        }

        Log.v("Total = ", String.valueOf(mTitlesSpinnerItemList.size()));
        // initialise SpinnerAdapter using mTitlesSpinnerItemList as Data source
        mSpinnerAdapter = new SpinnerAdapter(getActivity(),
                R.layout.spinner_titles_view, R.id.spinner_list_item, mTitlesSpinnerItemList);
        mTitlesSpinner.setAdapter(mSpinnerAdapter);

        mTitlesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0){
                    Log.e("Spinner Position", String.valueOf(position));
                    String listTitle = mTitlesSpinner.getItemAtPosition(position).toString();

                    if (mCreateListButtonLayout.getVisibility() == View.GONE){
                        mCreateListButtonLayout.setVisibility(View.VISIBLE);
                        mRemoveListButton.startAnimation(Animations.alphaMoveInAnim(getActivity()));
                    }
                    populateRecyclerView(listTitle);
                    getTotalItemsAndTime();

                } else {
                    // if spinner does not have selected item animate and hide view
                    mHomeFragmentListSpecCardView.startAnimation(Animations.alphaMoveOutAnim(getActivity()));
                    mRemoveListButton.startAnimation(Animations.alphaMoveOutAnim(getActivity()));
                    mHomeFragmentItemsTextView.startAnimation(Animations.alphaFadeOutAndIn(getActivity()));
                    mFAB.startAnimation(Animations.alphaFadeOutAndIn(getActivity()));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHomeFragmentListSpecCardView.setVisibility(View.GONE);
                            mCreateListButtonLayout.setVisibility(View.GONE);
                            mItemsArrayList.clear();
                            mRecyclerAdapter = new ItemRecyclerAdapter(getActivity(), mItemsArrayList);
                            mRecyclerView.setAdapter(mRecyclerAdapter);
                        }
                    }, 350);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // method to populate recycler view after any updates
    public void populateRecyclerView(String listTitle){

        mItemsArrayList.clear();
        mItemsArrayList = dbHelper.getItemsForTitle(listTitle);

        mRecyclerAdapter = new ItemRecyclerAdapter(getActivity(), mItemsArrayList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    // method to populate SpinnerList after ANY updates
    public void populateTitlesSpinner(){

        // clear current array lists
        mTitlesSpinnerItemList.clear();
        mTitlesArrayList.clear();

        // query database to return updated titles
        mTitlesArrayList = dbHelper.getTitles();

        // add the spinnerTitle hint
        SpinnerTitle spinnerTitle =
                new SpinnerTitle(getResources().getString(R.string.fragment_home_pick_title));
        mTitlesSpinnerItemList.add(spinnerTitle.getSpinnerItem());

        for (int i = 0; i < mTitlesArrayList.size(); i++) {

            spinnerTitle = new SpinnerTitle(mTitlesArrayList.get(i).getTitle());
            mTitlesSpinnerItemList.add(spinnerTitle.getSpinnerItem());
            Log.v("Title From DB = ", spinnerTitle.getSpinnerItem());
        }

        Log.v("Total = ", String.valueOf(mTitlesSpinnerItemList.size()));
        mSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mRemoveListButton.getId()){

            if (mTitlesSpinner.getSelectedItemPosition() != 0){

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle(getActivity().getResources().getString(R.string.dialog_remove_list));
                alertBuilder.setCancelable(true);
                alertBuilder.setMessage(getActivity().getResources().getString(R.string.dialog_message));
                alertBuilder.setPositiveButton(getActivity().getResources()
                        .getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // delete title - when deleting title all title items from items table
                        // should be deleted because of ON DELETE CASCADE
                        String title = mTitlesSpinner.getSelectedItem().toString();
                        dbHelper.deleteTitle(title);

                        // clear the recycler view
                        mItemsArrayList.clear();
                        mRecyclerAdapter.notifyDataSetChanged();

                        // populate spinner with new list with title gone
                        populateTitlesSpinner();
                    }
                });

                alertBuilder.setNegativeButton(getActivity().getResources()
                        .getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                });

                mAlertDialog = alertBuilder.create();
                mAlertDialog.show();

            }

        } else if (v.getId() == mFAB.getId()){

            Intent intent = new Intent(getActivity(),CreateListActivity.class);

            if (mTitlesSpinner.getSelectedItemPosition() != 0){
                // pass the spinner Title text to next activity to so when returned
                // it will repopulate with updated list, and also show last list before creating new one
                intent.putExtra("spinnerPosition", mTitlesSpinner.getSelectedItem().toString());
            }
            startActivityForResult(intent, Constants.NEW_LIST_RESULT_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if requestCode matches from CreateListActivity
        if (requestCode == Constants.NEW_LIST_RESULT_CODE){

            Log.e("RESULT CODE", String.valueOf(requestCode));

            populateTitlesSpinner();

            // check if there was a string extra passed and populate
            if (data.getStringExtra("spinnerPosition") != null){
                mTitlesSpinner.setSelection(getSpinnerIndex(data.getStringExtra("spinnerPosition")));
                populateRecyclerView(mTitlesSpinner.getSelectedItem().toString());
            } else {
                mTitlesSpinner.setSelection(0);
                mItemsArrayList.clear();
                mRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getSpinnerIndex(String spinnerPosition){

        int index = 0;
        for (int i = 0; i < mTitlesSpinner.getCount(); i++){
            if (mTitlesSpinner.getItemAtPosition(i).equals(spinnerPosition)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void getTotalItemsAndTime(){

        String[] totals = new String[2];

        // get total items from array list size
        totals[0] = String.valueOf(mItemsArrayList.size());

        int hours = 0;
        int mins = 0;

        // get total hours mins - iterate through array list durations
        // adding total hours and total mins
        for (int i = 0; i < mItemsArrayList.size(); i++){

            String itemDuration = mItemsArrayList.get(i).getDuration().substring(0,
                    (mItemsArrayList.get(i).getDuration().length() - 3));

            String[] durationArray = itemDuration.split(":");
            hours += Integer.valueOf(durationArray[0]);
            mins += Integer.valueOf(durationArray[1]);
        }

        int totalMins = (hours * 60) + mins;
        int finalHours = totalMins / 60;
        // modulus to get remainder minutes
        int finalMins = totalMins % 60;

        totals[1] = String.valueOf(finalHours) + "hrs " + String.valueOf(finalMins) + "mins";

        mHomeFragmentTotalItemsTextView.setText(totals[0]);
        mHomeFragmentTotalTimeTextView.setText(totals[1]);
        mHomeFragmentListSpecCardView.setVisibility(View.VISIBLE);
        mHomeFragmentListSpecCardView.startAnimation(Animations.alphaMoveInAnim(getActivity()));
        mHomeFragmentItemsTextView.startAnimation(Animations.alphaFadeOutAndIn(getActivity()));
    }
}

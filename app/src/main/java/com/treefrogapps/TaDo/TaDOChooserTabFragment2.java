package com.treefrogapps.TaDo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;


public class TaDOChooserTabFragment2 extends Fragment implements View.OnClickListener {

    private LinearLayout mLinearLayout;

    private TaDOChooserTimerObject mTaDOChooserTimerObject;
    private static final String TIMER_OBJECT = "com.treefrogapps.TaDo.TIMER_OBJECT";
    private boolean isActive;
    private boolean isPaused;

    // Database Item Inputs
    private TextView mTaDOChooserFragment2ListTextView;
    private TextView mTaDOChooserFragment2ItemTextView;
    private TextView mTaDOChooserFragment2ItemDetailTextView;
    private TextView mTaDOChooserFragment2HoursTextView;
    private TextView mTaDOChooserFragment2MinsTextView;
    private TextView mTaDOChooserFragment2PriorityTextView;
    private CurrentItemListData mCurrentItemListData;
    private ItemsListData mItemsListData;

    private View mRootView;
    private DBHelper dbHelper;
    private SharedPreferences mSharedPreferences;

    private ImageButton mTaDOChooserFragmentMenuButton;
    private TextView mTaDOChooserFragmentTimerTextView;
    private Button mTaDOChooserFragment2TimerButton;

    private ImageView mTaDOChooserFragmentProgressImageView;
    private CountDownTimer mCountDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_tado_chooser_tab_fragment2, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLinearLayout = (LinearLayout) mRootView.findViewById(R.id.taDOChooserFragmentMainLayout);
        dbHelper = new DBHelper(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);

        initialiseInputs();

        if (savedInstanceState != null) {
            // coming from screen rotation, or configuration change
            if (retrieveTimerObject()) {
                isActive = mTaDOChooserTimerObject.isActive();
                isPaused = mTaDOChooserTimerObject.isPaused();
                mCurrentItemListData = mTaDOChooserTimerObject.getCurrentItemListData();
                mItemsListData = dbHelper.getSingleItem(mCurrentItemListData.getItemId());
                populateInputs();
                startCountDown(isActive, isPaused);
            }
        } else {
            //
            checkCurrentItemExists();
        }
    }

    private void initialiseInputs() {

        mTaDOChooserFragment2ListTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ListTextView);
        mTaDOChooserFragment2ItemTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ItemTextView);
        mTaDOChooserFragment2ItemDetailTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ItemDetailTextView);
        mTaDOChooserFragment2HoursTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2HoursTextView);
        mTaDOChooserFragment2MinsTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2MinsTextView);
        mTaDOChooserFragment2PriorityTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2PriorityTextView);

        mTaDOChooserFragment2TimerButton = (Button) mRootView.findViewById(R.id.taDOChooserFragment2TimerButton);
        mTaDOChooserFragment2TimerButton.setOnClickListener(this);
        registerForContextMenu(mTaDOChooserFragment2TimerButton);

        mTaDOChooserFragmentMenuButton = (ImageButton) mRootView.findViewById(R.id.taDOChooserFragment2MenuButton);
        mTaDOChooserFragmentMenuButton.setOnClickListener(this);
        mTaDOChooserFragmentTimerTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragmentTimerTextView);

        mTaDOChooserFragmentProgressImageView = (ImageView) mRootView.findViewById(R.id.taDOChooserFragmentProgressImageView);

    }

    public void checkCurrentItemExists() {
        // retrieve current item and check against ItemsListDataId that
        // it hasn't been updated to complete
        mCurrentItemListData = dbHelper.getCurrentItem();
        if (mCurrentItemListData.getItemId() != null) {
            mItemsListData = dbHelper.getSingleItem(mCurrentItemListData.getItemId());

            if (mItemsListData.getItemDone().equals("N")) {
                // check if new current item, or saved Current Item as TimerObject
                if (retrieveTimerObject()) {
                    isActive = mTaDOChooserTimerObject.isActive();
                    isPaused = mTaDOChooserTimerObject.isPaused();
                } else {
                    isActive = false;
                    isPaused = true;
                }
                populateInputs();
                startCountDown(isActive, isPaused);

            } else {
                CustomToasts.Toast(getActivity(), "Current task marked as done");
                dbHelper.deleteCurrentItem(mCurrentItemListData);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove(TIMER_OBJECT).apply();
            }
        }
    }

    public void populateInputs(){

        mTaDOChooserFragment2ListTextView.setText(dbHelper.getTitle(mItemsListData.getTitleId()));
        mTaDOChooserFragment2ItemTextView.setText(mItemsListData.getItem());
        mTaDOChooserFragment2ItemDetailTextView.setText(mItemsListData.getItemDetail());

        String duration = mItemsListData.getDuration();
        String[] timeArray = duration.split(":");
        mTaDOChooserFragment2HoursTextView.setText(timeArray[0]);
        mTaDOChooserFragment2MinsTextView.setText(timeArray[1]);
        mTaDOChooserFragment2PriorityTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2PriorityTextView);

        switch (mItemsListData.getItemPriority()) {
            case "L":
                mTaDOChooserFragment2PriorityTextView.setBackgroundResource(R.drawable.priority_circle_low_40dp);
                break;
            case "M":
                mTaDOChooserFragment2PriorityTextView.setBackgroundResource(R.drawable.priority_circle_med_40dp);
                break;
            case "H":
                mTaDOChooserFragment2PriorityTextView.setBackgroundResource(R.drawable.priority_circle_high_40dp);
                break;
        }
        mTaDOChooserFragmentTimerTextView.setText(mItemsListData.getDuration());
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public void startCountDown(boolean isActive, boolean isPaused){

        if (isActive && !isPaused){

            // todo - startcountdown - has started and not paused (coming from outside app to inside)
            // cancel service here if started (only start service if isActive and !isPaused) in onPause
            // if service is called start notification and display time up and handle options from 3 dot popup menu

        } else if (!isActive && isPaused) {

            // default starting value for a not started item

        } else if (isActive) {

            // todo  - must have been exited with it paused, because it was started
            // has started, but is paused
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public int taskTimeInMilliseconds(String taskTime) {
        String[] TaskTimeArray = taskTime.split(":");
        int IntHours = Integer.parseInt(TaskTimeArray[0]);
        int IntMinutes = Integer.parseInt(TaskTimeArray[1]);
        int IntSeconds = Integer.parseInt(TaskTimeArray[2]);
        int TotalSeconds = (((IntHours * 60) + IntMinutes) * 60) + IntSeconds;
        return TotalSeconds * 1000;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveTimerObject();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mCountDownTimer != null) mCountDownTimer.cancel();

        saveTimerObject();
    }

    private void saveTimerObject() {

        if (isActive && mLinearLayout.getVisibility() == View.VISIBLE) {

            CurrentItemListData currentItemListData = dbHelper.getCurrentItem();
            ItemsListData itemsListData = dbHelper.getSingleItem(currentItemListData.getItemId());

            mTaDOChooserTimerObject =
                    new TaDOChooserTimerObject(currentItemListData,
                            (long) taskTimeInMilliseconds(itemsListData.getDuration()),
                            (long) taskTimeInMilliseconds(mTaDOChooserFragmentTimerTextView.getText().toString()),
                            isActive, isPaused);

            Gson gson = new Gson();
            String timerObjectToJson = gson.toJson(mTaDOChooserTimerObject);
            Log.i("Object to Json", timerObjectToJson);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(TIMER_OBJECT, timerObjectToJson);
            editor.apply();
        }
    }

    private boolean retrieveTimerObject() {

        String timerObjectJson = mSharedPreferences.getString(TIMER_OBJECT, "");
        if (!timerObjectJson.equals("")) {
            Gson gson = new Gson();
            mTaDOChooserTimerObject = gson.fromJson(timerObjectJson, TaDOChooserTimerObject.class);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.taDOChooserFragment2MenuButton:
                // TODO - inflatePopMenu(v);
                break;

            case R.id.taDOChooserFragment2TimerButton:
                // handle pausing and has started
                // changes has started to true (and stays true from now on)
                isActive = true;
                // toggles paused state - handled in startCountDown
                isPaused = !isPaused;
                startCountDown(isActive, isPaused);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {

            case R.id.taDOChooserFragment2TimerButton:
                menu.setHeaderTitle(getActivity().getString(R.string.tado_chooser_fragment_timer_menu_header));
                menu.setHeaderIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_nav_sync, null));
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.fragment_tado_chooser_timer_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.chooserFragmentTimerMenuReset:
                // TODO - reset timer
                break;

            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}

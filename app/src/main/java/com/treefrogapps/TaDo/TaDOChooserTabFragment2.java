package com.treefrogapps.TaDo;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class TaDOChooserTabFragment2 extends Fragment implements View.OnClickListener,
        View.OnLongClickListener {

    private LinearLayout mLinearLayout;

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
    private Button mTaDOChooserFragment1TimerButton;

    // CountDown Clock
    private ProgressBar mTaDOChooserFragment1Timer;
    private ObjectAnimator mAnimation;

    private CountDownTimer mCountDownTimer;
    private String[] mTaskTimeArray;
    private int mIntHours;
    private int mIntMinutes;
    private int mIntSeconds;
    private int mTotalSeconds;
    private int mTotalMilliSeconds;

    // onSaveInstanceState
    private boolean mCounterStarted = false;
    private int mTimerProgress = 1;
    private String mTimerText;

    public TaDOChooserTabFragment2() {
        // Required empty public constructor
    }


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
        checkCurrentItemExists();

        if (savedInstanceState != null) {
            mCounterStarted = savedInstanceState.getBoolean("mCounterStarted");
            mTimerProgress = savedInstanceState.getInt("mTimerProgress");
            mTimerText = savedInstanceState.getString("mTimerText");

            if (mCounterStarted) {
                mTaDOChooserFragmentTimerTextView.setText(mTimerText);
                mTotalMilliSeconds = taskTimeInMilliseconds(mTimerText);
                startCountDown();
            }
        }
    }

    private void initialiseInputs() {

        mTaDOChooserFragment2ListTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ListTextView);
        mTaDOChooserFragment2ItemTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ItemTextView);
        mTaDOChooserFragment2ItemDetailTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2ItemDetailTextView);
        mTaDOChooserFragment2HoursTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2HoursTextView);
        mTaDOChooserFragment2MinsTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2MinsTextView);
        mTaDOChooserFragment2PriorityTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragment2PriorityTextView);

        mTaDOChooserFragment1TimerButton = (Button) mRootView.findViewById(R.id.taDOChooserFragment2TimerButton);
        mTaDOChooserFragment1TimerButton.setOnClickListener(this);
        mTaDOChooserFragment1TimerButton.setOnLongClickListener(this);


        mTaDOChooserFragmentMenuButton = (ImageButton) mRootView.findViewById(R.id.taDOChooserFragment2MenuButton);
        mTaDOChooserFragmentMenuButton.setOnClickListener(this);
        mTaDOChooserFragment1Timer = (ProgressBar) mRootView.findViewById(R.id.taDOChooserFragment2Timer);
        mTaDOChooserFragmentTimerTextView = (TextView) mRootView.findViewById(R.id.taDOChooserFragmentTimerTextView);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.taDOChooserFragment2MenuButton:
                inflatePopMenu(v);
                break;
            case R.id.taDOChooserFragment2TimerButton:
                if (!mCounterStarted) {
                    startTimer();
                } else {
                    pauseTimer();
                    mCounterStarted = false;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.taDOChooserFragment2TimerButton:
                resetTimer();
                return true;
            default:
                return false;
        }
    }

    public void populateTimerLayout() {

        mCurrentItemListData = dbHelper.getCurrentItem();

        mItemsListData = dbHelper.getSingleItem(mCurrentItemListData.getItemId());

        mTaDOChooserFragment2ListTextView.setText(dbHelper.getTitle(mItemsListData.getTitleId()));
        mTaDOChooserFragment2ItemTextView.setText(mItemsListData.getItem());
        mTaDOChooserFragment2ItemDetailTextView.setText(mItemsListData.getItemDetail());

        String duration = mItemsListData.getDuration();
        String[] timeArray = duration.split(":");
        // done to drop first zero
        mTaDOChooserFragment2HoursTextView.setText(String.valueOf(Integer.parseInt(timeArray[0])));
        mTaDOChooserFragment2MinsTextView.setText(timeArray[1]);

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

    }

    public void checkCurrentItemExists() {

        mCurrentItemListData = dbHelper.getCurrentItem();

        if (mCurrentItemListData.getCurrentItemId() != null) {
            // check if user has manually updated item to be done
            mItemsListData = dbHelper.getSingleItem(mCurrentItemListData.getItemId());
            if (mItemsListData.getItemDone().equals("N")) {

                mLinearLayout.setVisibility(View.VISIBLE);
                populateTimerLayout();
            } else {
                CustomToasts.Toast(getActivity(), "Current task marked as done");
                dbHelper.deleteCurrentItem(mCurrentItemListData);
            }
        }
    }

    private void resetTimer() {

        if (mCountDownTimer != null && mAnimation != null) {
            mCountDownTimer.cancel();
            mAnimation.cancel();
        }
        mTaDOChooserFragmentTimerTextView.setText(mItemsListData.getDuration());
        mTaDOChooserFragment1Timer.clearAnimation();
        mTaDOChooserFragment1Timer.setProgress(0);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTimerProgress = 1;
        mCounterStarted = false;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("PAUSED");
        editor.remove("mCounterStarted");
        editor.remove("mTimerProgress");
        editor.remove("mTimerText");
        editor.remove("elapsedTimeOnExit");
        editor.apply();
    }

    private void pauseTimer() {
        // Place Boolean pause value in shared prefs
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("PAUSED", true);
        editor.apply();
        mCountDownTimer.cancel();
        mTimerProgress = mTaDOChooserFragment1Timer.getProgress();
        mAnimation.cancel();
        mTaDOChooserFragment1Timer.setProgress(mTimerProgress);
    }

    private void startTimer() {
        // Remove paused boolean status from Shared Prefs
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("PAUSED");
        editor.apply();
        mCounterStarted = true;
        // get initial time and milliseconds for progress animator
        mTotalMilliSeconds = taskTimeInMilliseconds(mTaDOChooserFragmentTimerTextView.getText().toString());
        startCountDown();
    }

    public void startCountDown() {

        // stop screen from going to sleep manually
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // start progressBar
        mAnimation = ObjectAnimator.ofInt(mTaDOChooserFragment1Timer, "progress", mTimerProgress, 5000);
        mAnimation.setDuration(mTotalMilliSeconds); //in milliseconds
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.start();
        mTaDOChooserFragment1Timer.clearAnimation();

        mCountDownTimer = new CountDownTimer((long) mTotalMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTaDOChooserFragmentTimerTextView.setText(
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {

                if (TaDOChooserTabFragment2.this.getView() != null) {
                    mCounterStarted = false;
                    mTaDOChooserFragmentTimerTextView.setText("TIME UP");

                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone notification = RingtoneManager.getRingtone(getActivity(), sound);
                    notification.play();
                }
            }
        }.start();
    }

    public int taskTimeInMilliseconds(String taskTime) {
        mTaskTimeArray = taskTime.split(":");
        mIntHours = Integer.parseInt(mTaskTimeArray[0]);
        mIntMinutes = Integer.parseInt(mTaskTimeArray[1]);
        mIntSeconds = Integer.parseInt(mTaskTimeArray[2]);
        mTotalSeconds = (((mIntHours * 60) + mIntMinutes) * 60) + mIntSeconds;
        return mTotalSeconds * 1000;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("mCounterStarted", mCounterStarted);
        outState.putInt("mTimerProgress", mTaDOChooserFragment1Timer.getProgress());
        outState.putString("mTimerText", mTaDOChooserFragmentTimerTextView.getText().toString());

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("mCounterStarted", mCounterStarted);
        editor.putInt("mTimerProgress", mTimerProgress);
        editor.putString("mTimerText", mTaDOChooserFragmentTimerTextView.getText().toString());
        editor.putLong("elapsedTimeOnExit", SystemClock.elapsedRealtime());
        editor.apply();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (dbHelper.getCurrentItem().getItemId() != null){
            mLinearLayout.setVisibility(View.VISIBLE);
            populateTimerLayout();
        }

        mCounterStarted = mSharedPreferences.getBoolean("mCounterStarted", false);

        if (mCounterStarted && !mSharedPreferences.getBoolean("PAUSED", false)) {

            long currentElapsedTime = SystemClock.elapsedRealtime();
            long elapsedTimeOnExit = mSharedPreferences.getLong("elapsedTimeOnExit", 0);

            if (currentElapsedTime > elapsedTimeOnExit) {
                long timeDifference = currentElapsedTime - elapsedTimeOnExit;
                int timeDifferenceInMilliSeconds = (int) timeDifference;
                mTotalMilliSeconds = taskTimeInMilliseconds(mSharedPreferences.getString("mTimerText", "00:00:00"));
                mTotalMilliSeconds = mTotalMilliSeconds - timeDifferenceInMilliSeconds;

                if (mTotalMilliSeconds > 0) {

                    String originalCounterText = mSharedPreferences.getString("counterTime", "00:00:00");
                    int originalTimeInMilliSeconds = taskTimeInMilliseconds(originalCounterText);
                    double timerProgressCalc = ((double) mTotalMilliSeconds / (double) originalTimeInMilliSeconds) * 5000;
                    mTimerProgress = 5000 - (int) timerProgressCalc;

                    mTaDOChooserFragmentTimerTextView.setText(
                            String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(mTotalMilliSeconds),
                                    TimeUnit.MILLISECONDS.toMinutes(mTotalMilliSeconds) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTotalMilliSeconds)),
                                    TimeUnit.MILLISECONDS.toSeconds(mTotalMilliSeconds) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTotalMilliSeconds))));

                    taskTimeInMilliseconds(mTaDOChooserFragmentTimerTextView.getText().toString());

                    startCountDown();

                }
            }
        } else if (mSharedPreferences.getBoolean("PAUSED", false)) {
            mTaDOChooserFragment1Timer.setProgress(mSharedPreferences.getInt("mTimerProgress", 0));
        }
    }

    private void inflatePopMenu(View v) {
        //create popUpMenu (context menu)
        Context style = new ContextThemeWrapper(getActivity(), R.style.PopUpMenu);
        PopupMenu popUpMenu = new PopupMenu(style, v);
        // inflate my context menu xml layout
        MenuInflater inflater = popUpMenu.getMenuInflater();
        inflater.inflate(R.menu.fragment_tado_chooser_popmenu, popUpMenu.getMenu());

        popUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.popmenu_tado_chooser_notify:

                        return true;

                    case R.id.popmenu_tado_chooser_done:
                        item.setChecked(true);
                        return true;

                    case R.id.popmenu_tado_chooser_delete:
                        item.setChecked(true);
                        return true;

                    case R.id.popmenu_tado_chooser_do_not_alter:
                        item.setChecked(true);
                        return true;

                    case R.id.popmenu_tado_chooser_remove:
                        mLinearLayout.startAnimation(Animations.alphaFadeOut(getActivity()));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLinearLayout.setVisibility(View.GONE);
                            }
                        }, 200);
                        resetTimer();
                        mCurrentItemListData = dbHelper.getCurrentItem();
                        dbHelper.deleteCurrentItem(mCurrentItemListData);

                    default:
                        return false;
                }
            }
        });

        popUpMenu.show();
    }
}

package com.treefrogapps.TaDo;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class TaDOChooserTabFragment1 extends Fragment implements View.OnClickListener {

    private View rootView;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private ImageButton taDOChooserFragmentMenuButton;
    private TextView taDOChooserFragmentTimerTextView;
    private Button taDOChooserFragment1ResetButton;
    private Button taDOChooserFragment1PauseButton;
    private Button taDOChooserFragment1StartButton;

    // CountDown Clock
    private ProgressBar taDOChooserFragment1Timer;
    private ObjectAnimator animation;

    private CountDownTimer countDownTimer;
    private String[] taskTimeArray;
    private int intHours;
    private int intMinutes;
    private int intSeconds;
    private int totalSeconds;
    private int totalMilliSeconds;

    // onSaveInstanceState
    private boolean counterStarted = false;
    private int timerProgress = 1;
    private String timerText;


    public TaDOChooserTabFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tado_chooser_tab_fragment1, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DBHelper(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);

        initialiseInputs();

        if (savedInstanceState != null) {
            counterStarted = savedInstanceState.getBoolean("counterStarted");
            timerProgress = savedInstanceState.getInt("timerProgress");
            timerText = savedInstanceState.getString("timerText");

            if (counterStarted) {
                taDOChooserFragmentTimerTextView.setText(timerText);
                totalMilliSeconds = taskTimeInMilliseconds(timerText);
                startCountDown();
            }
        }
    }

    private void initialiseInputs() {

        taDOChooserFragmentMenuButton = (ImageButton) rootView.findViewById(R.id.taDOChooserFragment1MenuButton);
        taDOChooserFragmentMenuButton.setOnClickListener(this);
        taDOChooserFragment1ResetButton = (Button) rootView.findViewById(R.id.taDOChooserFragment1ResetButton);
        taDOChooserFragment1ResetButton.setOnClickListener(this);
        taDOChooserFragment1PauseButton = (Button) rootView.findViewById(R.id.taDOChooserFragment1PauseButton);
        taDOChooserFragment1PauseButton.setOnClickListener(this);
        taDOChooserFragment1StartButton = (Button) rootView.findViewById(R.id.taDOChooserFragment1StartButton);
        taDOChooserFragment1StartButton.setOnClickListener(this);
        taDOChooserFragment1Timer = (ProgressBar) rootView.findViewById(R.id.taDOChooserFragment1Timer);
        taDOChooserFragmentTimerTextView = (TextView) rootView.findViewById(R.id.taDOChooserFragmentTimerTextView);
    }


    @Override
    public void onClick(View v) {

        if (v == taDOChooserFragmentMenuButton) {
            inflatePopMenu(v);
        } else if (v == taDOChooserFragment1ResetButton) {
            resetTimer();
        } else if (v == taDOChooserFragment1PauseButton) {

            pauseTimer();
        } else if (v == taDOChooserFragment1StartButton) {

            startTimer();

        }
    }

    private void resetTimer() {

        countDownTimer.cancel();
        taDOChooserFragmentTimerTextView.setText(sharedPreferences.getString("counterTime", "00:00:00"));
        animation.cancel();
        taDOChooserFragment1Timer.clearAnimation();
        taDOChooserFragment1Timer.setProgress(0);
        taDOChooserFragment1StartButton.setClickable(true);
        taDOChooserFragment1StartButton.setTextColor(getResources().getColor(R.color.primaryColor));
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timerProgress = 1;
        counterStarted = false;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerProgress = taDOChooserFragment1Timer.getProgress();
        animation.cancel();
        taDOChooserFragment1Timer.setProgress(timerProgress);
        taDOChooserFragment1StartButton.setClickable(true);
        taDOChooserFragment1StartButton.setTextColor(getResources().getColor(R.color.primaryColor));

    }

    private void startTimer() {
        // put original time into shared prefs in case navigated from
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("counterTime", taDOChooserFragmentTimerTextView.getText().toString()).apply();
        counterStarted = true;
        // get initial time and milliseconds for progress animator
        totalMilliSeconds = taskTimeInMilliseconds(taDOChooserFragmentTimerTextView.getText().toString());
        startCountDown();
    }



    public void startCountDown() {

        // stop screen from going to sleep manually
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        taDOChooserFragment1StartButton.setClickable(false);
        taDOChooserFragment1StartButton.setTextColor(getResources().getColor(R.color.grey_light));

        // start progressBar
        animation = ObjectAnimator.ofInt(taDOChooserFragment1Timer, "progress", timerProgress, 5000);
        animation.setDuration(totalMilliSeconds); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
        taDOChooserFragment1Timer.clearAnimation();

        countDownTimer = new CountDownTimer((long) totalMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                taDOChooserFragmentTimerTextView.setText(
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {

                if (TaDOChooserTabFragment1.this.getView() != null){
                    counterStarted = false;
                    taDOChooserFragmentTimerTextView.setText("TIME UP");
                    taDOChooserFragment1StartButton.setClickable(true);
                    taDOChooserFragment1StartButton.setTextColor(getResources().getColor(R.color.primaryColor));
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
        taskTimeArray = taskTime.split(":");
        intHours = Integer.parseInt(taskTimeArray[0]);
        intMinutes = Integer.parseInt(taskTimeArray[1]);
        intSeconds = Integer.parseInt(taskTimeArray[2]);
        totalSeconds = (((intHours * 60) + intMinutes) * 60) + intSeconds;
        return totalSeconds * 1000;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("counterStarted", counterStarted);
        outState.putInt("timerProgress", taDOChooserFragment1Timer.getProgress());
        outState.putString("timerText", taDOChooserFragmentTimerTextView.getText().toString());

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (countDownTimer !=null) countDownTimer.cancel();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("counterStarted", counterStarted);
        editor.putString("timerText", taDOChooserFragmentTimerTextView.getText().toString());
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

        counterStarted = sharedPreferences.getBoolean("counterStarted", false);

        if (counterStarted) {

            long currentElapsedTime = SystemClock.elapsedRealtime();
            long elapsedTimeOnExit = sharedPreferences.getLong("elapsedTimeOnExit", 0);

            if (currentElapsedTime > elapsedTimeOnExit) {
                long timeDifference = currentElapsedTime - elapsedTimeOnExit;
                int timeDifferenceInMilliSeconds = (int) timeDifference;
                totalMilliSeconds = taskTimeInMilliseconds(sharedPreferences.getString("timerText", "00:00:00"));
                totalMilliSeconds = totalMilliSeconds - timeDifferenceInMilliSeconds;

                if (totalMilliSeconds > 0) {

                    String originalCounterText = sharedPreferences.getString("counterTime", "00:00:00");
                    int originalTimeInMilliSeconds = taskTimeInMilliseconds(originalCounterText);
                    double timerProgressCalc = ((double) totalMilliSeconds / (double) originalTimeInMilliSeconds) * 5000;
                    timerProgress = 5000 - (int) timerProgressCalc;

                    taDOChooserFragmentTimerTextView.setText(
                            String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(totalMilliSeconds),
                                    TimeUnit.MILLISECONDS.toMinutes(totalMilliSeconds) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalMilliSeconds)),
                                    TimeUnit.MILLISECONDS.toSeconds(totalMilliSeconds) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalMilliSeconds))));

                    taskTimeInMilliseconds(taDOChooserFragmentTimerTextView.getText().toString());
                    startCountDown();

                }
            }
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

                    default:
                        return false;
                }
            }
        });

        popUpMenu.show();
    }
}

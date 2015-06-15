package com.treefrogapps.TaDo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CreateListActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Toolbar mToolbar;
    private DBHelper dbHelper;

    private CardView mCreateListCardView;
    private LinearLayout mCreateListSaveButtonLayout;

    private EditText mCreateListTitleEditText;
    private EditText mCreateListItemEditText;

    private ImageView mCreateListPlusHour;
    private TextView mCreateListHoursTextView;
    private ImageView mCreateListMinusHour;

    private ImageView mCreateListPlusMins;
    private TextView mCreateListMinsTextView;
    private ImageView mCreateListMinusMins;

    private ImageView mCreateListAddItem;

    private RecyclerView mCreateListRecyclerView;
    private ItemRecyclerAdapter mItemRecyclerAdapter;
    private ArrayList<ItemsListData> mItemsArrayList;

    private Button mCreateListTitleSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        dbHelper = new DBHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.myToolBar);
        mToolbar.setTitle(getResources().getString(R.string.activity_create_list));
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add to draw system bar (set for translucent to main activity for nav bar to go under and be visible)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
        }


        initialiseInputs();
        initialiseRecyclerView();
    }

    private void initialiseInputs() {

        mCreateListTitleEditText = (EditText) findViewById(R.id.createListTitleEditText);
        mCreateListItemEditText = (EditText) findViewById(R.id.createListItemEditText);

        mCreateListPlusHour = (ImageView) findViewById(R.id.createListPlusHour);
        mCreateListPlusHour.setOnClickListener(this);
        mCreateListHoursTextView = (TextView) findViewById(R.id.createListHoursTextView);
        mCreateListMinusHour = (ImageView) findViewById(R.id.createListMinusHour);
        mCreateListMinusHour.setOnClickListener(this);

        mCreateListPlusMins = (ImageView) findViewById(R.id.createListPlusMins);
        mCreateListPlusMins.setOnClickListener(this);
        mCreateListMinsTextView = (TextView) findViewById(R.id.createListMinsTextView);
        mCreateListMinusMins = (ImageView) findViewById(R.id.createListMinusMins);
        mCreateListMinusMins.setOnClickListener(this);

        mCreateListTitleSaveButton = (Button) findViewById(R.id.createListTitleSaveButton);
        mCreateListTitleSaveButton.setOnClickListener(this);

        mCreateListAddItem = (ImageView) findViewById(R.id.createListAddItem);
        mCreateListAddItem.setOnClickListener(this);

        mCreateListCardView = (CardView) findViewById(R.id.createListCardView);
        mCreateListSaveButtonLayout = (LinearLayout) findViewById(R.id.createListSaveButtonLayout);
    }

    public void initialiseRecyclerView() {

        mCreateListRecyclerView = (RecyclerView) findViewById(R.id.createListRecyclerView);
        mCreateListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mItemsArrayList = new ArrayList<>();
        mItemRecyclerAdapter = new ItemRecyclerAdapter(getApplicationContext(), mItemsArrayList);
        mCreateListRecyclerView.setAdapter(mItemRecyclerAdapter);
        mCreateListRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == mCreateListPlusHour.getId()) {

            String currentDuration = mCreateListHoursTextView.getText().toString();

            mCreateListHoursTextView.setText(setDuration(0, currentDuration));

        } else if (v.getId() == mCreateListMinusHour.getId()) {

            mCreateListHoursTextView.setText(setDuration(1, mCreateListHoursTextView.getText().toString()));

        } else if (v.getId() == mCreateListPlusMins.getId()) {

            mCreateListMinsTextView.setText(setDuration(2, mCreateListMinsTextView.getText().toString()));

        } else if (v.getId() == mCreateListMinusMins.getId()) {

            mCreateListMinsTextView.setText(setDuration(3, mCreateListMinsTextView.getText().toString()));

        } else if (v.getId() == mCreateListAddItem.getId()) {

            addItemToTitleTable(mCreateListTitleEditText.getText().toString(), mCreateListItemEditText.getText().toString(),
                    mCreateListHoursTextView.getText().toString(), mCreateListMinsTextView.getText().toString());

        } else if (v.getId() == mCreateListTitleSaveButton.getId()) {

            if (!mCreateListTitleEditText.getText().toString().equals("")) {

                saveListTitle(mCreateListTitleEditText.getText().toString());

            }
        }
    }

    public String setDuration(int switchCaseId, String currentDuration) {

        int curTimeAsint = Integer.parseInt(currentDuration);

        switch (switchCaseId) {

            case 0:
                if (curTimeAsint < 24 && curTimeAsint < 9) {
                    return "0" + String.valueOf(curTimeAsint + 1);
                } else if (curTimeAsint < 24) {
                    return String.valueOf(curTimeAsint + 1);
                } else {
                    return String.valueOf(curTimeAsint);
                }

            case 1:
                if (curTimeAsint <= 10 && curTimeAsint >= 1) {
                    return "0" + String.valueOf(curTimeAsint - 1);
                } else if (curTimeAsint > 10) {
                    return String.valueOf(curTimeAsint - 1);
                } else {
                    return "0" + String.valueOf(curTimeAsint);
                }

            case 2:
                if (curTimeAsint < 45) {
                    return String.valueOf(curTimeAsint + 15);
                } else {
                    return String.valueOf(curTimeAsint);
                }

            case 3:
                if (curTimeAsint >= 30) {
                    return String.valueOf(curTimeAsint - 15);
                } else if (curTimeAsint >= 15) {
                    return "0" + String.valueOf(curTimeAsint - 15);
                } else {
                    return "00";
                }

            default:
                return currentDuration;
        }
    }


    public void addItemToTitleTable(String titleName, String itemName, String hours, String mins) {

        if (!mCreateListItemEditText.equals("")){

            ItemsListData itemsListData = new ItemsListData();
            String duration = hours + ":" + mins;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());

            itemsListData.setItem(itemName);
            itemsListData.setTitle(titleName);
            itemsListData.setTitleId(dbHelper.getTitleId(titleName));
            itemsListData.setDuration(duration);
            itemsListData.setDateTime(date);

            dbHelper.insertIntoItemsTable(itemsListData);

            populateRecyclerView(mCreateListTitleEditText.getText().toString());

            mCreateListItemEditText.setText("");
            mCreateListHoursTextView.setText("00");
            mCreateListMinsTextView.setText("00");
        }

    }

    public void saveListTitle(String titleName) {

        TitlesListData titlesListData = new TitlesListData();

        String checkIfTitleExists  = dbHelper.checkTitleNameExists(titleName);

        if (checkIfTitleExists == null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date());

            titlesListData.setTitle(titleName);
            titlesListData.setDateTime(date);

            dbHelper.insertIntoTitlesTable(titlesListData);

            mCreateListCardView.setVisibility(View.VISIBLE);
            mCreateListCardView.startAnimation(Animations.alphaMoveInAnim(getApplicationContext()));
            mCreateListTitleEditText.setEnabled(false);
            mCreateListTitleEditText.setTypeface(null, Typeface.BOLD);
            mCreateListTitleEditText.setTextColor(getResources().getColor(R.color.primaryColorDark));
            mCreateListSaveButtonLayout.startAnimation(Animations.alphaMoveOutAnim(getApplicationContext()));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCreateListSaveButtonLayout.setVisibility(View.GONE);
                }
            }, 350);

        } else {
            CustomToasts.Toast(getApplicationContext(), "Title Already Exists");
        }
    }

    public void populateRecyclerView(String titleName) {

        mItemsArrayList = dbHelper.getItemsForTitle(titleName);
        mItemRecyclerAdapter = new ItemRecyclerAdapter(getApplicationContext(), mItemsArrayList);
        mCreateListRecyclerView.setAdapter(mItemRecyclerAdapter);
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public void onBackPressed() {
        String spinnerPosition;
        Intent intent = new Intent();

        Intent getIntent = getIntent();
        if (getIntent.hasExtra("spinnerPosition")) {
            spinnerPosition = getIntent.getStringExtra("spinnerPosition");
            Log.e("STRING FROM FRAGMENT", getIntent.getStringExtra("spinnerPosition"));
            intent.putExtra("spinnerPosition", spinnerPosition);
        }
        setResult(Constants.NEW_LIST_RESULT_CODE, intent);
        finish();
    }


}

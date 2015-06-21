package com.treefrogapps.TaDo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
    private SharedPreferences sharedPreferences;

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

    private LinearLayout mCreateListSwipeActionLinearLayout;
    private Button mCreateListSplashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        dbHelper = new DBHelper(this);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.TADO_PREFERENCES, Context.MODE_PRIVATE);

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
        checkEditIntent();
        checkSwipeScreenVisibility();
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

        mCreateListSwipeActionLinearLayout  = (LinearLayout) findViewById(R.id.createListSwipeActionLinearLayout);
        mCreateListSplashButton = (Button) findViewById(R.id.createListSplashButton);
        mCreateListSplashButton.setOnClickListener(this);
    }

    public void initialiseRecyclerView() {

        mCreateListRecyclerView = (RecyclerView) findViewById(R.id.createListRecyclerView);
        mCreateListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mItemsArrayList = new ArrayList<>();
        mItemRecyclerAdapter = new ItemRecyclerAdapter(getApplicationContext(), mItemsArrayList);
        mCreateListRecyclerView.setAdapter(mItemRecyclerAdapter);
        mCreateListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    removeItemFromRViewAndDB(viewHolder.getLayoutPosition());

                } else if (direction == ItemTouchHelper.RIGHT) {
                    markItemAsDoneToggle(viewHolder.getLayoutPosition());
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mCreateListRecyclerView);
    }

    private void removeItemFromRViewAndDB(int layoutPosition) {

        String itemIdToDelete = mItemsArrayList.get(layoutPosition).getItemId();

        ItemsListData itemsListData = new ItemsListData();
        itemsListData.setItemId(itemIdToDelete);
        dbHelper.deleteItem(itemsListData);

        mItemRecyclerAdapter.notifyItemRemoved(layoutPosition);
        mItemsArrayList.remove(layoutPosition);
    }

    public void markItemAsDoneToggle(final int layoutPosition) {

        // method for toggling on and off the done status of items based on current state
        String itemIdToUpdate = mItemsArrayList.get(layoutPosition).getItemId();

        ItemsListData itemsListData = new ItemsListData();
        itemsListData.setItemId(itemIdToUpdate);

        if (mItemsArrayList.get(layoutPosition).getItemDone().equals(Constants.ITEM_NOT_DONE)){
            itemsListData.setItemDone(Constants.ITEM_DONE);
            mItemsArrayList.get(layoutPosition).setItemDone(Constants.ITEM_DONE);
        } else {
            itemsListData.setItemDone(Constants.ITEM_NOT_DONE);
            mItemsArrayList.get(layoutPosition).setItemDone(Constants.ITEM_NOT_DONE);
        }

        dbHelper.updateItemDone(itemsListData);
        mItemRecyclerAdapter.notifyItemChanged(layoutPosition);

    }

    public void checkEditIntent() {

        Intent getIntent = getIntent();
        if (getIntent.hasExtra("editList")) {

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(Constants.EDIT_LIST_ACTIVITY_TITLE);
            String titleId = String.valueOf(getIntent.getIntExtra("spinnerPosition", 0));
            mCreateListTitleEditText.setText(dbHelper.getTitle(titleId));
            mCreateListCardView.setVisibility(View.VISIBLE);
            mCreateListTitleEditText.setEnabled(false);
            mCreateListTitleEditText.setTypeface(null, Typeface.BOLD);
            mCreateListTitleEditText.setTextColor(getResources().getColor(R.color.primaryColorDark));
            mCreateListSaveButtonLayout.setVisibility(View.GONE);
            populateRecyclerView(mCreateListTitleEditText.getText().toString());
        }
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
        } else if (id == R.id.createListEditTitle) {

            if (!mCreateListTitleEditText.getText().toString().equals("")) {
                String currentTitle = mCreateListTitleEditText.getText().toString();
                Log.e("CURRENT TITLE", currentTitle);
                changeTitleName(currentTitle);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public void changeTitleName(final String currentTitle) {

        final Dialog dialogBuilder = new Dialog(this);
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.dialog_rename_title);
        dialogBuilder.setCancelable(true);

        final EditText renameDialogEditText = (EditText) dialogBuilder.findViewById(R.id.createListDialogRenameEditText);
        Button cancelDialogButton = (Button) dialogBuilder.findViewById(R.id.createListDialogCancelButton);
        Button okDialogButton = (Button) dialogBuilder.findViewById(R.id.createListDialogOkButton);

        renameDialogEditText.setText(currentTitle);

        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });
        okDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newTitle = renameDialogEditText.getText().toString();

                String checkIfTitleExists = dbHelper.checkTitleNameExists(newTitle);

                if (checkIfTitleExists == null) {
                    TitlesListData oldTitleListData = new TitlesListData();
                    oldTitleListData.setTitle(currentTitle);

                    TitlesListData newTitleListData = new TitlesListData();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                    String date = sdf.format(new Date());

                    newTitleListData.setTitle(newTitle);
                    newTitleListData.setDateTime(date);

                    dbHelper.updateTitle(oldTitleListData, newTitleListData);
                    mCreateListTitleEditText.setText(newTitle);

                    dialogBuilder.dismiss();

                } else {
                    CustomToasts.Toast(getApplicationContext(), "Title Already Exists");
                }
            }
        });
        dialogBuilder.show();
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
                    mCreateListHoursTextView.getText().toString(), mCreateListMinsTextView.getText().toString(), Constants.ITEM_NOT_DONE, Constants.ITEM_PRIORITY_LOW);

        } else if (v.getId() == mCreateListTitleSaveButton.getId()) {

            if (!mCreateListTitleEditText.getText().toString().equals("")) {

                saveListTitle(mCreateListTitleEditText.getText().toString());

            }
        } else if (v.getId() == mCreateListSplashButton.getId()){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.SWIPE_SPLASH_SCREEN_VISIBILITY, Constants.SWIPE_SPLASH_SCREEN_OFF).apply();
            mCreateListSwipeActionLinearLayout.setVisibility(View.GONE);
        }
    }

    public void checkSwipeScreenVisibility() {

        int swipeSplashScreenVisible = sharedPreferences.getInt(Constants.SWIPE_SPLASH_SCREEN_VISIBILITY, 1);

        if (swipeSplashScreenVisible == 0) {
            mCreateListSwipeActionLinearLayout.setVisibility(View.GONE);
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


    public void addItemToTitleTable(String titleName, String itemName, String hours, String mins, String itemDone, String itemPriority) {

        if (!mCreateListItemEditText.equals("")) {

            ItemsListData itemsListData = new ItemsListData();
            String duration = hours + ":" + mins;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());

            itemsListData.setItem(itemName);
            itemsListData.setTitle(titleName);
            itemsListData.setTitleId(dbHelper.getTitleId(titleName));
            itemsListData.setDuration(duration);
            itemsListData.setDateTime(date);
            itemsListData.setItemDone(itemDone);
            itemsListData.setItemPriority(itemPriority);

            dbHelper.insertIntoItemsTable(itemsListData);

            populateRecyclerView(mCreateListTitleEditText.getText().toString());

            mCreateListItemEditText.setText("");
            mCreateListHoursTextView.setText("00");
            mCreateListMinsTextView.setText("00");
        }

    }

    public void saveListTitle(String titleName) {

        TitlesListData titlesListData = new TitlesListData();

        String checkIfTitleExists = dbHelper.checkTitleNameExists(titleName);

        if (checkIfTitleExists == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date());

            titlesListData.setTitle(titleName);
            titlesListData.setDateTime(date);

            dbHelper.insertIntoTitlesTable(titlesListData);

            showAddItemRemoveSaveButton(350L);

        } else {
            CustomToasts.Toast(getApplicationContext(), "Title Already Exists");
        }
    }

    public void showAddItemRemoveSaveButton(Long delay) {

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
        }, delay);
    }

    public void populateRecyclerView(String titleName) {

        mItemsArrayList = dbHelper.getItemsForTitleNotDone(titleName);
        mItemsArrayList.addAll(dbHelper.getItemsForTitleDone(titleName));

        mItemRecyclerAdapter = new ItemRecyclerAdapter(getApplicationContext(), mItemsArrayList);
        mCreateListRecyclerView.setAdapter(mItemRecyclerAdapter);
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public void onBackPressed() {
        int spinnerTitleId;
        Intent intent = new Intent();

        Intent getIntent = getIntent();
        if (getIntent.hasExtra("spinnerPosition")) {
            spinnerTitleId = getIntent.getIntExtra("spinnerPosition", 0);
            Log.e("STRING FROM FRAGMENT", String.valueOf(getIntent.getIntExtra("spinnerPosition", 0)));
            intent.putExtra("spinnerPosition", spinnerTitleId);
        }
        setResult(Constants.NEW_LIST_RESULT_CODE, intent);
        finish();
    }


}

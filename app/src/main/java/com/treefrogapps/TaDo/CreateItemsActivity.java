package com.treefrogapps.TaDo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CreateItemsActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Toolbar mToolbar;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private RecyclerView mCreateListRecyclerView;
    private ItemRecyclerAdapter mItemRecyclerAdapter;
    private ArrayList<ItemsListData> mItemsArrayList;
    private FloatingActionButton mCreateItemFAB;
    private String mTitleId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_items);

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

        // get the titleId passed from title recycler adapter click
        Intent getIntent = getIntent();
        if (getIntent.hasExtra("TITLE_ID")) {
            mTitleId = getIntent.getStringExtra("TITLE_ID");
            setTitlePopulateRecyclerView(mTitleId);
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
            changeTitleName(dbHelper.getTitle(mTitleId));
        } else if (id == R.id.createListEditSearch) {
            // TODO - search Items
        }
        return super.onOptionsItemSelected(item);
    }

    public void initialiseInputs() {

        mCreateItemFAB = (FloatingActionButton) findViewById(R.id.createItemsFAB);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCreateItemFAB.setVisibility(View.VISIBLE);
                mCreateItemFAB.startAnimation(Animations.floatingActionButtonAnim(getApplicationContext()));
                mCreateItemFAB.setOnClickListener(CreateItemsActivity.this);
            }
        }, 200);
    }

    public void initialiseRecyclerView() {

        mCreateListRecyclerView = (RecyclerView) findViewById(R.id.createItemRecyclerView);
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

    @Override
    public void onClick(View v) {

        if (v.getId() == mCreateItemFAB.getId()) {

            addItemDialog(dbHelper.getTitle(mTitleId));
        }

    }

    private void addItemDialog(String title) {

        Dialog dialogBuilder = new Dialog(getApplicationContext());
            dialogBuilder.setContentView(R.layout.fragment_my_lists_dialog);
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

        if (mItemsArrayList.get(layoutPosition).getItemDone().equals(Constants.ITEM_NOT_DONE)) {
            itemsListData.setItemDone(Constants.ITEM_DONE);
            mItemsArrayList.get(layoutPosition).setItemDone(Constants.ITEM_DONE);
        } else {
            itemsListData.setItemDone(Constants.ITEM_NOT_DONE);
            mItemsArrayList.get(layoutPosition).setItemDone(Constants.ITEM_NOT_DONE);
        }

        dbHelper.updateItemDone(itemsListData);
        mItemRecyclerAdapter.notifyItemChanged(layoutPosition);

    }

    public void setTitlePopulateRecyclerView(String titleId) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(dbHelper.getTitle(titleId));
        }
        populateRecyclerView(dbHelper.getTitle(titleId));

    }

    public void changeTitleName(final String currentTitle) {

        final Dialog dialogBuilder = new Dialog(this);
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.activity_create_items_dialog_rename_title);
        dialogBuilder.setCancelable(true);

        final EditText renameDialogEditText = (EditText) dialogBuilder.findViewById(R.id.createItemDialogRenameEditText);
        Button cancelDialogButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogCancelButton);
        Button okDialogButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogOkButton);

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

                String newTitle = renameDialogEditText.getText().toString().trim();

                String checkIfTitleExists = dbHelper.checkTitleNameExists(newTitle);

                if (checkIfTitleExists == null && !newTitle.equals("")) {
                    TitlesListData oldTitleListData = new TitlesListData();
                    oldTitleListData.setTitle(currentTitle);

                    TitlesListData newTitleListData = new TitlesListData();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                    String date = sdf.format(new Date());

                    newTitleListData.setTitle(newTitle);
                    newTitleListData.setDateTime(date);

                    dbHelper.updateTitle(oldTitleListData, newTitleListData);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(newTitle);
                    }

                    dialogBuilder.dismiss();

                } else if (newTitle.equals("")) {
                    CustomToasts.Toast(getApplicationContext(), "Choose a title name");
                } else {
                    CustomToasts.Toast(getApplicationContext(), "Title Already Exists");
                }
            }
        });
        dialogBuilder.show();
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


    public void addItemToTitleTable(String titleName, String itemName, String itemDetail,
                                    String hours, String mins, String itemDone, String itemPriority) {

        ItemsListData itemsListData = new ItemsListData();
        String duration = hours + ":" + mins;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        itemsListData.setItem(itemName);
        itemsListData.setItemDetail(itemDetail);
        itemsListData.setTitle(titleName);
        itemsListData.setTitleId(dbHelper.getTitleId(titleName));
        itemsListData.setDuration(duration);
        itemsListData.setDateTime(date);
        itemsListData.setItemDone(itemDone);
        itemsListData.setItemPriority(itemPriority);

        dbHelper.insertIntoItemsTable(itemsListData);

        populateRecyclerView(dbHelper.getTitle(mTitleId));
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

        Intent intent = new Intent();
        setResult(Constants.NEW_ITEMS_REQUEST_CODE, intent);
        super.onBackPressed();
        finish();
    }
}

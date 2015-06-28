package com.treefrogapps.TaDo;

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
import android.view.WindowManager;

import java.util.ArrayList;


public class CreateItemsActivity extends AppCompatActivity implements View.OnClickListener,
        CreateItemsRenameTitleDialog.onRenameDialogCallBack, CreateItemsAddEditItemDialog.OnAddEditItemCallback {

    private Toolbar mToolbar;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private RecyclerView mCreateListRecyclerView;
    private ItemRecyclerAdapter mItemRecyclerAdapter;
    private ArrayList<ItemsListData> mItemsArrayList;
    private FloatingActionButton mCreateItemFAB;
    private String mTitleId;

    private CreateItemsRenameTitleDialog createItemsRenameTitleDialog;
    private CreateItemsAddEditItemDialog createItemsAddEditItemDialog;


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
            openRenameDialog(mTitleId);
            return true;
        } else if (id == R.id.createListEditSearch) {
            // TODO - search Items
        }
        return super.onOptionsItemSelected(item);
    }

    public void openRenameDialog(String titleId){

        createItemsRenameTitleDialog = new CreateItemsRenameTitleDialog();
        createItemsRenameTitleDialog.mOnRenameDialogCallBack = CreateItemsActivity.this;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE_ID, titleId);
        createItemsRenameTitleDialog.setArguments(bundle);
        createItemsRenameTitleDialog.show(getSupportFragmentManager(), "Dialog02");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // required for reestablishing the callback from the dialog(s) after rotation
        createItemsRenameTitleDialog = (CreateItemsRenameTitleDialog) getSupportFragmentManager().findFragmentByTag("Dialog02");
        if (createItemsRenameTitleDialog != null){
            createItemsRenameTitleDialog.setCallBack(CreateItemsActivity.this);
        }

        createItemsAddEditItemDialog = (CreateItemsAddEditItemDialog) getSupportFragmentManager().findFragmentByTag("Dialog03");
        if (createItemsAddEditItemDialog != null){
            createItemsAddEditItemDialog.setCallBack(CreateItemsActivity.this);
        }
    }

    @Override
    public void updateRecyclerViewCallBack(String titleId) {
        // callback method from Rename title dialog - pass to method below to handle
        setTitlePopulateRecyclerView(titleId);
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
            addItemDialog(mTitleId);
        }
    }

    private void addItemDialog(String titleId) {

        createItemsAddEditItemDialog = new CreateItemsAddEditItemDialog();
        createItemsAddEditItemDialog.mOnAddItemCallBack = CreateItemsActivity.this;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE_ID, titleId);
        createItemsAddEditItemDialog.setArguments(bundle);
        createItemsAddEditItemDialog.show(getSupportFragmentManager(), "Dialog03");
    }

    @Override
    public void addEditItemDialogCallBack() {
        setTitlePopulateRecyclerView(mTitleId);
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
        populateRecyclerView(titleId);
    }

    public void populateRecyclerView(String titleId) {

        mItemsArrayList = dbHelper.getItemsForTitleNotDone(titleId);
        mItemsArrayList.addAll(dbHelper.getItemsForTitleDone(titleId));

        mItemRecyclerAdapter = new ItemRecyclerAdapter(getApplicationContext(), mItemsArrayList);
        mCreateListRecyclerView.setAdapter(mItemRecyclerAdapter);
    }
    
    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(Constants.NEW_ITEMS_REQUEST_CODE, intent);
        super.onBackPressed();
        finish();
    }

}

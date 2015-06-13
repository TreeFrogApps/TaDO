package com.treefrogapps.TaDo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;


public class CreateListActivity extends AppCompatActivity {

    Toolbar mToolbar;

    EditText mCreateListTitleEditText;
    EditText mCreateListItemEditText;

    ImageView mCreateListAddItem;
    RecyclerView mCreateListRecyclerView;
    ItemRecyclerAdapter mItemRecyclerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        mToolbar = (Toolbar) findViewById(R.id.myToolBar);
        mToolbar.setTitle(getResources().getString(R.string.activity_create_list));
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add to draw system bar (set for translucent to main activity for nav bar to go under and be visible)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
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
        } else if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        String spinnerPosition = null;
        Intent intent = new Intent();

        Intent getIntent = getIntent();
        if (getIntent.hasExtra("spinnerPosition")){
            spinnerPosition = getIntent.getStringExtra("spinnerPosition");
            Log.e("STRING FROM FRAGMENT", getIntent.getStringExtra("spinnerPosition"));
            intent.putExtra("spinnerPosition", spinnerPosition);
        }
        setResult(Constants.NEW_LIST_RESULT_CODE, intent);
        finish();
    }
}

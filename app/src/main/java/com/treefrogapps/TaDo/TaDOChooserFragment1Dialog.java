package com.treefrogapps.TaDo;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class TaDOChooserFragment1Dialog extends DialogFragment implements View.OnClickListener {

    public OnItemChosenCallback mOnItemChosenCallback;

    interface OnItemChosenCallback {
        void itemChosenCallBack();
    }

    public void setCallBack(OnItemChosenCallback onItemChosenCallback) {
        this.mOnItemChosenCallback = onItemChosenCallback;
    }

    private DBHelper dbHelper;
    private Dialog dialogBuilder;
    private String mCurrentItemId;
    private Spinner mTaDOChooserDialogListSpinner;
    private ArrayAdapter<String> mListSpinnerArrayAdapter;
    private String[] mListSpinnerArray;
    private String mSpinnerListTitle = "Choose a list";
    private Spinner mTaDOChooserDialogItemSpinner;
    private ArrayAdapter<String> mItemSpinnerArrayAdapter;
    private String[] mItemSpinnerArray;
    private String mSpinnerItemTitle = "Choose a task";
    private ArrayList<ItemsListData> itemsListDataNotDoneArrayList;
    private Button mTaDOChooserDialogCancelButton;
    private Button mTaDOChooserDialogQueueButton;
    private Button mTaDOChooserDialogPositiveButton;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new DBHelper(getActivity());
        dialogBuilder = new Dialog(getActivity());
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.fragment_tado_chooser_fragment1_dialog);
        dialogBuilder.setCancelable(false);

        initialiseInputs();
        setupSpinnerAdapters();
        spinnerListeners();


        return dialogBuilder;
    }

    private void initialiseInputs() {

        mTaDOChooserDialogListSpinner = (Spinner) dialogBuilder.findViewById(R.id.taDOChooserDialogListSpinner);
        mTaDOChooserDialogItemSpinner = (Spinner) dialogBuilder.findViewById(R.id.taDOChooserDialogItemSpinner);
        mTaDOChooserDialogCancelButton = (Button) dialogBuilder.findViewById(R.id.taDOChooserDialogCancelButton);
        mTaDOChooserDialogCancelButton.setOnClickListener(this);
        mTaDOChooserDialogQueueButton = (Button) dialogBuilder.findViewById(R.id.taDOChooserDialogQueueButton);
        mTaDOChooserDialogQueueButton.setOnClickListener(this);
        mTaDOChooserDialogPositiveButton = (Button) dialogBuilder.findViewById(R.id.taDOChooserDialogPositiveButton);
        mTaDOChooserDialogPositiveButton.setOnClickListener(this);
    }

    private void setupSpinnerAdapters() {

        ArrayList<TitlesListData> titlesListDataArrayList = dbHelper.getTitles();

        mListSpinnerArray = new String[titlesListDataArrayList.size() + 1];
        mListSpinnerArray[0] = mSpinnerListTitle;

        for (int i = 0; i < titlesListDataArrayList.size(); i++) {
            mListSpinnerArray[i + 1] = titlesListDataArrayList.get(i).getTitle();
        }
        mListSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_view, R.id.spinner_list_item, mListSpinnerArray);
        mTaDOChooserDialogListSpinner.setAdapter(mListSpinnerArrayAdapter);

        mItemSpinnerArray = new String[]{mSpinnerItemTitle};
        mItemSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_view, R.id.spinner_list_item, mItemSpinnerArray);
        mTaDOChooserDialogItemSpinner.setAdapter(mItemSpinnerArrayAdapter);
    }

    public void spinnerListeners() {

        mTaDOChooserDialogListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    String titleName = mTaDOChooserDialogListSpinner.getSelectedItem().toString();
                    String titleId = dbHelper.getTitleId(titleName);

                    itemsListDataNotDoneArrayList = dbHelper.getItemsForTitleNotDone(titleId);

                    mItemSpinnerArray = new String[itemsListDataNotDoneArrayList.size() + 1];
                    mItemSpinnerArray[0] = mSpinnerItemTitle;

                    for (int i = 0; i < itemsListDataNotDoneArrayList.size(); i++) {
                        mItemSpinnerArray[i + 1] = itemsListDataNotDoneArrayList.get(i).getItem();
                    }

                    mItemSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                            R.layout.spinner_view, R.id.spinner_list_item, mItemSpinnerArray);
                    mTaDOChooserDialogItemSpinner.setAdapter(mItemSpinnerArrayAdapter);
                } else {

                    mItemSpinnerArray = new String[]{mSpinnerItemTitle};
                    mItemSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                            R.layout.spinner_view, R.id.spinner_list_item, mItemSpinnerArray);
                    mTaDOChooserDialogItemSpinner.setAdapter(mItemSpinnerArrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.taDOChooserDialogCancelButton:
                dialogBuilder.dismiss();
                break;
            case R.id.taDOChooserDialogQueueButton:

                if (mTaDOChooserDialogListSpinner.getSelectedItemPosition() != 0 &&
                        mTaDOChooserDialogItemSpinner.getSelectedItemPosition() != 0) {

                    String itemId = itemsListDataNotDoneArrayList.get
                            (mTaDOChooserDialogItemSpinner.getSelectedItemPosition() - 1).getItemId();

                    // Check if already queued
                    ArrayList<QueuedItemListData> queuedItemListDataArrayList = dbHelper.getQueuedItems();

                    boolean itemQueued = false;

                    for (int i = 0; i < queuedItemListDataArrayList.size(); i++){

                        if (queuedItemListDataArrayList.get(i).getItemId().equals(itemId)){
                            itemQueued = true;
                            break;
                        }
                    }

                    if (!itemQueued){
                        QueuedItemListData queuedItemListData = new QueuedItemListData();
                        queuedItemListData.setItemId(itemId);
                        dbHelper.insertIntoQueuedItemsTable(queuedItemListData);
                    } else {
                        CustomToasts.Toast(getActivity(), "Task already in queue");
                    }
                } else {
                    CustomToasts.Toast(getActivity(), "Please choose a task");
                }
                break;
            case R.id.taDOChooserDialogPositiveButton:
                if (mTaDOChooserDialogListSpinner.getSelectedItemPosition() != 0 &&
                        mTaDOChooserDialogItemSpinner.getSelectedItemPosition() != 0) {

                    String itemId = itemsListDataNotDoneArrayList.get
                            (mTaDOChooserDialogItemSpinner.getSelectedItemPosition() - 1).getItemId();

                    if (checkIfItemQueued(itemId)){
                        QueuedItemListData queuedItemListData = new QueuedItemListData();
                        queuedItemListData.setItemId(itemId);
                        dbHelper.deleteQueuedItem(queuedItemListData);
                    }

                    // Insert into CURRENT ITEM table (will only hold one item - current item)
                    CurrentItemListData currentItemListData = new CurrentItemListData();
                    currentItemListData.setItemId(itemId);
                    dbHelper.insertIntoCurrentItemTable(currentItemListData);

                    mOnItemChosenCallback.itemChosenCallBack();
                } else {
                    CustomToasts.Toast(getActivity(), "Please choose a task");
                }
                break;
        }

    }

    public boolean checkIfItemQueued(String itemId){

        // Check if is already in queued items table if so - remove from queued items
        ArrayList<QueuedItemListData> queuedItemListDataArrayList = new ArrayList<>();
        queuedItemListDataArrayList = dbHelper.getQueuedItems();

        boolean isQueued = false;

        for (int i = 0; i < queuedItemListDataArrayList.size(); i++){
            if (queuedItemListDataArrayList.get(i).getItemId().equals(itemId)){
                isQueued = true;
                break;
            }
        }
        return isQueued;
    }
}

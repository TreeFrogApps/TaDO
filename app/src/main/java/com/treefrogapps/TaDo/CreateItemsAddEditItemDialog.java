package com.treefrogapps.TaDo;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateItemsAddEditItemDialog extends DialogFragment implements View.OnClickListener {

    public OnAddEditItemCallback mOnAddItemCallBack;

    interface OnAddEditItemCallback {
        void addEditItemDialogCallBack();
    }

    // method to call from initiating activity/fragment after screen rotation
    public void setCallBack(OnAddEditItemCallback onAddEditItemCallback) {
        this.mOnAddItemCallBack = onAddEditItemCallback;
    }


    private Dialog dialogBuilder;
    private DBHelper dbHelper;
    private String titleId;
    private String itemId;
    private String itemDone;
    private ItemsListData itemListData;

    private TextView createListAddItemTitleTextView;
    private LinearLayout createListAddItemFirstLayout;
    private EditText createItemDialogTaskEditText;
    private EditText createItemDialogDetailsEditText;

    private LinearLayout createListAddItemSecondLayout;
    private RadioGroup createItemRadioGroup;
    private RadioButton createItemRadioButtonLow;
    private RadioButton createItemRadioButtonMed;
    private RadioButton createItemRadioButtonHigh;
    private String radioGroupSelection;
    private NumberPicker createListAddItemHourPicker;
    private NumberPicker createListAddItemMinutePicker;
    private String[] numberPickerMinsDisplay;

    private Button createItemDialogCancelButton;
    private Button createItemDialogPositiveButton;


    public CreateItemsAddEditItemDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new DBHelper(getActivity());

        dialogBuilder = new Dialog(getActivity());
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.activity_create_items_dialog_add_item);
        dialogBuilder.setCancelable(true);

        initialiseInputs();

        Bundle bundle = getArguments();
        if (bundle.getString(Constants.TITLE_ID) != null) {
            titleId = bundle.getString(Constants.TITLE_ID);
        }

        // get bundle for editing item
        if (bundle.getString(Constants.ITEMS_ID) != null) {
            itemId = bundle.getString(Constants.ITEMS_ID);

            // TODO - use dbhelper to get create a itemslistdata from ITEMS_ID - then use to populate all fields
        } else {
            itemDone = "N";
        }

        radioGroupListener();
        numberPickerListener();

        createItemDialogCancelButton.setOnClickListener(this);
        createItemDialogPositiveButton.setOnClickListener(this);

        return dialogBuilder;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == createItemDialogCancelButton.getId()) {

            if (createListAddItemFirstLayout.getVisibility() == View.VISIBLE) {
                dialogBuilder.dismiss();
            } else if (createListAddItemFirstLayout.getVisibility() == View.GONE) {

                createListAddItemSecondLayout.startAnimation(Animations.moveOutAnimRight(getActivity()));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createListAddItemSecondLayout.setVisibility(View.GONE);
                        createListAddItemFirstLayout.setVisibility(View.VISIBLE);
                        createListAddItemFirstLayout.startAnimation(Animations.moveInAnimRight(getActivity()));
                    }
                }, 200);

                createListAddItemTitleTextView.setText(getResources().getString(R.string.activity_create_items_dialog_add_item));
                createItemDialogCancelButton.setText(getResources().getString(R.string.fragment_my_lists_dialog_cancel));
                createItemDialogPositiveButton.setText(getResources().getString(R.string.activity_create_items_dialog_item_next));
            }

        } else if (v.getId() == createItemDialogPositiveButton.getId()) {

            if (createListAddItemFirstLayout.getVisibility() == View.VISIBLE) {

                if (!createItemDialogTaskEditText.getText().toString().trim().equals("")) {

                    hideKeyboard();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createListAddItemFirstLayout.startAnimation(Animations.moveOutAnimLeft(getActivity()));

                        }
                    }, 300);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createListAddItemFirstLayout.setVisibility(View.GONE);
                            createListAddItemSecondLayout.startAnimation(Animations.moveInAnimLeft(getActivity()));
                            createListAddItemSecondLayout.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                    createListAddItemTitleTextView.setText(getResources().getString(R.string.activity_create_items_dialog_item_info));
                    createItemDialogCancelButton.setText(getResources().getString(R.string.activity_create_items_dialog_item_previous));
                    createItemDialogPositiveButton.setText(getResources().getString(R.string.fragment_my_lists_dialog_done));

                } else {
                    CustomToasts.Toast(getActivity(), "Choose a task name");
                }


            } else if (createListAddItemSecondLayout.getVisibility() == View.VISIBLE) {

                // collect all data from inputs
                String itemName = createItemDialogTaskEditText.getText().toString().trim();
                String itemDetail = createItemDialogDetailsEditText.getText().toString().trim();
                if (itemDetail.equals("")) {
                    itemDetail = "-";
                }

                addItemToTitleTable(titleId, itemName, itemDetail, getNumberPickerValues(),
                        itemDone, radioGroupSelection);

                mOnAddItemCallBack.addEditItemDialogCallBack();
                dialogBuilder.dismiss();
            }
        }

    }

    private void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(createItemDialogTaskEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        inputManager.hideSoftInputFromWindow(createItemDialogDetailsEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void initialiseInputs() {

        createListAddItemTitleTextView = (TextView) dialogBuilder.findViewById(R.id.createListAddItemTitleTextView);

        createListAddItemFirstLayout = (LinearLayout) dialogBuilder.findViewById(R.id.createListAddItemFirstLayout);
        createItemDialogTaskEditText = (EditText) dialogBuilder.findViewById(R.id.createItemDialogTaskEditText);
        createItemDialogDetailsEditText = (EditText) dialogBuilder.findViewById(R.id.createItemDialogDetailsEditText);

        createListAddItemSecondLayout = (LinearLayout) dialogBuilder.findViewById(R.id.createListAddItemSecondLayout);
        createItemRadioGroup = (RadioGroup) dialogBuilder.findViewById(R.id.createItemRadioGroup);
        createItemRadioButtonLow = (RadioButton) dialogBuilder.findViewById(R.id.createItemRadioButtonLow);
        createItemRadioButtonMed = (RadioButton) dialogBuilder.findViewById(R.id.createItemRadioButtonMed);
        createItemRadioButtonHigh = (RadioButton) dialogBuilder.findViewById(R.id.createItemRadioButtonHigh);
        radioGroupSelection = "L"; // initial setting
        createItemRadioGroup.check(R.id.createItemRadioButtonLow);
        createListAddItemHourPicker = (NumberPicker) dialogBuilder.findViewById(R.id.createListAddItemHourPicker);
        createListAddItemHourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        createListAddItemMinutePicker = (NumberPicker) dialogBuilder.findViewById(R.id.createListAddItemMinutePicker);
        createListAddItemMinutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMinsDisplay = new String[]{"00", "15", "30", "45"};

        createItemDialogCancelButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogCancelButton);
        createItemDialogPositiveButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogPositiveButton);
    }

    private void radioGroupListener() {

        createItemRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.createItemRadioButtonLow) {
                    radioGroupSelection = "L"; // low priority
                } else if (checkedId == R.id.createItemRadioButtonMed) {
                    radioGroupSelection = "M"; // medium priority
                } else {
                    radioGroupSelection = "H"; // high priority
                }
            }
        });

    }

    private void numberPickerListener() {

        createListAddItemHourPicker.setMinValue(0);
        createListAddItemHourPicker.setMaxValue(23);
        createListAddItemHourPicker.setWrapSelectorWheel(true);

        createListAddItemMinutePicker.setMinValue(0);
        createListAddItemMinutePicker.setMaxValue(3);
        createListAddItemMinutePicker.setDisplayedValues(numberPickerMinsDisplay);
        createListAddItemMinutePicker.setWrapSelectorWheel(true);
    }


    public String getNumberPickerValues() {

        int hours = createListAddItemHourPicker.getValue();
        // get mins by getting the position in the string array
        String mins = numberPickerMinsDisplay[createListAddItemMinutePicker.getValue()];

        return String.valueOf(hours < 10 ? "0" + hours : hours) + ":" + mins;
    }

    public void setNumberPickerValues(String duration) {

        String[] timeArray = duration.split(":");

        createListAddItemHourPicker.setValue(Integer.parseInt(timeArray[0]));

        switch (timeArray[1]) {
            case "00":
                createListAddItemMinutePicker.setValue(0);
                break;
            case "15":
                createListAddItemMinutePicker.setValue(1);
                break;
            case "30":
                createListAddItemMinutePicker.setValue(2);
                break;
            default:
                createListAddItemMinutePicker.setValue(3);
                break;
        }
    }

    public void addItemToTitleTable(String titleId, String itemName, String itemDetail,
                                    String duration, String itemDone, String itemPriority) {

        ItemsListData itemsListData = new ItemsListData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        itemsListData.setTitleId(titleId);

        itemsListData.setItem(itemName);
        itemsListData.setItemDetail(itemDetail);
        itemsListData.setDuration(duration);
        itemsListData.setDateTime(date);
        itemsListData.setItemDone(itemDone);
        itemsListData.setItemPriority(itemPriority);

        dbHelper.insertIntoItemsTable(itemsListData);
    }
}

package com.treefrogapps.TaDo;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateItemsAddEditItemDialog extends DialogFragment {

    public OnAddEditItemCallback mOnAddItemCallBack;
    
    interface OnAddEditItemCallback{
        void addEditItemDialogCallBack();
    }

    // method to call from initiating activity/fragment after screen rotation
    public void setCallBack(OnAddEditItemCallback onAddEditItemCallback){
        this.mOnAddItemCallBack = onAddEditItemCallback;
    }
    
    
    private Dialog dialogBuilder;
    private DBHelper dbHelper;
    private String titleId;

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

    

   

    public CreateItemsAddEditItemDialog(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new DBHelper(getActivity());

        dialogBuilder = new Dialog(getActivity());
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.activity_create_items_dialog_add_item);
        dialogBuilder.setCancelable(false);

        initialiseInputs();

        Bundle bundle = getArguments();
        if (bundle.getString(Constants.TITLE_ID) != null){
            // TODO - this will be for editing - populate all inputs with info from dbhelper
            titleId = bundle.getString(Constants.TITLE_ID);
        }

        radioGroupListener();
        numberPickerListener();






        return dialogBuilder;
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
        createListAddItemHourPicker = (NumberPicker) dialogBuilder.findViewById(R.id.createListAddItemHourPicker);
        createListAddItemMinutePicker = (NumberPicker) dialogBuilder.findViewById(R.id.createListAddItemMinutePicker);
        numberPickerMinsDisplay = new String[] {"0", "15", "30", "45"};

        createItemDialogCancelButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogCancelButton);
        createItemDialogPositiveButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogPositiveButton);
    }

    private void radioGroupListener() {

        createItemRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.createItemRadioButtonLow){
                    radioGroupSelection = "L"; // low priority
                } else if (checkedId == R.id. createItemRadioButtonMed){
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

        createListAddItemMinutePicker.setMinValue(0);
        createListAddItemMinutePicker.setMaxValue(3);
        createListAddItemMinutePicker.setDisplayedValues(numberPickerMinsDisplay);
    }

    public String getNumberPickerValues(){

        int hours = createListAddItemHourPicker.getValue();
        // get mins by getting the position in the string array
        int mins = Integer.parseInt(numberPickerMinsDisplay[createListAddItemMinutePicker.getValue()]);

        return String.valueOf(hours < 10 ? "0" + hours : hours) + ":" + String.valueOf(mins);
    }

    public void setNumberPickerValues(String duration){

        String[] timeArray = duration.split(":");

        createListAddItemHourPicker.setValue(Integer.parseInt(timeArray[0]));

        switch (timeArray[1]) {
            case "0":
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


}

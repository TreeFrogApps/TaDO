package com.treefrogapps.TaDo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MyListsFragmentDialog extends DialogFragment {

    public interface onDialogDonePressedCallBack {
        void updateRecyclerViewCallBack();
    }

    private DBHelper dbHelper;
    private String selected = "N";
    public onDialogDonePressedCallBack mOnDialogDonePressedCallBack;

    public MyListsFragmentDialog() {
        // default constructor
    }

    // method to call from initiating activity/fragment after screen rotation
    public void setCallBack(onDialogDonePressedCallBack mOnDialogDonePressedCallBack) {
        this.mOnDialogDonePressedCallBack = mOnDialogDonePressedCallBack;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new DBHelper(getActivity());

        final Dialog titleDialog = new Dialog(getActivity());
        titleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        titleDialog.setContentView(R.layout.fragment_my_lists_dialog);
        titleDialog.setCancelable(false);

        final EditText dialogTitleEditText = (EditText) titleDialog.findViewById(R.id.dialogTitleEditText);

        Button dialogTitleCancelButton = (Button) titleDialog.findViewById(R.id.dialogTitleCancelButton);
        dialogTitleCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleDialog.dismiss();
            }
        });

        Button dialogTitleDoneButton = (Button) titleDialog.findViewById(R.id.dialogTitleDoneButton);
        dialogTitleDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleName = dialogTitleEditText.getText().toString().trim();

                TitlesListData titlesListData = new TitlesListData();
                String checkIfTitleExists = dbHelper.checkTitleNameExists(titleName);

                // return null means no title already exists
                if (checkIfTitleExists == null && !titleName.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                    String date = sdf.format(new Date());

                    titlesListData.setTitle(titleName);
                    titlesListData.setDateTime(date);
                    dbHelper.insertIntoTitlesTable(titlesListData);
                    // start by making the list non selected - letter, not tick
                    dbHelper.insertIntoSelectedListTable(dbHelper.getTitleId(titlesListData.getTitle()), selected);

                    // callback to  MyListsFragment
                    mOnDialogDonePressedCallBack.updateRecyclerViewCallBack();
                    titleDialog.dismiss();

                } else if (titleName.equals("")) {
                    CustomToasts.Toast(getActivity(), "Choose a title name");
                } else {
                    CustomToasts.Toast(getActivity(), "Title Already Exists");
                }
            }
        });

        return titleDialog;
    }
}

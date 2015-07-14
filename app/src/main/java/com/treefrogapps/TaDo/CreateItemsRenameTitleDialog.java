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

public class CreateItemsRenameTitleDialog extends DialogFragment{


    public interface onRenameDialogCallBack {
        void updateRecyclerViewCallBack(String titleId);
    }

    private DBHelper dbHelper;
    private EditText renameDialogEditText;
    private Button cancelDialogButton;
    private Button okDialogButton;

    public String titleId;
    public onRenameDialogCallBack mOnRenameDialogCallBack;
    private Dialog dialogBuilder;


    public CreateItemsRenameTitleDialog() {
        // default constructor
    }

    // method to call from initiating activity/fragment after screen rotation
    public void setCallBack(onRenameDialogCallBack mOnRenameDialogCallBack) {
        this.mOnRenameDialogCallBack = mOnRenameDialogCallBack;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new DBHelper(getActivity());
        Bundle bundle = getArguments();
        titleId = bundle.getString(Constants.TITLE_ID);

        dialogBuilder = new Dialog(getActivity());
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setContentView(R.layout.activity_create_items_dialog_rename_title);
        dialogBuilder.setCancelable(false);

        renameDialogEditText = (EditText) dialogBuilder.findViewById(R.id.createItemDialogRenameEditText);
        cancelDialogButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogCancelButton);
        okDialogButton = (Button) dialogBuilder.findViewById(R.id.createItemDialogOkButton);

        renameDialogEditText.setText(dbHelper.getTitle(titleId));

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
                    oldTitleListData.setTitle(dbHelper.getTitle(titleId));

                    TitlesListData newTitleListData = new TitlesListData();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                    String date = sdf.format(new Date());
                    // update database with new title
                    newTitleListData.setTitle(newTitle);
                    newTitleListData.setDateTime(date);
                    dbHelper.updateTitle(oldTitleListData, newTitleListData);

                    // pass the new title name into the onDialogDonePressedCallBack for CreateItemsActivity to use
                    mOnRenameDialogCallBack.updateRecyclerViewCallBack(titleId);
                    dialogBuilder.dismiss();

                } else if (newTitle.equals("")) {
                    CustomToasts.Toast(getActivity(), "Choose a title name");
                } else {
                    CustomToasts.Toast(getActivity(), "Title Already Exists");
                }
            }
        });

        return dialogBuilder;

    }
}

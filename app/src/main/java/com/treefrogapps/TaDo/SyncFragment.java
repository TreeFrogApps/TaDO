package com.treefrogapps.TaDo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SyncFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private View rootView;
    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;

    private GoogleApiClient mGoogleApiClient;
    private DriveId driveId;
    private String driveFileId;
    private ImageView mSyncFragmentDriveLogo;

    private RadioGroup mSyncFragmentRadioGroup;
    private Button mSyncFragmentSyncButton;

    private boolean syncedState = false;

    private static int connectionType;
    // 1 = upload / amend file / overwrite file
    // 2 = download
    // 3 = delete

    public SyncFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sync, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            syncedState = savedInstanceState.getBoolean(Constants.SYNCED_STATE, false);
        }

        dbHelper = new DBHelper(getActivity());
        // build connection
        buildConnection();
        initialiseInputs();

        mSyncFragmentDriveLogo.startAnimation(Animations.moveInAnimBottom(getActivity()));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSyncFragmentRadioGroup.startAnimation(Animations.alphaFadeIn(getActivity()));
                mSyncFragmentRadioGroup.setVisibility(View.VISIBLE);
                mSyncFragmentSyncButton.setClickable(true);
                mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
            }
        }, 500);



    } // End of onActivityCreated

    public void buildConnection() {
        // ONE TWO THREE 1.
        // build the api client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(SyncFragment.this)
                .addOnConnectionFailedListener(SyncFragment.this)
                .build();
    }

    public void syncDrive(){
        // try to sync
        if (mGoogleApiClient.isConnected()) {
            Drive.DriveApi.requestSync(mGoogleApiClient).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                @Override
                public void onResult(com.google.android.gms.common.api.Status status) {
                    if (!status.getStatus().isSuccess()) {
                        Log.e("SYNCING", "ERROR " + status.getStatusMessage() + " " + status.getStatusCode());
                        Toast.makeText(getActivity(), "SYNCING ERROR : " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        syncedState = false;
                        mGoogleApiClient.disconnect();
                        mSyncFragmentSyncButton.setClickable(true);
                        mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    } else {
                        Log.e("SYNCING", "SUCCESS");
                        syncedState = true;
                        new AppFolderContentsAsyncTask(getActivity()).execute();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Unable to connect to Google Drive", Toast.LENGTH_SHORT).show();
        }
    }

    public void initialiseInputs(){

        mSyncFragmentDriveLogo = (ImageView) rootView.findViewById(R.id.syncFragmentDriveLogo);
        mSyncFragmentRadioGroup = (RadioGroup) rootView.findViewById(R.id.syncFragmentRadioGroup);
        mSyncFragmentRadioGroup.check(R.id.syncFragmentUploadRadioButton);
        connectionType = 1;


        mSyncFragmentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.syncFragmentUploadRadioButton) {
                    connectionType = 1; // upload - edit
                } else if (checkedId == R.id.syncFragmentDownloadRadioButton) {
                    connectionType = 2; // download
                } else {
                    connectionType = 3; // delete
                }
            }
        });

        mSyncFragmentSyncButton = (Button) rootView.findViewById(R.id.syncFragmentSyncButton);
        mSyncFragmentSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ONE TWO THREE - 2 - attempt to connect
                connectToDrive();
                mSyncFragmentSyncButton.setClickable(false);
                mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.grey_light));
            }
        });
    }

    public void connectToDrive(){
        if (mGoogleApiClient.isConnected() && syncedState){
            new AppFolderContentsAsyncTask(getActivity()).execute();
        } else {
            buildConnection();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // ONE TWO THREE 3 - connected, execute listing file, then do task after
        if (syncedState){
            new AppFolderContentsAsyncTask(getActivity()).execute();
        } else {
            //sync files - only necessary once per visit to fragment
            syncDrive();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // ONE 2b
        if (connectionResult.hasResolution()) {

            try {
                connectionResult.startResolutionForResult(getActivity(), Constants.REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();

                Toast.makeText(getActivity(), "Unable to complete connection to GDrive", Toast.LENGTH_SHORT).show();
                mSyncFragmentSyncButton.setClickable(true);
                mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
            }
        }
    }

    // ONE TWO THREE 4a - get appfolder contents - could be temp class just for seeing what files it find
    public class AppFolderContentsAsyncTask extends AsyncTask<Void, Void, String> {

        public AppFolderContentsAsyncTask(Context context){

        }

        @Override
        protected String doInBackground(Void... params) {

            String fileCount;

            MetadataBuffer buffer = Drive.DriveApi.getAppFolder(mGoogleApiClient)
                    .listChildren(mGoogleApiClient).await().getMetadataBuffer();

            fileCount = "found " + buffer.getCount() + " file(s)";

            for (Metadata m: buffer){

                DriveId id = m.getDriveId();
                DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, id);

                file.open( mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();

                DriveResource.MetadataResult result = file.getMetadata(mGoogleApiClient).await();
                Metadata metadata = result.getMetadata();

                // Get the modified date
                metadata.getModifiedDate();
            }

            buffer.release();
            return fileCount;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("DRIVE FILE COUNT ", s);

            // after contents listed - continue with sync task based on radio group selection
            switch (connectionType){
                case 1 : uploadFile(); break;
                case 2 : downloadFile(); break;
                case 3 : deleteTheFile(); break;
                default: break;
            }
        }
    }


    // ONE - 4b
    private void uploadFile() {

        // search for existing file
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "main_database.db"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"))
                .build();

        // execute query to look if file already exists
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(searchFileCallBack);

    }

    // TWO - 4c
    private void downloadFile() {
        // search for existing file
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "main_database.db"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"))
                .build();

        // execute query to look if file already exists
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(searchFileCallBack);
    }

    // THREE - 4d
    private void deleteTheFile() {
        // search for existing file
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "main_database.db"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"))
                .build();

        // execute query to look if file already exists
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(searchFileCallBack);
    }

    // ONE THREE - 4b/c/d - check if file exists and either overwrite using its unique DriveId (file id) or create new file if it doesn't exist
    private ResultCallback<DriveApi.MetadataBufferResult> searchFileCallBack = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
            if (!metadataBufferResult.getStatus().isSuccess()) {
                Toast.makeText(getActivity(), "Error searching", Toast.LENGTH_SHORT).show();

            } else if (metadataBufferResult.getMetadataBuffer().getCount() > 0) {

                if (metadataBufferResult.getMetadataBuffer().get(0).getMimeType().equals("application/x-sqlite3")) {

                    // ONE THREE = 4e - a result is returned meaning the file exists and we get its unique ID
                    driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                    Log.e("DRIVE ID FOUND", driveId.encodeToString());
                    // start async task to edit / overwrite contents of file
                    DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, driveId);

                    metadataBufferResult.release();

                    if (connectionType == 1) {
                        new EditContentsAsyncTask(getActivity()).execute(driveFile);
                    } else if (connectionType == 2) {
                        new RetrieveFileAsyncTask(getActivity()).execute(driveFile);
                    } else if (connectionType == 3) {
                        new DeleteFileAsyncTask(getActivity()).execute(driveFile);
                    }
                }


            } else if (connectionType == 1) {

                metadataBufferResult.release();
                // ONE 5
                // new drive contents - file doesn't exist yet
                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
            } else {
                metadataBufferResult.release();
                Toast.makeText(getActivity(), "NO MATCHING ENTRIES TO main_database.db", Toast.LENGTH_SHORT).show();

                mSyncFragmentSyncButton.setClickable(true);
                mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));

            }
        }
    };


    // ONE 5 - get drive contents callback
    private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

            if (!driveContentsResult.getStatus().isSuccess()) {
                Toast.makeText(getActivity(), "Error getting drive contents", Toast.LENGTH_SHORT).show();


            } else {
                // Get an outputStream for the contents
                OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                try {
                    FileInputStream inputStream = new FileInputStream(getActivity().getDatabasePath("main_database.db"));

                    // write the input stream into the drive output stream
                    byte[] byteBuffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(byteBuffer)) > 0) {
                        outputStream.write(byteBuffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // set its title and mime type
                MetadataChangeSet metaDataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("application/x-sqlite3").setTitle("main_database.db").build();

                // ONE - 4h
                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, metaDataChangeSet, driveContentsResult.getDriveContents())
                        .setResultCallback(fileUploadCallBack);
            }
        }
    };


    // ONE - 5 - check uploaded ok
    private ResultCallback<DriveFolder.DriveFileResult> fileUploadCallBack = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult driveFileResult) {

            if (!driveFileResult.getStatus().isSuccess()) {
                Toast.makeText(getActivity(), "Error creating file", Toast.LENGTH_SHORT).show();

            } else {
                driveFileId = driveFileResult.getDriveFile().getDriveId().encodeToString();
                Log.e("DRIVE FILE ID ", driveFileId);
                Toast.makeText(getActivity(), "File uploaded successfully", Toast.LENGTH_SHORT).show();

                mSyncFragmentSyncButton.setClickable(true);
                mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
            }
        }
    };


    //ONE - 5 - edit contents using async task
    public class EditContentsAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
        }

        @Override
        protected Boolean doInBackground(DriveFile[] driveFile) {

            DriveFile fileToEdit = driveFile[0];

            try {
                DriveApi.DriveContentsResult driveContentsResult =
                        fileToEdit.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();

                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }

                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();

                FileInputStream inputStream = new FileInputStream(getActivity().getDatabasePath("main_database.db"));

                byte[] byteBuffer = new byte[1024];
                int length;
                while ((length = inputStream.read(byteBuffer)) > 0) {
                    outputStream.write(byteBuffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                com.google.android.gms.common.api.Status status =
                        driveContents.commit(mGoogleApiClient, null).await();

                return status.getStatus().isSuccess();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (!result) {
                Toast.makeText(getActivity(), "Error whilst editing contents", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Successfully edited contents", Toast.LENGTH_SHORT).show();
            }

            mSyncFragmentSyncButton.setClickable(true);
            mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
        }
    }

    // TWO - 5 - asynctask to retrieve file and write into database file
    public class RetrieveFileAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

        public RetrieveFileAsyncTask(Context context) {
        }

        @Override
        protected Boolean doInBackground(DriveFile[] driveFile) {

            DriveFile fileToDownload = driveFile[0];

            try {
                DriveApi.DriveContentsResult driveContentsResult
                        = fileToDownload.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();

                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }

                File databaseFile;

                if (getActivity().getDatabasePath("main_database.db").exists()) {
                    databaseFile = getActivity().getDatabasePath("main_database.db");
                } else {
                    return false;
                }

                OutputStream outputStream = new FileOutputStream(databaseFile);

                DriveContents driveContents = driveContentsResult.getDriveContents();
                InputStream inputStream = driveContents.getInputStream();

                byte[] byteBuffer = new byte[1024];
                int length;
                while ((length = inputStream.read(byteBuffer)) > 0) {
                    outputStream.write(byteBuffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                return true;

            } catch (IOException eIO) {
                eIO.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (!result) {
                Toast.makeText(getActivity(), "Error whilst writing database locally", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Successfully downloaded database", Toast.LENGTH_SHORT).show();
            }

            mSyncFragmentSyncButton.setClickable(true);
            mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
        }
    }


    // THREE - 5  - asynctask to delete the file from g drive
    public class DeleteFileAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

        public DeleteFileAsyncTask(Context context) {
        }

        @Override
        protected Boolean doInBackground(DriveFile[] driveFile) {

            com.google.android.gms.common.api.Status deleteStatus
                    = driveFile[0].delete(mGoogleApiClient).await();

            return deleteStatus.getStatus().isSuccess();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (!result) {
                Toast.makeText(getActivity(), "Error whilst deleting", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Successfully deleted File", Toast.LENGTH_SHORT).show();
            }

            mSyncFragmentSyncButton.setClickable(true);
            mSyncFragmentSyncButton.setTextColor(getResources().getColor(R.color.primaryColor));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("CALLED", "OnActivity Result");

        switch (requestCode) {
            // ONE - 2c
            case Constants.REQUEST_CODE_RESOLUTION:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Constants.SYNCED_STATE, syncedState);
    }
}

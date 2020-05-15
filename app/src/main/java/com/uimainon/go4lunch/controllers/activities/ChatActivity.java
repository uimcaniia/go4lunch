package com.uimainon.go4lunch.controllers.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.MessageHelper;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.RecyclerView.ChatAdapter;
import com.uimainon.go4lunch.models.Message;
import com.uimainon.go4lunch.models.User;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_worker_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_worker_chat_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_worker_chat_message_edit_text)
    EditText editTextMessage;
    @BindView(R.id.activity_worker_chat_image_chosen_preview) ImageView imageViewPreview;

    @BindView(R.id.activity_worker_chat_restaurant_chat_button) ImageButton btnchatRestau;
    @BindView(R.id.activity_worker_chat_work_chat_button) ImageButton btnChatWorker;
    @BindView(R.id.activity_worker_chat_commute_chat_button) ImageButton btnChatCommute;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter workerChatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String currentChatName;
    private Toolbar toolbar;
    // 1 - Uri of image selected by user
    private Uri uriImageSelected;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_RESTAURANT = "restaurant";
    private static final String CHAT_NAME_WORK = "work";
    private static final String CHAT_NAME_COMMUTE = "commute";

    // 1 - STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        this.configureRecyclerView(CHAT_NAME_RESTAURANT);
        this.setTitle("Chat restaurant!");
        btnchatRestau.setBackgroundColor(getResources().getColor(R.color.colorChatDark));
        configureToolbar();
        this.getCurrentUserFromFirestore();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 6 - Calling the appropriate method after activity result
        this.handleResponse(requestCode, resultCode, data);
    }
    @Override
    public int getFragmentLayout() { return R.layout.activity_chat; }
    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.activity_worker_chat_send_button)
    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null){
            // Check if the ImageView is set
            if (this.imageViewPreview.getDrawable() == null) {
                // SEND A TEXT MESSAGE
                MessageHelper.createMessageForChat(editTextMessage.getText().toString(), this.currentChatName, modelCurrentUser).addOnFailureListener(this.onFailureListener());
                this.editTextMessage.setText("");
            } else {
                // SEND A IMAGE + TEXT IMAGE
                this.uploadPhotoInFirebaseAndSendMessage(editTextMessage.getText().toString());
                this.editTextMessage.setText("");
                this.imageViewPreview.setImageDrawable(null);
            }
        }
    }

    @OnClick({ R.id.activity_worker_chat_restaurant_chat_button, R.id.activity_worker_chat_work_chat_button, R.id.activity_worker_chat_commute_chat_button})
    public void onClickChatButtons(ImageButton imageButton) {
        // 8 - Re-Configure the RecyclerView depending chosen chat
        switch (Integer.valueOf(imageButton.getTag().toString())){
            case 10:
                this.setTitle("Chat restaurant!");
                btnchatRestau.setBackgroundColor(getResources().getColor(R.color.colorChatDark));
                btnChatWorker.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnChatCommute.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                this.configureRecyclerView(CHAT_NAME_RESTAURANT);
                break;
            case 20:
                this.setTitle("Chat work!");
                btnchatRestau.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnChatWorker.setBackgroundColor(getResources().getColor(R.color.colorChatDark));
                btnChatCommute.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                this.configureRecyclerView(CHAT_NAME_WORK);
                break;
            case 30:
                this.setTitle("Chat commute!");
                btnchatRestau.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnChatWorker.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnChatCommute.setBackgroundColor(getResources().getColor(R.color.colorChatDark));
                this.configureRecyclerView(CHAT_NAME_COMMUTE);
                break;
        }
    }

    // --------------------
    // PERMISSION
    // --------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    @OnClick(R.id.activity_worker_chat_add_file_button)
    // 5 - Calling the appropriate method
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() { this.chooseImageFromPhone(); }
    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.workerChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid());
        workerChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(workerChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.workerChatAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------
    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.workerChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        // 3 - Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageViewPreview);
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

     // 1 - Upload a picture in Firebase and send a message
     private void uploadPhotoInFirebaseAndSendMessage(final String message) {
         if(uriImageSelected != null)
         {
             final ProgressDialog progressDialog = new ProgressDialog(this);
             progressDialog.setTitle("Uploading...");
             progressDialog.show();

             StorageReference ref = storageReference.child("images"+UUID.randomUUID().toString());
             ref.putFile(uriImageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             progressDialog.dismiss();
                             Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                             ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     String downloadUrl = uri.toString();
                                     // B - SAVE MESSAGE IN FIRESTORE
                                     MessageHelper.createMessageWithImageForChat(downloadUrl, message, currentChatName, modelCurrentUser).addOnFailureListener(onFailureListener());
                                 }
                             });
                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             progressDialog.dismiss();
                             Toast.makeText(ChatActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     })
                     .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                             double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                     .getTotalByteCount());
                             progressDialog.setMessage("Uploaded "+(int)progress+"%");
                         }
                     });
         }
    }
}


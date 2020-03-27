package com.uimainon.go4lunch.controllers.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.MessageHelper;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.RecyclerView.ChatAdapter;
import com.uimainon.go4lunch.models.Message;
import com.uimainon.go4lunch.models.User;

import butterknife.BindView;
import butterknife.OnClick;
/*import com.uimainon.go4lunch.models.User;*/

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_worker_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_worker_chat_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_worker_chat_message_edit_text)
    EditText editTextMessage;
    @BindView(R.id.activity_worker_chat_image_chosen_preview) ImageView imageViewPreview;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter workerChatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String currentChatName;
    private Toolbar toolbar;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.configureRecyclerView(CHAT_NAME_ANDROID);
     /*   this.configureToolBar();*/
        this.getCurrentUserFromFirestore();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_chat; }
    // 1 - Configure Toolbar

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.activity_worker_chat_send_button)
    public void onClickSendMessage() { }

    @OnClick({ R.id.activity_worker_chat_android_chat_button, R.id.activity_worker_chat_firebase_chat_button, R.id.activity_worker_chat_bug_chat_button})
    public void onClickChatButtons(ImageButton imageButton) {
        // 8 - Re-Configure the RecyclerView depending chosen chat
        switch (Integer.valueOf(imageButton.getTag().toString())){
            case 10:
                this.configureRecyclerView(CHAT_NAME_ANDROID);
                break;
            case 20:
                this.configureRecyclerView(CHAT_NAME_FIREBASE);
                break;
            case 30:
                this.configureRecyclerView(CHAT_NAME_BUG);
                break;
        }
    }

    @OnClick(R.id.activity_worker_chat_add_file_button)
    public void onClickAddFile() { }

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
}


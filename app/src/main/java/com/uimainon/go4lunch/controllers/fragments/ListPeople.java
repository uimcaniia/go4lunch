package com.uimainon.go4lunch.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.controllers.RecyclerView.ListPeopleAdapter;
import com.uimainon.go4lunch.models.User;

public class ListPeople extends Fragment implements ListPeopleAdapter.Listener{

    private RecyclerView mRecyclerView;
    private String idUser;

    public static ListPeople newInstance() {
        return new ListPeople();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_people, container, false);
        Context context = rootView.getContext();
        assert this.getArguments() != null;
        idUser = this.getArguments().getString("idUser");
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_workmates);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        configureRecyclerView(context);
        return rootView;
    }

    private void configureRecyclerView(Context context){
        //Configure Adapter & RecyclerView
        ListPeopleAdapter listPeopleAdapter = new ListPeopleAdapter(generateOptionsForAdapter(UserHelper.getAllUser()), Glide.with(context), this, idUser);
        this.mRecyclerView.setAdapter(listPeopleAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){

        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------
    @Override
    public void onDataChanged() {

    }

}

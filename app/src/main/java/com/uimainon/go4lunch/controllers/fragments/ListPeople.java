package com.uimainon.go4lunch.controllers.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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

import java.util.Objects;

public class ListPeople extends Fragment implements ListPeopleAdapter.Listener{

    private RecyclerView mRecyclerView;
    private String idUser;
    private MenuItem searchItem;
    private SearchView sv;
    private Menu menu;

    public static ListPeople newInstance() {
        return new ListPeople();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Available workmates!");
        setHasOptionsMenu(true);
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        this.menu = menu;
        this.searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)  Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint(Html.fromHtml("<font color = #8D8D8D>Search workmates</font>"));
        sv.setBackgroundColor(getResources().getColor(R.color.colorBgNavBar));
        // ImageView searchIconTest=sv.findViewById(androidx.appcompat.R.id.search_src_text);
        sv.setIconifiedByDefault(false);
        sv.setSubmitButtonEnabled(false);
        assert searchManager != null;
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        int searchVoiceId = sv.getContext().getResources().getIdentifier("android:id/search_voice_btn", null, null);
        int searchId = sv.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        TextView textView = (TextView) sv.findViewById(id);
        ImageView searchIconVoice =sv.findViewById(searchVoiceId);
        ImageView searchIcon =sv.findViewById(searchId);
        textView.setTextColor(getResources().getColor(R.color.colortopBarLog));
        searchIconVoice.setColorFilter(R.color.colortopBarLog);
        searchIcon.setColorFilter(R.color.colortopBarLog);
        super.onCreateOptionsMenu(menu, inflater);
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

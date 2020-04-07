package com.uimainon.go4lunch.controllers.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.models.User;

public class ListPeopleAdapter extends FirestoreRecyclerAdapter<User, UserViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;

    //FOR COMMUNICATION
    private ListPeopleAdapter.Listener callback;

    public ListPeopleAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, ListPeopleAdapter.Listener callback, String idCurrentUser) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }
    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.updateProfileWorker(model, this.glide, idCurrentUser, holder);

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_list_workmates, parent, false);
        return new UserViewHolder.ViewHolder(view);
/*        return new UserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_list_workmates, parent, false));*/
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }


}

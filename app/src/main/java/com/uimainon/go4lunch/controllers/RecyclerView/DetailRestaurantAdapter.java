package com.uimainon.go4lunch.controllers.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.models.User;

import java.util.List;

public class DetailRestaurantAdapter extends RecyclerView.Adapter<DetailRestaurantAdapter.ViewHolder> {

    private final List<User> mUser;
    private final String idCurrentUser;

    public DetailRestaurantAdapter(List<User> items, String idCurrentUser) {
        mUser = items;
        this.idCurrentUser = idCurrentUser;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        User user = mUser.get(position);
        String userName = user.getUsername();
        String SEPARATEUR = " ";
        String[] word = userName.split(SEPARATEUR);
        String firstname = "";
        if(user.getUid().equals(idCurrentUser)) {
            firstname = " I'm joining !";
        }else{
            firstname = word[0] + " is joining ! ";
        }
        holder.textViewMessage.setText(firstname);
        // Update profile picture ImageView
        Glide.with(holder.imageViewProfile).load(user.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageViewProfile);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_liste_worker_eating, parent, false));
    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProfile;
        private TextView textViewMessage;

        public ViewHolder(View view) {
            super(view);
            imageViewProfile = (ImageView) itemView.findViewById(R.id.circle_avatar_worker_eating);
            textViewMessage = (TextView) itemView.findViewById(R.id.name_worker);
        }
    }
}

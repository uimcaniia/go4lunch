package com.uimainon.go4lunch.controllers.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.controllers.activities.DetailsRestaurantActivity;
import com.uimainon.go4lunch.models.User;

public class UserViewHolder extends RecyclerView.ViewHolder {

private ImageView imageViewProfile;
private TextView textViewMessage;
final String SEPARATEUR = " ";
private String firstname="";
private String idRestaurant = "";
/*private UserViewHolder.ClickListener mClickListener;*/

//FOR DATA
    public UserViewHolder(View itemView) {
        super(itemView);
        imageViewProfile = (ImageView) itemView.findViewById(R.id.circle_avatar_workmates);
        textViewMessage = (TextView) itemView.findViewById(R.id.name_worker);
    }

    public void updateProfileWorker(User user, RequestManager glide, String idUser, UserViewHolder holder) {
        Context context = itemView.getContext();
        String userName = user.getUsername();
        String word[] = userName.split(SEPARATEUR);

        if (!user.getIdRestaurant().equals("null")) {
            if(user.getUid().equals(idUser)) {
                firstname = " I eat to ";
            }else{
                firstname = word[0] + " is eating to ";
            }
            this.textViewMessage.setText(firstname + "(" + user.getNameRestaurant() + ")");
        } else {
            if(user.getUid().equals(idUser)) {
                firstname = " I haven't ";
            }else{
                firstname = word[0] + " hasn't ";
            }
            this.textViewMessage.setText(firstname + "decided yet");
            this.textViewMessage.setTextColor(ContextCompat.getColor(context, R.color.colorLight));
            this.textViewMessage.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        }
        // Update profile picture ImageView
        if (user.getUrlPicture() != null)
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override  // pour modifier une réunion existante
            public void onClick(View v) {
                if (!user.getIdRestaurant().equals("null")) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, DetailsRestaurantActivity.class);
                    intent.putExtra("idRestaurant", user.getIdRestaurant());
                    context.startActivity(intent);
                }
            }
        });

    }

    public static class ViewHolder extends UserViewHolder {
        public View mClickListener;
        public ViewHolder(View view) {
            super(view);
            this.mClickListener = view;
        }
    }
}

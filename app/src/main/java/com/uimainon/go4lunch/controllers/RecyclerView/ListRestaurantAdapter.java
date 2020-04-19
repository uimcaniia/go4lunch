package com.uimainon.go4lunch.controllers.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.util.List;
import java.util.Objects;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ViewHolder>{

    private List<Result> mGooglePlaceData;
    private int nbrWorker = 0;
    private Double firstStar = (1.00/3)*1;
    private Double secondStar = (1.00/3)*2;
    private Double thirdStar = (1.00/3)*3;

    public ListRestaurantAdapter(List<Result> mGooglePlaceData, int nbrWorker) {
      /*  super(itemView);*/
        this.mGooglePlaceData = mGooglePlaceData;
        this.nbrWorker = nbrWorker;
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_list_restaurant, parent, false);
        return new ListRestaurantAdapter.ViewHolder(view);
    }

    private int searchNbrStrars(Double nbrVote){
         double nbr = 1.00/nbrVote;

         int nbrStar = 0;
         if((nbr == 1.00)||(nbr >= 0.75)){
             nbrStar = 3;
         }
         if((nbr < 0.75)&&(nbr >= 0.50)){
             nbrStar = 2;
         }
        if((nbr < 0.50)&&(nbr >= 0.25)){
            nbrStar = 1;
        }
        if(nbr < 0.25){
            nbrStar = 0;
        }
        return nbrStar;
    }

    private void showNumberStarOnView(ViewHolder holder, int nbrStars){
        if(nbrStars == 3) {
            holder.restaurantLikeFirst.setVisibility(View.VISIBLE);
            holder.restaurantLikeSecond.setVisibility(View.VISIBLE);
            holder.restaurantLikeThird.setVisibility(View.VISIBLE);
        }
        if(nbrStars == 2){
            holder.restaurantLikeFirst.setVisibility(View.VISIBLE);
            holder.restaurantLikeSecond.setVisibility(View.VISIBLE);
            holder.restaurantLikeThird.setVisibility(View.GONE);
        }
        if(nbrStars == 1){
            holder.restaurantLikeFirst.setVisibility(View.VISIBLE);
            holder.restaurantLikeSecond.setVisibility(View.GONE);
            holder.restaurantLikeThird.setVisibility(View.GONE);
        }
        if(nbrStars == 0){
            holder.restaurantLikeFirst.setVisibility(View.GONE);
            holder.restaurantLikeSecond.setVisibility(View.GONE);
            holder.restaurantLikeThird.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final ListRestaurantAdapter.ViewHolder holder, int position) {
       /* Context context = ViewHolder.getContext();*/
        Result result = mGooglePlaceData.get(position);
        holder.itemNameRestaurant.setText(result.getName());
        holder.itemAdressRestaurant.setText(result.getVicinity());
        int nbrStars = searchNbrStrars(nbrWorker/result.getNbrVote());
        showNumberStarOnView(holder, nbrStars);
        holder.itemAdressRestaurant.setText(result.getVicinity());
        int castDistance = (int) result.getDistance();
        holder.itemDistanceRestaurant.setText(""+castDistance+"m");

        if(result.getNbrworkerEating() == 0){
            holder.itemNbrPeopleRestaurant.setVisibility(View.GONE);
            holder.nbrPeoplePicto.setVisibility(View.GONE);
        }else{
            holder.itemNbrPeopleRestaurant.setVisibility(View.VISIBLE);
            holder.itemNbrPeopleRestaurant.setText("("+result.getNbrworkerEating()+")");
            holder.nbrPeoplePicto.setVisibility(View.VISIBLE);
        }

       // System.out.println("object résult  =>" +result.getOpenHour() +" "+result.getOpenMinute());
        holder.itemHourRestaurant.setText(result.getOpeningHourDetails());
        if((Objects.equals(result.getOpeningHourDetails(), "Close today"))||(Objects.equals(result.getOpeningHourDetails(), "Close"))){
            holder.itemHourRestaurant.setTextColor(ContextCompat.getColor(holder.context, R.color.colorPrimaryDark));
        }

        if(result.getPhotoReference()!=null){
            Glide.with(holder.restaurantImage).load(result.getPhotoReference())
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.restaurantImage);
        }else{
           holder.restaurantImage.setImageResource(R.drawable.ic_logo_auth);
        }

    }

    @Override
    public int getItemCount() {
        return mGooglePlaceData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView restaurantImage;
        private ImageView restaurantLikeFirst;
        private ImageView restaurantLikeSecond;
        private ImageView restaurantLikeThird;
        private ImageView nbrPeoplePicto;

        private TextView itemNbrPeopleRestaurant;
        private TextView itemNameRestaurant;
        private TextView itemTypeRestaurant;
        private TextView itemAdressRestaurant;
        private TextView itemHourRestaurant;
        private TextView itemDistanceRestaurant;
        Context context;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            restaurantImage = (ImageView) itemView.findViewById(R.id.restaurant_image);
            restaurantLikeFirst = (ImageView) itemView.findViewById(R.id.restaurant_like_first);
            restaurantLikeSecond = (ImageView) itemView.findViewById(R.id.restaurant_like_second);
            restaurantLikeThird = (ImageView) itemView.findViewById(R.id.restaurant_like_third);
            nbrPeoplePicto = (ImageView) itemView.findViewById(R.id.nbr_people_picto);

            itemNbrPeopleRestaurant = (TextView) itemView.findViewById(R.id.item_nbr_people_restaurant);
            itemNameRestaurant = (TextView) itemView.findViewById(R.id.item_name_restaurant);
            itemDistanceRestaurant = (TextView)itemView.findViewById(R.id.item_distance_restaurant);
          /*  itemTypeRestaurant = (TextView) itemView.findViewById(R.id.item_type_restaurant);*/
            itemAdressRestaurant = (TextView) itemView.findViewById(R.id.contain_type_and_adress_restaurant);
            itemHourRestaurant = (TextView) itemView.findViewById(R.id.item_hour_restaurant);
        }
    }
}

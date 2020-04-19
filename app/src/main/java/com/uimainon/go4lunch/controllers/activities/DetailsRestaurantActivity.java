package com.uimainon.go4lunch.controllers.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.api.VoteHelper;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.RecyclerView.DetailRestaurantAdapter;
import com.uimainon.go4lunch.models.User;
import com.uimainon.go4lunch.models.Vote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class DetailsRestaurantActivity extends BaseActivity {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.list_worker_eating)
    RecyclerView recyclerViewDetailsRestaurant;
    // 2 - Declaring Adapter and data
    private DetailRestaurantAdapter detailRestauranttAdapter;
    @Nullable
    //@BindView(R.id.activity_detail_restaurant_text_view_recycler_view_empty)
    private TextView textViewRecyclerViewEmpty;

    @BindView(R.id.details_name_restaurant)
    TextView textViewNameRestaurant;
    @BindView(R.id.details_adress_restaurant)
    TextView textViewAdressRestaurant;
    @BindView(R.id.details_phone_restaurant)
    ImageButton imgPhoneRestaurant;
    @BindView(R.id.details_website_restaurant)
    ImageButton imgWebsiteRestaurant;
    @BindView(R.id.details_stars_restaurant)
    ImageButton imgLikeRestaurant;
    @BindView(R.id.btn_validate_restaurant)
    FloatingActionButton btnValidRestaurant;


    // FOR DATA
    // 2 - Declaring Adapter and data
    private String idRestaurant;
    private String idUser = "";
    private String idChoiceRestaurant = "";
    private String nameRestaurant= "";
    private String nameChoiceRestaurant= "";
    private Object GoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textViewRecyclerViewEmpty = (TextView) findViewById(R.id.activity_detail_restaurant_text_view_recycler_view_empty);

        this.updateUIWhenCreating();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        if (!Places.isInitialized()) {
            String gApiKey = this.getString(R.string.google_maps_key);
            Places.initialize(this, gApiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        idRestaurant = bundle.getString("idRestaurant");
        this.configureRecyclerView();
        configBtnLikeThisRestaurant();
        ImageView pictureRestaurant = (ImageView) findViewById(R.id.restaurant_picture);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.TYPES, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(idRestaurant, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            configBtnTelToRestaurant(place.getPhoneNumber());
            configBtnWebSiteToRestaurant(place.getWebsiteUri());
            textViewNameRestaurant.setText(place.getName());
            nameRestaurant = place.getName();
            configBtnValidChoiceRestaurant();
            String Adress = place.getAddress();
            String word[] = Adress.split(",");
            textViewAdressRestaurant.setText(word[0]);
            // Get the photo metadata.
            if(place.getPhotoMetadatas() != null){
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
/*                    System.out.println("Photo found: ");*/
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    pictureRestaurant.setImageBitmap(bitmap);

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        System.out.println("Photo not found: ");
                    }
                });
            }else{
                pictureRestaurant.setImageResource(R.drawable.ic_logo_auth);
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                System.out.println("Place not found: ");
            }
        });
    }

    private void configStars(int nbrUser) {
        Query listVote = VoteHelper.getAllVotesForRestaurants(idRestaurant);
        listVote.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private Double nbrVote = 0.00;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Vote voteModel = document.toObject(Vote.class);
                        this.nbrVote += 1.00;
                    }
                    showStars(nbrUser, nbrVote);
                }
            }
        });
    }

    private void showStars(int nbrUser, Double nbrVote) {
        ImageView starsOne = (ImageView) findViewById(R.id.detail_like_restaurant_first);
        ImageView starsTwo = (ImageView) findViewById(R.id.detail_like_restaurant_Second);
        ImageView starsThree = (ImageView) findViewById(R.id.detail_like_restaurant_third);

        double nbr = 1.00/(nbrUser/nbrVote);

        if((nbr == 1.00)||(nbr >= 0.75)){
            starsOne.setVisibility(View.VISIBLE);
            starsTwo.setVisibility(View.VISIBLE);
            starsThree.setVisibility(View.VISIBLE);
        }
        if((nbr < 0.75)&&(nbr >= 0.50)){
            starsOne.setVisibility(View.VISIBLE);
            starsTwo.setVisibility(View.VISIBLE);
            starsThree.setVisibility(View.GONE);
        }
        if((nbr < 0.50)&&(nbr >= 0.25)){
            starsOne.setVisibility(View.VISIBLE);
            starsTwo.setVisibility(View.GONE);
            starsThree.setVisibility(View.GONE);
        }
        if(nbr < 0.25){
            starsOne.setVisibility(View.GONE);
            starsTwo.setVisibility(View.GONE);
            starsThree.setVisibility(View.GONE);
        }

    }

    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(){
        //Configure Adapter & RecyclerView
        Query query = UserHelper.getAllUser();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> listUser = new ArrayList<>();
                if (task.isSuccessful()) {
                    configStars(Objects.requireNonNull(task.getResult()).size());
                    for (DocumentSnapshot document : task.getResult()) {
                        User userModel = document.toObject(User.class);
                        assert userModel != null;
                        if(userModel.getIdRestaurant().equals(idRestaurant)){
                            listUser.add(userModel);
                        }
                    }
                    if(listUser.size() == 0){
                        assert textViewRecyclerViewEmpty != null;
                        textViewRecyclerViewEmpty.setVisibility(View.VISIBLE);
                        recyclerViewDetailsRestaurant.setVisibility(View.GONE);
                    }else{
                        assert textViewRecyclerViewEmpty != null;
                        textViewRecyclerViewEmpty.setVisibility(View.GONE);
                        recyclerViewDetailsRestaurant.setVisibility(View.VISIBLE);
                        detailRestauranttAdapter = new DetailRestaurantAdapter(listUser, idUser);
                        recyclerViewDetailsRestaurant.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerViewDetailsRestaurant.setAdapter(detailRestauranttAdapter);
                    }
                }
            }
        });
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_details_restaurants;
    }
    private void updateUIWhenCreating(){
        if (this.getCurrentUser() != null){
            idUser = this.getCurrentUser().getUid();
        }
    }
    private void configBtnValidChoiceRestaurant() {
        Task documentUser = UserHelper.getUser(idUser);
        documentUser.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()) {
                     DocumentSnapshot document = task.getResult();
                     if (document.exists()) {
                         User userConnect = document.toObject(User.class);
                         idChoiceRestaurant = userConnect.getIdRestaurant();
                         changeValidChoiceBtn();
                     } else {
                         System.out.println( "No such document");
                     }
                 } else {
                     System.out.println("get failed");
                 }
             }
         });
    }
    private void changeValidChoiceBtn(){
        updateValidBtn();
        btnValidRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if((idChoiceRestaurant.equals("null"))||(!idChoiceRestaurant.equals(idRestaurant))){
                    idChoiceRestaurant = idRestaurant;
                    nameChoiceRestaurant = nameRestaurant;
                }else{
                    idChoiceRestaurant = "null";
                    nameChoiceRestaurant ="null";
                }
                UserHelper.updateRestaurant(idChoiceRestaurant, idUser, nameChoiceRestaurant);
                updateValidBtn();
                configureRecyclerView();
            }
        });
    }
    private void updateValidBtn(){
        if((idChoiceRestaurant.equals("null"))||(!idChoiceRestaurant.equals(idRestaurant))){
            btnValidRestaurant.setIcon(R.drawable.ic_check_24px);
        }else{
            btnValidRestaurant.setIcon(R.drawable.ic_green_check);
        }
    }
    private void configBtnWebSiteToRestaurant(Uri websiteUri) {
        if(websiteUri == null){
            imgWebsiteRestaurant.setImageResource(R.drawable.ic_link_off_24px);
        }else {
            imgWebsiteRestaurant.setImageResource(R.drawable.ic_language_24px);
            imgWebsiteRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
                    // Verify that the intent will resolve to an activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        // Here we use an intent without a Chooser unlike the next example
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void configBtnTelToRestaurant(String phoneNumber) {
        if(phoneNumber == null){
            imgPhoneRestaurant.setImageResource(R.drawable.ic_phone_disabled_24px);
        }else{
            imgPhoneRestaurant.setImageResource(R.drawable.ic_call_24px);
            imgPhoneRestaurant.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent appel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber.replaceAll(" ", "")));
                    startActivity(appel);
                }
            });
        }
    }
    private void configBtnLikeThisRestaurant() {
        Query listVote = VoteHelper.getAllVotesForRestaurants(idRestaurant);
        listVote.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!Objects.requireNonNull(task.getResult()).isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idVote = ""+document.getId();
                            if(idVote.equals(idUser)){
                                configAddOrRemoveVoteDesignButton(true);
                                break;
                            }else{
                               /* System.out.println("vote for this restaurant but not you");*/
                                configAddOrRemoveVoteDesignButton(false);
                            }
                        }
                    }else{
                  /*      System.out.println("No vote for this restaurant");*/
                        configAddOrRemoveVoteDesignButton(false);
                    }
                } else {
                  /*  System.out.println("Error getting documents");*/
                    configAddOrRemoveVoteDesignButton(false);
                }
            }
        });

    }

    private void configAddOrRemoveVoteDesignButton(Boolean isVote) {
   /*     System.out.println(isVote);*/
        if(!isVote){
            imgLikeRestaurant.setImageResource(R.drawable.ic_star_border_24px);
            configAddOrRemoveVote(false);
        }else{
            imgLikeRestaurant.setImageResource(R.drawable.ic_star_plain_24px);
            configAddOrRemoveVote(true);
        }
    }

    private void configAddOrRemoveVote(Boolean isVote){
        imgLikeRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!isVote){
                    Toast.makeText(getApplicationContext(), "Your vote is added !", Toast.LENGTH_SHORT).show();
                    VoteHelper.createVoteForRestaurant(idRestaurant, idUser);
                    configAddOrRemoveVoteDesignButton(true);
                }else{
                    Toast.makeText(getApplicationContext(), "Your vote is remove !", Toast.LENGTH_SHORT).show();
                    VoteHelper.deleteVoteForRestaurant(idRestaurant, idUser);
                    configAddOrRemoveVoteDesignButton(false);
                }
            }
        });
    }
}

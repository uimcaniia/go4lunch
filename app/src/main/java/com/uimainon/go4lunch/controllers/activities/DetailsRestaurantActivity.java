package com.uimainon.go4lunch.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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
import com.uimainon.go4lunch.models.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class DetailsRestaurantActivity extends BaseActivity {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.list_worker_eating)
    RecyclerView recyclerViewRestaurant;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.updateUIWhenCreating();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        configurePictureRestaurant(bundle.getString("photo_reference"));

        if (!Places.isInitialized()) {
            String gApiKey = this.getString(R.string.google_maps_key);
            Places.initialize(this, gApiKey);
        }
        PlacesClient placesClient = Places.createClient(this);
        idRestaurant = bundle.getString("idRestaurant");
        configBtnValidChoiceRestaurant();
        configBtnLikeThisRestaurant();

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.TYPES, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(idRestaurant, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            configBtnTelToRestaurant(place.getPhoneNumber());
            configBtnWebSiteToRestaurant(place.getWebsiteUri());
            textViewNameRestaurant.setText(place.getName());
            textViewAdressRestaurant.setText(" - "+place.getAddress());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                System.out.println("Place not found: ");
            }
        });
    }

    private void configurePictureRestaurant(String photo_reference) {
        ImageView pictureRestaurant = (ImageView) findViewById(R.id.restaurant_picture);
        if(photo_reference.equals("null")){
            pictureRestaurant.setImageResource(R.drawable.ic_logo_auth);
        }else{
            String urlPicture = getUrlPicture(photo_reference);
            Glide.with(this)
                .load(urlPicture)
                .into(pictureRestaurant);
        }
    }

    private String getUrlPicture(String photoReference) {

        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        googleUrl.append("maxheight=400");
        googleUrl.append("&photoreference=").append(photoReference);
        googleUrl.append("&key=").append(getString(R.string.google_maps_key));
        return googleUrl.toString();
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
                         /*System.out.println(idChoiceRestaurant);*/
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
                }else{
                    idChoiceRestaurant = "null";
                }
                UserHelper.updateRestaurant(idChoiceRestaurant, idUser);
                updateValidBtn();
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
                            }
                        }
                    }else{
                        System.out.println("No vote for this restaurant");
                        configAddOrRemoveVoteDesignButton(false);
                    }
                } else {
                    System.out.println("Error getting documents");
                    configAddOrRemoveVoteDesignButton(false);
                }
            }
        });

    }

    private void configAddOrRemoveVoteDesignButton(Boolean isVote) {
        System.out.println(isVote);
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

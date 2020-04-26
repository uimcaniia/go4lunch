package com.uimainon.go4lunch.api;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.uimainon.go4lunch.models.Vote;

public class VoteHelper {
    private static final String COLLECTION_NAME = "votes";

    // --- GET ---
    public static Query getAllVotesForRestaurants(String idRestaurant){
        return RestaurantHelper.getRestaurantsCollection()
                .document(idRestaurant)
                .collection(COLLECTION_NAME);
               }

/*    public static Query getAllVoteOfTheUser(){
        CollectionReference mAllRestaurant = RestaurantHelper.getRestaurantsCollection();
        return RestaurantHelper.getRestaurantsCollection();
    }*/

    public static void createVoteForRestaurant(String idRestaurant, String idUser) {
        DocumentReference mFirestoreProfiles = RestaurantHelper.getRestaurantsCollection().document(idRestaurant).collection(COLLECTION_NAME).document(idUser);
        Vote vote = new Vote(idUser);
        mFirestoreProfiles.set(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(getApplicationContext(), "Document written successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // --- DELETE ---
    public static Task<Void> deleteVoteForRestaurant(String idRestaurant, String idUser) {
        return RestaurantHelper.getRestaurantsCollection()
                .document(idRestaurant)
                .collection(COLLECTION_NAME)
                .document(idUser)
                .delete();
    }
}

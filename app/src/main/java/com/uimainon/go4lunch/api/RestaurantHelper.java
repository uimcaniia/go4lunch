package com.uimainon.go4lunch.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
/*    public static Task<DocumentReference> createRestaurant(String idRestaurant, User userSenderVote){
        // 1 - Create the Restaurant object
       // Restaurant restaurant = new Restaurant(idRestaurant);
       // RestaurantHelper.getRestaurantsCollection().document(uid).set(userToCreate);

        // 2 - Store Message to Firestore
*//*        return RestaurantHelper.getRestaurantsCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
                *//*

    }*/

    // --- GET ---
/*    public static Task<DocumentSnapshot> getVote(String idRestaurant){
        return RestaurantHelper.getRestaurantsCollection().document(idRestaurant).get();
    }
    public static Query getAllRestaurants(){
        return RestaurantHelper.getRestaurantsCollection();
    }

    // --- DELETE ---
    public static Task<Void> deleteVote(String uidUser) {
        return RestaurantHelper.getRestaurantsCollection().document(uidUser).delete();
    }*/
}

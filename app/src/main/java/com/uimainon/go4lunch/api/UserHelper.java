package com.uimainon.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uimainon.go4lunch.models.User;

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String urlPicture, String email, String idRestaurant, String nameRestaurant) {
        User userToCreate = new User(uid, username, urlPicture, email, idRestaurant, nameRestaurant);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }
    public static Query getAllUser(){
        return UserHelper.getUsersCollection();
    }
    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }
    public static Task<Void> updateLattitude(String latitude, String uid) {
       return UserHelper.getUsersCollection().document(uid).update("latitude", latitude);
    }
    public static Task<Void> updateLongitude(String longitude, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("longitude", longitude);
    }
    public static void updateRestaurant(String idRestaurant, String uid, String nameRestaurant) {
        UserHelper.getUsersCollection().document(uid).update("idRestaurant", idRestaurant);
        UserHelper.getUsersCollection().document(uid).update("nameRestaurant", nameRestaurant);
    }
    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}

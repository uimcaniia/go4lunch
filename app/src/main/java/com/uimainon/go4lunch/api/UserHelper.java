package com.uimainon.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uimainon.go4lunch.models.User;

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String urlPicture, String email) {
        User userToCreate = new User(uid, username, urlPicture, email);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }
    public static Task<Void> updateLattitude(Double latitude, String uid) {
       return UserHelper.getUsersCollection().document(uid).update("latitude", latitude);
    }
    public static Task<Void> updateLongitude(Double longitude, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("longitude", longitude);
    }
    public static Task<Void> updateRestaurant(int idRestaurant, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("idRestaurant", idRestaurant);
    }
    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}

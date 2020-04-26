package com.uimainon.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uimainon.go4lunch.models.Preference;

public class PreferenceHelper {
    private static final String COLLECTION_NAME = "preferences";
    // --- COLLECTION REFERENCE ---

    public static CollectionReference getPreferenceCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    // --- CREATE ---
    public static Task<Void> createPreference(int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday, int hour, int minute, String idUserPref) {
        Preference userPreference = new Preference(monday, tuesday, wednesday, thursday, friday,saturday, sunday, hour, minute ,idUserPref);
     return PreferenceHelper.getPreferenceCollection().document(idUserPref).set(userPreference);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getPreferenceForUser(String idUser){
        return PreferenceHelper.getPreferenceCollection().document(idUser).get();
    }

    public static void updatePreferenceNotification(String parametre, String idUserPref, int dayNotif) {
        PreferenceHelper.getPreferenceCollection().document(idUserPref).update(parametre, dayNotif);
    }
    // --- DELETE ---
    public static void deleteUser(String uid) {
        PreferenceHelper.getPreferenceCollection().document(uid).delete();
    }
}

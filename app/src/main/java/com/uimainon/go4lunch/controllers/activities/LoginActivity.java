package com.uimainon.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.base.BaseActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity  extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    //FOR DESIGN
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_activity_button_login)
    Button buttonLogin;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1252;

    //FOR DATA
    private static final int RC_SIGN_IN = 123;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Handle SignIn Activity response on activity result
       this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateUIWhenResuming();
    }

    // --------------------
    // ACTIONS
    // --------------------

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        if (this.isCurrentUserLogged()){
            this.startProfileActivity();
        } else {
            this.startSignInActivity();
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore(){
        if (this.getCurrentUser() != null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email = this.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, urlPicture, email).addOnFailureListener(this.onFailureListener());
        }
    }

    // --------------------
    // NAVIGATION
    // --------------------

    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                      /*  new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(), //twitter*/
                                        new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(), //GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startProfileActivity(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){//&& (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){//||(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                explain();//expliquer pourquoi l'autorisation est nécessaire
            } else {
                askForPermission();
            }
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
    private void explain(){
        Snackbar.make(coordinatorLayout, "Cette permission est nécessaire pour vous localiser sur la carte", Snackbar.LENGTH_LONG).setAction("Activer", new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view){
                askForPermission(); //demander l'autorisation
            }
        }).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission() {
        requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);//demander l'autorisation
        //requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE_COARSE);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){//||(requestCode == LOCATION_PERMISSION_REQUEST_CODE_COARSE)){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startProfileActivity();
            }else{
                explain();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // --------------------
    // UI
    // --------------------
//Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUIWhenResuming(){
        this.buttonLogin.setText(this.isCurrentUserLogged() ?
                getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // --------------------
    // UTILS
    // --------------------
//Method that handles response after SignIn Activity close
    //le CoordinatorLayout permettant d'afficher par la suite une Snackbar et son message.
    // Puis, nous avons créé une méthode (3) permettant de récupérer plus facilement le résultat renvoyé par l'activité de connexion/inscription auto-générée par FirebaseUI.
    // En fonction des résultats retournés, nous affichons une Snackbar avec un message personnalisé.
    // On n'oublie également pas d'appeler cette méthode dans le  onActivityResult()  de notre activité.
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }
}

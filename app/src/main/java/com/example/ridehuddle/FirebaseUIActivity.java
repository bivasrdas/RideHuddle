package com.example.ridehuddle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ridehuddle.models.FireStore;
import com.example.ridehuddle.models.FirestoreCallback;
import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FirebaseUIActivity extends AppCompatActivity {

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result

    int AUTHUI_REQUEST_CODE = 10001;
    private static final String TAG = "FirebaseUIActivity";
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        createSignInIntent();
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.ridehubble2)
                .setTheme(R.style.Theme_RideHuddle)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result)
    {
        try {
            IdpResponse response = result.getIdpResponse();
            if (result.getResultCode() == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    createUserInDB();
//                    Log.d(TAG, "Sign-in successful for user: "+ Objects.requireNonNull(user).getEmail());
//                    long userLoginTime = user.getMetadata().getLastSignInTimestamp();
//                    long userCreationTime = user.getMetadata().getCreationTimestamp();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//
//                    String loginTimeReadable = dateFormat.format(new Date(userLoginTime));
//                    String creationTimeReadable = dateFormat.format(new Date(userCreationTime));
//
//                    // Log or display the results
//                    Log.d("UserInfo", "User login time: " + loginTimeReadable);
//                    Log.d("UserInfo", "User creation time: " + creationTimeReadable);
//                    long usercreationtime = user.getMetadata().getCreationTimestamp();
//                    if (Objects.requireNonNull(user.getMetadata()).getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp())
//                    {
//                        createUserInDB();
//                        Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
//                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    this.finish();
                }
            }
            else {
                if (response == null)
                {
                    Log.d(TAG, "onSignInResult: the user canceled the sign in flow");
                }
                else {
                    Log.d(TAG, "Sign-in error: ", response.getError());
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Sign-in error: ", e);
        }
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.my_great_logo)      // Set logo drawable
                .setTheme(R.style.Theme_RideHuddle)      // Set theme
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_theme_logo]
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_pp_tos]
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                        "https://example.com/terms.html",
                        "https://example.com/privacy.html")
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_pp_tos]
    }

    public void emailLink() {
        // [START auth_fui_email_link]
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        /* yourPackageName= */ "...",
                        /* installIfNotAvailable= */ true,
                        /* minimumVersion= */ null)
                .setHandleCodeInApp(true) // This must be set to true
                .setUrl("https://google.com") // This URL needs to be whitelisted
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder()
                        .enableEmailLinkSignIn()
                        .setActionCodeSettings(actionCodeSettings)
                        .build()
        );
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_email_link]
    }

    public void catchEmailLink() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_email_link_catch]
        if (AuthUI.canHandleIntent(getIntent())) {
            if (getIntent().getExtras() == null) {
                return;
            }
            String link = getIntent().getExtras().getString("email_link_sign_in");
            if (link != null) {
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setEmailLink(link)
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);
            }
        }
        // [END auth_fui_email_link_catch]
    }

    public void createUserInDB()
    {
        FirebaseUser firebaseUseruser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUseruser != null) {
            FireStore fireStore = new FireStore();
            {
                fireStore.getDocument("user", firebaseUseruser.getUid(),User.class,user -> {
                            if(user ==null)
                            {   Uri getPhotoUrl = firebaseUseruser.getPhotoUrl();
                                String photoUrl = getPhotoUrl==null?"":getPhotoUrl.toString();
                                User newUser = new User(firebaseUseruser.getUid(), firebaseUseruser.getDisplayName(), photoUrl, new GeoPoint(1,1), null);
                                fireStore.addDocument("user",newUser.getUserId(),newUser,result ->
                                {
                                    if(result)
                                    {
                                        Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "User successfully written to DB");
                                    }
                                    else
                                    {
                                        Toast.makeText(this, "Error writing document", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Error writing document");
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "User already exists in DB : "+user.getUserName());
                            }
                        }
                );
            }
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            firestore.collection("user").document(user.getUid()).get().addOnSuccessListener(
//                    documentSnapshot -> {
//                        if(documentSnapshot.exists())
//                        {
//                            Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "User already exists in DB : "+documentSnapshot.getData());
//                        }
//                        else
//                        {
//                            Uri getPhotoUrl = user.getPhotoUrl();
//                            String photoUrl = getPhotoUrl==null?"":getPhotoUrl.toString();
//                            User newUser = new User(user.getUid(), user.getDisplayName(), photoUrl, new GeoPoint(1,1), null);
//                            firestore.collection("user").document(newUser.getUserId()).set(newUser)
//                                    .addOnSuccessListener(
//                                            aVoid -> Log.d(TAG, "User successfully written to DB"))
//                                    .addOnFailureListener(
//                                            e -> Log.w(TAG, "Error writing document", e));
//                            Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            ).addOnFailureListener(e -> {
//                Log.d(TAG, "Error getting user from DB", e);
//            });
        }
    }
}
package com.projectx.fitfloaw.firebase;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.credentials.GetCredentialException;
import android.credentials.GetCredentialResponse;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.projectx.fitfloaw.MainActivity;
import com.projectx.fitfloaw.R;

public class AuthManager {
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "AuthManager";
    private Activity activity;
    private final FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private CredentialManager credentialManager;

    public AuthManager(Activity activity){
        this.activity = activity;
        firebaseAuth = FirebaseAuth.getInstance();

        credentialManager = CredentialManager.create(activity);
    }

    private void startGoogleSignIn() {
        // Create Google Sign In Option
        GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption.Builder(String.valueOf(R.string.default_web_client_id))
                .setNonce(generateNonce()) // Implement nonce generation
                .build();

        // Create credential request
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build();

        // Start the sign-in flow
        credentialManager.getCredential(request, this, new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>(){

        })
    }
    private void handleSignIn(GetCredentialResponse result) {
        Credential credential = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            credential = Credential.createFrom(result.getCredential());
        }

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (customCredential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                GoogleIdTokenCredential googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(customCredential.getData());

                // Get the ID token
                String idToken = googleIdTokenCredential.getIdToken();

                // Send ID token to your backend for verification
                verifyTokenWithBackend(idToken);

            }
        }
    }
    private void verifyTokenWithBackend(String idToken) {
        // Implement your backend verification logic here
        // This should include sending the token to your server
        // and getting a response about the verification status

        // On successful verification:
        onSuccessfulSignIn();
    }

    private void onSuccessfulSignIn() {
        Toast.makeText(activity, "Sign in successful", Toast.LENGTH_SHORT).show();
        // Navigate to main activity
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(activity, intent, null);
    }

    private void handleFailure(GetCredentialException e) {
        Log.e(TAG, "Sign-in failed", e);
        String errorMessage = "Sign in failed: " + e.getMessage();
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }


    public void signInWithGoogle(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(activity, intent, RC_SIGN_IN, null);
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> callback){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> callback){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public void logout(){
        firebaseAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void resetPassword(String email, Activity activity){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(activity, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to send reset email", task.getException());
                    Toast.makeText(activity, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteUser(Activity activity){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to delete account", task.getException());
                        Toast.makeText(activity, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(activity, intent, null);
                }
            }
        });
    }
}

package com.projectx.fitfloaw.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.projectx.fitfloaw.MainActivity;
import com.projectx.fitfloaw.R;

public class LoginActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 9001;
    private TextInputEditText emailEditText, passwordEditText;
    private TextView forgotPasswordEditText, signUpEditText;
    private Button loginButton, googleButton;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUiElements();
        authManager = new AuthManager(this);

        setUiElements();
    }
    private void setUiElements(){
        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.passwordInput);
        forgotPasswordEditText = findViewById(R.id.forgotPasswordText);
        signUpEditText = findViewById(R.id.signUpText);
        loginButton = findViewById(R.id.loginButton);
        googleButton = findViewById(R.id.googleSignInButton);

        setLoginButtonListener();
        setSignUpButtonListener();
        setGoogleButtonListener();
    }

    private void setLoginButtonListener(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputsNotEmpty()){
                    authManager.registerUser(emailEditText.getText().toString(), passwordEditText.getText().toString(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                //Navigate to main activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(LoginActivity.this, "No account found with this email", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void setSignUpButtonListener(){
        signUpEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setGoogleButtonListener(){
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authManager.signInWithGoogle();
            }
        });
    }
    private boolean isInputsNotEmpty(){
        if (emailEditText.getText().toString().isEmpty()){
            emailEditText.setError("Email cannot be empty");
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()){
            passwordEditText.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authManager.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Google sign in failed: " + e.getStatus(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
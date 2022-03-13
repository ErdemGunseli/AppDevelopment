package com.example.PocketMaths;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.PocketMaths.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignUp, btnSignIn, btnContinueAsGuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determining if the user has used an account on this device before,
        // and continuing with that account if they have.
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        Account account = databaseHelper.getCurrentAccount();
        if (account != null) {
            Toast.makeText(this, "Automatically Logged Into Account with Parent Name: " + account.getParentName(), Toast.LENGTH_SHORT).show();
            Utils.getInstance().setUserAccount(account);
            startActivity(new Intent(this, MainMenuActivity.class));
        } else {
            Toast.makeText(this, "There is no current account found", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_welcome);

            initViews();
        }


    }

    private void initViews() {
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnContinueAsGuest = findViewById(R.id.btnContinueAsGuest);

        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnContinueAsGuest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            // Go to the appropriate activity depending on the Button clicked.
            case (R.id.btnSignUp):
                startActivity(new Intent(this, SignUpActivity.class));
                break;

            case (R.id.btnSignIn):
                startActivity(new Intent(this, SignInActivity.class));
                break;

            case (R.id.btnContinueAsGuest):
                // Creating a guest account:
                Utils.getInstance().setUserAccount(new Account());
                startActivity(new Intent(this, MainMenuActivity.class));
                break;
            default:
                break;

        }

    }

}
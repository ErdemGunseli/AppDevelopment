package com.example.PocketMaths;

import static com.example.PocketMaths.Account.Guest;
import static com.example.PocketMaths.Account.Member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relSignUp;

    private EditText edtTxtParentName, edtTxtStudentName, edtTxtEmail, edtTxtPassword, edtTxtConfirmPassword, edtTxtPin, edtTxtConfirmPin;

    private Button btnBack, btnConfirm;

    private String[] inputs;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();


    }
    /**
     * Initialising View objects:
     */
    private void initViews() {

        relSignUp = findViewById(R.id.relSignUp);

        edtTxtParentName = findViewById(R.id.edtTxtParentName);
        edtTxtStudentName = findViewById(R.id.edtTxtStudentName);
        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        edtTxtConfirmPassword = findViewById(R.id.edtTxtConfirmPassword);
        edtTxtPin = findViewById(R.id.edtTxtPin);
        edtTxtConfirmPin = findViewById(R.id.edtTxtConfirmPin);

        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case (R.id.btnBack):
                // Go back to the previous activity:
                finish();
                break;

            case (R.id.btnConfirm):
                signUpClicked();
                break;


            default:
                break;

        }

    }

    /**
     * Does the necessary validations when the Confirm Button is clicked:
     */
    private void signUpClicked(){

        inputs = new String[]{edtTxtParentName.getText().toString(),
                edtTxtStudentName.getText().toString(),
                edtTxtEmail.getText().toString(),
                edtTxtPassword.getText().toString(),
                edtTxtConfirmPassword.getText().toString(),
                edtTxtPin.getText().toString(),
                edtTxtConfirmPin.getText().toString()};

        // Displays appropriate Snack Bar if inputs are invalid:
        if (!Utils.getInstance().inputsFilled(inputs)){
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.empty_inputs), getString(R.string.ok));
        }
        else if (!Utils.getInstance().isValid(new String[]{edtTxtParentName.getText().toString(), edtTxtStudentName.getText().toString()}, 2)) {
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.input_lengths), getString(R.string.ok));
        }
        else if (!Utils.getInstance().isValid(new String[]{edtTxtPassword.getText().toString()}, 6)){
            Utils.getInstance().showSnackBar(this,relSignUp,getString(R.string.check_password_length), getString(R.string.ok));
        }
        else if (!Utils.getInstance().isValidEmail(edtTxtEmail.getText().toString())){
            Utils.getInstance().showSnackBar(this,relSignUp,getString(R.string.check_email), getString(R.string.ok));
        }
        else if (!edtTxtPassword.getText().toString().equals(edtTxtConfirmPassword.getText().toString())){
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.passwords_do_not_match), getString(R.string.ok));
        }
        else if (!Utils.getInstance().isValid(new String[]{edtTxtPin.getText().toString()}, 4)){
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.pin_length), getString(R.string.ok));
        }
        else if (!edtTxtPin.getText().toString().equals(edtTxtConfirmPin.getText().toString())){
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.pins_do_not_match), getString(R.string.ok));
        }
        else if (databaseHelper.getAccountByEmail(edtTxtEmail.getText().toString()) != null){
            Utils.getInstance().showSnackBar(this,relSignUp, getString(R.string.email_registered), getString(R.string.ok));
        }
        else{
            updateAccount();
        }
    }

    private void updateAccount(){
        // We are declaring an account depending on the data entered by the user:
        Account account = Utils.getInstance().getUserAccount();

        // If they do not have a guest account:
        if (account == null) {
            account = new Account(0, edtTxtParentName.getText().toString(),
                    edtTxtStudentName.getText().toString(), edtTxtEmail.getText().toString(),
                    edtTxtPassword.getText().toString(),
                    edtTxtPin.getText().toString());

            // Adding the account into the database:
            databaseHelper.addAccount(account);

            // Finding the account from the database and setting it so that it has the correct ID:
            account = databaseHelper.getAccountByEmail(account.getEmail());

            Utils.getInstance().setUserAccount(account);
            databaseHelper.useAccount(account.getId());



        }
        // If they have a quest account:
        else if (account.getAccountType().equals(Guest)){
            account.setParentName(edtTxtParentName.getText().toString().toUpperCase(Locale.ROOT));
            account.setStudentName(edtTxtStudentName.getText().toString().toUpperCase(Locale.ROOT));
            account.setEmail(edtTxtEmail.getText().toString().toLowerCase(Locale.ROOT));
            account.setPassword(edtTxtPassword.getText().toString());
            account.setPin(edtTxtPin.getText().toString());
            account.setAccountType(Member);

            databaseHelper.addAccount(account);

            // Finding the account from the database and setting it so that it has the correct ID:
            account = databaseHelper.getAccountByEmail(edtTxtEmail.getText().toString());


            Utils.getInstance().setUserAccount(account);
            databaseHelper.useAccount(account.getId());

        }



        // Going to the main menu activity:
        startActivity(new Intent(this, MainMenuActivity.class));
    }

}

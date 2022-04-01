package com.example.PocketMaths;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * This activity relates to the Main Menu Page of the app.
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * MainMenuActivity extends AppCompatActivity class to have access to Activity methods.
 * MainMenuActivity implements View.OnCLickListener interface to detect touch input.
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * It displays all instances of QuestionSet through its RecyclerView.
 * It has a search menu which filters through the QuestionSet instances by name and description.
 * It has buttons that take the user to various pages.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    private MainMenuRecyclerAdapter mainMenuRecyclerAdapter;

    private RecyclerView rvMainMenu;
    private TextView txtNoResults;
    private RelativeLayout relMainMenu;
    private ImageView imgAccount, imgTasks, imgCreateTask, imgSettings;
    private SearchView svQuestionSet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getInstance().setThemeId(databaseHelper.getTheme());
        Utils.getInstance().setShowRefreshers(databaseHelper.getShowRefreshers());
        setTheme(Utils.getInstance().getThemeId());
        setContentView(R.layout.activity_main_menu);

        initViews();

        setUpRecyclerView();

        // Initialising Adapter
        mainMenuRecyclerAdapter = new MainMenuRecyclerAdapter(this);

        // Setting our Question Sets Array List from the Utils to the Adapter
        mainMenuRecyclerAdapter.setQuestionSets(Utils.getQuestionSets());

        // Setting the adapter to the recycler view
        rvMainMenu.setAdapter(mainMenuRecyclerAdapter);

        // We also need to set a layout manager for our Recycler View:
        // Changing to Linear Layout Manager for the implementation of Expandable Card View:
        rvMainMenu.setLayoutManager((new LinearLayoutManager(this)));

        // Collapsing all Card Views to ensure that none is expanded when the activity is started:
        mainMenuRecyclerAdapter.collapseAll();


    }



    private void initViews() {
        relMainMenu = findViewById(R.id.relMainMenu);
        rvMainMenu = findViewById(R.id.rvMainMenu);
        txtNoResults = findViewById(R.id.txtNoResults);
        imgAccount = findViewById(R.id.imgAccount);
        imgTasks = findViewById(R.id.imgTasks);
        imgCreateTask = findViewById(R.id.imgCreateTask);
        imgSettings = findViewById(R.id.imgSettings);
        svQuestionSet = findViewById(R.id.svQuestionSet);

        imgAccount.setOnClickListener(this);
        imgTasks.setOnClickListener(this);
        imgCreateTask.setOnClickListener(this);
        imgSettings.setOnClickListener(this);

        svQuestionSet.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}

            @Override
            public boolean onQueryTextChange(String s) {
                // Filtering the Question Sets based on the value of s:
                filterQuestionSets(s);
                return false;
            }
        });
    }


    private void setUpRecyclerView() {
    }

    public void filterQuestionSets(String targetText){

        ArrayList<QuestionSet> questionSetsToShow = new ArrayList<>();
        targetText = targetText.toUpperCase(Locale.ROOT);

        // Iterating through the Question Set:
        for (QuestionSet questionSet: Utils.getQuestionSets()){
            // If the Question Set Name or Description contains the target text, include it:
            if (questionSet.getName().toUpperCase(Locale.ROOT).contains(targetText) ||
                    questionSet.getDescription().toUpperCase(Locale.ROOT).contains(targetText)) {
                questionSetsToShow.add(questionSet);
            }
        }

        // Setting the filtered array list of question sets:
        if (questionSetsToShow.size() == 0){
            TransitionManager.beginDelayedTransition(relMainMenu);
            txtNoResults.setVisibility(View.VISIBLE);
        }
        else{
            TransitionManager.beginDelayedTransition(relMainMenu);
            txtNoResults.setVisibility(View.GONE);
        }
        mainMenuRecyclerAdapter.setQuestionSets(questionSetsToShow);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case (R.id.imgAccount):
                // Starting the Account Activity:
                startActivity(new Intent(this, AccountActivity.class));
                break;

            case (R.id.imgTasks):
                startActivity(new Intent(this, TaskViewActivity.class));

                break;

            case (R.id.imgCreateTask):
                Utils.getInstance().setTargetClass(TaskCreateActivity.class);
                startActivity(new Intent(this, PinVerificationActivity.class));
                break;

            case (R.id.imgSettings):
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            default:
                break;


        }
    }


    @Override
    public void onBackPressed(){
        // The back button should not do anything here, so overriding into empty procedure.
    }
}
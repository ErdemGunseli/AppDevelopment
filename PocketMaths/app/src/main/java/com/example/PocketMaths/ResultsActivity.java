package com.example.PocketMaths;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import static com.example.PocketMaths.QuestionSetActivity.QUESTION_SET_ID;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView svResults;

    private ImageView imgExit, imgPerfect, imgPractice;

    private TextView txtQuestionSetName, txtResult, txtFirstAttempt, txtSecondAttempt, txtResultPercentage, txtPerfect, txtPractice;

    private QuestionSet questionSet;

    private RecyclerView rvQuestions;

    private Button btnFinish;

    private PieChart pieResults;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);



    private ArrayList<String> failedTopics = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getInstance().getThemeId());
        setContentView(R.layout.activity_results);

        // Initialising View Objects:
        initViews();

        // Setting data from the intent:
        setDataFromIntent();

        // Saving the Question Set to the account's list of Question Sets Completed:
        saveQuestionSet();

        ResultsRecyclerAdapter resultsRecyclerAdapter = new ResultsRecyclerAdapter(this);
        resultsRecyclerAdapter.setQuestionSet(questionSet);

        // Setting adapter to recycler view:
        rvQuestions.setAdapter(resultsRecyclerAdapter);

        rvQuestions.setLayoutManager(new LinearLayoutManager(this));



    }

    private void setDataFromIntent() {
        // Getting Question Set from the Intent:
        Intent intent = getIntent();

        // If the intent is not null:
        if (intent != null) {

            // Get the extra data from the Intent:
            // If the value is set to null, it will default to -1:
            int questionSetID = intent.getIntExtra(QUESTION_SET_ID, -1);

            if (questionSetID != -1) {
                // Finding our Question Set by ID:
                questionSet = Utils.getInstance().getQuestionSetById(questionSetID);

                if (questionSet != null) {

                    // Setting the data from the Question Set to our View item:
                    setData(questionSet);
                }
            }

        }
    }


    private void saveQuestionSet(){
        // Adding the question set to the account's list of completed question sets:

        // We can pass any ID as it will get its actual id when it is retrieved from the database:
        QuestionSetResult questionSetResult = new QuestionSetResult(
                0,
                questionSet.getId(),
                Utils.getInstance().getUserAccount().getId(),
                questionSet.calculatePointsEarned(),
                questionSet.calculatePointsPossible(),
                questionSet.calculateNumberOfQuestionsSolved()[0],
                questionSet.calculateNumberOfQuestionsSolved()[1],
                questionSet.calculateNumberOfQuestionsSolved()[2],
                DateFormat.getDateInstance().format(Calendar.getInstance().getTime())
        );

        databaseHelper.addQuestionSetResult(questionSetResult, Utils.getInstance().getUserAccount().getId());
    }


    private void initViews() {
        svResults = findViewById(R.id.svResults);
        rvQuestions = findViewById(R.id.rvQuestions);
        imgExit = findViewById(R.id.imgExit);
        txtQuestionSetName = findViewById(R.id.txtQuestionSetName);
        txtResult = findViewById(R.id.txtResult);

        imgPerfect = findViewById(R.id.imgPerfect);
        imgPractice = findViewById(R.id.imgPractice);


        txtFirstAttempt = findViewById(R.id.txtFirstAttempt);
        txtSecondAttempt = findViewById(R.id.txtSecondAttempt);

        txtResultPercentage = findViewById(R.id.txtResultPercentage);
        txtPerfect = findViewById(R.id.txtPerfect);
        txtPractice = findViewById(R.id.txtPractice);


        btnFinish = findViewById(R.id.btnFinish);

        pieResults = findViewById(R.id.pieResults);

        imgExit.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

    }

    private void setData(QuestionSet questionSet) {

        //TODO: Split setData()

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        ArrayList<Task> tasks = databaseHelper.getTasks();

        for (Task task: tasks){
            if (Objects.requireNonNull(Utils.getInstance().getQuestionSetById(task.getQuestionSetId())).getName().equals(questionSet.getName()) &&
            questionSet.calculateResult() >= task.getPassMark() && !task.isCompleted()){

                Utils.getInstance().showSnackBar(this, svResults, String.format(getString(R.string.task_completed), task.getName()), getString(R.string.ok));

                //Completing the task:
                databaseHelper.completeTask(task);
            }
        }

        txtQuestionSetName.setText(questionSet.getName());

        // X Points out of X
        txtResult.setText(String.format(getString(R.string.x_points_out_of_x), questionSet.calculatePointsEarned(), questionSet.calculatePointsPossible()));

        // Solved X out of X with one attempt.
        txtFirstAttempt.setText(String.format(getString(R.string.solved_x_out_of_x_1attempt),questionSet.calculateNumberOfQuestionsSolved()[0], questionSet.getQuestions().length ));

        // Calculating the remaining questions:
        int remaining = (questionSet.getQuestions().length - questionSet.calculateNumberOfQuestionsSolved()[0]);

        // Only present if score isn't 100%
        if (remaining > 0) {
            txtSecondAttempt.setVisibility(View.VISIBLE);

           // Solved X out of the remaining X with two attempts.
            txtSecondAttempt.setText(String.format(getString(R.string.solved_x_out_of_x_2attempts),  questionSet.calculateNumberOfQuestionsSolved()[1], (remaining) ));
        } else {
            // If 100%, we don't need it:
            txtSecondAttempt.setVisibility(View.GONE);
        }

        // Result: X%
        txtResultPercentage.setText(String.format(getString(R.string.result_percent), questionSet.calculateResult()));



        ArrayList<String> failedTopics = questionSet.getFailedTopics();
        ArrayList<String> perfectTopics = new ArrayList<>();


        // Removing failed topics from all topics to get perfect topics:
        // Elaborate since ArrayLists are pointer-based and any other technique does not work.
        for (String topic: questionSet.getTopics()) {
            if (!failedTopics.contains(topic)){
                perfectTopics.add(topic);
            }
        }

        if (failedTopics.size() == 0){
            // Invisible instead of gone to maintain layout
            txtPractice.setVisibility(View.INVISIBLE);
            imgPractice.setVisibility(View.INVISIBLE);
        }
        else{
            txtPractice.setVisibility(View.VISIBLE);
            imgPractice.setVisibility(View.VISIBLE);

            int count = 1;
            for (String topic: failedTopics){
                txtPractice.append(String.format(getString(R.string.list), count, topic));
                count++;
            }
        }

        if (perfectTopics.size() == 0){
            txtPerfect.setVisibility(View.INVISIBLE);
            imgPerfect.setVisibility(View.INVISIBLE);
        }
        else{
            txtPerfect.setVisibility(View.VISIBLE);
            imgPerfect.setVisibility(View.VISIBLE);

            int count = 1;
            for (String perfectTopic: perfectTopics){
                txtPerfect.append(String.format(getString(R.string.list), count, perfectTopic));
                count++;
            }
        }

        // Creating values for the pie chart:
        int firstAttempt = questionSet.calculateNumberOfQuestionsSolved()[0];
        int secondAttempt = questionSet.calculateNumberOfQuestionsSolved()[1];
        int moreAttempts = questionSet.getQuestions().length - (questionSet.calculateNumberOfQuestionsSolved()[0] + questionSet.calculateNumberOfQuestionsSolved()[1]);

        int[] values = {firstAttempt, secondAttempt, moreAttempts};
        String[] labels = {getString(R.string.first_attempt), getString(R.string.second_attempt), getString(R.string.other)};

        // Creating the Pie Chart
        Utils.getInstance().createPieChart(this, pieResults,
                values,
                13,
                "",
                0,
                labels,
                13,
                R.color.Secondary,
                getString(R.string.results),
                16,
                R.color.Primary,
                R.color.Silver);


    }


    @Override
    public void onClick(View view) {
        int v = view.getId();

        if (v == R.id.imgExit || v == R.id.btnFinish) {
            questionSet.reset();
            startActivity(new Intent(this, MainMenuActivity.class));
        }

    }



    @Override
    public void onBackPressed(){
        // Do nothing if the back button is pressed
    }
}
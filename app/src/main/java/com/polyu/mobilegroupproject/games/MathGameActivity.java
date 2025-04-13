package com.polyu.mobilegroupproject.games;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.polyu.mobilegroupproject.R;

import java.util.Random;

public class MathGameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvFormula, tvScore;
    private int correctAnswer;
    private int score = 0;
    private Random random = new Random();
    private int difficulty = 1; // Default difficulty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game);

        // Get difficulty level from intent
        if (getIntent().hasExtra("difficulty")) {
            difficulty = getIntent().getIntExtra("difficulty", 1);
        }

        // Initialize views
        tvFormula = findViewById(R.id.tvFormula);
        tvScore = findViewById(R.id.tvScore);
        
        // Set up button click listeners for all 16 buttons
        setupButtons();
        
        // Set up back button
        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // Set up close button
        Button closeButton = findViewById(R.id.close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                // Show confirmation dialog before closing
                new AlertDialog.Builder(this)
                    .setTitle("Exit Game")
                    .setMessage("Are you sure you want to exit? Your progress will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
            });
        }
        
        // Set up retry button
        Button retryButton = findViewById(R.id.retry_button);
        if (retryButton != null) {
            retryButton.setOnClickListener(v -> {
                // Reset score and generate new problem
                score = 0;
                if (tvScore != null) {
                    tvScore.setText("Score: 0");
                }
                generateMathProblem();
                Toast.makeText(this, "Game reset!", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Set up tips button
        Button tipsButton = findViewById(R.id.tips_button);
        if (tipsButton != null) {
            tipsButton.setOnClickListener(v -> showTips());
        }
        
        // Generate first math problem
        generateMathProblem();
    }
    
    private void setupButtons() {
        // Find and set up click listeners for all number buttons
        for (int i = 1; i <= 16; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }
    }
    
    private void generateMathProblem() {
        // Based on difficulty, generate different types of problems
        int num1, num2, num3;
        char op1, op2;
        
        // Keep generating until we get a result between 1-16
        do {
            // Generate random numbers based on difficulty
            if (difficulty == 1) {
                // Easy: Simple addition and subtraction with small numbers
                num1 = random.nextInt(10) + 1;  // 1-10
                num2 = random.nextInt(10) + 1;  // 1-10
                op1 = random.nextBoolean() ? '+' : '-';
                
                // Calculate
                if (op1 == '+') {
                    correctAnswer = num1 + num2;
                } else {
                    // Ensure the result is positive
                    if (num1 < num2) {
                        int temp = num1;
                        num1 = num2;
                        num2 = temp;
                    }
                    correctAnswer = num1 - num2;
                }
                
                // Display
                tvFormula.setText(num1 + " " + op1 + " " + num2 + " = ?");
            } 
            else {
                // More difficult: Add a third number and a second operation
                num1 = random.nextInt(15) + 1;  // 1-15
                num2 = random.nextInt(5) + 1;   // 1-5
                num3 = random.nextInt(5) + 1;   // 1-5
                
                // Pick two random operations
                String[] operations = {"+", "-", "×"};
                op1 = operations[random.nextInt(3)].charAt(0);
                op2 = operations[random.nextInt(3)].charAt(0);
                
                // Calculate following order of operations (× before + and -)
                if (op1 == '×') {
                    if (op2 == '+') {
                        correctAnswer = (num1 * num2) + num3;
                    } else {
                        correctAnswer = (num1 * num2) - num3;
                    }
                } else if (op2 == '×') {
                    if (op1 == '+') {
                        correctAnswer = num1 + (num2 * num3);
                    } else {
                        correctAnswer = num1 - (num2 * num3);
                    }
                } else {
                    // Neither operation is ×
                    if (op1 == '+') {
                        if (op2 == '+') {
                            correctAnswer = num1 + num2 + num3;
                        } else {
                            correctAnswer = num1 + num2 - num3;
                        }
                    } else {
                        if (op2 == '+') {
                            correctAnswer = num1 - num2 + num3;
                        } else {
                            correctAnswer = num1 - num2 - num3;
                        }
                    }
                }
                
                // Display
                tvFormula.setText(num1 + "" + op1 + "" + num2 + "" + op2 + "" + num3 + "=?");
            }
        } while (correctAnswer <= 0 || correctAnswer > 16);
    }
    
    @Override
    public void onClick(View view) {
        // Get the button that was clicked
        Button clickedButton = (Button) view;
        
        // Get the number from the button
        int selectedNumber = Integer.parseInt(clickedButton.getText().toString());
        
        // Check if the answer is correct
        if (selectedNumber == correctAnswer) {
            // Correct answer
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            tvScore.setText("Score: " + score);
            
            // Generate a new problem
            generateMathProblem();
        } else {
            // Wrong answer
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showTips() {
        String tip = "This math game is about solving problems:\n\n";
        tip += "1. Calculate the correct answer to the math problem\n";
        tip += "2. Click the button with the answer from the grid\n";
        tip += "3. Try to score as many points as possible\n";
        
        new AlertDialog.Builder(this)
            .setTitle("Game Tips")
            .setMessage(tip)
            .setPositiveButton("Got it", null)
            .show();
    }
}
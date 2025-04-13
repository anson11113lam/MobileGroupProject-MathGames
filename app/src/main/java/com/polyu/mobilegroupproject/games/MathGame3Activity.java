package com.polyu.mobilegroupproject.games;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.polyu.mobilegroupproject.R;

import java.util.Random;

public class MathGame3Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvFormula, tvScore, tvInstruction;
    private Button passButton;
    private int correctAnswer;
    private int score = 0;
    private Random random = new Random();
    private int difficulty = 1; // Default difficulty
    private boolean isLandscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game3);

        // Get difficulty level from intent
        if (getIntent().hasExtra("difficulty")) {
            difficulty = getIntent().getIntExtra("difficulty", 1);
        }

        // Initialize views
        tvFormula = findViewById(R.id.tvFormula);
        tvScore = findViewById(R.id.tvScore);
        tvInstruction = findViewById(R.id.tvInstruction);
        passButton = findViewById(R.id.passButton);
        
        // Set up button click listeners for all 16 number buttons
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
        
        // Set up pass button
        if (passButton != null) {
            passButton.setOnClickListener(v -> {
                // Only allow passing if in landscape mode
                if (isLandscape) {
                    Toast.makeText(this, "Correct! You solved the puzzle!", Toast.LENGTH_SHORT).show();
                    score++;
                    if (tvScore != null) {
                        tvScore.setText("Score: " + score);
                    }
                    generateMathProblem();
                }
            });
        }
        
        // Check current orientation and update UI
        checkOrientation();
        
        // Generate the first math problem
        generateMathProblem();
    }
    
    private void setupButtons() {
        for (int i = 1; i <= 16; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {  // Make sure we found the button
                button.setOnClickListener(this);
            }
        }
    }
    
    @Override
    public void onClick(View view) {
        // Get the button that was clicked
        Button clickedButton = (Button) view;
        
        // Get the number from the button
        int selectedNumber = Integer.parseInt(clickedButton.getText().toString());
        
        // In this game, number buttons do nothing in portrait mode
        if (!isLandscape) {
            // Hint that player needs to try something else
            Toast.makeText(this, "That doesn't seem to work...", Toast.LENGTH_SHORT).show();
        } else {
            // Even in landscape, clicking numbers doesn't help
            Toast.makeText(this, "Look for another way to solve this!", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        // Save current state
        int currentScore = score;
        String currentFormula = tvFormula != null ? tvFormula.getText().toString() : "";
        
        // Set the appropriate layout
        setContentView(R.layout.activity_math_game3);
        isLandscape = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        
        // Re-initialize views
        tvFormula = findViewById(R.id.tvFormula);
        tvScore = findViewById(R.id.tvScore);
        tvInstruction = findViewById(R.id.tvInstruction);
        passButton = findViewById(R.id.passButton);
        
        // Restore state
        score = currentScore;
        if (tvScore != null) {
            tvScore.setText("Score: " + score);
        }
        if (tvFormula != null) {
            tvFormula.setText(currentFormula);
        }
        
        // Re-setup all buttons
        setupButtons();
        
        // Update orientation-specific UI
        checkOrientation();
    }
    
    private void checkOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        isLandscape = (orientation == Configuration.ORIENTATION_LANDSCAPE);
        
        if (tvInstruction != null) {
            tvInstruction.setText(isLandscape ? "Find the special button" : "Calculate the correct answer");
        }
        
        // Pass button visibility is handled by the separate layouts
        if (passButton != null && !isLandscape) {
            passButton.setVisibility(View.GONE);
        }
    }
    
    private void showTips() {
        String tip = "This game requires thinking outside the box:\n\n";
        tip += "1. Calculate the math answer\n";
        tip += "2. Try different approaches when buttons don't work\n";
        tip += "3. Sometimes looking at things from a different angle helps\n";
        
        new AlertDialog.Builder(this)
            .setTitle("Game Tips")
            .setMessage(tip)
            .setPositiveButton("Got it", null)
            .show();
    }
    
    private void generateMathProblem() {
        // Generate random numbers and operations to create a formula
        // Make sure the result is between 1-16
        
        int num1, num2, num3;
        char op1, op2;
        
        // Keep generating until we get a result between 1-16
        do {
            // Adjust number ranges based on difficulty
            switch(difficulty) {
                case 1: // Easy
                    num1 = random.nextInt(5) + 1;  // 1-5
                    num2 = random.nextInt(3) + 1;  // 1-3
                    num3 = random.nextInt(2) + 1;  // 1-2
                    break;
                case 2: // Medium
                    num1 = random.nextInt(8) + 1;  // 1-8
                    num2 = random.nextInt(5) + 1;  // 1-5
                    num3 = random.nextInt(3) + 1;  // 1-3
                    break;
                case 3: // Hard
                    num1 = random.nextInt(10) + 1; // 1-10
                    num2 = random.nextInt(6) + 1;  // 1-6
                    num3 = random.nextInt(5) + 1;  // 1-5
                    break;
                case 4: // Very Hard
                    num1 = random.nextInt(12) + 1; // 1-12
                    num2 = random.nextInt(8) + 1;  // 1-8
                    num3 = random.nextInt(6) + 1;  // 1-6
                    break;
                default:
                    num1 = random.nextInt(10) + 1; // 1-10
                    num2 = random.nextInt(6) + 1;  // 1-6
                    num3 = random.nextInt(5) + 1;  // 1-5
            }
            
            // Generate operations (adjust operations based on difficulty)
            int opRange = (difficulty <= 2) ? 2 : 3; // Easy/Medium: only +/-, Hard/Very Hard: +/-/*
            int opCode1 = random.nextInt(opRange);
            int opCode2 = random.nextInt(opRange);
            
            op1 = getOperationChar(opCode1);
            op2 = getOperationChar(opCode2);
            
            // Calculate the correct answer based on order of operations
            correctAnswer = calculateResult(num1, num2, num3, op1, op2);
            
        } while (correctAnswer < 1 || correctAnswer > 16);
        
        // Create the formula string
        String formula = num1 + String.valueOf(op1) + num2 + String.valueOf(op2) + num3 + "=?";
        tvFormula.setText(formula);
        
        // Set the pass button text to show the answer
        if (passButton != null) {
            passButton.setText("PASS: " + correctAnswer);
        }
        
        // Check orientation again to ensure UI is consistent
        checkOrientation();
    }
    
    private char getOperationChar(int opCode) {
        switch (opCode) {
            case 0: return '+';
            case 1: return '-';
            case 2: return '×';
            default: return '+';
        }
    }
    
    private int calculateResult(int num1, int num2, int num3, char op1, char op2) {
        // Follow order of operations: multiplication first, then addition/subtraction
        int result;
        
        // If second operation is multiplication
        if (op2 == '×') {
            int temp = num2 * num3;
            result = (op1 == '+') ? num1 + temp : num1 - temp;
        } 
        // If first operation is multiplication
        else if (op1 == '×') {
            int temp = num1 * num2;
            result = (op2 == '+') ? temp + num3 : temp - num3;
        } 
        // Both operations are + or -
        else {
            // Calculate from left to right
            int temp = (op1 == '+') ? num1 + num2 : num1 - num2;
            result = (op2 == '+') ? temp + num3 : temp - num3;
        }
        
        return result;
    }
}
package com.polyu.mobilegroupproject.games;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.polyu.mobilegroupproject.R;

import java.util.Random;

public class MathGame2Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvFormula, tvScore, tvInstruction;
    private Button decoyButton;
    private FrameLayout gridContainer;
    private int correctAnswer;
    private int score = 0;
    private Random random = new Random();
    private int difficulty = 1; // Default difficulty
    private float dX, dY; // For drag tracking
    private boolean isDecoyMoved = false;
    // Store original position of decoy button
    private float originalX, originalY;
    private Button correctButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game2);

        // Get difficulty level from intent
        if (getIntent().hasExtra("difficulty")) {
            difficulty = getIntent().getIntExtra("difficulty", 1);
        }

        // Initialize views
        tvFormula = findViewById(R.id.tvFormula);
        tvScore = findViewById(R.id.tvScore);
        tvInstruction = findViewById(R.id.tvInstruction);
        gridContainer = findViewById(R.id.gridContainer);
        decoyButton = findViewById(R.id.decoyButton);
        
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
                
                // Make sure to reset the decoy button state
                isDecoyMoved = false;
                if (tvInstruction != null) {
                    tvInstruction.setText("Calculate the correct answer");
                }
                
                // Generate new problem which will also position the decoy button
                resetProblem();
                Toast.makeText(this, "Game reset!", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Set up tips button
        Button tipsButton = findViewById(R.id.tips_button);
        if (tipsButton != null) {
            tipsButton.setOnClickListener(v -> {
                showTips();
            });
        }
        
        // Set up decoy button to be draggable
        if (decoyButton != null) {
            decoyButton.setOnTouchListener((view, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        return true;
    
                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + dX);
                        view.setY(event.getRawY() + dY);
                        isDecoyMoved = true;
                        if (tvInstruction != null) {
                            tvInstruction.setText("Click the correct answer");
                        }
                        return true;
    
                    default:
                        return false;
                }
            });
        }
        
        // Generate the first math problem
        generateMathProblem();
    }
    
    private void setupButtons() {
        for (int i = 1; i <= 16; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {
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
        
        // Check if the answer is correct and if the decoy button has been moved
        if (selectedNumber == correctAnswer && isDecoyMoved) {
            // Correct answer and decoy moved
            Toast.makeText(this, "Correct! You solved the puzzle!", Toast.LENGTH_SHORT).show();
            score++;
            if (tvScore != null) {
                tvScore.setText("Score: " + score);
            }
            resetProblem();
        } else if (selectedNumber == correctAnswer && !isDecoyMoved) {
            // Right answer but decoy not moved
            Toast.makeText(this, "Move the decoy button first!", Toast.LENGTH_SHORT).show();
        } else {
            // Wrong answer
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void resetProblem() {
        // Reset button backgrounds
        for (int i = 1; i <= 16; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {
                button.setBackgroundResource(R.drawable.round_button);
            }
        }
        
        // Reset decoy button state
        isDecoyMoved = false;
        if (tvInstruction != null) {
            tvInstruction.setText("Calculate the correct answer");
        }
        
        // Generate new problem
        generateMathProblem();
    }
    
    private void showTips() {
        String tip = "This game is about strategy:\n\n";
        tip += "1. Calculate the correct math answer\n";
        tip += "2. Move the decoy button away from the correct answer\n";
        tip += "3. Click on the revealed answer button to score\n";
        
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
        if (tvFormula != null) {
            tvFormula.setText(formula);
        }
        
        // Find the correct button and position the decoy over it
        positionDecoyOverCorrectButton();
    }
    
    private void positionDecoyOverCorrectButton() {
        // Find the button with the correct answer
        int correctButtonId = getResources().getIdentifier("btn" + correctAnswer, "id", getPackageName());
        correctButton = findViewById(correctButtonId);
        
        if (correctButton != null && decoyButton != null && gridContainer != null) {
            // Set decoy button text to match correct answer
            decoyButton.setText(String.valueOf(correctAnswer));
            
            // Wait for layout to be ready
            gridContainer.post(() -> {
                // Get correct button position
                int[] correctButtonLocation = new int[2];
                correctButton.getLocationInWindow(correctButtonLocation);
                
                // Calculate position for decoy button (relative to the container)
                int[] containerLocation = new int[2];
                gridContainer.getLocationInWindow(containerLocation);
                
                // Set decoy position to overlay the correct button
                decoyButton.setX(correctButtonLocation[0] - containerLocation[0]);
                decoyButton.setY(correctButtonLocation[1] - containerLocation[1]);
                
                // Store original position
                originalX = decoyButton.getX();
                originalY = decoyButton.getY();
                
                // Make sure decoy button is visible and on top
                decoyButton.setVisibility(View.VISIBLE);
                decoyButton.bringToFront();
            });
        }
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
package org.example;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;


public class GUI {
    private JFrame frame;
    private JLabel[][] grid;
    private JTextField inputField;
    private JButton button;
    private JButton hintButton;

    private WordleGame game;
    private WordleSolver solver;
    private int currentRow = 0;
    private String[] guesses;

    private List<String> validWords;

    public GUI(){

        validWords = Stream.concat(JSONhandler.parseJsonFile("nonwordles.json").stream(), JSONhandler.parseJsonFile("wordles.json").stream()).toList();

        setupGameGrid();
        startNewGame();
    }

    private void setupGameGrid(){
        frame = new JFrame("Java Wordle");
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(6, 5, 5, 5));
        grid = new JLabel[6][5];

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setFont(new Font("Arial", Font.BOLD, 24));
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                grid[row][col] = label;
                gridPanel.add(label);
            }
        }
        frame.add(gridPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(10);
        button = new JButton("Guess");
        hintButton = new JButton("Get Hint");
        inputPanel.add(inputField);
        inputPanel.add(button);
        inputPanel.add(hintButton);
        frame.add(inputPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> handleGuess());
        hintButton.addActionListener(e -> handleHint());
        frame.setVisible(true);
    }

    private void startNewGame(){
        String target = WordGenerator.getWordFromFile("wordles.json");
        game = new WordleGame(target, 6);
        solver = new WordleSolver();
        currentRow = 0;
        guesses = new String[6];
        resetGrid();
        inputField.setEnabled(true);
        button.setEnabled(true);
    }

    private void resetGrid(){
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                grid[row][col].setText("");
                grid[row][col].setBackground(Color.WHITE);
                grid[row][col].setForeground(Color.BLACK);
            }
        }
        inputField.setText("");
    }

    private void handleGuess(){
        String guess = inputField.getText();
        solver.recordGuess(guess);

        boolean isSafe = GuessChecker.isValidInput(validWords, guesses, guess, frame);
        if (!isSafe) return;

        LetterResult[] results = game.makeGuess(guess);

        // solver working in the background with each guess
        solver.filterWords(results, guess);

        updateGrid(guess, results);

        guesses[currentRow] = guess;

        if (game.isWon()) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You Won!");
            startNewGame();
        } else if (game.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Game Over! Word was " + game.getTarget());
            inputField.setEnabled(false);
            button.setEnabled(false);
        } else {
            currentRow++;
            inputField.setText("");
        }
    }

    private void handleHint(){
        // prevent multiple clicks during calculation
        hintButton.setEnabled(false);
        hintButton.setText("Thinking...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return solver.getBestWord();
            }

            // run when background process complete
            @Override
            protected void done(){
                try{
                    String bestMove = get();
                    JOptionPane.showMessageDialog(frame, "The best move is: " + bestMove.toUpperCase());
                }
                catch (InterruptedException | ExecutionException e){
                    JOptionPane.showMessageDialog(frame, "Error calculating hint");
                }
                finally{
                    // always want to make the button available again
                    hintButton.setText("Get Hint");
                    hintButton.setEnabled(true);
                }
            }
        };

        // start background thread
        worker.execute();
    }

    private void updateGrid(String guess, LetterResult[] results){
        for (int i = 0; i < 5; i++) {
            JLabel label = grid[currentRow][i];
            label.setText(String.valueOf(guess.charAt(i)).toUpperCase());
            label.setForeground(Color.WHITE);

            switch (results[i]) {
                case CORRECT -> label.setBackground(Color.GREEN);
                case WRONG_POS -> label.setBackground(Color.ORANGE);
                case NOT_IN_WORD -> label.setBackground(Color.GRAY);
            }
        }
    }

}

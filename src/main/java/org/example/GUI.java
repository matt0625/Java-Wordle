package org.example;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class GUI {
    private JFrame frame;
    private JLabel[][] grid;
    private JTextField inputField;
    private JButton button;

    private HashMap<Character, Integer> charOccurrenceMap;
    private int currentRow = 0;
    // target not final since we have multiple games
    private String target = WordGenerator.getWordFromFile("wordles.json");
    private String[] guesses = new String[6];
    private List<String> validWords = Stream.concat(JSONhandler.parseJsonFile("nonwordles.json").stream(), JSONhandler.parseJsonFile("wordles.json").stream()).toList();

    public GUI(){
        // frame setup i.e. setting up the window
        frame = new JFrame("Java Wordle");
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        // creating the grid panel
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(6, 5, 5, 5)); // 6 rows, 5 cols, gap of 5x5 pixels
        grid = new JLabel[6][5];

        // initalise boxes
        for (int row = 0; row < 6; row++){
            for (int col = 0; col < 5; col++){
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder((Color.BLACK)));
                label.setFont(new Font("Arial", Font.BOLD, 24));
                label.setOpaque(true); // Required to show background color
                label.setBackground(Color.WHITE);

                grid[row][col] = label;
                gridPanel.add(label); // add to visual panel
            }
        }
        frame.add(gridPanel, BorderLayout.CENTER);

        // create input panel
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(10);
        button = new JButton("Guess");

        inputPanel.add(inputField);
        inputPanel.add(button);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // initialise charOccurences
        getCharOccurences();

        // adding button logic
         button.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 String guess = inputField.getText().toLowerCase();
                 boolean isSafe = GuessChecker.isValidInput(validWords, guesses, guess, frame);

                 if (!isSafe){
                     return;
                 }
                 processGuess(guess);
                 guesses[currentRow] = guess;

                 if (guess.equals(target)){
                     JOptionPane.showMessageDialog(frame, "Congratulations! You Won!");
                     resetGame();
                     return;
                 }

                 // prepare for next turn
                 inputField.setText("");
                 currentRow++;

                 if (currentRow >= 6){

                     JOptionPane.showMessageDialog(frame, "Game Over! Word was " + target);
                     inputField.setEnabled(false);
                     button.setEnabled(false);
                 }
             }
         });
         frame.setVisible(true);
    }

    private void processGuess(String guess) {
        for (int i = 0; i < 5; i++) {
            char letter = guess.charAt(i);

            grid[currentRow][i].setText("" + letter);

            if (letter == target.charAt(i)) {
                grid[currentRow][i].setBackground(Color.GREEN);
            } else if (target.contains("" + letter)) {
                if (charOccurrenceMap.get(letter) > 0){
                    grid[currentRow][i].setBackground(Color.ORANGE);
                    charOccurrenceMap.replace(letter, charOccurrenceMap.get(letter) - 1);
                }
                else{
                    grid[currentRow][i].setBackground(Color.GRAY);
                    grid[currentRow][i].setForeground(Color.WHITE);
                }

            } else {
                grid[currentRow][i].setBackground(Color.GRAY);
                grid[currentRow][i].setForeground(Color.WHITE);
            }
        }
    }

    private void resetGame(){
        currentRow = 0;
        guesses = new String[6];

        target = WordGenerator.getWordFromFile("wordles.json");


        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                grid[row][col].setText("");
                grid[row][col].setBackground(Color.WHITE);
                grid[row][col].setForeground(Color.BLACK);
            }
        }
    }

    private void getCharOccurences(){
        this.charOccurrenceMap = new HashMap<>();
        char[] guessArr = this.target.toCharArray();

        for (char c: guessArr){
            if (charOccurrenceMap.containsKey(c)){
                charOccurrenceMap.replace(c, charOccurrenceMap.get(c) + 1);
            }
            else{
                charOccurrenceMap.put(c, 1);
            }
        }
    }

}

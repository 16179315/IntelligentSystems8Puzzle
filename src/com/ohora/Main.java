package com.ohora;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // TODO update size so that puzzle can be any size
    public static final int NUMBER_OF_ROWS = 4;
    public static final int SIZE_OF_PUZZLE = NUMBER_OF_ROWS*NUMBER_OF_ROWS;
    public static int[] squaresAtMiddleOfPuzzle = findAllSquaresAtMiddleOfPuzzle();

    public static void main(String[] args) {

        int[] startState = new int[SIZE_OF_PUZZLE];
        int[] endState = new int[SIZE_OF_PUZZLE];

        startState = receiveInput("start");
        endState = receiveInput("end");

        int[] possibleSwaps = findPossibleSwaps(startState);
        // get possible movements
        // calculate h(distances of tiles from their pos) function of each movement
        // display possible
    }

    private static int[] findAllSquaresAtMiddleOfPuzzle() {
        int[] puzzle = new int[SIZE_OF_PUZZLE];
        //TODO find out what to subtract by relatively
        double numberOfSquaresInMiddle = SIZE_OF_PUZZLE - 4*(NUMBER_OF_ROWS-1);

        int row = 0;
        int col = 0;
        int middleSqaureIndex = 0;
        for(int i =0;i<=SIZE_OF_PUZZLE;i+=NUMBER_OF_ROWS){
            row++;
            for(int j = i; j<= NUMBER_OF_ROWS-1+i && j != SIZE_OF_PUZZLE; j++){
                col++;
                System.out.print("i:" + String.valueOf(i) + " j:" + String.valueOf(j)+  " " +String.valueOf(puzzle[j]) + " ");
                if(!(row == 1 || row == NUMBER_OF_ROWS || col == 1 || col ==NUMBER_OF_ROWS)){
                    squaresAtMiddleOfPuzzle[middleSqaureIndex] = j;
                    middleSqaureIndex++;
                }
            }
            col=0;
            System.out.print("\n");
        }
        return squaresAtMiddleOfPuzzle;
    }


    private static int[] findPossibleSwaps(int[] state) {
        int indexOfZero = Arrays.asList(state).indexOf(0);
        int[] possibleSwaps = new int[4];
        for(int i = 0;i<4;i++) {
            if (zeroIsAtMiddlePiece(indexOfZero)) {
                possibleSwaps[i] = indexOfZero - 1;
                possibleSwaps[i] = indexOfZero + 1;
                possibleSwaps[i] = indexOfZero + NUMBER_OF_ROWS;
                possibleSwaps[i] = indexOfZero - NUMBER_OF_ROWS;
            }
        }
        return possibleSwaps;
    }

    private static boolean zeroIsAtMiddlePiece(int indexOfZero) {
        return Arrays.asList(squaresAtMiddleOfPuzzle).contains(indexOfZero);
    }

    /**
     * Receives Input and returns valid configuration
     * @param requestedState
     * @return
     */
    private static int[] receiveInput(String requestedState) {
        String input = "";
        int[] intArray;
        boolean invalidInput = true;


        while (invalidInput) {
            input = JOptionPane.showInputDialog("Please enter the " + requestedState + " state of the puzzle");
            if(!validateInput(input)) {
                JOptionPane.showMessageDialog(null, "Invalid input", "Name", JOptionPane.ERROR_MESSAGE);
            }
            invalidInput = !validateInput(input);
        }
        String[] inputArray = input.trim().split(" ");
        intArray = Arrays.stream(inputArray).mapToInt(Integer::parseInt).toArray();

        return intArray;
    }

    /**
     * Checks if all numbers are unique and in the desired range
     * @param inputString
     * @return
     */
    private static boolean validateInput(String inputString) {
        String[] inputStringArray = inputString.trim().split(" ");
        int[] numbers;
        try {
            numbers = Arrays.stream(inputStringArray).mapToInt(Integer::parseInt).toArray();
        } catch(NumberFormatException e){
            return false;
        }
        Set<Integer> uniqueNumbers = new HashSet<Integer>();
        for(int number : numbers) {
            if (uniqueNumbers.contains(number) || (number < 0) || number > SIZE_OF_PUZZLE - 1) {
                return false;
            }
            uniqueNumbers.add(number);
        }
        if(uniqueNumbers.size() != SIZE_OF_PUZZLE){
            return false;
        }
        System.out.print("valid");
        return true;
    }


}

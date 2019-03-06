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
    public static final int NUMBER_OF_ROWS = 3;
    public static final int SIZE_OF_PUZZLE = NUMBER_OF_ROWS*NUMBER_OF_ROWS;
    public static int[] squaresAtMiddleOfPuzzle = new int[SIZE_OF_PUZZLE - 4*(NUMBER_OF_ROWS-1)];
    private static int[]squaresAtRow1OfPuzzle = new int[SIZE_OF_PUZZLE];
    public static int[] squaresAtRowLastOfPuzzle= new int[SIZE_OF_PUZZLE];;
    public static int[] squaresAtColumn1OfPuzzle= new int[SIZE_OF_PUZZLE];;
    public static int[] squaresAtColumnLastOfPuzzle= new int[SIZE_OF_PUZZLE];;

    public static void main(String[] args) {

        int[] startState = new int[SIZE_OF_PUZZLE];
        int[] endState = new int[SIZE_OF_PUZZLE];

        startState = receiveInput("start");
        endState = receiveInput("end");


        findAllSquaresAtMiddleOfPuzzle();
        Integer[] possibleSwaps = findPossibleSwaps(startState);
        displayPossibleMovements(possibleSwaps);
        // calculate h(distances of tiles from their pos) function of each movement
        // display possible
    }

    private static void displayPossibleMovements(Integer[] possibleSwaps) {
        String outputString = "";
        for(int i=0;i<possibleSwaps.length;i++){
            if(possibleSwaps[i] != null){
                switch(i){
                    case(0): outputString += displayPossibleMovement(possibleSwaps[i],"South"); break;
                    case(1): outputString += displayPossibleMovement(possibleSwaps[i],"West"); break;
                    case(2): outputString += displayPossibleMovement(possibleSwaps[i],"North"); break;
                    case(3): outputString += displayPossibleMovement(possibleSwaps[i],"East"); break;
                }
            }
        }
        JOptionPane.showMessageDialog(null, outputString, "Available Moves", JOptionPane.INFORMATION_MESSAGE);

    }

    private static String displayPossibleMovement(Integer possibleSwap, String direction) {
        //TODO get state[possibleSwap] not just index of possible swap
        return "Can move " + String.valueOf(possibleSwap) + " to the " + direction + "\n";
    }

    private static void findAllSquaresAtMiddleOfPuzzle() {
        int[] puzzle = new int[SIZE_OF_PUZZLE];

        int row = 0;
        int col = 0;
        int middleSqaureIndex = 0;
        int row1Index = 0;
        int rowLastIndex = 0;
        int col1Index = 0;
        int colLastIndex = 0;


        for(int i =0;i<=SIZE_OF_PUZZLE;i+=NUMBER_OF_ROWS){
            row++;
            for(int j = i; j<= NUMBER_OF_ROWS-1+i && j != SIZE_OF_PUZZLE; j++){
                col++;
                //System.out.print("i:" + String.valueOf(i) + " j:" + String.valueOf(j)+  " " +String.valueOf(puzzle[j]) + " ");
                if(!(row == 1 || row == NUMBER_OF_ROWS || col == 1 || col ==NUMBER_OF_ROWS)){
                    squaresAtMiddleOfPuzzle[middleSqaureIndex] = j;
                    middleSqaureIndex++;
                }
                if(row == 1){
                    squaresAtRow1OfPuzzle[row1Index] = j;
                    row1Index++;
                }
                if(col == 1){
                    squaresAtColumn1OfPuzzle[col1Index] = j;
                    col1Index++;
                }
                if(row == NUMBER_OF_ROWS){
                    squaresAtRowLastOfPuzzle[rowLastIndex] = j;
                    rowLastIndex++;
                }
                if(col == NUMBER_OF_ROWS){
                    squaresAtColumnLastOfPuzzle[colLastIndex] = j;
                    colLastIndex++;
                }


            }
            col=0;
           // System.out.print("\n");
        }

    }


    private static Integer[] findPossibleSwaps(int[] state) {
        int indexOfZero = findIndexOfZeroInState(state);
        Integer[] possibleSwaps = new Integer[4];
        if (zeroIsContainedInGivenArray(indexOfZero,squaresAtMiddleOfPuzzle)) {
            possibleSwaps[3] = indexOfZero - 1;
            possibleSwaps[1] = indexOfZero + 1;
            possibleSwaps[2] = indexOfZero + NUMBER_OF_ROWS;
            possibleSwaps[0] = indexOfZero - NUMBER_OF_ROWS;
        }
        if(zeroIsContainedInGivenArray(indexOfZero,squaresAtColumn1OfPuzzle)){
            possibleSwaps[1] = indexOfZero + 1;
        }
        else if(zeroIsContainedInGivenArray(indexOfZero,squaresAtColumnLastOfPuzzle)){
            possibleSwaps[3] = indexOfZero - 1;
        }
        else{
            possibleSwaps[1] = indexOfZero+1;
            possibleSwaps[3] = indexOfZero-1;
        }
        if(zeroIsContainedInGivenArray(indexOfZero,squaresAtRow1OfPuzzle)){
            possibleSwaps[2] = indexOfZero+NUMBER_OF_ROWS;
        }
        else if(zeroIsContainedInGivenArray(indexOfZero,squaresAtRowLastOfPuzzle)){
            possibleSwaps[0] = indexOfZero - NUMBER_OF_ROWS;
        }
        else {
            possibleSwaps[2] = indexOfZero + NUMBER_OF_ROWS;
            possibleSwaps[0] = indexOfZero -NUMBER_OF_ROWS;
        }
        return possibleSwaps;
    }

    private static int findIndexOfZeroInState(int[] state) {
        int index = 0;
        for(int i =0;i<state.length;i++){
            if(state[i] == 0){
                index = i;
                return index;
            }
        }
        return index;
    }

    private static boolean zeroIsContainedInGivenArray(int indexOfZero,int[] arrayToBeSearched) {
        boolean contains=false;
        for(int i =0;i<arrayToBeSearched.length;i++){
            if(arrayToBeSearched[i] == indexOfZero){
                contains = true;
                return contains;
            }
        }
        return contains;
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

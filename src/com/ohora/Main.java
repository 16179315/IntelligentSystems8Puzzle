package com.ohora;

import javax.swing.JOptionPane;
import java.util.*;

public class Main {

    // TODO update size so that puzzle can be any size
    public static final int NUMBER_OF_ROWS = 3;
    public static final int SIZE_OF_PUZZLE = NUMBER_OF_ROWS*NUMBER_OF_ROWS;

    private static int[]squaresAtRow1OfPuzzle = new int[SIZE_OF_PUZZLE];
    public static int[] squaresAtRowLastOfPuzzle= new int[SIZE_OF_PUZZLE];
    public static int[] squaresAtColumn1OfPuzzle= new int[SIZE_OF_PUZZLE];
    public static int[] squaresAtColumnLastOfPuzzle= new int[SIZE_OF_PUZZLE];

    public static int[] startState = new int[SIZE_OF_PUZZLE];
    public static int[] endState = new int[SIZE_OF_PUZZLE];
    public static int[][] endStateMatrix = new int[NUMBER_OF_ROWS][NUMBER_OF_ROWS];

    public static void main(String[] args) {
        findLocationOfUsefulPieces();

        startState = receiveInput("start");
        endState = receiveInput("end");

        endStateMatrix = convertToMatrix(endState);

        //determineIfStatesSolvable();

        Integer[] possibleSwaps = findPossibleSwaps(startState);
        HashMap<Integer,Integer> possibleMovementsWithH = calculateHValueOfPossibleMovements(possibleSwaps);
        displayPossibleMovements(possibleMovementsWithH);
    }

    /**
     * Receives a state in array form and converts it to a matrix
     * @param endState
     * @return
     */
    private static int[][] convertToMatrix(int[] endState) {
        int[][] matrix = new int[NUMBER_OF_ROWS][NUMBER_OF_ROWS];
        int index = 0;
        for(int i = 0;i<NUMBER_OF_ROWS;i++){
            for(int j =0;j<NUMBER_OF_ROWS;j++){
                matrix[i][j] = endState[index];
                index++;
            }
        }

        return matrix;
    }

    /**
     * Returns key value pair of possible movement along with h value
     * - loops through possible swaps
     * - gets state if swap applied
     * - get h value of state
     * @param possibleSwaps
     * @return
     */
    private static HashMap<Integer, Integer> calculateHValueOfPossibleMovements(Integer[] possibleSwaps) {
        //TODO will have to update to apply to current state not just start
        HashMap<Integer,Integer> possibleMovementsWithHValue = new HashMap<>();
        for(int i =0;i<possibleSwaps.length;i++){
            if(possibleSwaps[i] != null){
                    int[] appliedState = applyPossibleSwapToStartState(possibleSwaps[i]);
                    int hValueForState = calculateHForStateAgainstEndState(appliedState);
                    possibleMovementsWithHValue.put(possibleSwaps[i],hValueForState);
            }
            else{
                possibleMovementsWithHValue.put(null,null);
            }
        }
        return possibleMovementsWithHValue;
    }

    /**
     * Returns the H value for the state by returning converting to matrix and getting square distance
     * @param appliedState
     * @return
     */
    private static int calculateHForStateAgainstEndState(int[] appliedState) {

        int hValue = 0;
        int[][] convertedAppliedState = convertToMatrix(appliedState);

        //for each tile in puzzle
        for(int tile =0;tile<SIZE_OF_PUZZLE;tile++){
            int indexOfTileEndState = getIndexOfTile(tile,endStateMatrix);
            int indexOfTileAppliedState = getIndexOfTile(tile,convertedAppliedState);
            hValue += Math.abs(indexOfTileAppliedState - indexOfTileEndState);
        }

        return hValue;
    }

    /**
     * Returns the sum of the index of the tile in the passed matrix eg (2,2) = 4
     * @param tile
     * @param stateMatrix
     * @return
     */
    private static int getIndexOfTile(int tile, int[][] stateMatrix) {
        for(int i = 0;i<NUMBER_OF_ROWS;i++){
            for(int j = 0;j<NUMBER_OF_ROWS;j++){
                if(stateMatrix[i][j] == tile){
                    return(i+j);
                }
            }
        }
        return 0;
    }

    /**
     * Returns the puzzle state if the passed swap index was applied
     * @param possibleSwap
     * @return
     */
    private static int[] applyPossibleSwapToStartState(Integer possibleSwap) {
        int[] state = startState.clone();
        int temp = 0;
        int indexOfZero = findIndexOfZeroInState(startState);
        temp = startState[possibleSwap];
        state[possibleSwap] = 0;
        state[indexOfZero] = temp;
        return state;
    }

    /**
     * Loops through available movements and displays possible movements in JOptionPane along with corresponding h value
     * @param possibleSwaps
     */
    private static void displayPossibleMovements(HashMap<Integer,Integer> possibleSwaps) {
        String outputString = "";
        int direction = 0;
        Iterator<Map.Entry<Integer, Integer>> iterator = possibleSwaps.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer, Integer> next = iterator.next();
            if(next.getKey() != null){
                switch(direction){
                    case(0): outputString += formatPossibleMovementForDisplay(next.getKey(),next.getValue(),"South"); break;
                    case(1): outputString += formatPossibleMovementForDisplay(next.getKey(),next.getValue(),"West"); break;
                    case(2): outputString += formatPossibleMovementForDisplay(next.getKey(),next.getValue(),"North"); break;
                    case(3): outputString += formatPossibleMovementForDisplay(next.getKey(),next.getValue(),"East"); break;
                }
            }
            direction++;
        }
        JOptionPane.showMessageDialog(null, outputString, "Available Moves", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reformats the possible movement for the output dialogue
     * @param possibleSwap
     * @param direction
     * @return
     */
    private static String formatPossibleMovementForDisplay(Integer possibleSwap,Integer hValue, String direction) {
        return "Can move " + String.valueOf(startState[possibleSwap]) + " to the " + direction + " with a h Value of " + hValue + "\n";
    }

    /**
     * Based on the size of the array locates the locations of the pieces in the first and last row and column
     * Required for the checking what are the next possible legal moves
     */
    private static void findLocationOfUsefulPieces() {
        int[] puzzle = new int[SIZE_OF_PUZZLE];

        int row = 0;
        int col = 0;
        int row1Index = 0;
        int rowLastIndex = 0;
        int col1Index = 0;
        int colLastIndex = 0;

        for(int i =0;i<=SIZE_OF_PUZZLE;i+=NUMBER_OF_ROWS){
            row++;
            for(int j = i; j<= NUMBER_OF_ROWS-1+i && j != SIZE_OF_PUZZLE; j++){
                col++;
                //System.out.print("i:" + String.valueOf(i) + " j:" + String.valueOf(j)+  " " +String.valueOf(puzzle[j]) + " ");
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


    /**
     * Returns the index of the possible movements of the black tile for the given state
     * 0th index North
     * 1st Index West
     * 2nd Index South
     * 3rd Index East
     * Any non possible movements will be null
     * @param state
     * @return
     */
    private static Integer[] findPossibleSwaps(int[] state) {
        int indexOfZero = findIndexOfZeroInState(state);
        Integer[] possibleSwaps = new Integer[4];
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

    /**
     * Checks where the blank tile is located in the given state
     * @param state
     * @return
     */
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

    /**
     * Checks if the blank tile is located in the array(eg. the fist row)
     * @param indexOfZero
     * @param arrayToBeSearched
     * @return
     */
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
        return true;
    }


}

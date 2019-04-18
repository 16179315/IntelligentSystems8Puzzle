package com.ohora;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import java.util.*;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialiseStateVariables();
        runAlgorithm(primaryStage);
    }

    /**
     * Runs the A* algorithm based on the input states
     */
    private static void runAlgorithm(Stage primaryStage){
        State S = new State(State.startState,null);
        int[] E = State.endState;
        State C = S;

        ArrayList<State> open = new ArrayList<>();
        ArrayList<State> closed = new ArrayList<>();

        while(!(Arrays.equals(C.getState(),E))){
            ArrayList<State> X = getChildrenOfCurrentState(C);
            addChildrenToOpenIfNotClosed(X,open,closed);

            closed.add(C);

            C = findMinimumfInOpen(open);

            closed.add(C);
            open.remove(C);
        }

        System.out.println(printOutPath(C,closed));
        displayGraphicPath(primaryStage);

    }

    private static void displayGraphicPath(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(grid,500,500);

        // UI operation runs on different thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        reDrawBoard(State.pathTaken.get(State.pathCounter),grid);
                    }
                };

                for(int i =0;i<State.pathTaken.size();i++) {
                    try {
                        State.pathCounter = i;
                        // UI update is run on the Application thread
                        Platform.runLater(updater);
                        Thread.sleep(450);
                    } catch (InterruptedException ex) {
                    }
                }
            }

        });


        thread.setDaemon(true);
        thread.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Intelligent Systems Project");
        primaryStage.show();
    }

    private static void reDrawBoard(int[][] num,GridPane grid) {
        final int TILE_SIZE = (500/State.NUMBER_OF_ROWS) - State.NUMBER_OF_ROWS*3 ;
        final String FONT_SIZE = String.valueOf(TILE_SIZE/6);
        final Color[] colors = {Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTSALMON, Color.LIGHTCYAN, Color.LIGHTCORAL,Color.LIGHTGOLDENRODYELLOW};

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                grid.getChildren().removeAll();

                for (int row = 0; row < State.NUMBER_OF_ROWS; row++) {
                    for (int col = 0; col < State.NUMBER_OF_ROWS; col++) {

                        Text text = new Text(String.valueOf(num[row][col]));
                        text.setFill(Color.BLACK);
                        text.setStyle("-fx-font: "+FONT_SIZE+ " arial;");

                        //get color rect should be based on current text number and where that text should be
                        int currentNumber = num[row][col];
                        int positionInEndState = State.findPositionOfNumInEndStateMatrix(currentNumber);
                        Color colorOfRect = colors[positionInEndState%6];

                        Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE, colorOfRect);
                        //Setting the height and width of the arc
                        rect.setArcWidth(30.0);
                        rect.setArcHeight(20.0);

                        //shadow
                        rect.setStroke(Color.BLACK);
                        rect.setStrokeWidth(2);
                        DropShadow e = new DropShadow();
                        e.setWidth(10);
                        e.setHeight(10);
                        e.setOffsetX(5);
                        e.setOffsetY(5);
                        e.setRadius(10);
                        rect.setEffect(e);

                        StackPane stack = new StackPane();
                        stack.getChildren().addAll(rect, text);
                        grid.add(stack, col, row);
                    }
                }
            }

        });
    }

    /**
     * Takes in and sets up all the required info
     */
    private static void initialiseStateVariables() {
        State.NUMBER_OF_ROWS = receiveRowInput();
        State.SIZE_OF_PUZZLE =  State.NUMBER_OF_ROWS*State.NUMBER_OF_ROWS;

        State.findLocationOfUsefulPieces();

        State.startState = receiveInput("start");
        State.endState = receiveInput("end");
        State.endStateMatrix = State.convertToMatrix(State.endState);
    }

    /**
     * Takes in the number of rows in puzzle, eg for 8 puzzle, there are 3 rows
     * @return number of rows
     */
    private static int receiveRowInput() {
        boolean isValid = false;
        int numberOfRows = 0;
        String input = "";
        while(!isValid){
            input = JOptionPane.showInputDialog("Enter no. of Rows in puzzle, can be any size!");
            if(!validateRows(input)) {
                JOptionPane.showMessageDialog(null, "Invalid input", "Name", JOptionPane.ERROR_MESSAGE);
            }
            isValid = validateRows(input);
        }
        String[] inputArray = input.trim().split(" ");
        numberOfRows = Integer.parseInt(inputArray[0]);
        return numberOfRows;
    }

    /**
     * Validates the number of rows input
     * @param rows
     * @return
     */
    private static boolean validateRows(String rows){
        String[] inputStringArray = rows.trim().split(" ");
        int[] numbers;
        try {
            numbers = Arrays.stream(inputStringArray).mapToInt(Integer::parseInt).toArray();
        } catch(NumberFormatException e){
            return false;
        }
        if(numbers.length==1){
            return true;
        }
        else{
            return false;
        }
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
            if (uniqueNumbers.contains(number) || (number < 0) || number > State.SIZE_OF_PUZZLE - 1) {
                return false;
            }
            uniqueNumbers.add(number);
        }
        if(uniqueNumbers.size() != State.SIZE_OF_PUZZLE){
            return false;
        }
        return true;
    }



    /**
     * Adds any children in X to open if they are not in closed
     * @param X
     * @param open
     * @param closed
     */
    private static void addChildrenToOpenIfNotClosed(ArrayList<State> X, ArrayList<State> open, ArrayList<State> closed) {
        for(int i = 0;i<X.size();i++){
            boolean closedContainsState = false;
            for(int j = 0;j<closed.size();j++){
                if(Arrays.equals(closed.get(j).getState(),X.get(i).getState())){
                    closedContainsState = true;
                }
            }
            if(!closedContainsState){
                open.add(X.get(i));
            }
        }
    }

    /**
     * Prints out the path taken to solve the A* algo by following each state objects parent
     * @param c
     * @param closed
     * @return
     */
    private static String printOutPath(State c,ArrayList<State> closed) {
        String out = "";
        while(c.getState() != State.startState){
            out = c.toString() + out;
            State.pathTaken.add(0,State.convertToMatrix(c.getState()));
            c = c.getParent();
        }
        out = c.toString() + out;
        State.pathTaken.add(0,State.convertToMatrix(c.getState()));

        return out;
    }

    /**
     * Finds the state with the smallest F(n) in the open list
     * @param open
     * @return
     */
    private static State findMinimumfInOpen(ArrayList<State> open) {
        State temp = open.get(0);
        for(int i=0;i<open.size();i++){
            if(open.get(i).getfValue() < temp.getfValue()){
                temp = open.get(i);
            }
        }

        return temp;
    }

    /**
     * Finds all the possible children from the current state
     * @param currentState
     * @return array list of states containing children of param
     */
    private static ArrayList<State> getChildrenOfCurrentState(State currentState)
    {
        ArrayList<State> children = new ArrayList<>();
        Integer[] possibleSwaps = findPossibleSwaps(currentState.getState());

        for (int i = 0; i < possibleSwaps.length; i++) {
            if (possibleSwaps[i] != null) {
                int[] appliedState = applyPossibleSwapToCurrentState(possibleSwaps[i], currentState.getState());
                State child = new State(appliedState,currentState);

                children.add(child);
            }
        }

        return children;
    }


    /**
     * Swaps the 0 value in the currentState based on the first param
     * @param possibleSwap
     * @param currentState
     * @return the new state after swap applied
     */
    private static int[] applyPossibleSwapToCurrentState(Integer possibleSwap,int[] currentState) {
        int[] state = currentState.clone();
        int temp = 0;
        int indexOfZero = findIndexOfZeroInState(currentState);
        temp = currentState[possibleSwap];
        state[possibleSwap] = 0;
        state[indexOfZero] = temp;
        return state;
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
        if(zeroIsContainedInGivenArray(indexOfZero,State.squaresAtColumn1OfPuzzle)){
            possibleSwaps[1] = indexOfZero + 1;
        }
        else if(zeroIsContainedInGivenArray(indexOfZero,State.squaresAtColumnLastOfPuzzle)){
            possibleSwaps[3] = indexOfZero - 1;
        }
        else{
            possibleSwaps[1] = indexOfZero+1;
            possibleSwaps[3] = indexOfZero-1;
        }
        if(zeroIsContainedInGivenArray(indexOfZero,State.squaresAtRow1OfPuzzle)){
            possibleSwaps[2] = indexOfZero+State.NUMBER_OF_ROWS;
        }
        else if(zeroIsContainedInGivenArray(indexOfZero,State.squaresAtRowLastOfPuzzle)){
            possibleSwaps[0] = indexOfZero - State.NUMBER_OF_ROWS;
        }
        else {
            possibleSwaps[2] = indexOfZero + State.NUMBER_OF_ROWS;
            possibleSwaps[0] = indexOfZero -State.NUMBER_OF_ROWS;
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


}

class State{

    //Input data
    public static int NUMBER_OF_ROWS = 0;
    public static int SIZE_OF_PUZZLE = 0;
    public static int[] startState;
    public static int[] endState;
    public static int[][] endStateMatrix;

    //Required to check next possible moves
    public static int[]squaresAtRow1OfPuzzle;
    public static int[] squaresAtRowLastOfPuzzle;
    public static int[] squaresAtColumn1OfPuzzle;
    public static int[] squaresAtColumnLastOfPuzzle;

    public static ArrayList<int[][]> pathTaken = new ArrayList<>();
    public static int pathCounter = 0;

    //All required state data
    private int[] state;
    private State parent;
    private int hValue;
    private int gValue;
    private int fValue;

    /**
     * Constructor which sets the g value based off the parent and updates the h value based on given state
     * @param state
     * @param parentState
     */
    public State(int[] state,State parentState) {
        this.state = state;
        this.parent = parentState;
        hValue = calculateHForStateAgainstEndState();

        if(parentState != null){
            this.gValue = parentState.getgValue()+1;
        }

        this.fValue = gValue + hValue;
    }


    public String toString(){
        String out = printArray(state) ;
        return out;
    }

    /**
     * Prints array in grid format
     * @param state
     * @return
     */
    private String printArray(int[] state) {
        int [] tempMatrix = this.getState();
        String stateOutput = "";
        int indexCounter = 0;
        for(int i = 0; i<NUMBER_OF_ROWS; i++){
            for(int j=0; j<NUMBER_OF_ROWS; j++){
                stateOutput += tempMatrix[indexCounter] + " " ;
                indexCounter++;
            }
            stateOutput+= "\n";
        }
        return stateOutput + "\n";
    }

    /**
     * Receives a state in array form and converts it to a matrix
     * @param endState
     * @return
     */
    public static int[][] convertToMatrix(int[] endState) {
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


    public static int findPositionOfNumInEndStateMatrix(int num){
        for(int i =0;i<NUMBER_OF_ROWS;i++){
            for(int j = 0;j <NUMBER_OF_ROWS;j++){
                if(endStateMatrix[i][j] == num){
                    return i+j;
                }
            }
        }
        return -1;
    }
    /**
     * Returns the H value for the state by returning converting to matrix and getting square distance
     * @return
     */
    private int calculateHForStateAgainstEndState() {
        int hValue = 0;
        int[][] convertedAppliedState = convertToMatrix(this.state);

        //for each tile in puzzle
        for(int tile =0;tile<SIZE_OF_PUZZLE;tile++){
            hValue += calculateIndividualTileHValue(tile,convertedAppliedState);
        }
        return hValue;
    }

    /**
     * For a single tile, will find the X & Y difference compared to the end state
     * @param tile
     * @param convertedAppliedState
     * @return
     */
    private int calculateIndividualTileHValue(int tile, int[][] convertedAppliedState) {
        int xindexOfTileEndState = getIndexOfTile(tile,endStateMatrix,"x");
        int yindexOfTileEndState = getIndexOfTile(tile,endStateMatrix,"y");
        int xindexOfTileAppliedState = getIndexOfTile(tile,convertedAppliedState,"x");
        int yindexOfTileAppliedState = getIndexOfTile(tile, convertedAppliedState,"y");
        int diff1 = Math.abs(xindexOfTileAppliedState - xindexOfTileEndState);
        int diff2 = Math.abs(yindexOfTileEndState - yindexOfTileAppliedState);
        return diff1+diff2;
    }

    /**
     * Returns the desired  co ord of the designated tile
     * @param tile
     * @param stateMatrix
     * @return
     */
    private int getIndexOfTile(int tile, int[][] stateMatrix,String coOrd) {
        for(int i = 0;i<NUMBER_OF_ROWS;i++){
            for(int j = 0;j<NUMBER_OF_ROWS;j++){
                if(stateMatrix[i][j] == tile){
                    if(coOrd == "x"){
                        return i;
                    }
                    else{
                        return j;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Based on the size of the array locates the locations of the pieces in the first and last row and column
     * Required for the checking what are the next possible legal moves
     */
    public static void findLocationOfUsefulPieces() {
        int[] puzzle = new int[State.SIZE_OF_PUZZLE];

        int row = 0;
        int col = 0;
        int row1Index = 0;
        int rowLastIndex = 0;
        int col1Index = 0;
        int colLastIndex = 0;

        squaresAtRow1OfPuzzle = new int[State.SIZE_OF_PUZZLE];
        squaresAtColumn1OfPuzzle= new int[State.SIZE_OF_PUZZLE];
        squaresAtColumnLastOfPuzzle = new int[State.SIZE_OF_PUZZLE];
        squaresAtRowLastOfPuzzle = new int[State.SIZE_OF_PUZZLE];

        for(int i =0;i<=State.SIZE_OF_PUZZLE;i+=State.NUMBER_OF_ROWS){
            row++;
            for(int j = i; j<= State.NUMBER_OF_ROWS-1+i && j != State.SIZE_OF_PUZZLE; j++){
                col++;
                if(row == 1){
                    squaresAtRow1OfPuzzle[row1Index] = j;
                    row1Index++;
                }
                if(col == 1){
                    squaresAtColumn1OfPuzzle[col1Index] = j;
                    col1Index++;
                }
                if(row == State.NUMBER_OF_ROWS){
                    squaresAtRowLastOfPuzzle[rowLastIndex] = j;
                    rowLastIndex++;
                }
                if(col == State.NUMBER_OF_ROWS){
                    squaresAtColumnLastOfPuzzle[colLastIndex] = j;
                    colLastIndex++;
                }
            }
            col=0;
        }

    }


    public int[] getState() {
        return state;
    }

    public int getgValue() {
        return gValue;
    }

    public int getfValue() {
        return fValue;
    }

    public State getParent() {
        return parent;
    }
}
import javax.swing.JOptionPane;
import java.util.*;

public class is16179315 {

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
        State.setCurrentIdCount(0);
        findLocationOfUsefulPieces();

        startState = receiveInput("start");
        endState = receiveInput("end");


        endStateMatrix = convertToMatrix(endState);

        runAlgorithm();

//        int[] test = {0,1,2,3,4,5,6,7,8};
//        System.out.println(calculateHForStateAgainstEndState(test));

//        Integer[] possibleSwaps = findPossibleSwaps(startState);
//        LinkedHashMap<Integer,Integer> possibleMovementsWithH = calculateHValueOfPossibleMovements(possibleSwaps);
//        displayPossibleMovements(possibleMovementsWithH);
    }

    private static void runAlgorithm(){
        State S = new State(startState,0);
        S.sethValue(calculateHForStateAgainstEndState(S.getState()));
        S.setgValue(0);
        S.updatefValue();

        int[] E = endState;
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

        printOutPath(C,closed);

    }

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

    private static void printOutPath(State c,ArrayList<State> closed) {
        do{
            System.out.println(c.toString());
            for(int i =0;i<closed.size();i++){
                if(c.getParentId() == closed.get(i).getId()){
                    c = closed.get(i);
                }
            }
            //first state has id 1
        }while(c.getId() != 1);
    }

    private static State findMinimumfInOpen(ArrayList<State> open) {
        State temp = new State(null,-1);
        temp.setfValue(Integer.MAX_VALUE);
        for(int i=0;i<open.size();i++){
            if(open.get(i).getfValue() < temp.getfValue()){
                temp = open.get(i);
            }
        }

        return temp;
    }

    private static ArrayList<State> getChildrenOfCurrentState(State currentState)
    {
        ArrayList<State> children = new ArrayList<>();
        Integer[] possibleSwaps = findPossibleSwaps(currentState.getState());

        for (int i = 0; i < possibleSwaps.length; i++) {
            if (possibleSwaps[i] != null) {
                int[] appliedState = applyPossibleSwapToCurrentState(possibleSwaps[i], currentState.getState());

                State child = new State(appliedState,currentState.getId());
                child.sethValue(calculateHForStateAgainstEndState(child.getState()));
                child.setgValue(currentState.getgValue()+1);
                child.updatefValue();

                children.add(child);
            }
        }

        return children;
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
    private static LinkedHashMap<Integer, Integer> calculateHValueOfPossibleMovements(Integer[] possibleSwaps) {
        //TODO will have to update to apply to current state not just start
        LinkedHashMap<Integer,Integer> possibleMovementsWithHValue = new LinkedHashMap<>();
        for(int i =0;i<possibleSwaps.length;i++){
            if(possibleSwaps[i] != null){
                int[] appliedState = applyPossibleSwapToStartState(possibleSwaps[i]);
                int hValueForState = calculateHForStateAgainstEndState(appliedState);
                possibleMovementsWithHValue.put(possibleSwaps[i],hValueForState);
            }
            else{
                //no possible movement, used sizeofpuzzle as cant have two identical keys
                possibleMovementsWithHValue.put(SIZE_OF_PUZZLE+i,null);
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
            hValue += calculateIndividualTileHValue(tile,convertedAppliedState);
        }
        return hValue;
    }

    private static int calculateIndividualTileHValue(int tile, int[][] convertedAppliedState) {
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
    private static int getIndexOfTile(int tile, int[][] stateMatrix,String coOrd) {
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
     * Loops through available movements and displays possible movements in JOptionPane along with corresponding h value
     * @param possibleSwaps
     */
    private static void displayPossibleMovements(HashMap<Integer,Integer> possibleSwaps) {
        String outputString = "";
        int direction = 0;
        Iterator<Map.Entry<Integer, Integer>> iterator = possibleSwaps.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer, Integer> next = iterator.next();
            if(next.getValue() != null){
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

class State{
    private int[] state;
    private int id;
    private int parentId;
    private static int currentIdCount;
    private int hValue;
    private int gValue;
    private int fValue;

    public State(int[] state, int parentId) {
        currentIdCount++;
        this.state = state;
        this.id = currentIdCount;
        this.parentId = parentId;


    }

    public static void setCurrentIdCount(int currentIdCount) {
        State.currentIdCount = currentIdCount;
    }

    public String toString(){
        String out = "state: " + printArray(state) + "\n";
        out += "id:" + id + "\n";
        out += "parentId:" + parentId + "\n";
        out += "currentIdCount:" + currentIdCount + "\n";
        out += "hValue:" + hValue + "\n";
        out += "gValue:" + gValue + "\n";
        out += "fValue:" + fValue + "\n";
        return out;
    }

    private String printArray(int[] state) {
        String out = "[";
        for(int i = 0;i<state.length;i++){
            out+=state[i] + ",";
        }
        out += "]";
        return out;
    }


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getState() {
        return state;
    }

    public void setState(int[] state) {
        this.state = state;
    }

    public void sethValue(int hValue) {
        this.hValue = hValue;
    }

    public int gethValue() {
        return hValue;
    }

    public int getgValue() {
        return gValue;
    }

    public void setgValue(int gValue) {
        this.gValue = gValue;
    }

    public int getfValue() {
        return fValue;
    }

    public void setfValue(int fValue) {
        this.fValue = fValue;
    }

    public void updatefValue() {
        this.fValue = this.gValue+this.hValue;
    }
}
package com.ohora;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.JOptionPane;
public class Main {

    public static void main(String[] args) {
        // TODO update size so that puzzle can be any size

        int[] startState = new int[9];
        int[] endState = new int[9];

        startState = receiveInput("start");
        endState = receiveInput("end");

        // take input end and validate
        // get possible movements
        // calculate h(distances of tiles from their pos) function of each movement
        // display possible
    }

    private static int[] receiveInput(String state){
        String input = JOptionPane.showInputDialog("Please enter the " + state + " state of the puzzle");
        //if(validInput)
        String[] inputArray = input.trim().split(" ");
        int[] intArray =new int[9];
        for(int i = 0;i<9;i++){
            intArray[i] = Integer.parseInt(inputArray[i]);
        }
        return intArray;
    }

//    private static boolean validateInput(String input){
//
//    }


}

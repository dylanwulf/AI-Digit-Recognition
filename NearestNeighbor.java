//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

//This class contains the main method for the nearest neighbor program

import java.util.LinkedList;
import java.io.File;

public class NearestNeighbor {
    /* Command line arguments:
    0: location of folder containing images
    1: how many images to use as known images (max 10,000)
    */
    public static void main(String[] args) {
        String trainLoc = (args.length >= 1)? args[0] : ".." + File.separator + "train_images";
        int setSize = (args.length >= 2)? Integer.parseInt(args[1]) : 5000;
        
        //Linked Lists contain the top 3 results so far
        LinkedList<Integer> topDigits = new LinkedList<Integer>();
        LinkedList<Double> topDistances = new LinkedList<Double>();
        
        //Read in all the files that we will need
        System.out.println("Reading files...");
        //read in labels file
        int[] labels = MyFileReader.readLabels(trainLoc + File.separator + "labels.bin");
        
        //Read in known images
        double[][] knownImages = new double[setSize][784];
        double[][] testImages = new double[1000][784];
        for (int fileIndex = 1; fileIndex <= setSize; fileIndex++) {
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, knownImages[fileIndex - 1]);
        }
        //Read in test images
        for (int fileIndex = 10001; fileIndex <= 11000; fileIndex++) {
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, testImages[fileIndex - 10001]);
        }
        
        System.out.println("Finding nearest neighbors...");
        
        int numCorrect = 0;
        for (int t = 0; t < testImages.length; t++) { //iterate over every test image
            for (int k = 0; k < knownImages.length; k++) { //look at every known image
                double distance = distance(testImages[t], knownImages[k]);
                
                //If the linked lists are empty, fill them up with 3 copies
                //of the first image found.
                if (topDigits.size() == 0) {
                    topDigits.add(labels[k]);
                    topDigits.add(labels[k]);
                    topDigits.add(labels[k]);
                    topDistances.add(distance);
                    topDistances.add(distance);
                    topDistances.add(distance);
                }
                //If the linked lists already have stuff in them, 
                //check if the current known image is closer than 
                //any checked so far. if it is, insert it at that location
                //in the linked lists. then remove the last element
                //in the linked lists (the longest distance) to keep the size = 3
                else {
                    for (int i = 0; i < topDigits.size(); i++) {
                        if (distance < topDistances.get(i)) {
                            topDigits.add(i, labels[k]);
                            topDistances.add(i, distance);
                            topDigits.removeLast();
                            topDistances.removeLast();
                            break;
                        }
                    }
                }
            }
            
            //Get our best guess
            //If two of the three have the same digit, will use that digit for the guess
            //otherwise use the digit in the first spot
            int bestGuess;
            if (topDigits.get(1) == topDigits.get(2))
                bestGuess = topDigits.get(1);
            else
                bestGuess = topDigits.get(0);
            if (bestGuess == labels[t + 10000])
                numCorrect++;
            topDigits.clear(); //Clear the linked lists for the next test image
            topDistances.clear();
        }
        System.out.println("Percent correct: " + ((double)numCorrect / 1000 * 100) + "%");
    }
    
    //Calculate the distance between two images
    //Using the L2 (Euclidian) distance
    public static double distance(double[] image1, double[] image2) {
        double distance = 0.0;
        for (int i = 0; i < image1.length; i++) {
            distance += Math.pow(image1[i] - image2[i], 2);
        }
        return Math.sqrt(distance);
    }
}
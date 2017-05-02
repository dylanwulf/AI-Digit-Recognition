//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

//This file contains the main method for the neural net program.
//It is used to train the neural net and test it.

import java.io.File;

public class NeuralNet {
    
    /* Command line arguments: 
    0: "train" or "test"
    1: location of training and testing data (folder containing imageXXXXXX.bin files)
    2: size of training data set (1 - 10,000) (training only)
    3: learning rate (training only)
    4: number of epochs (training only)
    5: number of threads to use (training only)
    */
    public static void main(String[] args) {
        //Get the location of the training data, or default to ../train_images
        String trainLoc = (args.length >= 2)? args[1] : ".." + File.separator + "train_images";
        
        //Create array to hold perceptrons in. 10 perceptrons for 10 digits.
        Perceptron[] percs = new Perceptron[10];
        int[] labels = MyFileReader.readLabels(trainLoc + File.separator + "labels.bin");
        
        //If we are training
        if (args[0].equals("train")) {
            //Get more options from command line
            //if arguments are not given, defaults to:
            //setsize = 5000
            //learning rate = 0.2
            //epochs = 1
            //threads = 1
            int setSize = (args.length >= 3)? Integer.parseInt(args[2]) : 5000;
            double learningRate = (args.length >= 4)? Double.parseDouble(args[3]) : 0.2;
            int epochs = (args.length >= 5)? Integer.parseInt(args[4]) : 1;
            int threads = (args.length >= 6)? Integer.parseInt(args[5]) : 1;
            
            //Print out training configuration
            System.out.println("Training...");
            System.out.println("Sample size: " + setSize);
            System.out.println("Learning rate: " + learningRate);
            System.out.println("Epochs: " + epochs);
            System.out.println("Number of threads: " + threads);
            
            //Create a new perceptron for each digit
            for (int i = 0; i <= 9; i++)
                percs[i] = new Perceptron(i, learningRate);
            
            //Train the perceptrons
            trainPercs(percs, trainLoc, setSize, labels, threads, epochs);
            
            //Save the perceptron data in files DWweightsX.bin
            //where X is the digit it represents
            for (int i = 0; i <= 9; i++)
                percs[i].saveWeights("DWweights" + i + ".bin");
            
            System.out.println("Done training!");
        }
        //If we are testing
        else if (args[0].equals("test")) {
            System.out.println("Reading weights from files...");
            
            //Create perceptrons from weights in files
            for (int i = 0; i <= 9; i++)
                percs[i] = new Perceptron("DWweights" + i + ".bin", 0);
            
            System.out.println("Testing...");
            
            //Test the perceptrons against the last 1000 images and print the results
            double percentCorrect = testPercs(percs, trainLoc, labels);
            System.out.println("Testing complete. Percent correct: " + percentCorrect + "%");
        }
    }
    
    //Train the perceptrons with the given configuration
    public static void trainPercs(Perceptron[] percs, String trainLoc, int setSize, int[] labels, int numThreads, int epochsLimit) {
        Trainer[] trainers = new Trainer[numThreads]; //array to hold Trainer objects
        
        //For each epoch
        for (int e = 0; e < epochsLimit; e++) {
            //For each thread we are making
            for (int i = 0; i < numThreads; i++) {
                //Split up the files evenly among threads.
                //the last thread will get the remainder of the files
                //after splitting evenly.
                int start = setSize / numThreads * i + 1;
                int end = (i == numThreads - 1)? setSize : setSize / numThreads * (i+1);
                
                //Create a new Trainer object, one per thread
                trainers[i] = new Trainer(percs, start, end, trainLoc, labels);
            }
            //Start all the threads
            for (Trainer t : trainers)
                t.start();
            
            //Wait for all the threads to finish
            for (Trainer t : trainers) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    System.err.println("Thread interrupted");
                }
            }
        }
    }
    
    //Test the perceptrons
    public static double testPercs(Perceptron[] percs, String trainLoc, int[] labels) {
        int numCorrect = 0;
        
        //Image will be stored in this array. More efficient than creating a new array 
        //for each image. 
        double[] image = new double[784];
        
        //For each file between 10,001 and 11,000 (inclusive)
        for (int fileIndex = 10001; fileIndex <= 11000; fileIndex++) {
            //Generate the filename "[trainLoc]/image[fileIndex].bin"
            //fileIndex is 6 digits long, padded with 0s on the left
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, image);
            
            //For each perceptron, get the activation function (sigmoid) result
            int bestGuess = 0;
            double bestSigmoid = -100;
            for (int percIndex = 0; percIndex <= 9; percIndex++) {
                double recResult = percs[percIndex].recognize(image);
                //if this one is better than the previous best one,
                //update best to be this one.
                if (recResult > bestSigmoid) { 
                    bestGuess = percIndex;
                    bestSigmoid = recResult;
                }
            }
            //bestGuess will be the perceptron with the highest sigmoid result
            if (bestGuess == labels[fileIndex - 1]) //check if we were right.
                numCorrect++;
        }
        double percentCorrect = (double)numCorrect / 1000 * 100;
        return percentCorrect;
    }
}
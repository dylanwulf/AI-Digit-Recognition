//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

//This class allows the neural net training to be done using multiple threads.
//Each separate thread is responsible for training with a different section of the data files.
//For example, if we are training on 1000 images with 5 threads, each thread would be responsible
//for 200 image files.

import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class Trainer extends Thread {
    private static Perceptron[] percs;
    private static String trainLoc = "";
    private static int[] labels;
    private static Double totalChange = 0.0;
    private final int start; //which file index to start at (inclusive)
    private final int end; //which file index to end at (inclusive)
    
    //Constructor
    public Trainer(Perceptron[] percs, int startIndex, int endIndex, String trainLoc, int[] labels) {
        Trainer.percs = percs;
        start = startIndex;
        end = endIndex;
        Trainer.trainLoc = trainLoc;
        Trainer.labels = labels;
    }
    
    //Get the total change in weights, then clear the value
    public static double getTotalChangeAndClear() {
        double tmp = totalChange;
        totalChange = 0.0;
        return tmp;
    }
    
    //gets called when the thread starts
    public void run() {
        double change = 0.0;
        double[] image = new double[784];
        for (int fileIndex = start; fileIndex <= end; fileIndex++) {
            //Generate the filename "[trainLoc]/image[fileIndex].bin"
            //fileIndex is 6 digits long, padded with 0s on the left
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, image);
            
            //Train each perceptron on the image
            for (Perceptron p : percs) {
                change += p.train(image, labels[fileIndex - 1]);
            }
        }
        //synchronized block ensures no two threads are accessing totalChange at the same time
        synchronized (totalChange) {
            totalChange += change;
        }
    }
}
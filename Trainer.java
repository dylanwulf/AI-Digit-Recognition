//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

import java.util.LinkedList;
import java.util.Queue;

public class Trainer extends Thread {
    private static Perceptron[] percs;
    private static String trainLoc = "";
    private static int[] labels;
    private final int start; //which file index to start at (inclusive)
    private final int end; //which file index to end at (inclusive)
    
    public Trainer(Perceptron[] percs, int startIndex, int endIndex, String trainLoc, int[] labels) {
        Trainer.percs = percs;
        start = startIndex;
        end = endIndex;
        Trainer.trainLoc = trainLoc;
        Trainer.labels = labels;
    }
    
    public void run() {
        double[] image = new double[784];
        boolean keepGoing = true;
        for (int fileIndex = start; fileIndex <= end; fileIndex++) {
            //Generate the filename "[trainLoc]/image[fileIndex].bin"
            //fileIndex is 6 digits long, padded with 0s on the left
            String filename = String.format("%1$s/image%2$06d.bin", trainLoc, fileIndex);
            MyFileReader.readImageInto(filename, image);
            
            for (Perceptron p : percs) {
                p.train(image, labels[fileIndex - 1]);
            }
        }
    }
}
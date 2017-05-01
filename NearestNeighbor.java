//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

import java.util.LinkedList;
import java.io.File;

public class NearestNeighbor {
    public static void main(String[] args) {
        String trainLoc = (args.length >= 1)? args[0] : ".." + File.separator + "train_images";
        int setSize = (args.length >= 2)? Integer.parseInt(args[1]) : 5000;
        LinkedList<Integer> topDigits = new LinkedList<Integer>();
        LinkedList<Double> topDistances = new LinkedList<Double>();
        
        System.out.println("Reading files...");
        
        int[] labels = MyFileReader.readLabels(trainLoc + File.separator + "labels.bin");
        double[][] knownImages = new double[setSize][784];
        double[][] testImages = new double[1000][784];
        for (int fileIndex = 1; fileIndex <= setSize; fileIndex++) {
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, knownImages[fileIndex - 1]);
        }
        for (int fileIndex = 10001; fileIndex <= 11000; fileIndex++) {
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, testImages[fileIndex - 10001]);
        }
        
        System.out.println("Finding nearest neighbors...");
        
        int numCorrect = 0;
        for (int t = 0; t < testImages.length; t++) {
            for (int k = 0; k < knownImages.length; k++) {
                double distance = distance(testImages[t], knownImages[k]);
                if (topDigits.size() == 0) {
                    topDigits.add(labels[k]);
                    topDigits.add(labels[k]);
                    topDigits.add(labels[k]);
                    topDistances.add(distance);
                    topDistances.add(distance);
                    topDistances.add(distance);
                }
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
            int bestGuess;
            if (topDigits.get(1) == topDigits.get(2))
                bestGuess = topDigits.get(1);
            else
                bestGuess = topDigits.get(0);
            //System.out.println((t + 10001) + ". guess: " + bestGuess + " actual: " + labels[t + 10000]);
            if (bestGuess == labels[t + 10000])
                numCorrect++;
            topDigits.clear();
            topDistances.clear();
        }
        System.out.println((double)numCorrect / 1000 * 100);
    }
    
    public static double distance(double[] image1, double[] image2) {
        double distance = 0.0;
        for (int i = 0; i < image1.length; i++) {
            distance += Math.pow(image1[i] - image2[i], 2);
        }
        return Math.sqrt(distance);
    }
}
//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class Trainer extends Thread {
    private static Perceptron[] percs;
    private static String trainLoc = "";
    private static int[] labels;
    private static double stopPercent;
    private static Boolean stop = false;
    private static Queue<Double> last100;
    private static Double last100Sum;
    private final int start; //which file index to start at (inclusive)
    private final int end; //which file index to end at (inclusive)
    private int numEpochs;
    
    public Trainer(Perceptron[] percs, int startIndex, int endIndex, String trainLoc, int[] labels, double stopPercent) {
        Trainer.percs = percs;
        start = startIndex;
        end = endIndex;
        Trainer.trainLoc = trainLoc;
        Trainer.labels = labels;
        Trainer.stopPercent = stopPercent;
        stop = false;
        last100 = new LinkedList<Double>();
        last100Sum = 0.0;
        numEpochs = 0;
    }
    
    public int getNumEpochs() {
        return numEpochs;
    }
    
    public void run() {
        double[] image = new double[784];
        boolean keepGoing = true;
        while (keepGoing) {
            for (int fileIndex = start; fileIndex <= end; fileIndex++) {
                //Generate the filename "[trainLoc]/image[fileIndex].bin"
                //fileIndex is 6 digits long, padded with 0s on the left
                String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
                MyFileReader.readImageInto(filename, image);
                
                double maxPercentChange = 0.0;
                for (Perceptron p : percs) {
                    double percentChange = p.train(image, labels[fileIndex - 1]);
                    maxPercentChange = Math.max(maxPercentChange, percentChange);
                }
                
                synchronized (last100) {
                    synchronized (last100Sum) {
                        last100.add(maxPercentChange);
                        last100Sum += maxPercentChange;
                        if (last100.size() > 100) {
                            last100Sum -= last100.remove();
                        }
                        synchronized (stop) {
                            if (last100.size() >= 100 && last100Sum / 100 < stopPercent) {
                                stop = true;
                            }
                            if (stop == true) {
                                keepGoing = false; //exit after this epoch
                            }
                        }
                    }
                }
            }
            numEpochs++;
        }
    }
}
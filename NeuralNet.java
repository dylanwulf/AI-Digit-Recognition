//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

import java.io.File;

public class NeuralNet {
    
    /* Command line arguments: 
    0: "train" or "test"
    1: location of training and testing data (imageXXXXXX.bin files)
    2: size of training data set (1 - 10,000) (training only)
    3: learning rate (training only)
    4: number of threads to use (training only)
    */
    public static void main(String[] args) {
        String trainLoc = args[1];
        
        Perceptron[] percs = new Perceptron[10];
        int[] labels = MyFileReader.readLabels(trainLoc + File.separator + "labels.bin");
        
        if (args[0].equals("train")) {
            int setSize = Integer.parseInt(args[2]);
            double learningRate = Double.parseDouble(args[3]);
            int threads = Integer.parseInt(args[4]);
            System.out.println("Training...");
            System.out.println("Sample size: " + setSize);
            System.out.println("Learning rate: " + learningRate);
            System.out.println("Number of threads: " + threads);
            
            for (int i = 0; i <= 9; i++)
                percs[i] = new Perceptron(i, learningRate);
            int epochs = trainPercs(percs, trainLoc, setSize, labels, threads);
            
            for (int i = 0; i <= 9; i++)
                percs[i].saveWeights("weights" + i + ".bin");
            
            System.out.println("Done training!");
            System.out.println("Number of epochs: " + epochs);
        }
        else if (args[0].equals("test")) {
            System.out.println("Reading weights from files...");
            for (int i = 0; i <= 9; i++)
                percs[i] = new Perceptron("weights" + i + ".bin", 0);
            System.out.println("Testing...");
            double percentCorrect = testPercs(percs, trainLoc, labels);
            System.out.println("Testing complete. Percent correct: " + percentCorrect + "%");
        }
    }
    
    public static int trainPercs(Perceptron[] percs, String trainLoc, int setSize, int[] labels, int numThreads) {
        Trainer[] trainers = new Trainer[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int start = setSize / numThreads * i + 1;
            int end = (i == numThreads - 1)? setSize : setSize / numThreads * (i+1);
            trainers[i] = new Trainer(percs, start, end, trainLoc, labels, 500);
        }
        for (Trainer t : trainers)
            t.start();
        for (Trainer t : trainers) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.err.println("Thread interrupted");
            }
        }
        return trainers[0].getNumEpochs();
    }
    
    public static double testPercs(Perceptron[] percs, String trainLoc, int[] labels) {
        int numCorrect = 0;
        double[] image = new double[784];
        for (int fileIndex = 10001; fileIndex <= 11000; fileIndex++) {
            //Generate the filename "[trainLoc]/image[fileIndex].bin"
            //fileIndex is 6 digits long, padded with 0s on the left
            String filename = String.format("%1$s%3$simage%2$06d.bin", trainLoc, fileIndex, File.separator);
            MyFileReader.readImageInto(filename, image);
            int bestGuess = 0;
            double bestSigmoid = -100;
            for (int percIndex = 0; percIndex <= 9; percIndex++) {
                double recResult = percs[percIndex].recognize(image);
                if (recResult > bestSigmoid) {
                    bestGuess = percIndex;
                    bestSigmoid = recResult;
                }
            }
            if (bestGuess == labels[fileIndex - 1])
                numCorrect++;
        }
        double percentCorrect = (double)numCorrect / 1000 * 100;
        return percentCorrect;
    }
}
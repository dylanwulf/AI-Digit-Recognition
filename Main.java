//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

public class Main {
    public static void main(String[] args) {
        String trainLoc = "../train_images";
        Perceptron[] percs = new Perceptron[10];
        for (int i = 0; i <= 9; i++)
            percs[i] = new Perceptron(i, 0.05);
        int[] labels = MyFileReader.readLabels(trainLoc + "/labels.bin");
        
        long start, end;
        start = System.currentTimeMillis();
        trainPercs(percs, trainLoc, 7000, labels);
        end = System.currentTimeMillis();
        System.out.println("Single thread training time: " + (end - start) + " ms");
        System.out.println("Percent correct: " + testPercs(percs, trainLoc, labels));
        for (int i = 0; i <= 9; i++)
            percs[i] = new Perceptron(i, 0.05);
        start = System.currentTimeMillis();
        multiTrainPercs(percs, trainLoc, 7000, labels, 8);
        end = System.currentTimeMillis();
        System.out.println("Multi thread training time: " + (end - start) + " ms");
        double percentCorrect = testPercs(percs, trainLoc, labels);
        System.out.println("Percent correct: " + percentCorrect);
    }
    
    public static void trainPercs(Perceptron[] percs, String trainLoc, int setSize, int[] labels) {
        double[] image = new double[784];
        for (int fileIndex = 1; fileIndex <= setSize; fileIndex++) {
            String filename = String.format("%1$s/image%2$06d.bin", trainLoc, fileIndex);
            MyFileReader.readImageInto(filename, image);
            for (Perceptron p : percs) {
                p.train(image, labels[fileIndex - 1]);
            }
            //System.out.println("Trained on file #" + fileIndex);
        }
    }
    
    public static void multiTrainPercs(Perceptron[] percs, String trainLoc, int setSize, int[] labels, int numThreads) {
        Trainer[] trainers = new Trainer[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int start = setSize / numThreads * i + 1;
            int end = (i == numThreads - 1)? setSize : setSize / numThreads * (i+1);
            trainers[i] = new Trainer(percs, start, end, trainLoc, labels);
            trainers[i].start();
        }
        for (Trainer t : trainers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted");
            }
        }
    }
    
    public static double testPercs(Perceptron[] percs, String trainLoc, int[] labels) {
        int numCorrect = 0;
        double[] image = new double[784];
        for (int fileIndex = 10001; fileIndex <= 11000; fileIndex++) {
            String filename = String.format("%1$s/image%2$06d.bin", trainLoc, fileIndex);
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
            //System.out.println("#" + fileIndex + ". Guess: " + bestGuess + " Actual: " + labels[fileIndex - 1]);
            if (bestGuess == labels[fileIndex - 1])
                numCorrect++;
        }
        double percentCorrect = (double)numCorrect / 1000 * 100;
        return percentCorrect;
    }
}
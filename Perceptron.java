//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

import java.io.*;

public class Perceptron {
    
    private double[] weights;
    private int digit;
    private double learnRate;
    
    public Perceptron(int digit) {
        this(digit, 0.2); //default to learn rate of 0.2
    }
    
    public Perceptron(int digit, double learnRate) {
        this(digit, 785, learnRate); //784 pixel weights plus one bias weight
    }
    
    public Perceptron(int digit, int numWeights, double learnRate) {
        this.digit = digit;
        weights = new double[numWeights];
        this.learnRate = learnRate;
    }
    
    //Create perceptron from weights in file 'fname'
    public Perceptron(String fname, double learnRate) {
        this.learnRate = learnRate;
        try {
            FileInputStream fis = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fis);
            digit = dis.readInt();
            int weightsLength = dis.readInt();
            weights = new double[weightsLength]; 
            for (int i=0; i < weights.length; i++) {
                weights[i] = dis.readDouble();
            }
            fis.close();
            dis.close();
        } catch(IOException e) {
            System.err.println("Error reading file " + fname);
        }
    }
    
    //Save the weights array to a file
    public void saveWeights(String fname) {
        try {
            FileOutputStream fos = new FileOutputStream(fname);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(digit);
            dos.writeInt(weights.length);
            for (int i = 0; i < weights.length; i++) {
                dos.writeDouble(weights[i]);
            }
            dos.flush();
            dos.close();
            fos.close();
        } catch(IOException e) {
            System.err.println("Error writing to file " + fname);
        }
    }
    
    //Attempts to recognize whether the image is of the digit represented
    //by this perceptron. Returns sigmoid function result
    public double recognize(double[] image) {
        double sum = 0.0;
        sum += weights[0] * 1; //include bias weight with dummy input (1)
        for (int i = 0; i < image.length; i++) {
            sum += image[i] * weights[i + 1];
        }
        return (double)1 / (1 + Math.exp(-1 * sum));
    }
    
    //Trains this perceptron with the provided image and
    //corresponding label. If this perceptron gives the wrong
    //result, updates the weights. Returns the maximum % change made to a single weight.
    public synchronized double train(double[] image, int label) {
        double maxPercentChange = 0.0;
        double g = recognize(image);
        double y = (digit == label)? 1 : 0;
        double err = y - g;
        double gPrime = g * (1 - g);
        double updateCoef = learnRate * err * gPrime;
        weights[0] += updateCoef * 1; //dummy input = 1
        for (int i = 0; i < image.length; i++) {
            double before = weights[i + 1];
            weights[i + 1] += updateCoef * image[i];
            double after = weights[i + 1];
            double avg = (before + after) / 2;
            double percentDiff = Math.abs(before - after) / avg * 100;
            if (percentDiff > maxPercentChange)
                maxPercentChange = percentDiff;
        }
        return maxPercentChange;
    }
}
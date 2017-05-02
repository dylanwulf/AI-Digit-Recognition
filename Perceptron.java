//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

//This file describes a single perceptron, which represents one digit in 0, 1, .. 9

import java.io.*;

public class Perceptron {
    
    private double[] weights; //Array of 785 weights (784 pixel weights + 1 bias weight)
    private int digit; //which digit this perceptron represents
    private double learnRate; //the learning rate, a.k.a. alpha
    
    //Constructor just specifying the digit
    public Perceptron(int digit) {
        this(digit, 0.2); //default to learn rate of 0.2
    }
    
    //Constructor specifying the digit and the learning rate
    public Perceptron(int digit, double learnRate) {
        this(digit, 785, learnRate); //784 pixel weights plus one bias weight
    }
    
    //Constructor specifying the digit, the number of weights to store, and the learning rate
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
            digit = dis.readInt(); //first int is which digit
            int weightsLength = dis.readInt(); //second int is length of weights array
            weights = new double[weightsLength]; //rest of the file is contents of array
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
            dos.writeInt(digit); //first int is which digit
            dos.writeInt(weights.length); //second int is length of weights array
            for (int i = 0; i < weights.length; i++) { //rest of file is contents of array
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
        return (double)1 / (1 + Math.exp(-1 * sum)); //Sigmoid function
    }
    
    //Trains this perceptron with the provided image and
    //corresponding label. If this perceptron gives the wrong
    //result, updates the weights. Returns the total change in weights made.
    //synchronized keyword makes sure no two threads can access this method
    //for this instance of this class at the same time
    public synchronized double train(double[] image, int label) {
        double totalWeightChange = 0.0;
        double g = recognize(image);
        double y = (digit == label)? 1 : 0;
        double err = y - g;
        double gPrime = g * (1 - g);
        double updateCoef = learnRate * err * gPrime;
        weights[0] += updateCoef * 1; //dummy input = 1
        totalWeightChange += Math.abs(updateCoef * 1);
        
        //Go through weights and update them according to error and pixel value
        for (int i = 0; i < image.length; i++) {
            weights[i + 1] += updateCoef * image[i];
            totalWeightChange += Math.abs(updateCoef * image[i]);
        }
        return totalWeightChange;
    }
}
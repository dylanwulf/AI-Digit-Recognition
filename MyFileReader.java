//Dylan Wulf
//CSC380: Artificial Intelligence
//Project 3: Handwritten Digit Recognition

//This file contains code for reading data from files, such as images and labels
//Note: Most of the code in this file was adapted from the provided
//LabelList and SillyImage classes

import java.io.*;
public class MyFileReader {
    
    public static double[] readImage(String fname) {
        double[] image = null;
        try {
            FileInputStream fis = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fis);
            int height = dis.readInt();
            int width = dis.readInt();
            image = new double[width*height]; 
            for (int i=0; i < image.length; i++) {
                image[i] = (double)dis.readInt() / 255; //normalize value to between 0 and 1
            }
            fis.close();
            dis.close();
        } catch(IOException e) {
            System.err.println("Error reading file " + fname);
        }
        return image;
    }
    
    //Read image into a predefined array
    //avoids creating a new array for every image in order to improve efficiency
    public static void readImageInto(String fname, double[] image) {
        try {
            FileInputStream fis = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fis);
            int height = dis.readInt();
            int width = dis.readInt();
            //image = new double[width*height]; 
            for (int i=0; i < image.length; i++) {
                image[i] = (double)dis.readInt() / 255; //normalize value to between 0 and 1
            }
            fis.close();
            dis.close();
        } catch(IOException e) {
            System.err.println("Error reading file " + fname);
        }
    }
    
    public static int[] readLabels(String fname) {
        int[] labels = null;
        try {
            FileInputStream fis = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fis);
            int height = dis.readInt();
            int width = dis.readInt();
            int nlabels = width*height;
            if (width != 1) {
                System.err.println("Doesn't look like a list " + fname);
            }
            labels = new int[nlabels];
            for (int i=0; i<width*height; i++) {
                labels[i] = dis.readInt();
            }
            fis.close();
            dis.close();
        } catch(IOException e) {
            System.err.println("Error reading file " + fname);
        }
        return labels;
    }
}
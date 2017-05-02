Dylan Wulf
CSC380: Artificial Intelligence
Project 3: Handwritten Digit Recognition

Files:
NeuralNet.java: This file contains the main method for the neural net program.
Perceptron.java: This file describes a single perceptron, which represents one digit in 0, 1, .. 9
MyFileReader.java: This file contains code for reading data from files, such as images and labels
Trainer.java: This class allows the neural net training to be done using multiple threads.
NearestNeighbor.java: This class contains the main method for the nearest neighbor program

Building:
To build, run this command in the project directory:
javac *.java

Running:
To run the Neural Net program in training mode, run the following command:
java NeuralNet train [training images location] [training set size] [learning rate] [number of epochs] [number of threads]
example: java NeuralNet train ../train_images 5000 0.2 3 1

Note: when the NeuralNet program finishes training, it will save the perceptron weights in
files in the current directory with names DWweightsX.bin, where X represents the digit.
These files will be overwritten if the training is run again.

To run the Neural Net program in testing mode, run the following command:
java NeuralNet test [training images location]
example: java NeuralNet test ../train_images

To run the Nearest Neighbor program, run the following command:
java NearestNeighbor [training images location] [knowledge set size]
example: java NearestNeighbor ../train_images 5000
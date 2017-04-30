public class Trainer extends Thread {
    private static Perceptron[] percs;
    private static String trainLoc = "";
    private static int[] labels;
    private int start; //which file index to start at (inclusive)
    private int end; //which file index to end at (inclusive)
    
    public Trainer(Perceptron[] percs, int startIndex, int endIndex, String trainLoc, int[] labels) {
        Trainer.percs = percs;
        start = startIndex;
        end = endIndex;
        Trainer.trainLoc = trainLoc;
        Trainer.labels = labels;
    }
    
    public void run() {
        double[] image = new double[784];
        for (int fileIndex = start; fileIndex <= end; fileIndex++) {
            String filename = String.format("%1$s/image%2$06d.bin", trainLoc, fileIndex);
            MyFileReader.readImageInto(filename, image);
            for (Perceptron p : percs) {
                synchronized (p) {
                    p.train(image, labels[fileIndex - 1]);
                }
            }
        }
    }
}
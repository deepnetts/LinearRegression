package com.deepnetts.examples.linear;

import deepnetts.data.DataSets;
import javax.visrec.ml.eval.EvaluationMetrics;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.Tensor;
import java.io.IOException;
import java.util.Arrays;
import javax.visrec.ml.data.DataSet;

/**
 * Minimal example for linear regression using Deep Netts.
 * Linear regression fits a straight line through the given data, so the sum of differences between the given points and the line is minimal.
 * Uses a single layer with one output and linear activation function, and Mean Squared Error for Loss function.
 * Linear regression to roughly estimate a global trend in data.
 *
 * For the best performance and accuracy the recommended way to run this example is to use Deep Netts Pro, with Free Development License.
 * https://www.deepnetts.com/download
 *
 * Step-by-step guide for setting up Deep Netts is available at
 * https://www.deepnetts.com/getting-started
 * 
 */
public class LinearRegression {

    public static void main(String[] args) throws IOException {

            int inputsNum = 1;
            int outputsNum = 1;
            
            String csvFilename = "linear.csv"; // this file contains the training data

            // load and create data set from csv file
            DataSet dataSet = DataSets.readCsv(csvFilename , inputsNum, outputsNum, true);

            // create neural network using network specific builder
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(inputsNum)
                    .addOutputLayer(outputsNum, ActivationType.LINEAR)
                    .lossFunction(LossType.MEAN_SQUARED_ERROR)
                    .build();

            BackpropagationTrainer trainer = neuralNet.getTrainer();
            trainer.setStopError(0.002f)
                   .setStopEpochs(10000)
                   .setLearningRate(0.01f);

            // train network using loaded data set
            neuralNet.train(dataSet);
            
            // test model with same data
            EvaluationMetrics em = neuralNet.test(dataSet);
            System.out.println(em);

            // print out learned model
            float slope = neuralNet.getLayers().get(1).getWeights().get(0);
            float intercept = neuralNet.getLayers().get(1).getBiases().get(0);
            
            System.out.println("Original function: y = 0.5 * x + 0.2");
            System.out.println("Estimated/learned function: y = "+slope+" * x + "+intercept);

            // perform prediction for some input value
            float[] predictedOutput = neuralNet.predict(0.2f);
            System.out.println("Predicted output for 0.2 :" + Arrays.toString(predictedOutput));

            plotTrainingData();

            // plot predictions for some random data
            plotPredictions(neuralNet);            
    }


    public static void plotPredictions(FeedForwardNetwork nnet) {
        double[][] data = new double[100][2];

        for(int i=0; i<data.length; i++) {
            data[i][0] =  0.5-Math.random();
            nnet.setInput(new Tensor(1, 1, new float[] { (float)data[i][0] }));
            data[i][1] = nnet.getOutput()[0];
        }

        Plot.scatter(data, "Neural Network Predictions");
    }

    public static void plotTrainingData() throws IOException {
        double[][] dataPoints = CsvFile.read("linear.csv", 30);
        Plot.scatter(dataPoints, "Training data");
    }

}

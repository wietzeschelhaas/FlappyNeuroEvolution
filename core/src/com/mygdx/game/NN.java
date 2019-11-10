package com.mygdx.game;
import java.io.Serializable;
import java.lang.Math;

public class NN implements Serializable {

    int numberInput;
    int numberHidden;
    int numberOutput;

    Matrix weightsInputHidden;
    Matrix weightsHiddenOutput;

    Matrix biasHidden;
    Matrix biasOutput;

    float learningRate = 0.1f;

    // This simple neural network that has only three layers.
    public NN(int numInput, int numHidden, int numOutput){
        this.numberInput = numInput;
        this.numberHidden = numHidden;
        this.numberOutput = numOutput;

        //We make a matrix where the rows are the number of hidden nodes and the columns the number of input nodes
        //so row i contains the weights going to hidden node i.
        weightsInputHidden = new Matrix(numHidden, numInput);

        // the same applies here
        weightsHiddenOutput = new Matrix(numOutput, numHidden);

        // Bias matrixes
        biasHidden = new Matrix(this.numberHidden,1);
        biasOutput = new Matrix(this.numberOutput,1);

        //randomize the weights,
        //TODO research on how to best randomize weight ie, what range and so on
        weightsInputHidden.randomize(-1,1);
        weightsHiddenOutput.randomize(-1,1);

        biasHidden.randomize(-1,1);
        biasOutput.randomize(-1,1);


    }

    float softmax(float x, Matrix o){
        float exp = (float) Math.exp(x);
        float sum = 0;
        for (int i = 0; i < o.rows; i++) {
            sum += o.matrix[i][0];
        }
        return exp/sum;
    }


    float crossEntr(Matrix guess, Matrix targets){
        float sum = 0;
        for (int i = 0; i < targets.rows; i++) {
            sum += targets.matrix[i][0] * (Math.log(guess.matrix[i][0]));
        }
        return -sum;
    }
    // sigmoid activation function
    float sigmoid(float x){
        return (float) (1 / (1+ Math.exp(-x)));
    }

    float dSigmoid(float x){
        //return sigmoid(x) * (1- sigmoid(x));

        //x has allready been sigmoided
        return x*(1-x);
    }

    // this is returning the output matrix
    public Matrix feedForward(Matrix input){
        //W_ij is the weight matrix from input to hidden
        // I is input
        // B is bias
        // H = ActivationFunc(W_ij * I + B)

        // multiply weights with input for every hidden node
        Matrix hidden = Matrix.mult(this.weightsInputHidden,input);
        //add the bias
        hidden.add(this.biasHidden);

        //Activation function
        for (int i = 0; i < hidden.rows; i++) {
            hidden.matrix[i][0] = sigmoid(hidden.matrix[i][0]);
        }

        //W_ij is the weight matrix from hidden to output
        // H is result from previous calc
        // B is bias
        //O = ActivationFunc(W_ij * H + B)
        Matrix output = Matrix.mult(this.weightsHiddenOutput,hidden);
        output.add(this.biasOutput);

        for (int i = 0; i < output.rows; i++) {
            output.matrix[i][0] = sigmoid(output.matrix[i][0]);
        }

        return output;
    }

    // Backprop
    public void train(Matrix inputs, Matrix answers){

        Matrix hidden = Matrix.mult(this.weightsInputHidden,inputs);
        //add the bias
        hidden.add(this.biasHidden);

        //Activation function
        for (int i = 0; i < hidden.rows; i++) {
            hidden.matrix[i][0] = sigmoid(hidden.matrix[i][0]);
        }

        //W_ij is the weight matrix from hidden to output
        // H is result from previous calc
        // B is bias
        //O = ActivationFunc(W_ij * H + B)
        Matrix output = Matrix.mult(this.weightsHiddenOutput,hidden);
        output.add(this.biasOutput);

        for (int i = 0; i < output.rows; i++) {
            output.matrix[i][0] = sigmoid(output.matrix[i][0]);
        }


        //Calculate error
        //error = target - output, this is the error between the output and the target
        //Usually its output - target but this way we can add the delta weights instead of subtract?

        //sigmoid error
        Matrix outputError = Matrix.sub(answers,output);



        //Delta W_ij for hiddenOutput = lr * outputError * (O*(1-O)) X H^T
        //O above is the output, (O*(1-O)) is the derivative of sigmoid
        //H^t is the output you got from hidden
        //X above is matrix multiplication
        Matrix gradientOutput = new Matrix(output.rows,output.columns);
        //calculate the gradient of sigmoid
        for (int i = 0; i < gradientOutput.rows; i++) {
            gradientOutput.matrix[i][0] = dSigmoid(output.matrix[i][0]);
        }

        //multiply with errors
        gradientOutput.hadamardMult(outputError);
        //multiply with learning rate
        gradientOutput.scl(learningRate);


        //mutliply with hidden transposed
        Matrix hiddenT = Matrix.transpose(hidden);

        Matrix weightsHiddenOutputDeltas = Matrix.mult(gradientOutput,hiddenT);





        //now we calculate the error for the hidden nodes.
        //by transposing the weights from hidden to output and multiplying that with the error from output
        //we get every weight multiplied by its corresponding error (the output node error the weight is connected to)
        Matrix weightsHiddenOutputTransposed = Matrix.transpose(weightsHiddenOutput);
        Matrix hiddenError = Matrix.mult(weightsHiddenOutputTransposed,outputError);



        Matrix hiddenGradient = new Matrix(hidden.rows,hidden.columns);
        //calculate the gradient of sigmoid
        for (int i = 0; i < hiddenGradient.rows; i++) {
            hiddenGradient.matrix[i][0] = dSigmoid(hidden.matrix[i][0]);
        }

        //multiply with errors
        hiddenGradient.hadamardMult(hiddenError);
        //multiply with learning rate
        hiddenGradient.scl(learningRate);


        //mutliply with inputs transposed
        Matrix inputT = Matrix.transpose(inputs);

        Matrix weightsInputHiddenDeltas = Matrix.mult(hiddenGradient,inputT);

        //Adjust the weights!!
        weightsHiddenOutput.add(weightsHiddenOutputDeltas);
        weightsInputHidden.add(weightsInputHiddenDeltas);

        biasOutput.add(gradientOutput);
        biasHidden.add(hiddenGradient);


    }


}

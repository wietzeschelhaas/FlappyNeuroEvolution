package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bird {
    Sprite b;
    Vector2 gravity;
    Vector2 vel;

    float maxVel = -10f;
    float flyspeed = 5f;

    NN brain;
    Matrix input;

    Pipe pipes;

    float maxHeight = 480;

    Rectangle bRec;

    float fitness = 0;

    // this is a probability derived from fittnes later
    float prob;



    public Bird(Pipe pipe){
        b = new Sprite(new Texture("bluebird-midflap.png"));
        b.setOrigin(b.getWidth()/2,b.getHeight()/2);
        b.setPosition(288/2- b.getWidth()/2,MathUtils.random()*512);
        b.rotate(20);
        gravity = new Vector2(0,-0.2f);
        vel = new Vector2();
        brain = new NN(4,4,1);
        this.pipes = pipe;
        //input to the NN, which is gonna be:
        //Bird Y pos
        //Pipes Xpos
        //upper pipe ypos
        //lower pipe ypos
        input = new Matrix(4,1);



    }

    void guessMove(){
        input.matrix[0][0] = b.getY();
        input.matrix[1][0] = pipes.pipeUp.getX();
        input.matrix[2][0] = pipes.pipeUp.getY();
        input.matrix[3][0] = pipes.pipeDown.getY();

        Matrix output = brain.feedForward(input);
        float g = output.getVal(0,0);

        if(g>0.5f){
            jump();
        }
    }

    public void update(float delta){
        if(vel.y > maxVel)
            vel.add(gravity);
        if(b.getRotation() > -90)
            b.rotate(vel.y*0.8f);

        b.setPosition(b.getX()+vel.x,b.getY()+vel.y);
        guessMove();


        bRec = b.getBoundingRectangle();
        bRec.setWidth(bRec.width*0.8f);
        bRec.setHeight(bRec.height*0.8f);
        bRec.setPosition(b.getX(),b.getY()+5);

        fitness += 1;
    }
    void jump(){
        if(b.getY() < maxHeight) {
            vel.y = 0;
            vel.y += flyspeed;
            b.setRotation(0);
        }
    }
    //TODO find way to fix this ugly code
    void mutate(float mutationRate){
        for (int i = 0; i < brain.weightsInputHidden.rows; i++) {
            for (int j = 0; j < brain.weightsInputHidden.columns; j++) {
                if(MathUtils.random()<mutationRate) { brain.weightsInputHidden.matrix[i][j] = (2* MathUtils.random()-1);
                }
            }
        }
        for (int i = 0; i < brain.weightsHiddenOutput.rows; i++) {
            for (int j = 0; j < brain.weightsHiddenOutput.columns; j++) {
                if(MathUtils.random()<mutationRate) {
                    brain.weightsHiddenOutput.matrix[i][j] = (2* MathUtils.random()-1);
                }
            }
        }
        for (int i = 0; i < brain.biasHidden.rows; i++) {
            for (int j = 0; j < brain.biasHidden.columns; j++) {
                if(MathUtils.random()<mutationRate) {
                    brain.biasHidden.matrix[i][j] = (2* MathUtils.random()-1);
                }
            }
        }
        for (int i = 0; i < brain.biasOutput.rows; i++) {
            for (int j = 0; j < brain.biasOutput.columns; j++) {
                if(MathUtils.random()<mutationRate) {
                    brain.biasOutput.matrix[i][j] = (2* MathUtils.random()-1);
                }
            }
        }
    }


    public void draw(SpriteBatch batch){
        b.draw(batch);
    }
}

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.*;

public class main extends ApplicationAdapter {
	float screenWidth = 288;
	float screenHeight = 512;

	SpriteBatch batch;
	FillViewport viewport;
	OrthographicCamera cam;

	Sprite background;
	Sprite base;

	ArrayList<Bird> population;
	ArrayList<Bird> dieded;
	Pipe pipe;

	FPSLogger fps;

	int popSize = 100;

	boolean canPlay = true;

	String filename = "nn.ser";

	@Override
	public void create () {



		batch = new SpriteBatch();

		cam = new OrthographicCamera(screenWidth,screenHeight);
		cam.position.set(screenWidth/2,screenHeight/2,0);
		viewport = new FillViewport(screenWidth,screenHeight,cam);



		background = new Sprite(new Texture("background-day.png"));
		base = new Sprite((new Texture("base.png")));

		pipe = new Pipe();
		population = new ArrayList<Bird>();
		dieded = new ArrayList<Bird>();

		//init the first generation
		for (int i = 0; i < popSize; i++) {
			population.add(new Bird(pipe));
		}


		fps = new FPSLogger();
;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		background.draw(batch);
		pipe.draw(batch);
		base.draw(batch);
		for(Bird b: population){

			b.draw(batch);

		}
		batch.end();

		if(canPlay)
			update(Gdx.graphics.getDeltaTime());
	}

	public void update(float delta){
		for(Bird b: population){

			b.update(delta);

		}
		pipe.update();



		Rectangle pUp = pipe.pipeUp.getBoundingRectangle();
		pUp.setPosition(pipe.pipeUp.getX(),pipe.pipeUp.getY()+10);
		Rectangle pDown = pipe.pipeDown.getBoundingRectangle();
		pUp.setWidth(pUp.width*0.8f);
		pUp.setHeight(pUp.height*0.8f);
		pDown.setWidth(pDown.width*0.8f);


		//TODO dont remove the bird, instead save its score
		for (int i = population.size()-1; i >=0; i--) {
			if((population.get(i).bRec.overlaps(pUp)||population.get(i).bRec.overlaps(pDown))||population.get(i).b.getY()<base.getHeight()){
				dieded.add(population.get(i));
				population.remove(population.get(i));

			}
		}


		if(dieded.size() == popSize){
			nextGen();
		}

		if(Gdx.input.justTouched()){
			randomRestart();
		}

		fps.log();
	}

	void nextGen(){
		//randomRestart();
		System.out.println(dieded.get(popSize-1).fitness);
		//Collections.shuffle(dieded);
		float totalFit = 0;
		for(Bird b : dieded){
			totalFit += b.fitness;
		}
		System.out.println("total fittnes = " + totalFit);
		//normalise the fittnes
		for(Bird b : dieded){
			b.prob = b.fitness/totalFit;
		}



		for (int i = 0; i < popSize; i++) {
			Bird b = pickRandom(dieded);
			Bird b2 = pickRandom(dieded);
			Bird child = new Bird(pipe);
			//crossover
			child.brain.weightsInputHidden = b.brain.weightsInputHidden;
			child.brain.weightsHiddenOutput = b2.brain.weightsHiddenOutput;

			child.mutate(0.1f);
			population.add(child);
		}

		dieded.clear();
		pipe.reset();

	}
	Bird pickRandom(ArrayList<Bird> p){
		int index = 0;
		float r = MathUtils.random();
		while(r>0){
			r -= p.get(index).prob;
			index++;

			//System.out.println("fittens = " + p.get(index).fitness);
			//System.out.println("r = " + r);
		}
		index--;
		Bird bird = p.get(index);
		return bird;
	}


	void randomRestart(){
	    population.clear();
		for (int i = 0; i < popSize; i++) {
			population.add(new Bird(pipe));
		}
		dieded.clear();
		pipe.reset();
	}

	//TODO continue here later
	void saveNN(NN brain){
		try
		{
			//Saving of object in a file
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			// Method for serialization of object
			out.writeObject(brain);

			out.close();
			file.close();


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void dispose () {
		batch.dispose();
	}


	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width,height);
	}
}

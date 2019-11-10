package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import org.graalvm.compiler.loop.MathUtil;

public class Pipe {
    Sprite pipeDown;
    Sprite pipeUp;

    float vel = 1;


    // distance between the two pipes,
    float pipeDist = 130;
    public Pipe(){
        pipeDown = new Sprite(new Texture("pipe-green.png"));
        pipeUp = new Sprite(new Texture("pipe-greenUp.png"));
        reset();
    }


      void reset(){
        //between 200
        float yDown = MathUtils.random()* -150;
        float yUp = yDown + pipeDist + pipeUp.getHeight();

        pipeDown.setPosition(290,yDown);
        pipeUp.setPosition(290,yUp);


    }

    public void update(){
        if(pipeDown.getX() < -60){
            this.reset();
        }

        pipeDown.setPosition(pipeDown.getX()-vel,pipeDown.getY());
        pipeUp.setPosition(pipeUp.getX()-vel,pipeUp.getY());

    }

    public void draw(SpriteBatch batch){
        pipeDown.draw(batch);
        pipeUp.draw(batch);
    }
}

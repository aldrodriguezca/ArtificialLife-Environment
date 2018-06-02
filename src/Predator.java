import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.List;

public class Predator extends LivingBeing {
    boolean ate;
    public Predator(PApplet sketch, int x, int y, float maxSpeed, float minimumEnergy, float energyLevel, float metabolismRate){
        super(sketch, x, y, "predator", maxSpeed, minimumEnergy, energyLevel, metabolismRate);
        this.img = sketch.loadImage("f2.png");
        img.resize(100,100);
        deformedImg = sketch.createImage(img.width, img.height, PConstants.ARGB);
        if(Math.random() > 0.2)
            transform( img.width/2, img.height/2);
        else
            deformedImg = img;

//        sketch.texture(img);
        this.maxForce = 0.8f;
        ate = false;
    }


    public void act( List<PVector> preysDistance,List<PVector> others){
        boundaries();
        PVector closestPrey = getClosestTarget( preysDistance );
        PVector huntForce = hunt( closestPrey );
        //PVector huntForce = hunt( new PVector( sketch.mouseX, sketch.mouseY));

           if( energyLevel < 2.5f*minimumEnergy){
               huntForce.mult(2);
             applyForce(huntForce);
           }else{


               PVector restingForce = rest();
               applyForce(restingForce);
          }

        metabolize();
        update();
        display();
    }


    PVector rest(){
        PVector desired = new PVector( sketch.random(position.x,position.x+200), sketch.random(position.y,position.y+200));
        desired.setMag( maxSpeed / 5);

        PVector steer = PVector.sub(desired,velocity);
        return steer;
    }

    PVector hunt(PVector target) {
        PVector desired = PVector.sub(target, this.position);
        //float d =  position.dist(target);
        float d = desired.mag();
        //System.out.println("D Magnitud: " + d);
        //System.out.println("Distance: " + this.position.dist( target ));
        // if( type.equals("predator")){

        if(d < 35) {
                if(d < 27.5)
                if (!ate) {
                    energyLevel += 35;
                    ate = true;
                }
        }

        if(d > 40)
            ate = false;
        //}

        desired.setMag(maxSpeed);
        PVector steer = PVector.sub(desired,velocity);
        steer.limit(maxForce);  // Limit to maximum steering force
        //  applyForce(steer);
        return steer;
    }
}

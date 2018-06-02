import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import java.util.HashMap;
import java.util.List;

public class Prey extends LivingBeing{

    SkinInfo skinInfo;
    HashMap<Integer, HashMap<Integer, boolean []>> code;

    static final int SPEED = 1;
    static  final int META = 2;
    static final int VISION = 3;


    public Prey(int id,PApplet sketch, int x, int y, float maxSpeed, float minimumEnergy, float energyLevel, float metabolismRate, float maxVisionRange){
        super(sketch, x, y, "prey", maxSpeed, minimumEnergy, energyLevel, metabolismRate);
        this.img = sketch.loadImage("gold.png");


        img.resize(100,100);
        //skin.resize(60,60);
        deformedImg = sketch.createImage(img.width, img.height, PConstants.ARGB);
        //if(Math.random() > 0.2)
            transform( img.width/2, img.height/2);
        //else
          //  deformedImg = img;

        this.maxForce = 0.2f;

        this.maxVisionRange = maxVisionRange;
        this.id = id;

        code = new HashMap<>();

        HashMap<Integer, boolean[]> aux = new HashMap<>();

        String binaryRepresentation;
        boolean []binary;

        //Speed
        Integer intBinary = Float.floatToIntBits( maxSpeed );
        binaryRepresentation = Integer.toBinaryString(  intBinary );
        binary = new boolean[ binaryRepresentation.length()];

        for(int i = 0; i < binaryRepresentation.length(); i++)
            binary[i] = binaryRepresentation.charAt(i) == '1'? true: false;

        aux.put( intBinary, binary);

        code.put(SPEED, aux);

        //Metabolism
        intBinary = Float.floatToIntBits( metabolismRate );
        binaryRepresentation = Integer.toBinaryString(  intBinary );
        binary = new boolean[ binaryRepresentation.length()];

        for(int i = 0; i < binaryRepresentation.length(); i++)
            binary[i] = binaryRepresentation.charAt(i) == '1'? true: false;

        aux = new HashMap<>();
        aux.put( intBinary, binary);
        code.put(META, aux);

        //Vision
        intBinary = Float.floatToIntBits( maxVisionRange );
        binaryRepresentation = Integer.toBinaryString(  intBinary );
        binary = new boolean[ binaryRepresentation.length()];

        for(int i = 0; i < binaryRepresentation.length(); i++)
            binary[i] = binaryRepresentation.charAt(i) == '1'? true: false;

        aux = new HashMap<>();
        aux.put(intBinary, binary);
        code.put(VISION, aux);

    }


    PVector runFromPredator( PVector closestPredator){
        PVector desired = PVector.sub(closestPredator, position);
        float d = desired.mag();

        //too close, escape!
        if(d < 250){
            //System.out.println("Distancia de predador: " + d);
            desired.setMag(-maxSpeed);

            if(d < 22){
                //Killed fish
                alive = false;
            }
        }

        PVector steer = PVector.sub(desired,velocity);
        steer.limit(maxForce);
        //applyForce(steer);
        return steer;
    }

    void act( List<PVector> predators,  List<PVector> foodSources, List<Prey> others, List<PVector> othersPos){

        PVector closestPredator = null;
        if(predators != null)
            closestPredator = getClosestTarget( predators );


        PVector closestFood = getClosestTarget( foodSources );

        PVector separationForce = separate( othersPos );
        PVector alignForce = alignF( others );
        PVector escapeForce = null;
        if(predators != null)
            escapeForce = runFromPredator( closestPredator ) ;
        //escapeForce = runFromPredator( new PVector(sketch.mouseX, sketch.mouseY));
        PVector eatForce = arrive( closestFood );
        PVector cohesionForce = cohesion(others);

        boundaries();

        if( energyLevel < 1.9f * minimumEnergy){
            eatForce.mult(4.1f);
            //applyForce(eatForce);
            applyForce(eatForce);
        }
            //eatForce.mult(0);
            alignForce.mult(1.2f);
            separationForce.mult(1.8f);
            cohesionForce.mult(2.2f);
            applyForce(separationForce);
            applyForce(alignForce);
            applyForce(cohesionForce);
            //eatForce.mult(1);
            //applyForce(eatForce);
            //eatForce.mult(1);
            //applyForce(eatForce);

        if( predators != null){
            if(getPosition().dist(closestPredator) < 200) {
                escapeForce.mult(9.5f);
                separationForce.mult(3.8f);
                applyForce(escapeForce);
                applyForce(separationForce);
            }
       }

        metabolize();

        update();
        display();
    }


    public PVector alignF(List<Prey> others){
        float neighborDistance = 40;
        PVector sum = new PVector(0,0);
        int count = 0;
        float d;


        for(LivingBeing other: others){
            d = PVector.dist(position, other.position);

            if(d > 0 && d < neighborDistance){
                sum.add(other.velocity);
                count++;
            }
        }

        if(count > 0){
            sum.div((float)count);
            sum.normalize();
            sum.mult(maxSpeed);

            PVector steer = PVector.sub(sum, velocity);
            steer.limit(maxForce);
            //applyForce(steer);
            return steer;
        }

        return new PVector(0,0);
    }

    PVector cohesion (List<Prey> others) {
        float neighbordist = 250; //50 --> default
        PVector sum = new PVector(0,0);
        int count = 0;
        for (Prey other : others) {
            float d = PVector.dist(position,other.position);

            if(skinInfo != null){
                if ((d > 0) && (d < neighbordist*2) && Math.abs( skinInfo.getColorFactor() - other.skinInfo.getColorFactor()) < 0.15) {
                    sum.add(other.position); // Add position
                    count++;
                }else if ((d > 0) && (d < neighbordist)) {
                    sum.add(other.position); // Add position
                    count++;
                }
            }
            else
            if ((d > 0) && (d < neighbordist)) {
                sum.add(other.position); // Add position
                count++;
            }

        }
        if (count > 0) {
            sum.div(count);
            return seek(sum);  // Steer towards the position
        } else {
            return new PVector(0,0);
        }
    }


    public void loadSkin(){
        this.skin = sketch.loadImage("skin"+this.id+".png");
        skin.resize(60,60);
    }


    public PVector separateF(List<Prey> others){
        float desiredSeparation = 45;
        PVector steer = new PVector(0,0);
        int count = 0;
        float d;

        for(Prey other : others){
            d = PVector.dist(position, other.position);
            if(d > 0 && d < desiredSeparation){
                PVector diff =  PVector.sub(this.position, other.position);
                diff.normalize();
                diff.div(d);
                steer.add(diff);
                count++;
            }

            if( Math.abs( this.skinInfo.getColorFactor() - other.skinInfo.getColorFactor() ) < 0.2 && d > 0  ){

            }
        }

        if(count > 0)
            steer.div((float)count);

        if(steer.mag() > 0){
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(velocity);
            steer.limit(maxForce);
        }

        return steer;
    }

}

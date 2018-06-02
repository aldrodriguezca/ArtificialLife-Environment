import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import java.util.List;

//Flock behavior based on The nature of code book
//available at: http://natureofcode.com/book/chapter-6-autonomous-agents/

public class LivingBeing {

    protected PApplet sketch;
    int id;
    PVector position;
    PVector velocity;
    PVector acceleration;
    float maxForce;
    float maxSpeed;
    float r;
    String type; //this indicates it is a prey to predators
    PImage img;
    float metabolismRate;
    float minimumEnergy;
    float energyLevel;
    boolean alive;
    float maxWealth;
    PImage deformedImg;
    float maxVisionRange;
    PImage skin;
    boolean showSkin;

    //Skin generator
    TuringMorphGenerator skinGenerator;


    ShapeInfo shapeInfo;

    public LivingBeing(PApplet sketch, int x, int y, String type, float maxSpeed, float minimumEnergy, float energyLevel, float livingSuffering){
        this.sketch = sketch;
        acceleration = new PVector(0,0);
        velocity = new PVector(0,0);
        position = new PVector(x,y);
        r = 3;
        maxForce = 0.04f;

        this.maxSpeed = maxSpeed;
        this.minimumEnergy = minimumEnergy;
        this.energyLevel = energyLevel;
        this.metabolismRate = livingSuffering;

        this.type = type;
        alive = true;
        maxWealth = 100;

        showSkin = false;
    }

    public void update(){
        velocity.add(acceleration);
        velocity.limit(maxSpeed);
        position.add(velocity);
        acceleration.mult(0);
    }

    void applyForce(PVector force) {
        acceleration.add(force);
    }

    PVector seek(PVector target) {
        PVector desired = PVector.sub(target, position);
        float d = desired.mag();
        desired.setMag(maxSpeed);
        // Steering = Desired minus velocity
        PVector steer = PVector.sub(desired,velocity);
        steer.limit(maxForce);  // Limit to maximum steering force
      //  applyForce(steer);
        return steer;
    }

    PVector arrive(PVector target){
        PVector desired = PVector.sub(target, position);
        float d = desired.mag();


        if(d < 100){
            float m  = sketch.map(d,0,100,0, maxSpeed);
            desired.setMag(m);

            if(d < 100){
                //System.out.println("Eating plants: "+  d);
                if( energyLevel < maxWealth)
                    energyLevel += 0.2f;
            }
        }else{
            desired.setMag(maxSpeed);
        }

        if( d > maxVisionRange ){
            desired = PVector.sub( new PVector( sketch.random(position.x, position.x+10), sketch.random( position.y, position.y +10)), position);
            float m  = sketch.map(d,0,100,0, maxSpeed);
            desired.setMag(m);
        }

        PVector steer = PVector.sub(desired,velocity);
        steer.limit(maxForce);
        //applyForce(steer);
        return steer;
    }

    void boundaries(){
        //Toroidal space
        if (position.x < 0) position.x = sketch.width;
        if (position.y < 0) position.y = sketch.height;
        if (position.x > sketch.width) position.x = 0;
        if (position.y > sketch.height) position.y = 0;
    }

    void display() {

        float theta = velocity.heading2D() + sketch.PI/2;
        //if(type.equals("prey"))
          //  sketch.fill(0, 0, 200);

      //  if(type.equals("predator")) {
        //    sketch.fill(200, 0, 0);

        sketch.image(deformedImg, position.x-35, position.y-35, 70,70);

        if(type.equals("prey"))
            if(showSkin)
                sketch.image(skin, position.x-20, position.y-20,40,40);



        sketch.stroke(0);
        sketch.strokeWeight(1);
        sketch.pushMatrix();
        sketch.translate(position.x,position.y);

        sketch.rotate(theta);

        sketch.popMatrix();
    }


    public PVector separate(List<PVector> others){
        float desiredSeparation = 45;
        PVector steer = new PVector(0,0);
        int count = 0;
        float d;

        for(PVector other : others){
            d = PVector.dist(position, other);
            if(d > 0 && d < desiredSeparation){
                PVector diff =  PVector.sub(position, other);
                diff.normalize();
                diff.div(d);
                steer.add(diff);
                count++;
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

    public PVector getClosestTarget(List<PVector> targets){

        PVector closest = targets.get(0);

        for(PVector v: targets){
            if(position.dist(v) < position.dist(closest))
                closest = v;
        }
        return closest;
    }


    public void metabolize(){
        energyLevel -= metabolismRate;
        if(energyLevel < minimumEnergy)
            alive = false;
    }


    public PVector getPosition(){
        return position;
    }

    //Perform fisheye-like distortion
    public void transform(int x, int y){
        int lsize = img.width, lsize2 = lsize*lsize;
        int u, v, r2;
        float f;
        float mag = 1.7f;
        float k = -0.00016f;

        int a,b,c;
        if(shapeInfo == null) {
            a = (int) (Math.random() * 5) + 1;
            b = (int) (Math.random() * 14) + 1;
            c = (int) (Math.random() * 2);

            this.shapeInfo = new ShapeInfo(a,b,c);
        }
        else{
            a = shapeInfo.getA();
            b = shapeInfo.getB();
            c = shapeInfo.getC();
        }

        for (int vd = -lsize; vd < lsize; vd++) {
            for (int ud = -lsize; ud < lsize; ud++) {
                r2 =(a*ud*ud + b*vd*vd + c*ud*vd);
                if (r2 <= lsize2) {
                    f = mag + k * r2;
                    u = sketch.floor(ud/f) + x;
                    v = sketch.floor(vd/f) + y;

                    if (u >= 0 && u < img.width && v >=0 && v < img.height)
                        deformedImg.set(ud + x, vd + y, img.get(u, v));
                }
            }

        }
    }

    @Override
    public boolean equals(Object o){
        LivingBeing other = (LivingBeing) o;
        return other.id == this.id && this.type.equals(other.type);
    }

}

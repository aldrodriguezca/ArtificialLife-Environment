import processing.core.PVector;

public class Food {
    private PVector position;
    private float maxLevel;
    private float growthRate;


    private float currentLevel;
    private float radius;

    private boolean active;

    public Food(int x, int y, boolean active){
        position = new PVector(x,y);
        radius = 100;
        //currentLevel = maxLevel;
        this.active = active;
    }

    public Food(int x, int y, float maxLevel, float growthRate){
        this.position = new PVector(x ,y);
    }




    public PVector getPosition(){
        return position;
    }

    public void consume(float consumedFood){
        if(currentLevel - consumedFood >0)
            currentLevel += consumedFood;
        else
            currentLevel = 0;
    }

    public void grow(){
        currentLevel += growthRate;
    }

    public float getRadius(){ return radius;}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}

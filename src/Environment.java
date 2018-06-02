import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.*;

public class Environment extends PApplet {

    List<LSystem_Plant> lPlants;
    List<Food> foodSources;
    int nFoodSources = 4;
    int nPreys = 60;
    int nPredators = 3;
    List<Predator> predators;
    List<Prey> preys;
    List<PVector> preysPos;
    List<PVector> foodPos;
    List<PVector> predatorsPos;
    List<Prey> repopulation;
    boolean repopulationNeeded = false;

    //predators parameters
    float minPredatorsSpeed;
    float maxPredatorsSpeed;

    //preys parameters
    float minPreySpeed;
    float maxPreySpeed;

    float minimunEnergy;
    float metaRate;
    float initialEnergyLevel;

    //Period for seasons change (in seconds)
    int periodSeconds = 40;

    //Period for showing skins
    int periodSkins = 20;

    //Timer --> stations
    Timer timer = new Timer();

    //Timer for changing display mode
    Timer timerDisplayMode = new Timer();

    Timer timerEvolution = new Timer();

    //ArrayList<int[]> skins = new ArrayList<>(nPreys);
    ArrayList<SkinInfo> skins = new ArrayList<>(nPreys);

    PImage skinImage;
    int iteration = 0;

    //proper config
    boolean madeSkins = false;
    boolean writeSkins = true;


//    boolean madeSkins = true;
//    boolean writeSkins = false;

    Evolution evolution;

    public void settings(){
        size(1850, 950);
        //secondaryGraphics = createGraphics(100, 100);
        lPlants = new ArrayList<>(nFoodSources);
        foodSources = new ArrayList<>(nFoodSources);
        predators = new LinkedList<>();
        preys = new LinkedList<>();

        preysPos = new ArrayList<>(nPreys);
        foodPos = new ArrayList<>(nFoodSources);
        predatorsPos = new ArrayList<>(nPredators);

       setFoodSources();
       setLSystems();
       //setPredators();
       setPreys();
       //loadSkins();
//       evolution = new Evolution(preys);
       //Set timer
        setTimer();

        //Configure secondary screen
        skinImage = createImage(100,100, RGB);
        //setSkins();
        //  thread("setSkins");

        //thread("generateTuringSkins");
        thread("makeSkins");

        //Create turing morph skins
    }


    public void setup(){
        background( 0);
    }

    public void draw(){

        background(0,0,102);
        for(LSystem_Plant plant: lPlants)
            plant.branch(plant.production);

        if(!madeSkins){
            noFill();
            noStroke();
        }

        if(madeSkins) { //Wait until the skins (arrays) are done
            if(writeSkins){
                for (int s = 0; s < skins.size(); s++) {
                    //noFill();
                    skinImage.loadPixels();
                    image(skinImage, width - 100, 0);
                    for (int i = 1; i < 100 - 1; i++) {
                        for (int j = 1; j < 100 - 1; j++) {
                            int pos = i + j * 100;
                            //skinImage.pixels[pos] = skins.get(s)[pos];
                            skinImage.pixels[pos] = skins.get(s).skin[pos];
                        }
                    }
                    skinImage.updatePixels();

                    skinImage.save("skin" + s + ".png");
                }
                loadSkins();
                setPredators();
                setLateTimers();
            }
            writeSkins = false;
        }

        iteration++;

        //------ Food marks
        noFill();
        strokeWeight(1.5f);
        stroke(255,0,0);
        for(Food src: foodSources) {
            if(!src.isActive())
                fill(192,192,192);
            else
                noFill();
            ellipse(src.getPosition().x, src.getPosition().y, src.getRadius(), src.getRadius());
        }
        //-------

        //Preys behavior
        if( preys.size() >0 ){
            for(Prey prey : preys){

                if(predators.size() > 0)
                    prey.act( getPredatorsPos(), foodSourcesPosition(), preys, getPreysPosition());
                else
                    prey.act( null, foodSourcesPosition(), preys, getPreysPosition());
            }

            for(int i = 0; i < preys.size(); i++)
                if(!preys.get(i).alive) {
                    preys.remove(i);

                }

        }else{
            if( repopulation != null && repopulationNeeded){
                rePopulate(repopulation);
            }
        }


        if(preys.size() > 0 && predators.size() > 0){
            //Predators behavior
            for(Predator predator: predators){
                predator.act( getPreysPosition(), getPredatorsPos());
            }

            for(int i = 0; i < predators.size(); i++)
                if(!predators.get(i).alive)
                    predators.remove(i);
        }
    }


    //Set positions for food sourcers
    public void setFoodSources(){
        //foodSources.add(new Food((int)random(width-350)+250, (int)random(height-350)+250));
        foodSources.add(new Food(80, 130, false));
        foodSources.add(new Food(35, 100, false));
        foodSources.add(new Food(width-40, height-40, true));
        foodSources.add(new Food(width-100, height-100, true));
    }


    //Set rules for L-Systems
    public void setLSystems(){
        HashMap<String, String> rules = new HashMap<>();

        rules.put("F", "FF+[+F-F-F]-[-F+F+F]");
        LSystem_Plant p = new LSystem_Plant( this, foodSources.get(0).getPosition().x, foodSources.get(0).getPosition().y, rules, "F", 4);
        lPlants.add( p );

        rules = new HashMap<>();
        rules.put("X", "F-[[X]+X]+F[+FX]-X");
        rules.put("F", "FF");
        p = new LSystem_Plant( this, foodSources.get(1).getPosition().x, foodSources.get(1).getPosition().y, rules, "X", 4);
        lPlants.add( p );


        rules = new HashMap<>();
        rules.put("F", "FF+[+F-F-F]-[-F+F-F]");
        p = new LSystem_Plant( this, foodSources.get(2).getPosition().x, foodSources.get(2).getPosition().y, rules, "F", 4);
        lPlants.add( p );


        rules = new HashMap<>();
        rules.put("F", "F[+F]F[-F][F]");
        p = new LSystem_Plant( this, foodSources.get(3).getPosition().x, foodSources.get(3).getPosition().y, rules, "F", 4);
        lPlants.add( p );

        for(LSystem_Plant plant: lPlants)
            plant.generate();
    }

    public void setPredators(){

        //set parameters
        minimunEnergy = 60;
        minPredatorsSpeed = 7;
        maxPredatorsSpeed = 9;
        metaRate = 0.05f;
        initialEnergyLevel = 90;

        for(int i = 0; i < nPredators;i++)
            predators.add( new Predator( this, (int) random(width), (int) random(height), random(minPredatorsSpeed, maxPredatorsSpeed), minimunEnergy, initialEnergyLevel, random(metaRate-0.02f, metaRate)));
    }

    public void setPreys(){
        //set parameters

        minimunEnergy = 50;
        metaRate = 0.03f;
        minPreySpeed = 4;
        maxPreySpeed = 9;
        initialEnergyLevel = 70;
        //float visionRange = random(500, 2300);

        //float visionRange = random(1000, 2000);
        for(int i  = 0; i < nPreys; i++)
            preys.add(  new Prey( i,this, (int) random(width), (int) random(height), random(minPreySpeed, maxPreySpeed), minimunEnergy,  initialEnergyLevel, random(metaRate - 0.02f,metaRate), random(450, 2400)) );
    }


    public List<PVector> getPreysPosition(){

        preysPos.clear();
        for(Prey prey: preys)
            preysPos.add( new PVector( prey.getPosition().x, prey.getPosition().y) );

        return preysPos;
    }

    public List<PVector> foodSourcesPosition(){
        foodPos.clear();

        for(Food food : foodSources) {
            if(food.isActive())
                foodPos.add(new PVector(food.getPosition().x, food.getPosition().y));
        }

        return foodPos;
    }

    public List<PVector> getPredatorsPos(){
        predatorsPos.clear();

        for(Predator predator : predators)
            predatorsPos.add( new PVector( predator.getPosition().x, predator.getPosition().y));

        return predatorsPos;
    }

    // Configures timer for execute a task periodically, in this case: seasons & display mode change
    public void setTimer(){
        //Schedule seasons change
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                changeSeason();
            }
        },0, periodSeconds*1000);
    }


    public void evolvePopulation(){
        evolution = new Evolution(preys, this, skinImage);
        evolution.performEvolution();
        List<Prey> daPreys = ((List) ((LinkedList) evolution.preys).clone());;

        //Kill all the population to be replaced
        for(Prey p: preys)
            p.alive = false;


        repopulation = daPreys;
        repopulationNeeded = true;

    }

    public  void rePopulate(List<Prey> newPreys){

        for(Prey p :  newPreys)
            preys.add(p);

        repopulationNeeded = false;
    }

    //Changes the state of each food source
    public void changeSeason(){
        for(Food src: foodSources)
            src.setActive( !src.isActive() );
    }

    public void changeDisplayMode(){
        if( madeSkins && !writeSkins)
            for(Prey prey: preys)
                prey.showSkin = !prey.showSkin;
    }

    //Skins configuration

    //Make each prey to look for the appropriate file(img.png)
    public void loadSkins(){

        for(Prey prey: preys)
            prey.loadSkin(); //loads the generated image


        Prey p;
        for(int i = 0; i < preys.size(); i++){
            p = preys.get( i );
            p.skinInfo = skins.get(i);
            preys.set(i, p);
        }
    }

    public void makeSkins(){
        for(int i = 0; i < preys.size(); i++){
            try{
                IndependentGenerator generator = new IndependentGenerator(true, 0,0,0);
                Thread t = new Thread( generator );
                t.start();

                t.join();
                //skins.add( generator.getSkin());
                skins.add( new SkinInfo( generator.getSkin(), generator.generator.feed, generator.generator.kill, generator.generator.factor) );
            }
            catch (InterruptedException e){
                System.out.println("Catched thread exception");
            }
        }

        madeSkins = true;
    }

    public void setLateTimers(){

        timerDisplayMode.scheduleAtFixedRate( new TimerTask(){
            @Override
            public void run() {
                changeDisplayMode();
            }
        },1000, periodSkins*1000);


        timerEvolution.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                evolvePopulation();
            }
        }, 60*1000, 120*1000);

    }
    public static void main(String[] args) {
        PApplet.main("Environment");
    }
}
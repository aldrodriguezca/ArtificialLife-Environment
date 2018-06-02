import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Evolution {

    protected  List<Prey> preys;
    List<Prey> parents;
    List<SkinInfo> skins;

    PApplet sketch;
    PImage skinImage;

    public Evolution(List<Prey> preys, PApplet sketch, PImage skinImage){
        this.preys =  ((List) ((LinkedList) preys).clone());

        parents = new LinkedList<>();
        skins = new ArrayList<>(preys.size());

        //Both required to draw - create images
        this.sketch = sketch;
        this.skinImage = skinImage;
    }

    public void doSelection(int n){
        //Select best parents
        int [] fastests = selectFastest((int)( 0.40 * n));

        //Select most vision
        int [] mostVision = selectByVision( (int)( 0.40 * n)) ;

        //Select most Speed
        int [] lowestMeta = selectByMeta( (int)( 0.20 * n));


        for(int i = 0; i < fastests.length ; i++)
            parents.add( preys.get( fastests[i]));

        for(int i = 0; i < mostVision.length ; i++)
            parents.add( preys.get( mostVision[i]));

        for(int i = 0; i < lowestMeta.length ; i++)
            parents.add( preys.get(lowestMeta[i]));
    }

    public void doCross(Prey f1, Prey f2, int i , int j){

        float maxSpeed = Math.max(f1.maxSpeed, f2.maxSpeed);
        float minSpeed = Math.min(f1.maxSpeed, f2.maxSpeed);
        float speed = (float)Math.random() * ( (maxSpeed - minSpeed) +1 ) + minSpeed;

        float maxMeta = Math.max(f1.metabolismRate, f2.metabolismRate);
        float minMeta = Math.min(f1.metabolismRate, f2.metabolismRate);
        float metaRate = (float) Math.random() * ( (maxMeta - minMeta) ) + minMeta;

        float maxVisionRange = Math.max(f1.maxVisionRange, f2.maxVisionRange);
        float minVisionRange = Math.min(f2.maxVisionRange, f2.maxVisionRange);

        float visionRange = (float)Math.random() * ((maxVisionRange - minVisionRange)+1) + minVisionRange;

        float energyLevel = (float)Math.random() * ((Math.max( f1.energyLevel, f2.energyLevel)- Math.min(f1.energyLevel, f2.energyLevel)) +1) + Math.min(f1.energyLevel, f2.energyLevel);

        Prey son1 = new Prey(i, f1.sketch, (int)f1.position.x, (int)f1.position.y, speed, f1.minimumEnergy, energyLevel, metaRate, visionRange);

        speed = (float)Math.random() * ( (maxSpeed - minSpeed) +1 ) + minSpeed;
        metaRate = (float) Math.random() * ( (maxMeta - minMeta) ) + minMeta;
        visionRange = (float)Math.random() * ((maxVisionRange - minVisionRange)+1)+ visionRange;
        energyLevel = (float)Math.random() * ((Math.max( f1.energyLevel, f2.energyLevel)- Math.min(f1.energyLevel, f2.energyLevel)) +1)+Math.min(f1.energyLevel, f2.energyLevel);

        Prey son2 = new Prey(j, f2.sketch, (int)f2.position.x, (int)f2.position.y, speed, f2.minimumEnergy, energyLevel, metaRate, visionRange);


        //son2.deformedImg = Math.random() < 0.5 ? f1.deformedImg: f2.deformedImg;
        //son1.deformedImg = Math.random() < 0.5 ? f1.deformedImg: f2.deformedImg;


        int maxA = Math.max(f1.shapeInfo.getA(), f2.shapeInfo.getA());
        int minA = Math.min(f1.shapeInfo.getA(), f2.shapeInfo.getA());

        int maxB = Math.max(f1.shapeInfo.getB(), f2.shapeInfo.getB());
        int minB = Math.min(f1.shapeInfo.getB(), f2.shapeInfo.getB());

        int maxC = Math.max(f1.shapeInfo.getC(), f2.shapeInfo.getC());
        int minC = Math.min(f1.shapeInfo.getC(), f2.shapeInfo.getC());


        int a = (int)(Math.random() * (maxA - minA) + minA);
        int b = (int)(Math.random() * (maxB - minB) + minB);
        int c = (int)(Math.random() * (maxC - minC) + minC);

        a = Math.random() < 0.5? f1.shapeInfo.getA(): f2.shapeInfo.getA();
        b = Math.random() < 0.5? f1.shapeInfo.getB(): f2.shapeInfo.getB();
        c = Math.random() < 0.5? f1.shapeInfo.getC(): f2.shapeInfo.getC();


        ShapeInfo shape = new ShapeInfo(a,b,c);
        son1.shapeInfo = shape;

        son1.img = sketch.loadImage("gold.png");
        son1.img.resize(100,100);

        son1.transform(son1.img.width/2, son1.img.height/2);


        a = (int)(Math.random() * (maxA - minA) + minA);
        b = (int)(Math.random() * (maxB - minB) + minB);
        c = (int)(Math.random() * (maxC - minC) + minC);

        a = Math.random() < 0.5? f1.shapeInfo.getA(): f2.shapeInfo.getA();
        b = Math.random() < 0.5? f1.shapeInfo.getB(): f2.shapeInfo.getB();
        c = Math.random() < 0.5? f1.shapeInfo.getC(): f2.shapeInfo.getC();

        shape = new ShapeInfo(a,b,c);
        son2.shapeInfo = shape;

        son2.img = sketch.loadImage("gold.png");
        son2.img.resize(100,100);

        son2.transform(son2.img.width/2, son2.img.height/2);


        //son2.skin = Math.random() < 0.5? f1.skin: f2.skin;
        //son1.skin = Math.random() < 0.5? f1.skin: f2.skin;


        //Cross skin parameters
        SkinInfo info;
        //color
        float maxColor = Math.max(f1.skinInfo.getColorFactor(), f2.skinInfo.getColorFactor());
        float minColor = Math.min(f1.skinInfo.getColorFactor(), f2.skinInfo.getColorFactor());
        float color = (float) Math.random() * ( (maxColor - minColor) ) + minColor;
        //F
        double fMax = Math.max(f1.skinInfo.getFeed(), f2.skinInfo.getFeed());
        double fMin = Math.min(f1.skinInfo.getFeed(), f2.skinInfo.getFeed());

        double f = Math.random() * (fMax - fMin) + fMin;

        //K
        double kMax = Math.max(f1.skinInfo.getKill(), f2.skinInfo.getKill());
        double kMin = Math.min(f1.skinInfo.getKill(), f2.skinInfo.getKill());

        double k = Math.random() * (kMax - kMin) + kMin;

        info = new SkinInfo(new int[100*100], f, k,color );
        son1.skinInfo = info;

        color = (float) Math.random() * ( (maxColor - minColor) ) + minColor;
        f = Math.random() * (fMax - fMin) + fMin;
        k = Math.random() * (kMax - kMin) + kMin;

        info = new SkinInfo(new int[100*100], f, k, color );
        son2.skinInfo = info;
        //

        preys.set( i, son1);
        preys.set( j, son2);
    }

    //binary cross and mutation

  /*  public void doBinaryCross(Prey f1, Prey f2, int i, int j){

        Prey son1 = new Prey(i, f1.sketch, (int)f1.position.x, (int)f1.position.y, f1.maxSpeed, f1.minimumEnergy, f1.energyLevel, f1.metabolismRate, f1.maxVisionRange);
        Prey son2 = new Prey(j, f2.sketch, (int)f2.position.x, (int)f2.position.y, f2.maxSpeed, f2.minimumEnergy, f2.energyLevel, f2.metabolismRate, f2.maxVisionRange);

        Integer index = Float.floatToIntBits( f1.maxSpeed);
        Integer index2 = Float.floatToIntBits(f2.maxSpeed);
        boolean[][] speed = binaryCross(f1.code.get(f1.SPEED).get( Float.floatToIntBits( f1.maxSpeed)), f2.code.get(f2.SPEED).get(index2) );
        son1.code.get(f1.SPEED).replace(index,  speed[0]);

        son2.code.get(f2.SPEED).replace(index, speed[1]);

        index = Float.floatToIntBits( f1.metabolismRate);
        index2 = Float.floatToIntBits( f1.metabolismRate);

        boolean[][] metabolism = binaryCross(f1.code.get(f1.META).get(index), f2.code.get(f2.META).get(index2) );
        son1.code.get(f1.META).replace(index, );
        son2.code.replace(f2.META, metabolism[1]);

        boolean[][] vision = binaryCross(f1.code.get(f1.VISION), f2.code.get(f2.VISION) );
        son1.code.replace(f1.VISION, vision[0]);
        son2.code.replace(f2.VISION, vision[1]);


        son1.shapeInfo = f1.shapeInfo;
        son2.shapeInfo = f2.shapeInfo;


        //back to float

    }
*/


    public boolean[][] binaryCross(boolean[] x1, boolean[] x2) {
        int pos = (int)((x1.length-1)*Math.random()) + 1;
        boolean[][] children = new boolean[][]{x1.clone(), x2.clone()};
        for( int i=pos; i< x1.length; i++){
            children[0][i] = x2[i];
            children[1][i] = x1[i];
        }
        return children;
    }

    public Prey doBinaryMutation(Prey prey){
        Prey mutated = prey;
        boolean[] aux;

        aux = prey.code.get(prey.SPEED).get( Float.floatToIntBits( prey.maxSpeed));
        mutated.code.get(prey.SPEED).replace( Float.floatToIntBits( prey.maxSpeed), aux);

        aux = prey.code.get(prey.META).get( Float.floatToIntBits( prey.metabolismRate));
        mutated.code.get(prey.META).replace( Float.floatToIntBits( prey.metabolismRate), aux);

        aux = prey.code.get(prey.VISION).get( Float.floatToIntBits( prey.maxVisionRange));
        mutated.code.get(prey.VISION).replace( Float.floatToIntBits( prey.maxVisionRange), aux);

        //Now do the cast back to float

        Integer index =  (Integer)mutated.code.get(mutated.SPEED).keySet().toArray()[0];
        mutated.maxSpeed = Float.intBitsToFloat(  index );


        index =  (Integer)mutated.code.get(mutated.META).keySet().toArray()[0];
        mutated.metabolismRate = Float.intBitsToFloat(  index );

        index =  (Integer)mutated.code.get(mutated.VISION).keySet().toArray()[0];
        mutated.maxVisionRange = Float.intBitsToFloat(  index );

        return mutated;
    }

    public boolean [] mutateBinary( boolean[] x){
        double p = 1.0 / x.length;
        boolean[] son = x.clone();
        for(int i = 0; i < son.length; i++)
            if(Math.random() < p)
                son[i] = !son[i];
        return son;
    }

    public void performEvolution(){
        System.out.println("TRYING TO EVOLVE!!!!!! ");

        doSelection( preys.size() );

        //System.out.println("Cantidad de padres seleccionados: " + parents.size());
        //System.out.println("Cantidad de preys presentes: " + preys.size());
        for(int i = 0; i < parents.size()-1; i+=2){
           // System.out.println("Usando " + i + " padre");
            doCross( parents.get(i), parents.get(i+1), i,i+1);
        }

        //Mutate new population
        for(int i = 0; i < preys.size()-1;i++){
            preys.set(i, doMutation(preys.get(i)));
        }

        generateSkins();
    }

    public Prey doMutation(Prey prey){
        Prey mutated = prey;
        mutated.maxSpeed += Math.random() < 0.65? 0.5: -0.5;
        mutated.metabolismRate -= Math.random() < 0.65? 0.01f: -0.01f;
        mutated.maxVisionRange += Math.random() < 0.65? 100: -50;

        mutated.metabolismRate = mutated.metabolismRate < 0.01f? 0.01f: mutated.metabolismRate;
        return mutated;
    }

    public int[] selectFastest(int n){
        Prey prey, prey2;
        int m1, m2;

        int [] indexes = new int[n];

        for(int i = 0 ; i < n; i++ ){
            m1 = getOne(n);
            prey = preys.get(m1);

            for(int k = 0; k < 4; k ++){
                m2 = getOne(n);
                prey2 = preys.get(m2);
                if(prey2.maxSpeed >  prey.maxSpeed){m1 = m2;}
            }
            indexes[i] = m1;
        }
        return indexes;
    }

    public  int[] selectByMeta(int n){
        Prey prey, prey2;
        int m1, m2;

        int [] indexes = new int[n];

        for(int i = 0 ; i < n; i++ ){
            m1 = getOne(n);
            prey = preys.get(m1);

            for(int k = 0; k < 4; k ++){
                m2 = getOne(n);
                prey2 = preys.get(m2);
                if(prey2.metabolismRate < prey.metabolismRate){m1 = m2;}
            }
            indexes[i] = m1;
        }
        return indexes;
    }

    public int[] selectByVision(int n){
        Prey prey, prey2;
        int m1, m2;

        int [] indexes = new int[n];

        for(int i = 0 ; i < n; i++ ){
            m1 = getOne(n);
            prey = preys.get(m1);

            for(int k = 0; k < 4; k ++){
                m2 = getOne(n);
                prey2 = preys.get(m2);
                if(prey2.maxVisionRange > prey.maxVisionRange){m1 = m2;}
            }
            indexes[i] = m1;
        }
        return indexes;
    }


    public void generateSkins(){
        Prey thePrey;
        SkinInfo info;
        for(int i = 0; i < preys.size(); i++){
            try{
                thePrey = preys.get(i);
                info = thePrey.skinInfo;
                IndependentGenerator generator = new IndependentGenerator(false, info.getFeed(), info.getKill(), info.getColorFactor());
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

        createSkinFiles();
        loadSkins();
    }

    public void createSkinFiles(){
        for (int s = 0; s < skins.size(); s++) {
            //noFill();
            skinImage.loadPixels();
            sketch.image(skinImage, sketch.width - 100, 0);
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
    }


    //Load skin images for new population
    public void loadSkins(){
        for(Prey p : preys) {
            p.loadSkin();
            p.showSkin = true;
        }
    }

    public int getOne(int N){
        return (int)(Math.random()*preys.size());
    }
}

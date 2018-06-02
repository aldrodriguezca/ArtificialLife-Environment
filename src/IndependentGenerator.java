public class IndependentGenerator implements Runnable {

    TuringMorphGenerator generator;
    int[] skin;

    boolean predefined;
    double f, k;
    float cf;

    public IndependentGenerator(boolean predefined, double f, double k, float cf){
        generator = new TuringMorphGenerator();

        this.predefined = predefined;
        this.f = f;
        this.k = k;
        this.cf = cf;
    }

    @Override
    public void run() {
        generator.generateSkin(predefined, f, k, cf);
        this.skin = generator.getSkin();
    }

    public int[] getSkin(){
        return skin;
    }
}

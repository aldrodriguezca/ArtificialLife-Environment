public class SkinInfo {
    int[] skin;
    private double feed;
    private double kill;
    private float colorFactor;

    public SkinInfo(int[] skin, double f, double k, float c){
        this.skin = skin;
        this.feed = f;
        this.kill = k;
        this.colorFactor = c;
    }

    public int[] getSkin() {
        return skin;
    }

    public double getFeed() {
        return feed;
    }

    public double getKill() {
        return kill;
    }

    public float getColorFactor() {
        return colorFactor;
    }
}

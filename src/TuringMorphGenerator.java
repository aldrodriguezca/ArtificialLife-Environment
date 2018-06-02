import processing.core.PApplet;

import java.util.HashMap;

public class TuringMorphGenerator extends PApplet {

    static  final int TOTAL_ITERATIONS = 3800;
    //parameters
    double dA = 1.0;
    double dD = 0.5;
    double feed =  0.0300;
    double kill = 0.0550;
    double dt = 0.5;

    float factor;

    public TuringCell[][] turingGrid;
    public TuringCell[][] newGrid;
    int []skin;
    int size;

    HashMap<Integer, double[]> params;

    TuringMorphGenerator(){

        size = 100;
        this.skin = new int[size*size];

        //Configure possible parameters
        params = new HashMap<>();

        params.put(1, new double[]{0.055,0.062});
        params.put(2, new double[]{ 0.0580,  0.0650});
        params.put(3, new double[]{0.0860, 0.0590});
        params.put(4, new double[]{0.0220, 0.0590});
        params.put(5, new double[] {0.0420, 0.0610});
        params.put(6, new double[] {0.0500, 0.0610});
    }


    public void generateSkin(boolean predefined, double f, double k, float cf){
        System.out.println("GENERANDO PIEL");
        int skinWidth = 100;
        int skinHeight = 100;

        int sum;
        sum = Math.random() > 0.5? 1:-1;

        if(predefined){
            int index = (int)(Math.random()*6) +1 ;
            this.feed = params.get(index) != null? params.get(index)[0]: feed;
            this.kill = params.get(index) != null? params.get(index)[1]:kill;

            this.factor = (float)(Math.random()*0.3);
            this.factor *= sum;
        }
        else{
            this.feed = f;
            this.kill = k;
            this.factor = cf;
            this.factor *= sum;
        }

        turingGrid = new TuringCell[skinWidth][skinHeight];
        newGrid = new TuringCell[skinWidth][skinHeight];

        int nSeeds = 15;


        //Set initial configuration for grids
        //--------------
        for (int i = 0; i < skinWidth; i++)
            for (int j = 0; j < skinHeight; j++) {
                turingGrid[i][j] = new TuringCell(1, 0);
                newGrid[i][j] = new TuringCell(1, 0);
            }

        for (int n = 0; n < nSeeds; n++) {
            int startx = (int)(random(10, skinWidth-10));
            int starty = (int)(random(10, skinHeight-10));

            for (int i = startx; i < startx+10; i++)
                for (int j = starty; j < starty+10; j ++) {
                    float a = 1;
                    float d = 1;
                    newGrid[i][j] = new TuringCell(a, d);
                    turingGrid[i][j] = new TuringCell(a, d);
                }
        }
        //--------------
        skin = new int[skinWidth*skinHeight];

        for(int itera = 0; itera < TOTAL_ITERATIONS ; itera++) {
            //update();
            //--------------------------------------
            for (int i = 1; i < skinWidth - 1; i++) {
                for (int j = 1; j < skinHeight - 1; j++) {
                    newGrid[i][j] = getChemicals(turingGrid, i, j, dA, dD, feed, kill, dt);
                    //                System.out.println("Nivel A:" + newGrid[i][j].getAcQuantity());
                    //                System.out.println("Nivel A:" + newGrid[i][j].getDiffQuantity());
                }
            }
            //--------------------------------------
            //swap();
            //--------------------------------------
            TuringCell[][] temp = new TuringCell[width][height];

            for (int i = 0; i < skinWidth; i++)
                for (int j = 0; j < skinHeight; j++)
                    temp[i][j] = turingGrid[i][j];

            for (int i = 0; i < skinWidth; i++)
                for (int j = 0; j < skinHeight; j++)
                    turingGrid[i][j] = newGrid[i][j];

            for (int i = 0; i < skinWidth; i++)
                for (int j = 0; j < skinHeight; j++)
                    newGrid[i][j] = temp[i][j];
            //--------------------------------------

            //            loadPixels();
            for (int i = 1; i < skinWidth - 1; i++) {
                for (int j = 1; j < skinHeight - 1; j++) {
                    TuringCell spot = newGrid[i][j];
                    float a = (float) spot.getAcQuantity();
                    float b = (float) spot.getDiffQuantity();

                    int pos = i + j * skinWidth;

                    int c1 = color(a * 200, 255, 0, 350);
                    int c2 = color(255, b * 200, 28, 100);

                    skin[pos] = lerpColor(c1, c2, 0.5f+factor);
                }
            }
        }
    }

    public TuringCell getChemicals(TuringCell[][] turingGrid, int i, int j, double dA, double dD, double feed, double kill, double dt){
        TuringCell spot = turingGrid[i][j];
        float a = (float)spot.getAcQuantity();
        float d = (float)spot.getDiffQuantity();

        float newA = (float)(a + (dA*laPlaceAc(turingGrid, i, j) - a*d*d + feed*(1-a))*dt);
        float newD = (float)(d + (dD*laPlaceDif(turingGrid, i,j) + a*d*d - (kill+feed)*d)*dt);

        newA = constrain(newA, 0,1);
        newD = constrain(newD, 0, 1);
        return new TuringCell(newA, newD);
    }

    //Compute convolutions --> one method for each "chemical"
    public double laPlaceAc(TuringCell[][] turingGrid, int currentI, int currentJ){
        double result = turingGrid[currentI][currentJ].getAcQuantity()*-1;

        //Moore neighborhood
        //NEWS directions
        result += turingGrid[currentI][currentJ - 1].getAcQuantity() * 0.2;
        result += turingGrid[currentI-1][currentJ].getAcQuantity() * 0.2;
        result += turingGrid[currentI + 1][currentJ].getAcQuantity() * 0.2;
        result += turingGrid[currentI][currentJ+1].getAcQuantity() * 0.2;

        //Diagonals
        result += turingGrid[currentI+1][currentJ-1].getAcQuantity() * 0.05;
        result += turingGrid[currentI-1][currentJ-1].getAcQuantity() * 0.05;
        result += turingGrid[currentI-1][currentJ+1].getAcQuantity() * 0.05;
        result += turingGrid[currentI+1][currentJ+1].getAcQuantity() * 0.05;

        return result;
    }

    public double laPlaceDif(TuringCell[][] turingGrid, int currentI, int currentJ){
        double result = turingGrid[currentI][currentJ].getDiffQuantity()*-1;

        //Moore neighborhood
        //NEWS directions
        result += turingGrid[currentI-1][currentJ].getDiffQuantity() * 0.2;
        result += turingGrid[currentI + 1][currentJ].getDiffQuantity() * 0.2;
        result += turingGrid[currentI][currentJ - 1].getDiffQuantity() * 0.2;
        result += turingGrid[currentI][currentJ+1].getDiffQuantity() * 0.2;

        //Diagonals
        result += turingGrid[currentI+1][currentJ-1].getDiffQuantity() * 0.05;
        result += turingGrid[currentI-1][currentJ+1].getDiffQuantity() * 0.05;
        result += turingGrid[currentI+1][currentJ+1].getDiffQuantity() * 0.05;
        result += turingGrid[currentI-1][currentJ-1].getDiffQuantity() * 0.05;

        return result;
    }

    public int[] getSkin(){
        return skin;
    }

}
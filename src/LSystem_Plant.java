import processing.core.PApplet;

import java.util.HashMap;
import java.util.LinkedList;

public class LSystem_Plant {
    PApplet sketch;
    private float x, y; //initial position
    private HashMap<String, String> rules;
    private String axiom;
    float theta;
    float length;
    int iterations;
    String production;

    public LSystem_Plant(PApplet sketch, float x, float y) {
        this.sketch = sketch;
        this.x = x;
        this.y = y;
    }

    public LSystem_Plant(PApplet sketch, float x, float y, HashMap<String, String> rules, String axiom, int iterations) {
        this.sketch = sketch;
        this.x = x;
        this.y = y;
        this.rules = rules;
        this.axiom = axiom;
        this.iterations = iterations;

        this.length = 2.5f;
        this.theta = 75;
    }


    public void branch(String tree){
        sketch.pushMatrix();
        sketch.translate(x,y);


        sketch.stroke(0,205,0);
        sketch.strokeWeight(0.5f);

        for(int i = 0; i < tree.length(); i++){

            switch (tree.charAt(i)){
                case 'F':
                    sketch.line(0,0,0,-length);
                    sketch.translate(0,-length);

                    break;
                case '+':
                    sketch.rotate(-theta);

                    break;
                case '-':
                    sketch.rotate(theta);

                    break;

                case '[':
                    sketch.pushMatrix();

                    break;

                case ']':
                    sketch.popMatrix();

                    break;
            }
        }
        sketch.popMatrix();
        theta = 75;
    }


    public void generate ( ){
        StringBuilder builder = new StringBuilder();
        LinkedList<String> myTree = new LinkedList<>();
        LinkedList<String> aux;

        myTree.add(axiom);

        for(int i = 0; i < iterations; i++){
            for(int j = 0; j < myTree.size(); j++){
                for(String key: rules.keySet()){
                    if(myTree.get(j).equals(key))
                        myTree.set(j, rules.get(key));
                }
            }

            aux = new LinkedList<>();

            for(int k = 0; k < myTree.size(); k++){
                String[] splited = myTree.get(k).split("(?!^)");

                for(int q = 0; q < splited.length; q++)
                    aux.add(splited[q]);

            }
            myTree = aux;
        }

        for(String s: myTree)
            builder.append(s);

        production = builder.toString();
       // return builder.toString();
    }
}

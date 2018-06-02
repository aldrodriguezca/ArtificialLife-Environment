public class TuringCell {

    private double acQuantity;
    private double diffQuantity;

    public TuringCell(double acQuantity, double diffQuantity){
        this.acQuantity = acQuantity;
        this.diffQuantity = diffQuantity;
    }

    public double getAcQuantity() {
        return acQuantity;
    }

    public double getDiffQuantity() {
        return diffQuantity;
    }

    public void setAcQuantity(double acQuantity) {
        this.acQuantity = acQuantity;
    }

    public void setDiffQuantity(double diffQuantity) {
        this.diffQuantity = diffQuantity;
    }
}

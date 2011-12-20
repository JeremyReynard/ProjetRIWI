package extractor;

public class Couple {

    public int docNum;
    public int nbOcc;

    public Couple(int docNum, int nbOcc) {
        this.docNum = docNum;
        this.nbOcc = nbOcc;
    }

    @Override
    public String toString() {
        return "docNum=" + docNum + ", nbOcc=" + nbOcc + "\n";
    }
}

public class Node {
    private int id;

    private int punctaj;

    public Node(int id, int punctaj) {
        this.id = id;
        this.punctaj = punctaj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPunctaj() {
        return punctaj;
    }

    public void setPunctaj(int punctaj) {
        this.punctaj = punctaj;
    }
}

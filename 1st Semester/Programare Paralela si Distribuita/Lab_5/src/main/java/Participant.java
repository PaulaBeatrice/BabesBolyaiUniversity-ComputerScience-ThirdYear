public class Participant {
    private final String id;
    private int punctaj;
    private final String tara;

    public Participant(String id, int punctaj, String tara) {
        this.id = id;
        this.punctaj = punctaj;
        this.tara = tara;
    }

    public String getId() {
        return id;
    }

    public int getPunctaj() {
        return punctaj;
    }

    public void setPunctaj(int punctaj) {
        this.punctaj = punctaj;
    }

    public String getTara() {
        return tara;
    }

}

package AF_cls;

public class Tranzitie {
    private String stare_initiala;
    private String stare_finala;
    private String valoare;

    public Tranzitie(String stare_initiala, String stare_finala, String valoare) {
        this.stare_initiala = stare_initiala;
        this.stare_finala = stare_finala;
        this.valoare = valoare;
    }

    public String getStare_initiala() {
        return stare_initiala;
    }

    public String getStare_finala() {
        return stare_finala;
    }

    public String getValoare() {
        return valoare;
    }

    @Override
    public String toString() {
        return "{" + stare_initiala + " -- " + valoare + " -- > " + stare_finala + "}";
    }
}

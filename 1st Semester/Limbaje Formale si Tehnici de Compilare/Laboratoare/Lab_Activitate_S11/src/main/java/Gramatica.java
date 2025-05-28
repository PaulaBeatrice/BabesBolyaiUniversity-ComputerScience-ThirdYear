import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Gramatica {
    private Set<String> neterminale ;
    private Set<String> terminale;
    private String start;
    private Map<String, Set<String>> reguli;

    public Gramatica() {
        neterminale = new HashSet<>();
        terminale = new HashSet<>();
        start = "";
        reguli = new HashMap<>();
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Map<String, Set<String>> getReguli() {
        return reguli;
    }

    public void setReguli(Map<String, Set<String>> reguli) {
        this.reguli = reguli;
    }

    public Set<String> getNeterminale() {
        return neterminale;
    }

    public void setNeterminale(Set<String> neterminale) {
        this.neterminale = neterminale;
    }

    public Set<String> getTerminale() {
        return terminale;
    }

    public void setTerminale(Set<String> terminale) {
        this.terminale = terminale;
    }

    public void getReguliCeContinStartInMembruDrept(){
        for (Map.Entry<String, Set<String>> entry : this.getReguli().entrySet()) {
            String neterminal = entry.getKey();
            Set<String> productii = entry.getValue();
            for (String productie : productii) {
                if(productie.contains(start))
                    System.out.println(neterminal + " -> " + productie);
            }
        }
    }
}

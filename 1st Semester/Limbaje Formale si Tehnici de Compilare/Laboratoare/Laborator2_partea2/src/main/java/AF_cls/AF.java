package AF_cls;
import AF_cls.Tranzitie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class AF {
    private List<String> multimea_starilor;
    private List<String> alfabetul;
    private List<Tranzitie> multimea_tranzitiilor;
    private String stare_initiala;
    private List<String> multimea_starilor_finale;

    public AF() {
        this.multimea_starilor = new ArrayList<>();
        this.multimea_starilor_finale = new ArrayList<>();
        this.multimea_tranzitiilor = new ArrayList<>();
        this.alfabetul = new ArrayList<>();
    }

    public AF(List<String> multimea_starilor, List<String> alfabetul, List<Tranzitie> multimea_tranzitiilor, String stare_initiala, List<String> multimea_starilor_finale) {
        this.multimea_starilor = multimea_starilor;
        this.alfabetul = alfabetul;
        this.multimea_tranzitiilor = multimea_tranzitiilor;
        this.stare_initiala = stare_initiala;
        this.multimea_starilor_finale = multimea_starilor_finale;
    }

    public List<String> getMultimea_starilor() {
        return multimea_starilor;
    }

    public List<String> getAlfabetul() {
        return alfabetul;
    }

    public List<Tranzitie> getMultimea_tranzitiilor() {
        return multimea_tranzitiilor;
    }

    public String getStare_initiala() {
        return stare_initiala;
    }

    public List<String> getMultimea_starilor_finale() {
        return multimea_starilor_finale;
    }

    public void info_AF(int nr) {
        switch (nr) {
            case 1:
                System.out.println("MULTIMEA STARILOR: ");
                for (String stare : this.getMultimea_starilor())
                    System.out.println(stare);
                break;
            case 2:
                System.out.println("ALFABETUL:");
                for (String alfabet : this.getAlfabetul()) {
                    System.out.println(alfabet);
                }
                break;
            case 3:
                System.out.println("MULTIMEA TRANZITIILOR:");
                for (Tranzitie tranzitie : this.getMultimea_tranzitiilor()) {
                    System.out.println(tranzitie);
                }
                break;
            case 4:
                System.out.println("MULTIMEA STARILOR FINALE:");
                for (String stare_finala : this.getMultimea_starilor_finale()) {
                    System.out.println(stare_finala);
                }
                break;
            default:
                System.out.println("Optiune invalida!");
        }
    }

    /**
     * Verifica daca automatul finit este determinist
     * @return : true, daca e determinist, false altfel
     */
    public boolean verifica_AFD(){
        HashMap<String, String> perechi = new HashMap<>();
//        for(Tranzitie tranzitie: this.getMultimea_tranzitiilor())
//            if(tranzitie.getStare_initiala().equals("p0"))
//                System.out.println(tranzitie);
        for(Tranzitie tranzitie: this.getMultimea_tranzitiilor())
            if(perechi.containsKey(tranzitie.getStare_initiala())) {
                if (perechi.get(tranzitie.getStare_initiala()).equals(tranzitie.getValoare()))
                {
                    System.out.println(tranzitie);
                    return false;
                }

            }
            else
                perechi.put(tranzitie.getStare_initiala(), tranzitie.getValoare());
        return true;
    }

    /**
     * Verifica daca o secventa data este acceptata de un automat finit determinist
     * @param secventa : secventa data
     * @return : true, daca secventa e acceptata, false altfel
     */
    public boolean verifica_secventa(String secventa) {
        String stare_curenta = this.getStare_initiala();

        for (int i = 0; i < secventa.length(); i++) {
            String simbol = secventa.substring(i, i + 1);
            boolean gasit = false; // variabila pentru a verifica daca s-a gasit o tranzitie pentru simbolul si starea curenta

            for (Tranzitie tranzitie : this.getMultimea_tranzitiilor()) {
                if (tranzitie.getStare_initiala().equals(stare_curenta) && tranzitie.getValoare().equals(simbol)) {
                    stare_curenta = tranzitie.getStare_finala();
                    gasit = true;
                    break;
                }
            }

            // Daca nu s-a gasit o tranzitie pentru simbolul curent, secventa nu este acceptata
            if (!gasit) {
                return false;
            }
        }

        // Daca am parcurs intreaga secventa si am ajuns intr-o stare finala, secventa este acceptata
        return this.getMultimea_starilor_finale().contains(stare_curenta);
    }

    /** Determina cel mai lung prefix dintr-o secventa data acceptat de automatul finit
     * @param secventa
     * @return
     */
    public String celMaiLungPrefix(String secventa) {
        if(!this.verifica_AFD())
            System.out.println("Automatul finit este nedeterminist!!!");
        else{
            String stare_curenta = this.getStare_initiala();
            String prefix = "";
            String backupPrefix = "";

            for (int i = 0; i < secventa.length(); i++) {
                String simbol = secventa.substring(i, i + 1);
                boolean gasit = false;

                for (Tranzitie tranzitie : this.getMultimea_tranzitiilor()) {
                    if (tranzitie.getStare_initiala().equals(stare_curenta) && tranzitie.getValoare().equals(simbol)) {
                        stare_curenta = tranzitie.getStare_finala();
                        prefix += tranzitie.getValoare();
                        gasit = true;
                        break;
                    }
                }

                if (!gasit) {
//                    System.out.println("AICI");
                    break; // daca nu s-a gasit o tranzitie valida, opreste cautarea
                }

                if (this.getMultimea_starilor_finale().contains(stare_curenta)) {
                    backupPrefix = prefix;
                }
            }
            if(backupPrefix.length() > 0)
                return backupPrefix;
        }
        return "";
    }

    public void citireFisierAF(String fisier){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fisier))) {
            // multimea starilor
            String[] data = bufferedReader.readLine().split(" ");
            Collections.addAll(this.multimea_starilor, data);
            // starea initiala
            this.stare_initiala = bufferedReader.readLine();
            // nr. de stari finale
            int numStariFinale = Integer.parseInt(bufferedReader.readLine());
            for (int i = 0; i < numStariFinale; i++) { // starile finale
                String stareFinala = bufferedReader.readLine();
                this.multimea_starilor_finale.add(stareFinala);
            }
            // nr. de tranzitii
            int num = Integer.parseInt(bufferedReader.readLine());
            for (int i = 0; i < num; i++) { // tranzitiile
                String tranzitie = bufferedReader.readLine();
                data = tranzitie.split(" ");
                this.multimea_tranzitiilor.add(new Tranzitie(data[0], data[1], data[2]));
                if (!this.alfabetul.contains(data[2])) {
                    this.alfabetul.add(data[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisier!");
            e.printStackTrace();
        }
    }
}



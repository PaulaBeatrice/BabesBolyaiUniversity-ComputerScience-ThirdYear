import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static void writeListToFile(BufferedWriter writer, String listName, List<String> list) throws IOException {
        writer.write(listName + ":\n");
        for (String item : list) {
            writer.write(item + "\n");
        }
        writer.write("\n"); // Adăugăm o linie goală între liste
    }

    public static void main(String[] args) {
        String numeFisier = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Lab_1\\Laborator_1_partea_3\\src\\main\\java\\program.txt";

        // Definim urmatoarele liste in care vom plasa identificatorii, constantele, operatorii, separatorii si cuvintele cheie din fisierul citit
        List<String> IDs = new ArrayList<>();
        List<String> CONSTs = new ArrayList<>();
        List<String> OPERATORI = new ArrayList<>();
        List<String> SEPARATORI = new ArrayList<>();
        List<String> CUVINTE_CHEIE = new ArrayList<>();

        HashMap<Integer, String> TS_ID = new HashMap<>();
        TS TS_IDENDTIFIERS = new TS(TS_ID);

        HashMap<Integer, String> TS_CT = new HashMap<>();
        TS TS_CONSTANTS = new TS(TS_CT);

        int eroare = 0;


        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie;
            while ((linie = br.readLine()) != null && eroare == 0) { // parcurgem fiecare linie
                List<String> elemente = List.of(linie.split(" ")); // split la linie dupa " "
                for (String e: elemente) { // parcurgem elementele liniei
                    e = e.trim(); // elimina spatiile de la inceput si sfarsit
                    Atom atom = new Atom(e); // definim un obiect de tip Atom care va avea ca text elementul curent
                    if(!e.equals(""))
                    {
                        if(atom.isCUVANT_CHEIE()){ // verificam daca e cuvant-cheie
                            CUVINTE_CHEIE.add(e);
                        }else if(atom.isCONST()) { // verificam daca e constanta
                            CONSTs.add(e);
                            TS_CONSTANTS.add(e); // adaugam in tabela de constante
                        }
                        else if (atom.isID()) { // verificam daca e identificator
                            IDs.add(e);
                            TS_IDENDTIFIERS.add(e); // adaugam in tabela de identificatori
                        }
                        else if(atom.isOPERATOR()){ // verificam daca e operator
                            OPERATORI.add(e);
                        }
                        else if(atom.isSEPARATOR()){ // verificam daca e separator
                            SEPARATORI.add(e);
                        }
                        else {
                            System.out.println("Eroare! -" + e + "-");
                            eroare = 1;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea fișierului: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writeListToFile(writer, "IDs", IDs);
            writeListToFile(writer, "CONSTs", CONSTs);
            writeListToFile(writer, "OPERATORI", OPERATORI);
            writeListToFile(writer, "SEPARATORI", SEPARATORI);
            writeListToFile(writer, "CUVINTE_CHEIE", CUVINTE_CHEIE);
        } catch (IOException e) {
            System.err.println("Eroare la scrierea în fișierul de ieșire: " + e.getMessage());
        }

        System.out.println("   TS IDENTIFIERS");
        System.out.println("-------------------");
        TS_IDENDTIFIERS.afisare_tabela();
        System.out.println();
        System.out.println();

        System.out.println("   TS CONSTANTS");
        System.out.println("-------------------");
        TS_CONSTANTS.afisare_tabela();
        System.out.println();
        System.out.println();


        System.out.println("         FIP");
        System.out.println("---------------------------");
        System.out.println(" COD_ATOM | COD_TS");
        System.out.println("---------------------------");
        // 0 - CONST, 1 - ID
        HashMap<Integer, Map.Entry<String, String>> cod_FIP = new HashMap<>();
        int cod = 2;
        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie;
            int ok = 1;
            while ((linie = br.readLine()) != null && ok == 1) {
                List<String> elemente = List.of(linie.split(" "));
                for (String e: elemente) {
                    e = e.trim();
                    Atom atom = new Atom(e);
                    if(!e.equals(""))
                    {
                        if(atom.isCONST() && !atom.isCUVANT_CHEIE()) {
//                            System.out.println("      0 | " + TS_CONSTANTS.getCodBySimbol(e));
                            String line = String.format("%9s | %3d", "0", TS_CONSTANTS.getCodBySimbol(e));
                            System.out.println(line);
                        }
                        else if (atom.isID() && !atom.isCUVANT_CHEIE()) {
//                            System.out.println("      1 | " + TS_IDENDTIFIERS.getCodBySimbol(e));
                            String line = String.format("%9s | %3d", "1", TS_IDENDTIFIERS.getCodBySimbol(e));
                            System.out.println(line);
                        }
                        else if(atom.isSEPARATOR() || atom.isCUVANT_CHEIE() || atom.isOPERATOR()){
                            boolean existaInFIP = false;
                            for (Map.Entry<Integer, Map.Entry<String, String>> entry : cod_FIP.entrySet()) {
                                if (entry.getValue().getKey().equals(e)) {
                                    existaInFIP = true;
//                                    System.out.println(entry.getKey() + " | " + entry.getValue().getValue() + " --- " + entry.getValue().getKey());
                                    String line = String.format("%9s | %3s", entry.getKey(), entry.getValue().getValue());
                                    System.out.println(line);
                                    break;
                                }
                            }

                            if (!existaInFIP)  {
                                // Dacă e nu există în valorile lui FIP, incrementați cod și adăugați o pereche <cod, <e, "-"> în FIP
                                cod_FIP.put(cod, new AbstractMap.SimpleEntry<>(e, "-"));
//                                System.out.println(cod + " | -  " + e);
                                String line = String.format("%9s | %3s", cod, "-");
                                System.out.println(line);
                                cod++;
                            }
                        }
                        else {
                            ok = 0;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea fișierului: " + e.getMessage());
        }


    }
}

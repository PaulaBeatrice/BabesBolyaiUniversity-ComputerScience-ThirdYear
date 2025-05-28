import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static void writeListToFile(BufferedWriter writer, String listName, List<String> list) throws IOException {
        writer.write(listName + ":\n");
        for (String item : list) {
            writer.write(item + "\n");
        }
        writer.write("\n"); // Adăugăm o linie goală între liste
    }

    public static void main(String[] args) {
        String numeFisier = "C:\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Laborator_2\\src\\main\\java\\program_3.txt";

        // Definim urmatoarele liste in care vom plasa identificatorii, constantele, operatorii, separatorii si cuvintele cheie din fisierul citit
        List<String> IDs = new ArrayList<>();
        List<String> CONSTs = new ArrayList<>();
        List<String> OPERATORI = new ArrayList<>();
        List<String> SEPARATORI = new ArrayList<>();
        List<String> CUVINTE_CHEIE = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie;
            while ((linie = br.readLine()) != null) { // parcurgem fiecare linie
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
                        }
                        else if (atom.isID()) { // verificam daca e identificator
                            IDs.add(e);
                        }
                        else if(atom.isOPERATOR()){ // verificam daca e operator
                            OPERATORI.add(e);
                        }
                        else if(atom.isSEPARATOR()){ // verificam daca e separator
                            SEPARATORI.add(e);
                        }
                        else {
                            System.out.println("Eroare! -" + e + "-");
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
    }
}

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MainSecv {
    private static final List<Node> clasament = new LinkedList<>(); // lista inlantuita ce contine clasamentul
    private static final List<Integer> partEliminati = new LinkedList<>(); // lista ce contine id-urile participantilor eliminati (care au obtinut scorul 0)

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // start

        String numeFisier = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\data\\dataCountry";
        for (int i = 1; i <= 5; i++) {
            String numeDir = numeFisier + i;
            for (int j = 1; j <= 10; j++) { // parcurgem fisierele folderului
                String fisier = "\\RezultatC" + i + "_P" + j + ".txt";
                String pathFisier = numeDir + fisier;
                File myObj = new File(pathFisier);
                try (Scanner scanner = new Scanner(myObj)) { // citim linie cu linie din fisier
                    while (scanner.hasNextLine()) {
                        String data = scanner.nextLine();
                        int idParticipant = Integer.parseInt(data.split(" ")[0]);
                        int pctParticipant = Integer.parseInt(data.split(" ")[1]);
                        addToResultList(new Node(idParticipant, pctParticipant)); // adaugam in lista un nod format din id ul si punctajul citit
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        afisare();
        long stopTime = System.nanoTime(); // stop
        System.out.println("Timp de executie: " + (stopTime - startTime) / 1E6);
    }

    private static void addToResultList(Node node) {
        for (Node n : clasament) {
            if (n.getId() == node.getId()) { // daca exista deja
                if (node.getPunctaj() != -1) { // daca are punctajul diferit de -1
                    Node newNode = new Node(node.getId(),n.getPunctaj() + node.getPunctaj()); // cream un nou nod cu punctajul actualizat
                    clasament.remove(n); // stergem nodul cu punctajul vechi
                    addClasament(newNode); // adaugam nodul cu punctajul actualizat
                } else { // daca are punctajul -1
                    partEliminati.add(n.getId()); // adaugam in lista de participanti eliminati
                    clasament.remove(n); // stergem nodul din clasament
                }
                return; // nodul a fost gasit in lista si actualizat; oprim executia
            }
        }
        // daca nu exista in lista
        if (node.getPunctaj() == -1) { // are punctaj invalid
            partEliminati.add(node.getId()); // adaugam id-ul in lista de participanti eliminati
        }
        if (!partEliminati.contains(node.getId())) { // daca nu se afla in lista participantilor eliminati adaugam nodul in lista
            addClasament(node);
        }
    }

    private static void addClasament(Node node) {
        int pos = 0; // pozitia pe care urmeaza sa inseram participantul, astfel incat punctajele sa fie ordonate descrescator
        // iar in cazul in care punctajele sunt egale, sa fie ordonati dupa id
        while (pos < clasament.size() && (node.getPunctaj() < clasament.get(pos).getPunctaj() ||
                (node.getPunctaj() == clasament.get(pos).getPunctaj() && node.getId() > clasament.get(pos).getId()))) {
            pos++;
        }
        clasament.add(pos, node);  // adaugam node-ul in lista clasamentului pe pozitia pos
    }

    private static void afisare() {
        File file = new File("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\output\\ClasamentSecvential.txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("Id  \tPunctaj\n"); // header
            for (Node entry : clasament) {
                fileWriter.write(entry.getId() + "\t\t" + entry.getPunctaj() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

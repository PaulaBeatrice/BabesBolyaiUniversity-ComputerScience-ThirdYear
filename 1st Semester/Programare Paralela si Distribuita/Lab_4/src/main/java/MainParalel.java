
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainParalel {

    private static final int nrThrCitire = 2; // nr threaduri citire
    private static final int nrThrWorkers = 16; // nr threaduri workers
    private static final int nrFisiere = 5; // nr fisiere
    private static final MyQueue queue = new MyQueue(nrThrCitire); // coada in care vom stoca nodurile (participanti si punctajul lor total)
    private static final List<Node> clasament = new LinkedList<>(); // lista inlantuita ce contine clasamentul
    private static final List<Integer> partEliminati = new LinkedList<>(); // lista ce contine id-urile participantilor eliminati (care au obtinut scorul 0)
    private static final Lock lock = new ReentrantLock();



    private static class MyThreadReader extends Thread {
        private final int start; // indicele fisierului de la care se incepe citirea
        private final int end; // indicele fisierului la care se incheie citirea

        private MyThreadReader(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            String numeFisier = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\data\\dataCountry";
            for (int i = start; i < end; i++) { // parcugem folderele
                int indice = i + 1;
                String numeDir = numeFisier + indice;
                for (int j = 1; j <= 10; j++) { // parcurgem fisierele folderului
                    String fisier = "\\RezultatC" + indice + "_P" + j + ".txt";
                    String pathFisier = numeDir + fisier;
                    File myObj = new File(pathFisier);
                    try (Scanner scanner = new Scanner(myObj)) { // citim linie cu linie din fisier
                        while (scanner.hasNextLine()) {
                            String data = scanner.nextLine();
                            int idParticipant = Integer.parseInt(data.split(" ")[0]); // extragem id ul
                            int pctParticipant = Integer.parseInt(data.split(" ")[1]); // extragem punctajul
                            queue.add(new Node(idParticipant, pctParticipant)); // adaugam in coada un nod format din id ul si punctajul citit
                        }
                        queue.decreaseNrReaders();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static class MyThreadWorker extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    Node node = queue.get(); // obtinem un nod din coada
                    if (node == null) { // daca e null, inseamna ca coada e vida
                        break;
                    }
                    lock.lock(); // blocam accesul concurent la lista
                    try {
                        addToResultList(node); // adaugam nodul in lista clasamentului
                    } finally {
                        lock.unlock(); // deblocam accesul
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void addToResultList(Node node) throws InterruptedException { // adauga un node in lista clasamentului
            for (Node n : clasament) { // daca exista deja
//                System.out.println();
                if (n.getId() == node.getId()) {
                    if (node.getPunctaj() != -1) { // daca are punctajul diferit de -1
                        Node newNode = new Node(node.getId(), node.getPunctaj() + n.getPunctaj()); // cream un nou nod cu punctajul actualizat
                        clasament.remove(n); // stergem nodul cu punctajul vechi
                        addClasament(newNode); // adaugam nodul cu punctajul actualizat
                    } else { // daca are punctajul -1
                        partEliminati.add(node.getId()); // adaugam in lista de participanti eliminati
                        clasament.remove(n); // stergem nodul din clasament
                    }
                    return; // nodul a fost gasit in lista si actualizat; oprim executia
                }
            }
            // daca nu exista deja in lista
            if (node.getPunctaj() == -1) { // are punctaj invalid
                partEliminati.add(node.getId()); // adaugam id-ul in lista de participanti eliminati
            }
            if (!partEliminati.contains(node.getId())) { // daca nu se afla in lista participantilor eliminati adaugam nodul in lista
                addClasament(node);
            }
        }

        private void addClasament(Node node) {
            int pos = 0; // pozitia pe care urmeaza sa inseram participantul, astfel incat punctajele sa fie ordonate descrescator
            // iar in cazul in care punctajele sunt egale, sa fie ordonati dupa id
            while (pos < clasament.size() && (node.getPunctaj() < clasament.get(pos).getPunctaj() ||
                    (node.getPunctaj() == clasament.get(pos).getPunctaj() && node.getId() > clasament.get(pos).getId()))) {
                pos++;
            }
            clasament.add(pos, node); // adaugam node-ul in lista clasamentului pe pozitia pos
        }
    }

    private static void afisare() {
        File file = new File("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\output\\ClasamentParalel.txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("Id  \tPunctaj\n"); // header
            lock.lock();
            try {
                for (Node entry : clasament) {
                    fileWriter.write(entry.getId() + "\t\t" + entry.getPunctaj() + "\n");
                }
            } finally {
                lock.unlock();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) {
        long startTime = System.nanoTime(); // start

        MyThreadWorker[] thWorkers = new MyThreadWorker[nrThrWorkers];
        for (int i = 0; i < nrThrWorkers; i++) { // initializarea si pornirea threadurilor workers
            thWorkers[i] = new MyThreadWorker();
            thWorkers[i].start();
        }

        MyThreadReader[] thReaders = new MyThreadReader[nrThrCitire];
        // distribuim fisierele la threadurile de citire
        int cat = nrFisiere / nrThrCitire;
        int rest = nrFisiere % nrThrCitire;
        int start = 0, end = 0;

        for (int i = 0; i < nrThrCitire; i++) {
            end = start + cat;
            if (rest > 0) {
                end++;
                rest--;
            }
            // initializare si pornire threaduri citire
            thReaders[i] = new MyThreadReader(start, end);
            thReaders[i].start();
            start = end;
        }

        // asteptam finalizarea threadurilor de citire
        for (int i = 0; i < nrThrCitire; i++) {
            try {
                thReaders[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // asteptam finalizarea threadurilor workers
        for (int i = 0; i < nrThrWorkers; i++) {
            try {
                thWorkers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // scriem clasamentul final in fisier
        afisare();

        // comparam cele doua clasamente
        String clasamentParalel = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\output\\ClasamentParalel.txt";
        String clasamentSecvential = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\output\\ClasamentSecvential.txt";
        int ok = 1;
        try (BufferedReader reader1 = new BufferedReader(new FileReader(clasamentParalel));
             BufferedReader reader2 = new BufferedReader(new FileReader(clasamentSecvential))) {
            String line1, line2 = null;
            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) { // comparam linie cu linie
                // line1 - linie din clasamentul paralel
                // line2 - linie din clasamentul secvential
                if (!line1.equals(line2)) {
                    System.out.println("Clasamentele sunt diferite: " + line1 + " | " + line2);
                    ok = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            ok = 0;
        }
        if (ok == 1)
            System.out.println("Clasamentele sunt identice!");

        long stopTime = System.nanoTime(); // stop
        System.out.println("Timp de executie: " + (stopTime - startTime) / 1E6);

    }
}

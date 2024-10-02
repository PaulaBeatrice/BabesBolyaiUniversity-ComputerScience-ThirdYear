import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int nrThrCitire = 4;
    private static final int nrThrWorkers  = 12;
    private static final List<String> listaFisiere = List.of(
            "RezultatC1_P1.txt", "RezultatC1_P2.txt", "RezultatC1_P3.txt", "RezultatC1_P4.txt", "RezultatC1_P5.txt", "RezultatC1_P6.txt", "RezultatC1_P7.txt", "RezultatC1_P8.txt", "RezultatC1_P9.txt", "RezultatC1_P10.txt",
            "RezultatC2_P1.txt", "RezultatC2_P2.txt", "RezultatC2_P3.txt", "RezultatC2_P4.txt", "RezultatC2_P5.txt", "RezultatC2_P6.txt", "RezultatC2_P7.txt", "RezultatC2_P8.txt", "RezultatC2_P9.txt", "RezultatC2_P10.txt",
            "RezultatC3_P1.txt", "RezultatC3_P2.txt", "RezultatC3_P3.txt", "RezultatC3_P4.txt", "RezultatC3_P5.txt", "RezultatC3_P6.txt", "RezultatC3_P7.txt", "RezultatC3_P8.txt", "RezultatC3_P9.txt", "RezultatC3_P10.txt",
            "RezultatC4_P1.txt", "RezultatC4_P2.txt", "RezultatC4_P3.txt", "RezultatC4_P4.txt", "RezultatC4_P5.txt", "RezultatC4_P6.txt", "RezultatC4_P7.txt", "RezultatC4_P8.txt", "RezultatC4_P9.txt", "RezultatC4_P10.txt",
            "RezultatC5_P1.txt", "RezultatC5_P2.txt", "RezultatC5_P3.txt", "RezultatC5_P4.txt", "RezultatC5_P5.txt", "RezultatC5_P6.txt", "RezultatC5_P7.txt", "RezultatC5_P8.txt", "RezultatC5_P9.txt", "RezultatC5_P10.txt"
    );
    private static final MyLinkedList clasament = new MyLinkedList(); // lista inlantuita care contine clasamentul
    private static final List<String> partEliminati = new ArrayList<>(); // lista care contine id-urile participantilor eliminati
    private static final ExecutorService executor = Executors.newFixedThreadPool(nrThrCitire);
    private static final AtomicInteger nrCititoriRamasi = new AtomicInteger(50);
    private static final MyQueue queue = new MyQueue(nrCititoriRamasi);
    private static final Map<String, ReentrantLock> lock = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // start

        for (int i = 0; i < 1000; ++i) { // creeaza 1000 de obiecte de tip ReentrantLock
            lock.put(String.valueOf(i), new ReentrantLock());
        }

        Thread[] thWorkers = new Thread[nrThrWorkers]; // creare si pornirea threadurilor
        for (int i = 0; i < nrThrWorkers ; i++) {
            Thread thread = new MyThreadWorker();
            thWorkers[i] = thread;
        }
        for (Thread thread : thWorkers) {
            thread.start();
        }

        listaFisiere.forEach(fisier -> executor.execute(() -> { // parcurgem lista de fisiere si cream threadurile de citire
            try {
                String taraParticipant = String.valueOf(fisier.charAt(fisier.indexOf('C')) + fisier.charAt(fisier.indexOf('C') + 1)); // extrag tara din numele fisierului
                File obj = new File("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_5\\src\\main\\java\\data\\" + fisier);
                Scanner scanner = new Scanner(obj);

                while (scanner.hasNextLine()) {
                    String data = scanner.nextLine();
                    String idParticipant = data.split(" ")[0];
                    int pctParticipant = Integer.parseInt(data.split(" ")[1]);
                    Participant participant = new Participant(idParticipant, pctParticipant, taraParticipant);
                    queue.push(participant); // adaugam participantul in coada
                }
                if (nrCititoriRamasi.decrementAndGet() == 0) { // daca nu mai avem cititori, finalizam operatiile pe coada
                    queue.finish();
                }
            } catch (FileNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        executor.shutdown(); // inchidem executorul si asteptam ca threadurile sa isi termine executia
        for (Thread thread : thWorkers) {
            try {
                thread.join(); // asteptam finalizarea fiecarui thread
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // obtinem clasamentul final din lista noastra inlantuita, sortand descrescator dupa scor si in caz de punctaje egale, crescator dupa i
        List<Participant> participantList = new ArrayList<Participant>(); // convertim lista inlantuita intr-o lista simpla
        Node current = clasament.head.next;
        while (current != clasament.tail) {
            participantList.add(current.getParticipant()); // adaugam pe rand participantii
            current = current.next;
        }
        participantList.sort(Comparator // sortam lista, descrescator dupa punctaj, apoi crescator dupa id
                .comparingInt(Participant::getPunctaj)
                .reversed()
                .thenComparing(Participant::getId));
        afisare(participantList);
        verificaClasament();

        long stopTime = System.nanoTime(); // stop
        System.out.println("Timp de executie: " + (stopTime - startTime) / 1E6);
    }

    private static boolean compareLines(String line1, String line2) {
        // Se desparte liniile în token-uri folosind virgula ca separator
        String[] tokens1 = line1.split("\\s+");
        String[] tokens2 = line2.split("\\s+");

        // Verifică dacă liniile au cel puțin două elemente și dacă primele două elemente sunt egale
        return tokens1.length >= 2 && tokens2.length >= 2 && tokens1[0].equals(tokens2[0]) && tokens1[1].equals(tokens2[1]);
    }

    private static void verificaClasament() {
        String clasamentParalel = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_5\\src\\main\\java\\output\\ClasamentParalel.txt";
        String clasamentSecvential = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_5\\src\\main\\java\\output\\ClasamentSecvential.txt";
        int ok = 1;

        try (BufferedReader reader1 = new BufferedReader(new FileReader(clasamentParalel));
             BufferedReader reader2 = new BufferedReader(new FileReader(clasamentSecvential))) {
            reader1.readLine();
            reader2.readLine();

            String line1, line2 = null;
            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) { // comparam linie cu linie
                // line1 - linie din clasamentul paralel
                // line2 - linie din clasamentul secvential
                if (!compareLines(line1,line2)) {
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
    }

    private static void afisare(List<Participant> list) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_5\\src\\main\\java\\output\\ClasamentParalel.txt"));
            bw.write("Id  \tPct  \tȚară\n");
            for(Participant p: list){
                bw.write(p.getId() + "\t\t" + p.getPunctaj() + "\t\t" + p.getTara() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MyThreadWorker extends Thread {
        @Override
        public void run() {
            while (nrCititoriRamasi.get() != 0 || !queue.isEmpty()) { // cat timp mai sunt cititori sau coada nu e goala
                Participant participant = null;
                try {
                    participant = queue.pop(); // extrage un element din coada
                } catch (InterruptedException ignored) {
                }

                if (participant == null) { // daca elementul extras din coada e null
                    queue.finish(); // semnalam ca operatiile pe coada s-au incheiat
                    continue;
                }

                lock.get(participant.getId()).lock(); // blocam accesul concurent la participantul curent
                if(!partEliminati.contains(participant.getId())){ // daca participantul nu a fost eliminat
                    if (participant.getPunctaj() == -1) { // il eliminam daca are punctajul -1
                        clasament.delete(participant); // stergem nodul din lista inlantuita
                        partEliminati.add(participant.getId()); // adaugam id-ul in lista de participanti eliminati
                    } else {
                        if(clasament.find(participant) == null){ // daca participantul nu e in lista, il adaugam
                            clasament.add(participant);
                        }
                        else{ // exista deja, deci actualizam punctajul
                            clasament.update(participant);
                        }
                    }
                }
                lock.get(participant.getId()).unlock();
            }
        }
    }
}

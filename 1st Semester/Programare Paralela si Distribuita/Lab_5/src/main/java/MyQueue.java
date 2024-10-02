import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue {
    private final int MAX = 100; // capacitatea maxima a cozii
    private final Lock lock = new ReentrantLock(); // un obiect lock, pentru a asigura sincronizarea accesului la coada
    private final Condition notFull = lock.newCondition(); // variabila conditionala, pentru a bloca threadurile cand incearca sa adauge in coada plina
    private final Condition notEmpty = lock.newCondition(); // variabila conditionala, pentru a bloca threadurile cand incearca sa extraga un element din coada goala
    private final AtomicInteger nrCititoriRamasi; // variabila atomica care tine evidenta nr de cititori ramasi

    public int start; // indexul de inceput al cozii
    public int end; // indexul de final al cozii
    public int dim; // nr curent de elemente din coada
    private final Participant[] queue = new Participant[101];
    public MyQueue(AtomicInteger nrCititoriRamasi) {
        this.nrCititoriRamasi = nrCititoriRamasi;
        this.start = 0;
        this.end = 0;
        this.dim = 0;
    }

    public void finish() {
        lock.lock(); // blocheaza accesul concurent
        notEmpty.signal(); // atunci cand coada nu e goala, si un element este adaugat, sunt notificate threadurile care asteapta sa extraga un element
        notFull.signal(); // cand coada nu este plina, si un element este extras, sunt notificate threadurile care asteapta sa adauge un element in coada
        lock.unlock(); // elibereaza; permite altor threaduri accesul la resurse
    }

    public void push(Participant node) throws InterruptedException { // adauga un participant in coada
        lock.lock();
        try {
            while (dim == MAX) { // daca coada e plina
                notFull.await(); // asteapta pana cand coada nu mai este plina
            }
            queue[end++] = node; // adauga un element la finalul cozii
            if (end == MAX) { // am ajuns la finalul cozii
                end = 0; // se revine la inceputul cozii
            }
            dim++;
            notEmpty.signal(); // semnaleaza thread-urile care asteapta la conditia notEmpty, adica care vor sa extraga un element
        } finally {
            lock.unlock();
        }
    }

    public Participant pop() throws InterruptedException { // extragem un participant din coada
        Participant node;
        lock.lock();
        try {
            while (dim == 0) { // daca coada e goala
                if (nrCititoriRamasi.get() == 0) {
                    return null; // returnam null daca nu mai sunt cititori
                }
                notEmpty.await(); // astepta pana cand coada nu mai este goala
            }
            node = queue[start++]; // extragem primul element din coada
            if (start == MAX) { // am ajuns la finalul cozii
                start = 0;
            }
            dim--;
            notFull.signal(); // semnaleaza thread-urile care asteapta la conditia notFull, adica care vor sa adauge un element in coada
            return node;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() { // verificam daca coada este goala
        return dim == 0;
    }
}

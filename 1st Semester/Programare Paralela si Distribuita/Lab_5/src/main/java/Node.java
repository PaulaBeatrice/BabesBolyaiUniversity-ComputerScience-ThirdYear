import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    public final Lock lock = new ReentrantLock(); // un obiect lock pentru a sincroniza accesul la un nod
    private Participant participant; // participantul stocat in nod
    public Node next; // ref la urmatorul nod
    public Node previous;// ref la nodul anterior

    public Node(Participant participant, Node next, Node previous) {
        this.participant = participant;
        this.next = next;
        this.previous = previous;
    }

    public Participant getParticipant() {
        return participant;
    }
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
    public boolean notNull() {
        return participant != null;
    }
    public void lock() { // blocarea accesului concurent la nod
        lock.lock();
    }
    public void unlock() { // deblocarea accesului concurent la nod
        lock.unlock();
    }
}

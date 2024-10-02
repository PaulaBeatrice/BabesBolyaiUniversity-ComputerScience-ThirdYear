
import java.util.LinkedList;
import java.util.Queue;

public class MyQueue {
  // Coada in care vom stoca nodurile (participantii)
  public final Queue<Node> queue = new LinkedList<>();

  public int nrReaders; // nr cititori

  public MyQueue(int nrReaders) {
    this.nrReaders = nrReaders;
  }

  public synchronized void add(Node node) { // adauga un nod in coada
    queue.add(node);
    notify(); // notifica (threadurile in asteptare) adaugarea unui element
  }

  public synchronized Node get() throws InterruptedException { // returneaza un nod din coada
    while (queue.isEmpty() && nrReaders > 0) { // cat timp coada este goala si mai exista cititori, asteapta
      wait();
    }
    if (!queue.isEmpty()) { // returneaza si elimina din coada, nodul din capul cozii
      return queue.poll();
    }
    return null; // daca coada este goala si nu mai avem cititori
  }

  public synchronized void decreaseNrReaders() { // scade nr de cititori
    nrReaders--;
    if (nrReaders == 0) { // daca nu mai exista cititori notificam threadurile in asteptare
      notifyAll();
    }
  }

}

import java.util.*;

public class MyLinkedList {  // lista inlantuita cu santinele
    public final Node head = new Node(null, null, null); // nodul de start fara informatie
    public final Node tail = new Node(null, null, null); // nodul de final fara informatie

    public MyLinkedList() {
        head.next = tail; // initial head-ul are referinta de next la tail
        tail.previous = head; // tail-ul are referinta de previous la head
    }

    /** actualizeaza nodul pe care se afla participantul dat
     * se cauta nodul in lista inlantuita si se actualizeaza punctajul sau
     * @param participant
     * @return : nodul actualizat (participantul cu noul punctaj) sau null, daca nu e in lista
     */
    Node update(Participant participant) {
        Node currentNode = head.next; // parcurgem lista pornind de la primul nod nenul
        if (notEmpty()) { // daca avem noduri in lista
            while (currentNode.notNull()) { // parcurge, lista pana la sfarsitul listei (excluzand santinela de sfarsit)
                currentNode.lock(); // blocam accesul concurent la nod
                try {
                    if (Objects.equals(currentNode.getParticipant().getId(), participant.getId())) { // cand gasim node-ul pe care se afla participantul
                        int pctNou = currentNode.getParticipant().getPunctaj() + participant.getPunctaj();
                        currentNode.getParticipant().setPunctaj(pctNou); // actualizam punctajul
                        return currentNode; // returnam nodul actualizat
                    }
                } finally {
                    currentNode.unlock();
                }
                currentNode = currentNode.next; // mergem la urmatorul nod
            }
        }
        return null; // am ajuns la finalul listei, return null; nu am gasit participantul
    }

    Node find(Participant participant){
        Node currentNode = head.next; // parcurgem lista pornind de la primul nod nenul
        if (notEmpty()) { // daca avem noduri in lista
            while (currentNode.notNull()) { // parcurge, lista pana la sfarsitul listei (excluzand santinela de sfarsit)
                currentNode.lock(); // blocam accesul concurent la nod
                try {
                    if (Objects.equals(currentNode.getParticipant().getId(), participant.getId())) { // cand gasim node-ul pe care se afla participantul
                        return currentNode; // returnam nodul actualizat
                    }
                } finally {
                    currentNode.unlock();
                }
                currentNode = currentNode.next; // mergem la urmatorul nod
            }
        }
        return null;
    }

    /** Adaugam un participant la inceputul listei
     * @param participant informatia ce va fi adaugata in noul nod
     */
    void add(Participant participant) {
        Node newNode = new Node(participant, null, null); // cream noul nod, si adaugam informatiile participantului in el
        newNode.lock(); // blocam accesul concurent la nod
        head.lock(); // blocam accesul concurent la head si la urm nod, pentru ca urmeaza sa le modificam referintele next/previous
        head.next.lock();
        Node firstNode = head.next; // primul nod, de dupa head(nenul)

        head.next = newNode; // adaugam noul nod dupa head
        newNode.previous = head; // noul nod are referinta previous la head
        newNode.next = firstNode; // noul nod are referinta next la vechiul "prim" nod
        firstNode.previous = newNode; // vechiul "prim" nod are referinta previous la noul nod

        newNode.unlock();
        head.unlock();
        firstNode.unlock();
    }

    /** Stergem din lista node-ul cu informatiile unui participant
     * @param participant: participantul de sters din lista
     */
    void delete(Participant participant) {
        head.lock();
        head.next.lock();
        int stop = 0;
        if (!notEmpty()) { // daca lista are doar cele 2 noduri de santinela eliberam accesul si oprim cautarea nodului de sters
            head.unlock();
            head.next.unlock();
            stop = 1;
        }

        if(stop == 0){ // cautam nodul in lista
            Node currentNode = head.next; // pornim de la primul nod cu informatii din lista
            while (currentNode.notNull()) {
                currentNode.next.lock(); // se blocheaza accesul concurent la nodul urmator
                if (currentNode.getParticipant().getId().equals(participant.getId())) {
                    // stergem nodul currentNode => refacem legaturile dintre nodurile vecine
                    // nodul din stangava primi referinta de next la nodul din dreapta al nodului curent
                    // nodul din dreapta va primi referinta de previous la nodul din stranga al nodului curent
                    Node previousNode = currentNode.previous;
                    previousNode.next = currentNode.next;
                    Node nextNode = currentNode.next;
                    nextNode.previous = currentNode.previous;

                    // deblocam accesul concurent la cele 3 noduri
                    previousNode.unlock();
                    currentNode.unlock();
                    nextNode.unlock();
                    return;
                }
                currentNode.previous.unlock(); // deblocam accesul doar la nodul din stanga al nodului curent
                currentNode = currentNode.next;
            }
            currentNode.previous.unlock(); // am ajuns la finalul listei, si nu am gasit participantul
            currentNode.unlock();
        }
    }

    boolean notEmpty(){ // verificam daca avem un nod cu informatii in lista (adica inca un nod pe langa cele de santinela)
        return head.next != tail;
    }
}

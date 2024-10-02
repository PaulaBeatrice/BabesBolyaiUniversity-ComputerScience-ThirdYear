import java.util.Arrays;
import java.util.List;

/**
 * Definesc o clasa Atom care va avea ca parametru un text
 *
 */
public class Atom {

    private String text;
    private final String ID = "^[a-zA-Z_][a-zA-Z0-9_]{0,7}$"; // incepe cu o litera sau "_" si poate contine litere, cifre sau caractere de subliniere in continuare
    private final String CONST = "^-?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?$"; // numere întregi, numere cu virgulă mobilă și notație științifică
    private final String[] OPERATORI = {"<", ">", "<=", ">=", "+", "-", "/", "*", "%", ">>", "<<", "==", "!=", "="};
    private final List<String> OPERATORI_LIST = Arrays.asList(OPERATORI);
    private final String[] SEPARATORI = {".", ",", ";", "{", "}", "(", ")"};
    private final List<String> SEPARATORI_LIST = Arrays.asList(SEPARATORI);
    private final String[] CUVINTE_CHEIE = {"#include", "<iostream>", "<cmath>", "<cstring>", "using", "namespace", "std", "int", "main", "double", "struct", "cin", "cout", "if", "else", "while"};
    private final List<String> CUVINTE_CHEIE_LIST = Arrays.asList(CUVINTE_CHEIE);

    public Atom(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * Verifica daca un text este identificator
     * @return true, daca textul este un identificator, false altfel
     */
    public boolean isID(){
        return text.matches(ID);
    }

    /**
     * Verifica daca un text este constanta
     * @return true, daca textul e o constanta, false altfel
     */
    public boolean isCONST(){
        return text.matches(CONST);
    }

    /**
     * Verifica daca un text este un operator
     * @return true, daca textul e un operator, false altfel
     */
    public boolean isOPERATOR(){
        return OPERATORI_LIST.contains(text);
    }

    /**
     * Verifica daca un text este un separator
     * @return true, daca textul e un separator, false altfel
     */
    public boolean isSEPARATOR(){
        return SEPARATORI_LIST.contains(text);
    }

    /**
     * Verifica daca un text este un key-word
     * @return true, daca textul e key-word, false altfel
     */
    public boolean isCUVANT_CHEIE(){
        return CUVINTE_CHEIE_LIST.contains(text);
    }


}

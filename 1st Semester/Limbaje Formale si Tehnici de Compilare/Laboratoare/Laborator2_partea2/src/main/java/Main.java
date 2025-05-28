import AF_cls.AF;
import AF_cls.Tranzitie;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static List<String> multimeaStarilor_AF_CT = new ArrayList<>();
    private static List<String> alfabet_AF_CT = new ArrayList<>();
    private static List<Tranzitie> multimeaTranzitiilor = new ArrayList<>();
    private static String stareInitiala = "";
    private static List<String> stariFinale = new ArrayList<>();
    private static AF af;


    private static void writeListToFile(BufferedWriter writer, String listName, List<String> list) throws IOException {
        writer.write(listName + ":\n");
        for (String item : list) {
            writer.write(item + "\n");
        }
        writer.write("\n"); // Adăugăm o linie goală între liste
    }




    public static void main(String[] args) {
        String numeFisier = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Laborator2_partea2\\src\\main\\java\\program_3.txt";

        // Definim urmatoarele liste in care vom plasa identificatorii, constantele, operatorii, separatorii si cuvintele cheie din fisierul citit
        List<String> IDs = new ArrayList<>();
        List<String> CONSTs = new ArrayList<>();
        List<String> OPERATORI = new ArrayList<>();
        List<String> SEPARATORI = new ArrayList<>();
        List<String> CUVINTE_CHEIE = new ArrayList<>();

        String[] OPERATORI_LIST = {"<", ">", "<=", ">=", "+", "-", "/", "*", "%", ">>", "<<", "==", "!=", "=", ";"};

        HashMap<Integer, String> TS_ID = new HashMap<>();
        TS TS_IDENDTIFIERS = new TS(TS_ID);

        HashMap<Integer, String> TS_CT = new HashMap<>();
        TS TS_CONSTANTS = new TS(TS_CT);

        int eroare = 0;


        // CREAREA AUTOMATELOR FINITE
        AF AF_Constante_Intregi = new AF();
        AF_Constante_Intregi.citireFisierAF("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Laborator2_partea2\\src\\main\\java\\AF_cls\\AF_CT_INT.txt");
//        System.out.println(AF_Constante_Intregi.verifica_secventa("85"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("0213"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("0x4b"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("30"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("30u"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("30l"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("30ul"));
//        System.out.println(AF_Constante_Intregi.verifica_secventa("-10"));

        AF AF_Constante_Reale = new AF();
        AF_Constante_Reale.citireFisierAF("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Laborator2_partea2\\src\\main\\java\\AF_cls\\AF_CT_RL.txt");
//        System.out.println(AF_Constante_Reale.verifica_secventa("0.1"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("0.14"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("1.14"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("14.1"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("14.14"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("1.1"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("3.14159"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("314159E-5L"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("1e4"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("2.5e-2"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("3.14e2"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("6.02e23"));
//        System.out.println(AF_Constante_Reale.verifica_secventa("0.75e-3"));

        AF AF_Identificatori = new AF();
        AF_Identificatori.citireFisierAF("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Laborator2_partea2\\src\\main\\java\\AF_cls\\AF_IDENTIFICATORI.txt");
//        System.out.println(AF_Identificatori.verifica_secventa("_a"));
//        System.out.println(AF_Identificatori.verifica_secventa("a"));
//        System.out.println(AF_Identificatori.verifica_secventa("A"));
//        System.out.println(AF_Identificatori.verifica_secventa("_a43"));
//        System.out.println(AF_Identificatori.verifica_secventa("_a4C_"));
//        System.out.println(AF_Identificatori.verifica_secventa("ab"));
//        System.out.println(AF_Identificatori.verifica_secventa("nr"));
//        System.out.println(AF_Identificatori.celMaiLungPrefix("nrrrrrrrrrrrr;"));

        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie;

            while ((linie = br.readLine()) != null && eroare == 0) {
                String token = "";

                for (int i = 0; i < linie.length(); i++) {
                    char currentChar = linie.charAt(i);

                    if (Character.isWhitespace(currentChar)) {
                        if (!token.isEmpty()) {
                            Atom atom = new Atom(token.trim());
//                            System.out.println(atom.getText());

                            while (!token.equals("")) {
//                                System.out.println(token);
                                if (atom.isCUVANT_CHEIE()) {
//                                    System.out.println("AICI ch " + token);
                                    CUVINTE_CHEIE.add(token);
                                    token = "";
                                } else if (atom.isOPERATOR()) {
//                                    System.out.println("AICI oppp");
                                    OPERATORI.add(token);
                                    token = "";
                                } else if(Arrays.asList(OPERATORI_LIST).contains(String.valueOf(token.charAt(0)))){
//                                    System.out.println("OP");

                                    OPERATORI.add(String.valueOf(token.charAt(0)));
                                    token = token.substring(1);
                                }
                                else if (atom.isSEPARATOR()) {
                                    SEPARATORI.add(token);
//                                    System.out.println("sep " + token);
                                    token = "";
                                }else if (/*atom.isCONST()*/ AF_Constante_Intregi.verifica_secventa(token) || AF_Constante_Reale.verifica_secventa(token)) {
                                    CONSTs.add(token);
                                    TS_CONSTANTS.add(token);
                                    token = "";
//                                    System.out.println("AICI CT");
                                } else if (/*atom.isID()*/ AF_Identificatori.verifica_secventa(token)) {
//                                    System.out.println("AICI la id");
                                    IDs.add(token);
                                    TS_IDENDTIFIERS.add(token);
                                    token = "";
//                                    System.out.println("AICI ID");
                                } else if(!Objects.equals(AF_Constante_Intregi.celMaiLungPrefix(token), "") && !Objects.equals(AF_Constante_Reale.celMaiLungPrefix(token), ""))
                                {
//                                    System.out.println("AICI la ct");
                                    if(!Objects.equals(AF_Constante_Intregi.celMaiLungPrefix(token), "") ){
                                        CONSTs.add(AF_Constante_Intregi.celMaiLungPrefix(token));
                                        TS_CONSTANTS.add(AF_Constante_Intregi.celMaiLungPrefix(token));
                                        token = token.substring(AF_Constante_Intregi.celMaiLungPrefix(token).length());
                                    }

                                    if(!Objects.equals(AF_Constante_Reale.celMaiLungPrefix(token), "") ){
                                        CONSTs.add(AF_Constante_Reale.celMaiLungPrefix(token));
                                        TS_CONSTANTS.add(AF_Constante_Reale.celMaiLungPrefix(token));
                                        token = token.substring(AF_Constante_Reale.celMaiLungPrefix(token).length());
                                    }

                                }
                                else if(!Objects.equals(AF_Identificatori.celMaiLungPrefix(token),"")){
//                                    System.out.println("AICI");
                                    IDs.add(AF_Identificatori.celMaiLungPrefix(token));
                                    TS_IDENDTIFIERS.add(AF_Identificatori.celMaiLungPrefix(token));
                                    token = token.substring(AF_Identificatori.celMaiLungPrefix(token).length());
                                }
                                else {
                                    System.out.println("Eroare! -" + token + "-");
                                    eroare = 1;
                                    token = "";
                                    break;
                                }
                            }

                        }
                    } else {
                        token += currentChar;
                    }
                }

                if (!token.isEmpty()) {
                    Atom atom = new Atom(token.trim());
//                    System.out.println(atom.getText());
                    while (!token.equals("")) {
//                                System.out.println(token);
                        if (atom.isCUVANT_CHEIE()) {
//                                    System.out.println("AICI ch " + token);
                            CUVINTE_CHEIE.add(token);
                            token = "";
                        } else if (atom.isOPERATOR()) {
//                                    System.out.println("AICI oppp");
                            OPERATORI.add(token);
                            token = "";
                        } else if(Arrays.asList(OPERATORI_LIST).contains(String.valueOf(token.charAt(0)))){
//                                    System.out.println("OP");

                            OPERATORI.add(String.valueOf(token.charAt(0)));
                            token = token.substring(1);
                        }
                        else if (atom.isSEPARATOR()) {
                            SEPARATORI.add(token);
//                                    System.out.println("sep " + token);
                            token = "";
                        }else if (/*atom.isCONST()*/ AF_Constante_Intregi.verifica_secventa(token) || AF_Constante_Reale.verifica_secventa(token)) {
                            CONSTs.add(token);
                            TS_CONSTANTS.add(token);
                            token = "";
//                                    System.out.println("AICI CT");
                        } else if (/*atom.isID()*/ AF_Identificatori.verifica_secventa(token)) {
//                                    System.out.println("AICI la id");
                            IDs.add(token);
                            TS_IDENDTIFIERS.add(token);
                            token = "";
//                                    System.out.println("AICI ID");
                        } else if(!Objects.equals(AF_Constante_Intregi.celMaiLungPrefix(token), "") && !Objects.equals(AF_Constante_Reale.celMaiLungPrefix(token), ""))
                        {
//                                    System.out.println("AICI la ct");


                            if(!Objects.equals(AF_Constante_Reale.celMaiLungPrefix(token), "") ){
                                CONSTs.add(AF_Constante_Reale.celMaiLungPrefix(token));
                                TS_CONSTANTS.add(AF_Constante_Reale.celMaiLungPrefix(token));
                                token = token.substring(AF_Constante_Reale.celMaiLungPrefix(token).length());
                            }
                            if(!Objects.equals(AF_Constante_Intregi.celMaiLungPrefix(token), "") ){
                                CONSTs.add(AF_Constante_Intregi.celMaiLungPrefix(token));
                                TS_CONSTANTS.add(AF_Constante_Intregi.celMaiLungPrefix(token));
                                token = token.substring(AF_Constante_Intregi.celMaiLungPrefix(token).length());
                            }
                        }
                        else if(!Objects.equals(AF_Identificatori.celMaiLungPrefix(token),"")){
//                                    System.out.println("AICI");
                            IDs.add(AF_Identificatori.celMaiLungPrefix(token));
                            TS_IDENDTIFIERS.add(AF_Identificatori.celMaiLungPrefix(token));
                            token = token.substring(AF_Identificatori.celMaiLungPrefix(token).length());
                        }
                        else {
                            System.out.println("Eroare! -" + token + "-");
                            eroare = 1;
                            token = "";
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
                            String line = String.format("%9s | %3d", "0", TS_CONSTANTS.getCodBySimbol(e));
                            System.out.println(line);
                        }
                        else if (atom.isID() && !atom.isCUVANT_CHEIE()) {
                            String line = String.format("%9s | %3d", "1", TS_IDENDTIFIERS.getCodBySimbol(e));
                            System.out.println(line);
                        }
                        else if(atom.isSEPARATOR() || atom.isCUVANT_CHEIE() || atom.isOPERATOR()){
                            boolean existaInFIP = false;
                            for (Map.Entry<Integer, Map.Entry<String, String>> entry : cod_FIP.entrySet()) {
                                if (entry.getValue().getKey().equals(e)) {
                                    existaInFIP = true;
                                    String line = String.format("%9s | %3s", entry.getKey(), entry.getValue().getValue());
                                    System.out.println(line);
                                    break;
                                }
                            }

                            if (!existaInFIP)  {
                                // Dacă e nu există în valorile lui FIP, incrementați cod și adăugați o pereche <cod, <e, "-"> în FIP
                                cod_FIP.put(cod, new AbstractMap.SimpleEntry<>(e, "-"));
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

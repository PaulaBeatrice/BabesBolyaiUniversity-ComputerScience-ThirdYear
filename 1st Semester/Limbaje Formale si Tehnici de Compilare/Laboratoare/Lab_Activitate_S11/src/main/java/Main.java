import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fileName = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Limbaje Formale si Tehnici de Compilare\\Laboratoare\\Lab_Activitate_S11\\src\\main\\java\\fisier.txt";
        Map<String, Set<String>> reguli = new HashMap<>();
        Set<String> neterminale = new HashSet<>();
        Set<String> terminale = new HashSet<>();
        String start = "";
        Gramatica gramatica = new Gramatica();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("e", "ε");
                String[] parts = line.split("->");
                String stanga = parts[0].trim();
                String dreapta = parts[1].trim();

                if (start.isEmpty()) {
                    start = stanga; // Setarea simbolului de start
                    gramatica.setStart(start);
                }

                neterminale.add(stanga);

                if (dreapta.equals("ε")) {
                    if (reguli.containsKey(stanga)) {
                        reguli.get(stanga).add("ε");
                    } else {
                        Set<String> epsilonSet = new HashSet<>();
                        epsilonSet.add("ε");
                        reguli.put(stanga, epsilonSet);
                    }
                    continue;
                }

                for (String simbol : dreapta.split("\\|")) {
                    for (int i = 0; i < simbol.length(); i++) {
                        char caracter = simbol.charAt(i);
                        if (Character.isUpperCase(caracter)) {
                            neterminale.add(String.valueOf(caracter));
                        } else {
                            terminale.add(String.valueOf(caracter));
                        }
                    }

                    if (reguli.containsKey(stanga)) {
                        reguli.get(stanga).add(simbol);
                    } else {
                        Set<String> productionSet = new HashSet<>();
                        productionSet.add(simbol);
                        reguli.put(stanga, productionSet);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        gramatica.setNeterminale(neterminale);
        gramatica.setTerminale(terminale);
        gramatica.setReguli(reguli);

        System.out.println("Reguli ce contin simbolul de start in membrul drept: ");
        gramatica.getReguliCeContinStartInMembruDrept();
    }
}

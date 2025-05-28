import java.util.HashMap;

public class TS {
    HashMap<Integer, String> tabela_simboluri;

    public TS(HashMap<Integer, String> tabela_simboluri) {
        this.tabela_simboluri = tabela_simboluri;
    }

    public void add(String simbol) {
        if (tabela_simboluri.containsValue(simbol)) { // daca exista deja
            return;
        }
        int index = 0;
        // parcurgem tabela pentru a gasi pozitia corecta de inserare a noului simbol
        for (Integer key : tabela_simboluri.keySet()) {
            String val = tabela_simboluri.get(key);
            // comparam simbolul nou cu cele existente
            int comparatie = simbol.compareTo(val);
            if (comparatie < 0) {
                // daca simbolul e mai mic sau egal lexicografic, inserăm noul simbol înaintea valorii curente
                // rearanjam indecsii si valorile din HashMap pentru a face loc noului simbol
                for (int i = tabela_simboluri.size() - 1; i >= index; i--) {
                    tabela_simboluri.put(i + 1, tabela_simboluri.get(i));
                }
                tabela_simboluri.put(index, simbol);
                return;
            }
            index++;
        }
        // daca nu am gasit nici un simbol mai mic sau egal lexicografic, inseram noul simbol la sfarsitul HashMap-ului
        tabela_simboluri.put(index, simbol);
    }

    public void afisare_tabela(){
        System.out.println("SIMBOL | COD_SIMBOL");
        System.out.println("-------------------");
        for (Integer key : tabela_simboluri.keySet()) {
            String val = tabela_simboluri.get(key);
            String line = String.format("%6s | %3d", val, key);
            System.out.println(line);
        }
    }

    public Integer getCodBySimbol(String simbol){
        for (Integer key : tabela_simboluri.keySet()) {
            String val = tabela_simboluri.get(key);
            if(val.equals(simbol))
                return key;
        }
        return 0;
    }
}

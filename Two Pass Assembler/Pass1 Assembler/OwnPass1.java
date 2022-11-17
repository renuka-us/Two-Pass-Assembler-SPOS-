import java.io.*;
import java.util.*;

class obj {
    String name;
    int addr;

    obj(String nm, int address) {
        this.name = nm;
        this.addr = address;
    }
}

public class OwnPass1 {
    String REG[] = { "AREG", "BREG", "CREG" };
    String IS[] = { "MOVER", "MOVEM", "ADD", "SUB", "DIV", "MUL" };
    String DL[] = { "DS", "DC" };
    obj[] literal_table = new obj[10];
    obj[] symb_table = new obj[10];
    BufferedReader br;
    int libtab_ptr = 0;
    ArrayList<Integer> pool_table = new ArrayList<>();
    int pool_tb_ptr = 0;

    public static void main(String args[]) {
        OwnPass1 oPass1 = new OwnPass1();
        try {

            oPass1.parseFile();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void parseFile() throws Exception {
        String code, code2;
        String line;
        int total_symb = 0;
        int total_ltr = 0;
        br = new BufferedReader(new FileReader("input.asm"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("OwnIC.txt"));
        int lc = 0;
        while ((line = br.readLine()) != null) {
            String parts[] = line.split("\\t");
            if (parts[1].equalsIgnoreCase("START")) {
                lc = Integer.parseInt(parts[2]);
                code = "(AD,01)\t(C," + lc + ")";
                bw.write(code + "\n");
                if (lc != 0) {
                    lc--;
                }
            }

            if (parts[0] != "") {
                code = "";
                for (int j = 0; j < total_symb; j++) {
                    if (symb_table[j].addr != 0)
                        continue;
                    else {
                        if (symb_table[j].name.equals(parts[0])) {
                            lc++;
                            code = lc + "\t";
                            Arrays.asList(symb_table).set(j, new obj(Arrays.asList(symb_table).get(j).name, lc));
                            break;
                        }
                    }

                }
                if (parts[1].equals("DC")) {
                    int constant = Integer.parseInt(parts[2].replace("'", ""));
                    code += "\t" + "(DL,01)\t(C," + constant + ")";
                    bw.write(code + "\n");
                } else if (parts[1].equals("DS")) {
                    int size = Integer.parseInt(parts[2].replace("'", ""));
                    code += "\t" + "(DL,02)\t(C," + size + ")";
                    lc = lc + size - 1;
                    bw.write(code + "\n");
                }

            }
            if (parts[1].equalsIgnoreCase("LTORG")) {
                int ptr = 0;
                for (int j = 0; j < total_ltr; j++) {
                    if (literal_table[j].addr != 0)
                        continue;
                    else {
                        ptr = j;
                        break;
                    }

                }
                code2 = "(AD,05)";
                bw.write(code2 + "\n");
                for (int j = ptr; j < libtab_ptr; j++) {
                    lc++;
                    if (literal_table[j].addr == 0)
                        Arrays.asList(literal_table).set(j, new obj(Arrays.asList(literal_table).get(j).name, lc));
                    code = lc + "\t(C," + Arrays.asList(literal_table).get(j).name + ")";
                    bw.write(code + "\n");
                }
                pool_tb_ptr++;
                pool_table.add(++ptr);
            }

            
            if (parts[1].equalsIgnoreCase("END")) {
                int ptr = 0;
                for (int j = 0; j < total_ltr; j++) {
                    if (literal_table[j].addr != 0)
                        continue;
                    else {
                        ptr = j;
                        break;
                    }

                }
                code2 = "(AD,02)";
                bw.write(code2 + "\n");
                for (int j = ptr; j < libtab_ptr; j++) {
                    lc++;
                    if (literal_table[j].addr == 0)
                        Arrays.asList(literal_table).set(j, new obj(Arrays.asList(literal_table).get(j).name, lc));
                    code = lc + "\t(C," + Arrays.asList(literal_table).get(j).name + ")";
                    bw.write(code + "\n");
                }

                pool_table.add(++ptr);
                pool_tb_ptr++;
            }

            if (parts[1].equalsIgnoreCase("ORIGIN")) {
                lc = Integer.parseInt(parts[2]);
                code = "(AD,03)\t(C," + lc + ")";
                bw.write(code + "\n");
                if (lc != 0) {
                    lc--;
                }
            }
if (parts[1].equalsIgnoreCase("STOP")) {
    lc++;
    code = lc + "\t(IS,00)";
    bw.write(code + "\n");
    
}

            if (Arrays.asList(IS).contains(parts[1])) {
                int i = Arrays.asList(IS).indexOf(parts[1]);
                if (parts[0] == "") {
                    lc++;
                }
                code = lc + "\t(IS,0" + (i + 1) + ")\t";

                code2 = "";
                int j = 2;
                while (j < parts.length) {
                    parts[j] = parts[j].replace(",", "");

                    if (Arrays.asList(REG).contains(parts[j])) {
                        int k = Arrays.asList(REG).indexOf(parts[j]);
                        code2 += "(RG" + "," + (k + 1) + ")" + "\t";
                    } else {
                        if (parts[j].contains("=")) {
                            parts[j] = parts[j].replace("=", "").replace("'", "");
                            obj a = literal_table[libtab_ptr++] = new obj(parts[j], 0);
                            int litIndex = Arrays.asList(literal_table).indexOf(a);
                            code2 += "(L," + (litIndex+1) + ")";
                            total_ltr++;
                        }
                        else {
                            obj b = symb_table[total_symb++] = new obj(parts[j], 0);
                            int symIndex = Arrays.asList(symb_table).indexOf(b);
                            code2 += "(S," + (symIndex+1) + ")";

                        }
                    }
                    j++;
                }
                bw.write(code + code2 + "\n");
            }
        }

        br.close();
        bw.close();
        System.out.println("Literal table");
        System.out.println("Index" + "\t Literal" + "\tAddress");
        for (int i = 0; i < total_ltr; i++) {
            if (literal_table[i].addr == 0)
                literal_table[i].addr = lc++;
            System.out.println((i + 1) + "\t" + literal_table[i].name + "\t" + literal_table[i].addr);
        }
        System.out.println("Pool table");
        System.out.println("Index" + "\tIndex of first literal in every literal pool");
        for (int i = 0; i < pool_tb_ptr; i++) {
            System.out.println((i + 1) + "\t" + pool_table.get(i));
        }
        System.out.println("Symbol table");
        System.out.println("Index" + "\tSymbol" + "\tAddress");
        for (int i = 0; i < total_symb; i++) {
            System.out.println((i + 1) + "\t" + symb_table[i].name + "\t" + symb_table[i].addr);
        }
    }
}

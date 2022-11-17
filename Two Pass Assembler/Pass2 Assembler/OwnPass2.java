import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

class obj {
    int index;
    String name;
    int addr;

    obj(int index, String nm, int address) {
        this.index = index;
        this.name = nm;
        this.addr = address;
    }

}

public class OwnPass2 {
    ArrayList<obj> SYMTAB, LITTAB;

    public OwnPass2() {
        SYMTAB = new ArrayList<>();
        LITTAB = new ArrayList<>();
    }

    public static void main(String[] args) {
        OwnPass2 pass2 = new OwnPass2();

        try {
            pass2.generateCode("OwnIC.txt");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("EXE");
        }
    }

    public void readtables() {
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader("SYMBOL.txt"));
            br.readLine();
            line = null;
            while ((line = br.readLine()) != null) {
                String parts[] = line.split("\\s+");
                SYMTAB.add(new obj(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2])));
            }
            br.close();
            br = new BufferedReader(new FileReader("LITERAL.txt"));
            br.readLine();
            line = null;
            while ((line = br.readLine()) != null) {
                String parts[] = line.split("\\s+");
                LITTAB.add(new obj(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2])));
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void generateCode(String filename) throws Exception {
        readtables();
        BufferedReader br = new BufferedReader(new FileReader(filename));

        BufferedWriter bw = new BufferedWriter(new FileWriter("Pass2Out.txt"));
        String line, code;
        while ((line = br.readLine()) != null) {
            String parts[] = line.split("\\s+");
            if (parts[0].contains("AD")) {
                bw.write("\n");
                continue;
            } else if (parts.length == 2) {
                if (parts[1].contains("C")) // DC INSTR
                {
                    int constant = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                    code = "00\t0\t" + String.format("%03d", constant) + "\n";
                    bw.write(parts[0] + "\t\t" + code);
                }
                else if (parts[1].contains("IS")) {

                    code = "00\t0\t000" + "\n";
                    bw.write(parts[0] + "\t\t" + code);

                }
            } else if (parts.length == 3) {
                if (parts[1].contains("DL")) // DC INSTR
                {
                    parts[1] = parts[1].replaceAll("[^0-9]", "");
                    if (Integer.parseInt(parts[1]) == 02) {
                        int constant = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                        code = "00\t0\t" + String.format("%03d", constant) + "\n";
                        bw.write(parts[0] + "\t\t" + code);

                    } else if (Integer.parseInt(parts[1]) == 01) {

                        code = "00\t0\t000" + "\n";
                        bw.write(parts[0] + "\t\t" + code);

                    }
                }
            }
            else if (parts[1].contains("IS")) {
                code = "";
                int opcode = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                if (parts[2].contains("RG"))
                {
                    int regOp = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
                    code = String.format("%02d", opcode) + "\t" + String.format("%01d", regOp)+"\t";
                        
                }
                    if (parts[3].contains("S")) {
                        int symIndex = Integer.parseInt(parts[3].replaceAll("[^0-9]", ""));
                        code += String.format("%03d", SYMTAB.get(symIndex - 1).addr) + "\n";
                    } else if (parts[3].contains("L")) {
                        int symIndex = Integer.parseInt(parts[3].replaceAll("[^0-9]", ""));
                        code +=String.format("%03d", LITTAB.get(symIndex - 1).addr) + "\n";
                    }
                    bw.write(parts[0]+"\t\t"+code);
              }
        }
        br.close();
        bw.close();
    }
}
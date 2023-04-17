package Prog2.Clock;

import java.io.*;
import java.util.*;

public class Clock {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("Usage: java Clock <memsize> <t1> <t2> <inputfile> <outputfile>");
            System.exit(0);
        }
        int memSize = Integer.parseInt(args[0]);
        int t1 = Integer.parseInt(args[1]);
        int t2 = Integer.parseInt(args[2]);
        String inputFile = args[3];
        String outputFile = args[4];

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        int time = 0;
        int fault = 0;
        int count = 0;

        ArrayList<Integer> memory = new ArrayList<Integer>();
        ArrayList<Boolean> refbit = new ArrayList<Boolean>();

        for (int i = 0; i < memSize; i++) {
            memory.add(-1);
            refbit.add(false);
        }

        String line;
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            String type = st.nextToken();
            int pageNum = Integer.parseInt(st.nextToken());

            bw.write(String.format("%d: %-3s %d  [", time, type, pageNum));
            boolean found = false;

            for (int i = 0; i < memory.size(); i++) {
                if (memory.get(i) == pageNum) {
                    found = true;
                    refbit.set(i, true);
                    break;
                }
            }

            if (!found) {
                fault++;
                bw.write("F");
                while (true) {
                    if (refbit.get(count)) {
                        refbit.set(count, false);
                    } else {
                        if (memory.get(count) >= 0) {
                            bw.write("S");
                        }
                        memory.set(count, pageNum);
                        refbit.set(count, true);
                        count = (count + 1) % memSize;
                        break;
                    }
                    count = (count + 1) % memSize;
                }
            } else {
                bw.write(" ");
            }

            for (int i = 0; i < memory.size(); i++) {
                if (memory.get(i) == -1) {
                    bw.write("* ");
                } else {
                    bw.write(memory.get(i) + " ");
                }
            }

            bw.write("]");
            bw.newLine();
            time += t1;
        }

        bw.write("Number of faults: " + fault);
        bw.newLine();
        bw.write("Time elapsed: " + time);
        bw.newLine();

        br.close();
        bw.close();
    }
}

package Prog2;

import java.util.*;
import java.io.*;

public class ClockPageReplacement {
    public static void main(String[] args) throws IOException {
        int numPageFrames = Integer.parseInt(args[0]);
        int timeSwapIn = Integer.parseInt(args[1]);
        int timeSwapOut = Integer.parseInt(args[2]);
        String inputFile = args[3];
        String outputFile = args[4];

        List<Integer> pageReferences = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            pageReferences.add(Integer.parseInt(line.trim()));
        }
        reader.close();

        List<Integer> memory = new ArrayList<>();
        List<Boolean> referenced = new ArrayList<>();
        List<Boolean> modified = new ArrayList<>();
        int pointer = 0;
        int pageFaults = 0;
        int swapInCosts = 0;
        int swapOutCosts = 0;
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (int pageReference : pageReferences) {
            int index = memory.indexOf(pageReference);
            if (index == -1) {
                while (referenced.get(pointer)) {
                    referenced.set(pointer, false);
                    pointer = (pointer + 1) % numPageFrames;
                }
                if (memory.size() < numPageFrames) {
                    memory.add(pageReference);
                    referenced.add(true);
                    modified.add(false);
                    swapInCosts += timeSwapIn;
                } else {
                    index = pointer;
                    while (modified.get(index)) {
                        modified.set(index, false);
                        referenced.set(index, false);
                        pointer = (pointer + 1) % numPageFrames;
                        index = pointer;
                    }
                    swapOutCosts += timeSwapOut;
                    memory.set(index, pageReference);
                    referenced.set(index, true);
                    modified.set(index, false);
                    swapInCosts += timeSwapIn;
                }
                pageFaults++;
            } else {
                referenced.set(index, true);
            }
            writer.write(pageReference + " ");
            for (int i = 0; i < memory.size(); i++) {
                writer.write(memory.get(i) + " ");
                if (referenced.get(i)) {
                    writer.write("R");
                } else {
                    writer.write("-");
                }
                if (modified.get(i)) {
                    writer.write("M ");
                } else {
                    writer.write("- ");
                }
            }
            writer.write("\n");
        }
        writer.close();

        System.out.println("Number of page faults: " + pageFaults);
        System.out.println("Time cost for swap-in: " + swapInCosts);
        System.out.println("Time cost for swap-out: " + swapOutCosts);
    }
}


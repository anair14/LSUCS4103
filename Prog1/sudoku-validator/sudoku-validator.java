import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuValidator {
    private static final int THREAD_POOL_SIZE = 3;
    private static final int SUBGRID_SIZE = 3;
    private static final int GRID_SIZE = SUBGRID_SIZE * SUBGRID_SIZE;
    private static final int[] VALID_VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("[!] Usage: java SudokuValidator <input_file>")
            System.exit(1);
        }
        int[][] grid = readGridFromFile(args[0]);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for (int i = 0; i < GRID_SIZE; i++) {
            executor.execute(new RowValidator(grid, i));
            executor.execute(new ColumnValidator(grid, i));
            executor.execute(new SubgridValidator(grid, i));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            // This space is so all tasks are given time to stop
        }
        int validRows = 0;
        int validColumns = 0;
        int validSubgrids = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (RowValidator.isValid(grid, i)) {
                System.out.printf("[Thread %d] Row %d: Valid%n", Thread.currentThread().getId(), i + 1);
            } else {
                System.out.printf("[Thread %d] Row %d: Invalid%n", Thread.currentThread().getId(), i + 1);
            }
            if (ColumnValidator.isValid(grid, i)) {
                System.out.printf("[Thread %d] Column %d: Valid%n", Thread.currentThread().getId(), i + 1);
            } else {
                System.out.printf("[Thread %d] Column %d: Invalid%n", Thread.currentThread().getId(), i + 1);
            }
            if (SubgridValidator.isValid(grid, i)) {
                System.out.printf("[Thread %d] Subgrid R%dC%d: Valid%n", Thread.currentThread().getId(), 
                    SUBGRID_SIZE * (i / SUBGRID_SIZE) + 1, SUBGRID_SIZE * (i % SUBGRID_SIZE) + 1);
            } else {
                System.out.printf("[Thread %d] Subgrid R%dC%d: Invalid%n", Thread.currentThread.getId(),
                    SUBGRID_SIZE * (i / SUBGRID_SIZE) + 1, SUBGRID_SIZE * (i % SUBGRID_SIZE) + 1);
            }
        }

        System.out.printf("Valid rows: %d%n", validRows);
        System.out.printf("Valid columns: %d%n", validColumns);
        System.out.printf("Valid subgrids: %d%n", validSubgrids);

        if (validRows == GRID_SIZE && validColumns == GRID_SIZE && validSubgrids == GRID_SIZE) {
            System.out.println("This sudoku solution is: Valid!");
        } else {
            System.out.println("This sudoku solution is: Invalid!");
        }

    }

    private static int[][] readGridFromFile(String filename) throws IOException {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int row = 0
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\s+");
                if (values.length != GRID_SIZE) {
                    throw new IllegalArgumentException("Invalid input file format: expected" + GRID_SIZE + " values per row");
                }
                for (int col = 0; col < GRID_SIZE; col++) {
                    int value = Integer.parseInt(values[col]);
                    if (value < 1 || value > 9) {
                        throw new IllegalArgumentException("Invalid input file format: expected values between 1 and 9");
                    }
                }
                row++;
            }
            if (row != GRID_SIZE) {
                throw new IllegalArgumentException("Invalid input file format: expected " + GRID_SIZE + " rows");
            }

        }
        return grid;
    }

    private static class RowValidator implements Runnable {

        private final int[][] grid; 
        private final int row;

        RowValidator(int[][] grid, int row) {
            this.grid = grid;
            this.row = row;
        }

        @Override
        public void run() {
            if (!isValid(grid, row)) {
                System.out.printf("[Thread %d] Row %d: Invalid%n", Thread.currentThread().getId(), row + 1);
            }
        }

        static boolean isValid(int[][] grid, int row) {
            int[] values = grid[row];
            return containsAllValidValues(values);
        }
    }

    private static class ColumnValidator implements Runnable {
        // continue here.
    }



    
} // eof
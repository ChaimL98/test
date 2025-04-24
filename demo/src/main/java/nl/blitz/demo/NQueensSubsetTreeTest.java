package nl.blitz.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class NQueensSubsetTreeTest {
    public static void main(String[] args) {
        // Test with different board sizes
        List<Integer> boardSizes = Arrays.asList(4, 5, 6, 7, 8);
        
        for (int size : boardSizes) {
            testNQueens(size);
        }
    }

    private static void testNQueens(int boardSize) {
        System.out.println("\nTesting " + boardSize + "x" + boardSize + " board...");
        
        // Create N-Queens solver
        NQueensSubsetTree nQueens = new NQueensSubsetTree(boardSize);
        
        try {
            // Get the current working directory
            Path currentPath = Paths.get("").toAbsolutePath();
            
            // Create output directory if it doesn't exist
            Path outputDir = currentPath.resolve("output");
            if (!outputDir.toFile().exists()) {
                System.out.println("Creating output directory: " + outputDir);
                Files.createDirectories(outputDir);
            }
            
            // Create PDF filename
            String pdfFilename = "n_queens_" + boardSize + "x" + boardSize + ".pdf";
            String pdfPath = outputDir.resolve(pdfFilename).toString();
            
            // Check if file exists and delete it
            Path pdfFilePath = Paths.get(pdfPath);
            if (Files.exists(pdfFilePath)) {
                System.out.println("Existing PDF file found, deleting...");
                Files.delete(pdfFilePath);
            }
            
            // Generate PDF
            System.out.println("Generating PDF for " + boardSize + "x" + boardSize + " board...");
            nQueens.saveSolutionsToPDF(pdfPath);
            
            // Verify and print results
            if (Files.exists(pdfFilePath)) {
                System.out.println("Successfully generated PDF: " + pdfFilename);
                System.out.println("File size: " + Files.size(pdfFilePath) + " bytes");
                
                // Print statistics
                int totalSolutions = nQueens.getSolutionCount();
                System.out.println("Number of solutions found: " + totalSolutions);
                
                // Print expected number of solutions (known values for small boards)
                int expectedSolutions = getExpectedSolutions(boardSize);
                if (expectedSolutions > 0) {
                    System.out.println("Expected number of solutions: " + expectedSolutions);
                    if (totalSolutions == expectedSolutions) {
                        System.out.println("✓ All solutions found!");
                    } else {
                        System.out.println("✗ Some solutions might be missing!");
                    }
                }
            } else {
                System.err.println("Error: Failed to generate PDF for " + boardSize + "x" + boardSize + " board!");
            }
            
        } catch (IOException e) {
            System.err.println("Error processing " + boardSize + "x" + boardSize + " board: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Returns known number of solutions for small boards
    private static int getExpectedSolutions(int boardSize) {
        switch (boardSize) {
            case 4: return 2;
            case 5: return 10;
            case 6: return 4;
            case 7: return 40;
            case 8: return 92;
            default: return -1; // Unknown for larger boards
        }
    }
} 
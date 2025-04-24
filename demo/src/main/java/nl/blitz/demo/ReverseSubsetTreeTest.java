package nl.blitz.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ReverseSubsetTreeTest {
    public static void main(String[] args) {
        // Generate trees for sizes 3, 4, 5, and 6
        for (int size = 3; size <= 6; size++) {
            generateReverseSubsetTree(size);
        }
    }

    private static void generateReverseSubsetTree(int size) {
        // Create a list of distinct numbers based on size
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6).subList(0, size);
        
        // Create a subset tree
        SubsetTree subsetTree = new SubsetTree(numbers);
        
        try {
            // Get the current working directory
            Path currentPath = Paths.get("").toAbsolutePath();
            System.out.println("Current working directory: " + currentPath);
            
            // Create output directory if it doesn't exist
            Path outputDir = currentPath.resolve("output");
            if (!outputDir.toFile().exists()) {
                System.out.println("Creating output directory: " + outputDir);
                Files.createDirectories(outputDir);
            }
            
            String pdfPath = outputDir.resolve("reverse_subset_tree_size_" + size + ".pdf").toString();
            System.out.println("Attempting to save PDF to: " + pdfPath);
            
            // Check if file exists and delete it
            Path pdfFilePath = Paths.get(pdfPath);
            if (Files.exists(pdfFilePath)) {
                System.out.println("Existing PDF file found, deleting...");
                Files.delete(pdfFilePath);
                System.out.println("Existing PDF file deleted.");
            }
            
            // Save the tree to a PDF file
            System.out.println("Generating PDF for size " + size + "...");
            subsetTree.saveReverseTreeToPDF(pdfPath);
            System.out.println("PDF generation completed for size " + size + ".");
            
            // Verify the file was created
            if (Files.exists(pdfFilePath)) {
                System.out.println("Successfully saved PDF to: " + pdfPath);
                System.out.println("File size: " + Files.size(pdfFilePath) + " bytes");
            } else {
                System.err.println("Error: PDF file was not created for size " + size + "!");
            }
            
            // Print statistics for this size
            System.out.println("\nStatistics for size " + size + ":");
            int totalSubsets = subsetTree.getSubsetCount();
            System.out.println("Total number of subsets: " + totalSubsets);
            
            // Calculate expected number of subsets (2^n where n is number of elements)
            int expectedSubsets = (int) Math.pow(2, numbers.size());
            System.out.println("Expected number of subsets (2^" + numbers.size() + "): " + expectedSubsets);
            
            // Verify we have all subsets
            if (totalSubsets == expectedSubsets) {
                System.out.println("✓ All subsets generated successfully for size " + size + "!");
            } else {
                System.err.println("✗ Missing some subsets for size " + size + "! Expected " + expectedSubsets + ", got " + totalSubsets);
            }
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
        } catch (IOException e) {
            System.err.println("Error saving tree to PDF for size " + size + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
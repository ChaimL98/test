package nl.blitz.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ColorPermutationTreeTest {
    public static void main(String[] args) {
        // Create a list of 4 distinct colors
        List<String> colors = Arrays.asList("Red", "Green", "Blue", "Yellow");
        
        // Create a color subset tree
        ColorPermutationTree colorTree = new ColorPermutationTree(colors);
        
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
            
            String pdfPath = outputDir.resolve("color_subset_tree.pdf").toString();
            System.out.println("Attempting to save PDF to: " + pdfPath);
            
            // Check if file exists and delete it
            Path pdfFilePath = Paths.get(pdfPath);
            if (Files.exists(pdfFilePath)) {
                System.out.println("Existing PDF file found, deleting...");
                Files.delete(pdfFilePath);
                System.out.println("Existing PDF file deleted.");
            }
            
            // Save the tree to a PDF file
            System.out.println("Generating PDF...");
            colorTree.saveTreeToPDF(pdfPath);
            System.out.println("PDF generation completed.");
            
            // Verify the file was created
            if (Files.exists(pdfFilePath)) {
                System.out.println("Successfully saved PDF to: " + pdfPath);
                System.out.println("File size: " + Files.size(pdfFilePath) + " bytes");
            } else {
                System.err.println("Error: PDF file was not created!");
            }
        } catch (IOException e) {
            System.err.println("Error saving tree to PDF: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Print the tree structure
        colorTree.printTree();
        
        // Print all subsets
        colorTree.printSubsets();
        
        // Print statistics
        System.out.println("\nStatistics:");
        int totalSubsets = colorTree.getSubsetCount();
        System.out.println("Total number of subsets: " + totalSubsets);
        
        // Calculate expected number of subsets (2^n where n is number of colors)
        int expectedSubsets = (int) Math.pow(2, colors.size());
        System.out.println("Expected number of subsets (2^" + colors.size() + "): " + expectedSubsets);
        
        // Verify we have all subsets
        if (totalSubsets == expectedSubsets) {
            System.out.println("✓ All subsets generated successfully!");
        } else {
            System.err.println("✗ Missing some subsets! Expected " + expectedSubsets + ", got " + totalSubsets);
        }
    }
} 
package nl.blitz.demo;

import java.util.Arrays;
import java.util.List;

public class BinaryTreeSubsetTest {
    public static void main(String[] args) {
        // Create a list of 4 distinct numbers
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        
        // Create a subset tree
        SubsetTree subsetTree = new SubsetTree(numbers);
        
        // Print the tree structure
        subsetTree.printTree();
        
        // Print all subsets
        subsetTree.printSubsets();
        
        // Print statistics
        System.out.println("\nStatistics:");
        int totalSubsets = subsetTree.getSubsetCount();
        System.out.println("Total number of subsets: " + totalSubsets);
        
        // Calculate expected number of subsets (2^n where n is number of elements)
        int expectedSubsets = (int) Math.pow(2, numbers.size());
        System.out.println("Expected number of subsets (2^" + numbers.size() + "): " + expectedSubsets);
        
        // Verify we have all subsets
        if (totalSubsets == expectedSubsets) {
            System.out.println("✓ All subsets generated successfully!");
        } else {
            System.err.println("✗ Missing some subsets! Expected " + expectedSubsets + ", got " + totalSubsets);
        }
    }
} 
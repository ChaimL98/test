package nl.blitz.demo;

import java.util.Arrays;
import java.util.List;

public class BinaryTreeSubsetTest {
    public static void main(String[] args) {
        // Create a list of elements
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        
        // Create a subset tree
        SubsetTree subsetTree = new SubsetTree(elements);
        
        // Print the tree structure
        subsetTree.printTree();
        
        // Print all traversals
        subsetTree.printTraversals();
        
        // Print subset mapping
        subsetTree.printSubsetMapping();
        
        // Search for some subsets
        System.out.println("\nSearch results:");
        System.out.println("Search for {A, B}: " + subsetTree.searchSubset("{A, B}"));
        System.out.println("Search for {B, C, D}: " + subsetTree.searchSubset("{B, C, D}"));
        System.out.println("Search for {A, C}: " + subsetTree.searchSubset("{A, C}"));
        System.out.println("Search for {A, B, C, D}: " + subsetTree.searchSubset("{A, B, C, D}"));
        System.out.println("Search for {X}: " + subsetTree.searchSubset("{X}")); // Should return false
    }
} 
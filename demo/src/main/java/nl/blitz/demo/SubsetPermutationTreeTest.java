package nl.blitz.demo;

import java.util.Arrays;
import java.util.List;

public class SubsetPermutationTreeTest {
    public static void main(String[] args) {
        // Create a list of elements
        List<String> elements = Arrays.asList("A", "B");
        
        // Create a subset-permutation tree
        SubsetPermutationTree tree = new SubsetPermutationTree(elements);
        
        // Print the tree structure
        tree.printTree();
        
        // Print all traversals
        tree.printTraversals();
        
        // Print result mapping
        tree.printResultMapping();
        
        // Search for some results
        System.out.println("\nSearch results:");
        System.out.println("Search for {}: " + tree.searchResult("{}"));
        System.out.println("Search for {A}: " + tree.searchResult("{A}"));
        System.out.println("Search for {B}: " + tree.searchResult("{B}"));
        System.out.println("Search for {A, B}: " + tree.searchResult("{A, B}"));
        System.out.println("Search for {B, A}: " + tree.searchResult("{B, A}"));
        System.out.println("Search for {X}: " + tree.searchResult("{X}")); // Should return false
    }
} 
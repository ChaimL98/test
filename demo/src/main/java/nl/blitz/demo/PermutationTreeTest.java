package nl.blitz.demo;

import java.util.Arrays;
import java.util.List;

public class PermutationTreeTest {
    public static void main(String[] args) {
        // Create a list of numbers
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        
        // Create a permutation tree
        PermutationTree<Integer> permutationTree = new PermutationTree<>(numbers);
        
        // Print the tree structure (might be very large, so we'll skip it)
        // permutationTree.printTree();
        
        // Print all traversals
        permutationTree.printTraversals();
        
        // Print permutation mapping
        permutationTree.printPermutationMapping();
        
        // Search for some specific permutations
        System.out.println("\nSearch results:");
        System.out.println("Search for [1, 2, 3, 4]: " + 
            permutationTree.searchPermutation("[1, 2, 3, 4]"));
        System.out.println("Search for [3, 1, 2, 4]: " + 
            permutationTree.searchPermutation("[3, 1, 2, 4]"));
        System.out.println("Search for [5, 6, 1, 2]: " + 
            permutationTree.searchPermutation("[5, 6, 1, 2]"));
        System.out.println("Search for [4, 5, 6, 1]: " + 
            permutationTree.searchPermutation("[4, 5, 6, 1]"));
        
        // Print some statistics
        System.out.println("\nStatistics:");
        System.out.println("Total number of permutations: " + permutationTree.getPermutationCount());
        System.out.println("Expected number of permutations (6P4): " + (6*5*4*3));
    }
} 
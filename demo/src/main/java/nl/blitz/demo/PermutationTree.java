package nl.blitz.demo;

import java.util.ArrayList;
import java.util.List;

public class PermutationTree<T> {
    private Node root;
    private List<T> elements;
    private List<String> permutationMapping;

    private static class Node {
        String label;
        Node[] children;  // Each child represents a possible next element
        boolean isLeaf;

        public Node(String label, boolean isLeaf, int numChildren) {
            this.label = label;
            this.children = new Node[numChildren];
            this.isLeaf = isLeaf;
        }
    }

    public PermutationTree(List<T> elements) {
        this.elements = elements;
        this.permutationMapping = new ArrayList<>();
        this.root = buildPermutationTree(new ArrayList<>(), elements);
        generateMapping();
    }

    private Node buildPermutationTree(List<T> currentPermutation, List<T> remainingElements) {
        if (remainingElements.isEmpty()) {
            return new Node(formatPermutation(currentPermutation), true, 0);
        }

        // Create a decision node
        Node node = new Node("Choose next element", false, remainingElements.size());
        
        // Create a child for each remaining element
        for (int i = 0; i < remainingElements.size(); i++) {
            List<T> newPermutation = new ArrayList<>(currentPermutation);
            List<T> newRemaining = new ArrayList<>(remainingElements);
            
            // Add the chosen element to the permutation
            T chosenElement = newRemaining.remove(i);
            newPermutation.add(chosenElement);
            
            // Create child node
            node.children[i] = buildPermutationTree(newPermutation, newRemaining);
        }
        
        return node;
    }

    private String formatPermutation(List<T> permutation) {
        if (permutation.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < permutation.size(); i++) {
            sb.append(permutation.get(i));
            if (i < permutation.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void generateMapping() {
        traverseAndMap(root);
    }

    private void traverseAndMap(Node node) {
        if (node != null) {
            if (node.isLeaf) {
                permutationMapping.add(node.label);
            }
            for (Node child : node.children) {
                traverseAndMap(child);
            }
        }
    }

    public void printTree() {
        System.out.println("Permutation Tree:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLast) {
        if (node != null) {
            String nodeLabel = node.isLeaf ? 
                "Permutation: " + node.label : 
                node.label;
            System.out.println(prefix + (isLast ? "\\-- " : "+-- ") + nodeLabel);
            
            String newPrefix = prefix + (isLast ? "    " : "|   ");
            for (int i = 0; i < node.children.length; i++) {
                printTreeRecursive(node.children[i], newPrefix, i == node.children.length - 1);
            }
        }
    }

    public void printTraversals() {
        System.out.println("\nPermutation Traversals (only showing actual permutations):");
        System.out.print("Preorder: ");
        preorderTraversal(root);
        System.out.println();
        System.out.print("Postorder: ");
        postorderTraversal(root);
        System.out.println();
    }

    private void preorderTraversal(Node node) {
        if (node != null) {
            if (node.isLeaf) {
                System.out.print(node.label + " ");
            }
            for (Node child : node.children) {
                preorderTraversal(child);
            }
        }
    }

    private void postorderTraversal(Node node) {
        if (node != null) {
            for (Node child : node.children) {
                postorderTraversal(child);
            }
            if (node.isLeaf) {
                System.out.print(node.label + " ");
            }
        }
    }

    public void printPermutationMapping() {
        System.out.println("\nPermutation Mapping:");
        for (int i = 0; i < permutationMapping.size(); i++) {
            System.out.println(i + ": " + permutationMapping.get(i));
        }
    }

    public int getPermutationCount() {
        return permutationMapping.size();
    }

    public boolean searchPermutation(String permutation) {
        return searchPermutationRecursive(root, permutation);
    }

    private boolean searchPermutationRecursive(Node node, String permutation) {
        if (node == null) {
            return false;
        }
        if (node.isLeaf && node.label.equals(permutation)) {
            return true;
        }
        for (Node child : node.children) {
            if (searchPermutationRecursive(child, permutation)) {
                return true;
            }
        }
        return false;
    }
} 
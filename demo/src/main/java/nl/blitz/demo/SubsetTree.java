package nl.blitz.demo;

import java.util.ArrayList;
import java.util.List;

public class SubsetTree {
    private Node root;
    private List<String> elements;
    private List<String> subsetMapping;

    private static class Node {
        String label;
        Node left;  // Include
        Node right; // Exclude
        boolean isLeaf;

        public Node(String label, boolean isLeaf) {
            this.label = label;
            this.left = null;
            this.right = null;
            this.isLeaf = isLeaf;
        }
    }

    public SubsetTree(List<String> elements) {
        this.elements = elements;
        this.subsetMapping = new ArrayList<>();
        this.root = buildSubsetTree(0, new ArrayList<>());
        generateMapping();
    }

    private Node buildSubsetTree(int index, List<String> currentSubset) {
        if (index == elements.size()) {
            return new Node(formatSubset(currentSubset), true);
        }

        // Create a decision node for the current element
        Node node = new Node("Decide: " + elements.get(index), false);
        
        // Left branch: include current element
        List<String> includeSubset = new ArrayList<>(currentSubset);
        includeSubset.add(elements.get(index));
        node.left = buildSubsetTree(index + 1, includeSubset);
        
        // Right branch: exclude current element
        node.right = buildSubsetTree(index + 1, new ArrayList<>(currentSubset));
        
        return node;
    }

    private String formatSubset(List<String> subset) {
        if (subset.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(", ", subset) + "}";
    }

    private void generateMapping() {
        traverseAndMap(root);
    }

    private void traverseAndMap(Node node) {
        if (node != null) {
            if (node.isLeaf) {
                subsetMapping.add(node.label);
            }
            traverseAndMap(node.left);
            traverseAndMap(node.right);
        }
    }

    public void printTree() {
        System.out.println("Binary Tree of All Subsets:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLeft) {
        if (node != null) {
            String nodeLabel = node.isLeaf ? 
                "Subset: " + node.label : 
                node.label;
            System.out.println(prefix + (isLeft ? "+-- " : "\\-- ") + nodeLabel);
            printTreeRecursive(node.left, prefix + (isLeft ? "|   " : "    "), true);
            printTreeRecursive(node.right, prefix + (isLeft ? "|   " : "    "), false);
        }
    }

    public void printTraversals() {
        System.out.println("\nSubset Traversals (only showing actual subsets):");
        System.out.print("Preorder: ");
        preorderTraversal(root);
        System.out.println();
        System.out.print("Inorder: ");
        inorderTraversal(root);
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
            preorderTraversal(node.left);
            preorderTraversal(node.right);
        }
    }

    private void inorderTraversal(Node node) {
        if (node != null) {
            inorderTraversal(node.left);
            if (node.isLeaf) {
                System.out.print(node.label + " ");
            }
            inorderTraversal(node.right);
        }
    }

    private void postorderTraversal(Node node) {
        if (node != null) {
            postorderTraversal(node.left);
            postorderTraversal(node.right);
            if (node.isLeaf) {
                System.out.print(node.label + " ");
            }
        }
    }

    public void printSubsetMapping() {
        System.out.println("\nSubset Mapping:");
        for (int i = 0; i < subsetMapping.size(); i++) {
            System.out.println(i + ": " + subsetMapping.get(i));
        }
    }

    public boolean searchSubset(String subset) {
        return searchSubsetRecursive(root, subset);
    }

    private boolean searchSubsetRecursive(Node node, String subset) {
        if (node == null) {
            return false;
        }
        if (node.isLeaf && node.label.equals(subset)) {
            return true;
        }
        return searchSubsetRecursive(node.left, subset) || 
               searchSubsetRecursive(node.right, subset);
    }
} 
package nl.blitz.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubsetPermutationTree {
    private Node root;
    private List<String> elements;
    private List<String> resultMapping;
    private Set<String> uniqueResults;

    private static class Node {
        String label;
        Node include;    // Branch for including the current element
        Node exclude;    // Branch for excluding the current element
        Node[] position; // If included, branches for different positions
        boolean isLeaf;

        public Node(String label, boolean isLeaf, int numPositions) {
            this.label = label;
            this.include = null;
            this.exclude = null;
            this.position = numPositions > 0 ? new Node[numPositions] : null;
            this.isLeaf = isLeaf;
        }
    }

    public SubsetPermutationTree(List<String> elements) {
        this.elements = elements;
        this.resultMapping = new ArrayList<>();
        this.uniqueResults = new HashSet<>();
        this.root = buildTree(0, new ArrayList<>());
        generateMapping();
    }

    private Node buildTree(int index, List<String> currentResult) {
        if (index == elements.size()) {
            String result = formatResult(currentResult);
            // Only create a new leaf node if this result hasn't been seen before
            if (!uniqueResults.contains(result)) {
                uniqueResults.add(result);
                return new Node(result, true, 0);
            }
            return null; // Skip duplicate results
        }

        // Create a decision node
        Node node = new Node("Decide: " + elements.get(index), false, 0);
        
        // Exclude branch
        node.exclude = buildTree(index + 1, new ArrayList<>(currentResult));
        
        // Include branch with position choices
        node.include = new Node("Choose position for " + elements.get(index), false, currentResult.size() + 1);
        
        // Create position choices
        for (int pos = 0; pos <= currentResult.size(); pos++) {
            List<String> newResult = new ArrayList<>(currentResult);
            newResult.add(pos, elements.get(index));
            node.include.position[pos] = buildTree(index + 1, newResult);
        }
        
        // If both branches are null, return null to prune the tree
        if (node.exclude == null && node.include == null) {
            return null;
        }
        
        return node;
    }

    private String formatResult(List<String> result) {
        if (result.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(", ", result) + "}";
    }

    private void generateMapping() {
        traverseAndMap(root);
    }

    private void traverseAndMap(Node node) {
        if (node != null) {
            if (node.isLeaf) {
                resultMapping.add(node.label);
            }
            if (node.include != null) {
                traverseAndMap(node.include);
            }
            if (node.exclude != null) {
                traverseAndMap(node.exclude);
            }
            if (node.position != null) {
                for (Node posNode : node.position) {
                    traverseAndMap(posNode);
                }
            }
        }
    }

    public void printTree() {
        System.out.println("Subset-Permutation Tree:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLast) {
        if (node != null) {
            String nodeLabel = node.isLeaf ? 
                "Result: " + node.label : 
                node.label;
            System.out.println(prefix + (isLast ? "\\-- " : "+-- ") + nodeLabel);
            
            String newPrefix = prefix + (isLast ? "    " : "|   ");
            
            // Print include branch
            if (node.include != null) {
                printTreeRecursive(node.include, newPrefix, false);
            }
            
            // Print exclude branch
            if (node.exclude != null) {
                printTreeRecursive(node.exclude, newPrefix, true);
            }
            
            // Print position choices
            if (node.position != null) {
                for (int i = 0; i < node.position.length; i++) {
                    printTreeRecursive(node.position[i], newPrefix, i == node.position.length - 1);
                }
            }
        }
    }

    public void printTraversals() {
        System.out.println("\nResult Traversals (only showing actual results):");
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
            if (node.include != null) {
                preorderTraversal(node.include);
            }
            if (node.exclude != null) {
                preorderTraversal(node.exclude);
            }
            if (node.position != null) {
                for (Node posNode : node.position) {
                    preorderTraversal(posNode);
                }
            }
        }
    }

    private void postorderTraversal(Node node) {
        if (node != null) {
            if (node.include != null) {
                postorderTraversal(node.include);
            }
            if (node.exclude != null) {
                postorderTraversal(node.exclude);
            }
            if (node.position != null) {
                for (Node posNode : node.position) {
                    postorderTraversal(posNode);
                }
            }
            if (node.isLeaf) {
                System.out.print(node.label + " ");
            }
        }
    }

    public void printResultMapping() {
        System.out.println("\nResult Mapping:");
        for (int i = 0; i < resultMapping.size(); i++) {
            System.out.println(i + ": " + resultMapping.get(i));
        }
    }

    public boolean searchResult(String result) {
        return searchResultRecursive(root, result);
    }

    private boolean searchResultRecursive(Node node, String result) {
        if (node == null) {
            return false;
        }
        if (node.isLeaf && node.label.equals(result)) {
            return true;
        }
        if (node.include != null && searchResultRecursive(node.include, result)) {
            return true;
        }
        if (node.exclude != null && searchResultRecursive(node.exclude, result)) {
            return true;
        }
        if (node.position != null) {
            for (Node posNode : node.position) {
                if (searchResultRecursive(posNode, result)) {
                    return true;
                }
            }
        }
        return false;
    }
} 
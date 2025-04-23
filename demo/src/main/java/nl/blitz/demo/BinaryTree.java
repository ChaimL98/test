package nl.blitz.demo;

public class BinaryTree {
    private Node root;

    private static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public BinaryTree() {
        this.root = null;
    }

    public void insert(int value) {
        root = insertRecursive(root, value);
    }

    private Node insertRecursive(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }

        if (value < current.value) {
            current.left = insertRecursive(current.left, value);
        } else if (value > current.value) {
            current.right = insertRecursive(current.right, value);
        }

        return current;
    }

    public boolean search(int value) {
        return searchRecursive(root, value);
    }

    private boolean searchRecursive(Node current, int value) {
        if (current == null) {
            return false;
        }

        if (value == current.value) {
            return true;
        }

        return value < current.value
                ? searchRecursive(current.left, value)
                : searchRecursive(current.right, value);
    }

    // Inorder Traversal (Left, Root, Right)
    public void inorderTraversal() {
        System.out.print("Inorder Traversal: ");
        inorderTraversalRecursive(root);
        System.out.println();
    }

    private void inorderTraversalRecursive(Node node) {
        if (node != null) {
            inorderTraversalRecursive(node.left);
            System.out.print(node.value + " ");
            inorderTraversalRecursive(node.right);
        }
    }

    // Preorder Traversal (Root, Left, Right)
    public void preorderTraversal() {
        System.out.print("Preorder Traversal: ");
        preorderTraversalRecursive(root);
        System.out.println();
    }

    private void preorderTraversalRecursive(Node node) {
        if (node != null) {
            System.out.print(node.value + " ");
            preorderTraversalRecursive(node.left);
            preorderTraversalRecursive(node.right);
        }
    }

    // Postorder Traversal (Left, Right, Root)
    public void postorderTraversal() {
        System.out.print("Postorder Traversal: ");
        postorderTraversalRecursive(root);
        System.out.println();
    }

    private void postorderTraversalRecursive(Node node) {
        if (node != null) {
            postorderTraversalRecursive(node.left);
            postorderTraversalRecursive(node.right);
            System.out.print(node.value + " ");
        }
    }

    // Tree Structure Printing
    public void printTree() {
        System.out.println("\nTree Structure:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "+-- " : "\\-- ") + node.value);
            printTreeRecursive(node.left, prefix + (isLeft ? "|   " : "    "), true);
            printTreeRecursive(node.right, prefix + (isLeft ? "|   " : "    "), false);
        }
    }
} 
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

    // Visual Tree Printing
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

    // Traditional ASCII Tree Printing
    public void printTraditionalTree() {
        if (root == null) {
            System.out.println("Tree is empty");
            return;
        }

        System.out.println("\nTraditional Tree View:");
        int height = getHeight(root);
        // Calculate dimensions with appropriate spacing
        int width = (int) Math.pow(2, height) * 3 - 1;
        char[][] treeArray = new char[height * 2][width];
        
        // Initialize array with spaces
        for (int i = 0; i < treeArray.length; i++) {
            for (int j = 0; j < treeArray[i].length; j++) {
                treeArray[i][j] = ' ';
            }
        }
        
        // Fill the array with tree structure
        fillTreeArray(root, treeArray, 0, width / 2, width / 4);
        
        // Print the tree
        for (char[] row : treeArray) {
            System.out.println(new String(row));
        }
    }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private void fillTreeArray(Node node, char[][] treeArray, int row, int col, int offset) {
        if (node == null || row >= treeArray.length || col < 0 || col >= treeArray[0].length) {
            return;
        }

        // Place the node value
        String value = String.valueOf(node.value);
        int startCol = col - (value.length() - 1) / 2;
        for (int i = 0; i < value.length() && startCol + i < treeArray[0].length; i++) {
            if (startCol + i >= 0) {
                treeArray[row][startCol + i] = value.charAt(i);
            }
        }

        // Draw branches and children
        if (node.left != null) {
            // Draw left branch
            for (int i = 1; i <= offset / 2 && row + i < treeArray.length; i++) {
                int branchCol = col - i;
                if (branchCol >= 0) {
                    treeArray[row + i][branchCol] = '/';
                }
            }
            fillTreeArray(node.left, treeArray, row + 2, col - offset, offset / 2);
        }

        if (node.right != null) {
            // Draw right branch
            for (int i = 1; i <= offset / 2 && row + i < treeArray.length; i++) {
                int branchCol = col + i;
                if (branchCol < treeArray[0].length) {
                    treeArray[row + i][branchCol] = '\\';
                }
            }
            fillTreeArray(node.right, treeArray, row + 2, col + offset, offset / 2);
        }
    }
} 
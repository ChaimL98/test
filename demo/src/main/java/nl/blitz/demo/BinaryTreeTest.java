package nl.blitz.demo;

public class BinaryTreeTest {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        // Insert some values
        tree.insert(50);
        tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(45);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);

        // Print the tree structure
        tree.printTree();

        // Print all traversals
        System.out.println("\nTree Traversals:");
        tree.preorderTraversal();   // Root, Left, Right
        tree.inorderTraversal();    // Left, Root, Right
        tree.postorderTraversal();  // Left, Right, Root

        // Search for values
        System.out.println("\nSearch results:");
        System.out.println("Search for 40: " + tree.search(40));
        System.out.println("Search for 90: " + tree.search(90));
        System.out.println("Search for 20: " + tree.search(20));
    }
} 
package nl.blitz.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class SubsetTree {
    private Node root;
    private List<Integer> elements;
    private List<String> subsetMapping;
    private static final float INITIAL_HORIZONTAL_SPACING = 80f; // Longer initial branches
    private static final float HORIZONTAL_DECREASE_FACTOR = 0.7f; // How much to decrease horizontal spacing
    private static final float MIN_HORIZONTAL_SPACING = 30f; // Minimum horizontal spacing
    private static final float VERTICAL_SPACING = 50f;
    private static final float PAGE_MARGIN = 30f;
    private static final float FONT_SIZE = 12f;
    private static final float MIN_BRANCH_SPACING = 40f;
    private static final float LEAF_SPACING = 30f;
    private static final float SPACING_DECREASE_FACTOR = 0.6f;
    private static final float MIN_SPACING = 15f;

    private static class Node {
        Node[] children;
        boolean isLeaf;
        List<Integer> subset;
        Integer chosenElement;
        boolean isIncluded;

        public Node(boolean isLeaf, int numChildren) {
            this.children = new Node[numChildren];
            this.isLeaf = isLeaf;
            this.subset = new ArrayList<>();
            this.chosenElement = null;
            this.isIncluded = false;
        }
    }

    public SubsetTree(List<Integer> elements) {
        this.elements = elements;
        this.subsetMapping = new ArrayList<>();
        this.root = buildSubsetTree(new ArrayList<>(), 0);
        generateMapping();
    }

    private Node buildSubsetTree(List<Integer> currentSubset, int currentIndex) {
        if (currentIndex >= elements.size()) {
            Node leaf = new Node(true, 0);
            leaf.subset.addAll(currentSubset);
            return leaf;
        }

        Node node = new Node(false, 2);
        node.chosenElement = elements.get(currentIndex);

        node.children[0] = buildSubsetTree(currentSubset, currentIndex + 1);
        node.children[0].isIncluded = false;

        List<Integer> newSubset = new ArrayList<>(currentSubset);
        newSubset.add(elements.get(currentIndex));
        node.children[1] = buildSubsetTree(newSubset, currentIndex + 1);
        node.children[1].isIncluded = true;

        return node;
    }

    private void generateMapping() {
        subsetMapping.clear();
        traverseAndMap(root, new ArrayList<>());
    }

    private void traverseAndMap(Node node, List<Integer> currentSubset) {
        if (node != null) {
            if (node.isLeaf) {
                subsetMapping.add(formatSubset(currentSubset));
            }
            for (Node child : node.children) {
                List<Integer> newSubset = new ArrayList<>(currentSubset);
                if (child.isIncluded) {
                    newSubset.add(child.chosenElement);
                }
                traverseAndMap(child, newSubset);
            }
        }
    }

    private String formatSubset(List<Integer> subset) {
        if (subset.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(", ", subset.stream().map(String::valueOf).toList()) + "}";
    }

    public void saveTreeToPDF(String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            
            float pageHeight = page.getMediaBox().getHeight();
            float startX = PAGE_MARGIN;
            float startY = pageHeight / 2;
            
            drawTreeToPDF(contentStream, root, startX, startY, INITIAL_HORIZONTAL_SPACING, calculateTotalHeight(root), 0);
            
            contentStream.close();
            document.save(filename);
        }
    }

    private float calculateTotalHeight(Node node) {
        if (node == null) return 0;
        if (node.isLeaf) return LEAF_SPACING;
        return Math.max(calculateTotalHeight(node.children[0]), calculateTotalHeight(node.children[1])) + MIN_BRANCH_SPACING;
    }

    private void drawTreeToPDF(PDPageContentStream contentStream, Node node, float x, float y, float xOffset, float totalHeight, int depth) throws IOException {
        if (node == null) return;
        
        String nodeText = node.isLeaf ? formatSubset(node.subset) : node.chosenElement.toString();
        float textWidth = FONT_SIZE * nodeText.length() * 0.3f;
        
        contentStream.beginText();
        contentStream.newLineAtOffset(x - textWidth/2, y - FONT_SIZE/3);
        contentStream.showText(nodeText);
        contentStream.endText();
        
        if (!node.isLeaf) {
            float leftHeight = calculateTotalHeight(node.children[0]);
            float rightHeight = calculateTotalHeight(node.children[1]);
            
            // Calculate dynamic vertical spacing
            float baseSpacing = MIN_BRANCH_SPACING * (float)Math.pow(SPACING_DECREASE_FACTOR, depth);
            float dynamicSpacing = Math.max(Math.max(baseSpacing, MIN_SPACING), (leftHeight + rightHeight) / 6);
            
            // Calculate dynamic horizontal spacing that decreases with depth
            float currentHorizontalSpacing = Math.max(
                INITIAL_HORIZONTAL_SPACING * (float)Math.pow(HORIZONTAL_DECREASE_FACTOR, depth),
                MIN_HORIZONTAL_SPACING
            );
            
            if (node.children[0] != null) {
                float leftChildX = x + currentHorizontalSpacing;
                float leftChildY = y - rightHeight/2 - dynamicSpacing;
                contentStream.moveTo(x, y);
                contentStream.lineTo(leftChildX, leftChildY);
                contentStream.stroke();
                drawTreeToPDF(contentStream, node.children[0], leftChildX, leftChildY, currentHorizontalSpacing, leftHeight, depth + 1);
            }
            
            if (node.children[1] != null) {
                float rightChildX = x + currentHorizontalSpacing;
                float rightChildY = y + leftHeight/2 + dynamicSpacing;
                contentStream.moveTo(x, y);
                contentStream.lineTo(rightChildX, rightChildY);
                contentStream.stroke();
                drawTreeToPDF(contentStream, node.children[1], rightChildX, rightChildY, currentHorizontalSpacing, rightHeight, depth + 1);
            }
        }
    }

    public void printTree() {
        System.out.println("Subset Tree:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLast) {
        if (node != null) {
            String nodeText = node.isLeaf ? formatSubset(node.subset) : node.chosenElement.toString();
            System.out.println(prefix + (isLast ? "\\-- " : "+-- ") + nodeText);
            
            String newPrefix = prefix + (isLast ? "    " : "|   ");
            for (int i = 0; i < node.children.length; i++) {
                printTreeRecursive(node.children[i], newPrefix, i == node.children.length - 1);
            }
        }
    }

    public void printSubsets() {
        System.out.println("\nAll Possible Subsets:");
        for (String subset : subsetMapping) {
            System.out.println(subset);
        }
    }

    public int getSubsetCount() {
        return subsetMapping.size();
    }
} 
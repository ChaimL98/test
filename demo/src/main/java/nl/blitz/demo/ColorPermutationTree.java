package nl.blitz.demo;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class ColorPermutationTree {
    private Node root;
    private List<String> colors;
    private List<String> subsetMapping;
    private static final float CIRCLE_RADIUS = 8f;
    private static final float HORIZONTAL_SPACING = 60f;
    private static final float VERTICAL_SPACING = 50f;
    private static final float CIRCLE_SPACING = 5f;
    private static final float PAGE_MARGIN = 30f;

    private static class Node {
        Node[] children;
        boolean isLeaf;
        List<String> availableColors;
        String chosenColor;

        public Node(boolean isLeaf, int numChildren) {
            this.children = new Node[numChildren];
            this.isLeaf = isLeaf;
            this.availableColors = new ArrayList<>();
            this.chosenColor = "";
        }
    }

    public ColorPermutationTree(List<String> colors) {
        this.colors = colors;
        this.subsetMapping = new ArrayList<>();
        this.root = buildSubsetTree(colors);
        generateMapping();
    }

    private Node buildSubsetTree(List<String> availableColors) {
        // Create a node for the current set of available colors
        Node node = new Node(availableColors.isEmpty(), availableColors.size());
        node.availableColors.addAll(availableColors);

        // If there are no more colors to choose from, this is a leaf node
        if (availableColors.isEmpty()) {
            return node;
        }

        // For each available color, create a child node
        for (int i = 0; i < availableColors.size(); i++) {
            List<String> newAvailable = new ArrayList<>(availableColors);
            String chosenColor = newAvailable.remove(i);
            
            node.children[i] = buildSubsetTree(newAvailable);
            node.children[i].chosenColor = chosenColor;
        }
        
        return node;
    }

    private void generateMapping() {
        subsetMapping.clear();
        traverseAndMap(root, new ArrayList<>());
    }

    private void traverseAndMap(Node node, List<String> currentSubset) {
        if (node != null) {
            if (node.isLeaf) {
                subsetMapping.add(formatSubset(currentSubset));
            }
            for (int i = 0; i < node.children.length; i++) {
                List<String> newSubset = new ArrayList<>(currentSubset);
                newSubset.add(node.children[i].chosenColor);
                traverseAndMap(node.children[i], newSubset);
            }
        }
    }

    private String formatSubset(List<String> subset) {
        if (subset.isEmpty()) {
            return "[]";
        }
        return "[" + String.join(", ", subset) + "]";
    }

    public void saveTreeToPDF(String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Calculate initial position
            float pageHeight = page.getMediaBox().getHeight();
            
            // Position the root node in the vertical center and left side
            float startX = PAGE_MARGIN;
            float startY = pageHeight / 2;
            
            // Draw the tree
            drawTreeToPDF(contentStream, root, startX, startY, HORIZONTAL_SPACING, VERTICAL_SPACING);
            
            contentStream.close();
            document.save(filename);
        }
    }

    private void drawTreeToPDF(PDPageContentStream contentStream, Node node, float x, float y, float xOffset, float yOffset) throws IOException {
        if (node == null) return;
        
        // Draw available colors at the current node
        float currentX = x;
        for (String colorName : node.availableColors) {
            drawColoredCircle(contentStream, currentX, y, colorName);
            currentX += (CIRCLE_RADIUS * 2) + CIRCLE_SPACING;
        }
        
        // Draw children
        float childX = x + xOffset;
        float childY = y - (node.children.length - 1) * yOffset / 2;
        
        for (int i = 0; i < node.children.length; i++) {
            Node child = node.children[i];
            if (child != null) {
                // Draw line to child
                contentStream.moveTo(x, y);
                contentStream.lineTo(childX, childY);
                contentStream.stroke();
                
                // Draw the chosen color at the connection point
                if (!child.chosenColor.isEmpty()) {
                    drawColoredCircle(contentStream, childX, childY, child.chosenColor);
                }
                
                drawTreeToPDF(contentStream, child, childX, childY, xOffset, yOffset);
                childY += yOffset;
            }
        }
    }

    private void drawColoredCircle(PDPageContentStream contentStream, float x, float y, String colorName) throws IOException {
        Color color = getColorFromName(colorName);
        contentStream.setNonStrokingColor(color);
        
        float radius = CIRCLE_RADIUS;
        contentStream.moveTo(x + radius, y);
        contentStream.curveTo(
            x + radius, y + radius * 0.552f,
            x + radius * 0.552f, y + radius,
            x, y + radius
        );
        contentStream.curveTo(
            x - radius * 0.552f, y + radius,
            x - radius, y + radius * 0.552f,
            x - radius, y
        );
        contentStream.curveTo(
            x - radius, y - radius * 0.552f,
            x - radius * 0.552f, y - radius,
            x, y - radius
        );
        contentStream.curveTo(
            x + radius * 0.552f, y - radius,
            x + radius, y - radius * 0.552f,
            x + radius, y
        );
        contentStream.fill();
        
        contentStream.setNonStrokingColor(Color.BLACK);
    }

    private Color getColorFromName(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            case "yellow" -> Color.YELLOW;
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            default -> Color.GRAY;
        };
    }

    public void printTree() {
        System.out.println("Color Subset Tree:");
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isLast) {
        if (node != null) {
            System.out.println(prefix + (isLast ? "\\-- " : "+-- ") + formatSubset(node.availableColors));
            
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
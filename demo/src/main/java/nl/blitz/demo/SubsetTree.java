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
    private float INITIAL_HORIZONTAL_SPACING = 150f;
    private static final float HORIZONTAL_DECREASE_FACTOR = 0.85f;
    private float MIN_HORIZONTAL_SPACING = 40f;
    private static final float VERTICAL_SPACING = 45f;
    private static final float PAGE_MARGIN = 30f;
    private static final float FONT_SIZE = 11f;
    private static final float MIN_BRANCH_SPACING = 40f;
    private static final float LEAF_SPACING = 30f;
    private static final float SPACING_DECREASE_FACTOR = 0.8f;
    private static final float MIN_SPACING = 15f;
    private static final float NODE_PADDING = 8f;
    private static final float MAX_DEPTH = 4;
    private static final float COMPRESSION_FACTOR = 0.7f;
    private static final float CIRCLE_RADIUS = 15f;
    private static final float CIRCLE_STROKE_WIDTH = 1.5f;
    private static final float DEPTH_SCALING_FACTOR = 0.85f;
    private static final float ANGLE_DECREASE_FACTOR = 0.7f;
    private static final float INITIAL_ANGLE = 0.8f;
    private static final float LEAF_ANGLE_FACTOR = 0.5f;
    private static final float[] INCLUSION_COLOR = {0f, 0.5f, 0f}; // Green for inclusion
    private static final float[] EXCLUSION_COLOR = {0.8f, 0f, 0f}; // Red for exclusion

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

    private float calculateTotalWidth(Node node, int depth) {
        if (node == null) return 0;
        if (node.isLeaf) return calculateNodeWidth(formatSubset(node.subset));
        
        float currentSpacing = Math.max(
            INITIAL_HORIZONTAL_SPACING * (float)Math.pow(HORIZONTAL_DECREASE_FACTOR, depth),
            MIN_HORIZONTAL_SPACING
        );
        
        // Apply compression if we're at or beyond max depth
        if (depth >= MAX_DEPTH) {
            currentSpacing *= COMPRESSION_FACTOR;
        }
        
        float leftWidth = calculateTotalWidth(node.children[0], depth + 1);
        float rightWidth = calculateTotalWidth(node.children[1], depth + 1);
        
        return currentSpacing + Math.max(leftWidth, rightWidth);
    }

    public void saveTreeToPDF(String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            
            // Calculate total width needed
            float totalWidth = calculateTotalWidth(root, 0);
            
            // Adjust starting position if tree is too wide
            float startX = PAGE_MARGIN;
            if (totalWidth > pageWidth - (2 * PAGE_MARGIN)) {
                // Scale down the initial spacing to fit
                float scaleFactor = (pageWidth - (2 * PAGE_MARGIN)) / totalWidth;
                INITIAL_HORIZONTAL_SPACING *= scaleFactor;
                MIN_HORIZONTAL_SPACING *= scaleFactor;
            }
            
            float startY = pageHeight / 2;
            
            // Draw the tree
            drawTreeToPDF(contentStream, page, root, startX, startY, INITIAL_HORIZONTAL_SPACING, calculateTotalHeight(root), 0);
            
            contentStream.close();
            document.save(filename);
        }
    }

    private float calculateTotalHeight(Node node) {
        if (node == null) return 0;
        if (node.isLeaf) return LEAF_SPACING;
        return Math.max(calculateTotalHeight(node.children[0]), calculateTotalHeight(node.children[1])) + MIN_BRANCH_SPACING;
    }

    private float calculateNodeWidth(String text) {
        // Estimate width based on text length and font size
        return (text.length() * FONT_SIZE * 0.6f) + (2 * NODE_PADDING);
    }

    private void drawCircle(PDPageContentStream contentStream, float x, float y) throws IOException {
        // Set stroke color to white (invisible)
        contentStream.setStrokingColor(1f, 1f, 1f);
        contentStream.setLineWidth(CIRCLE_STROKE_WIDTH);
        contentStream.moveTo(x + CIRCLE_RADIUS, y);
        contentStream.curveTo(
            x + CIRCLE_RADIUS, y + CIRCLE_RADIUS * 0.552f,
            x + CIRCLE_RADIUS * 0.552f, y + CIRCLE_RADIUS,
            x, y + CIRCLE_RADIUS
        );
        contentStream.curveTo(
            x - CIRCLE_RADIUS * 0.552f, y + CIRCLE_RADIUS,
            x - CIRCLE_RADIUS, y + CIRCLE_RADIUS * 0.552f,
            x - CIRCLE_RADIUS, y
        );
        contentStream.curveTo(
            x - CIRCLE_RADIUS, y - CIRCLE_RADIUS * 0.552f,
            x - CIRCLE_RADIUS * 0.552f, y - CIRCLE_RADIUS,
            x, y - CIRCLE_RADIUS
        );
        contentStream.curveTo(
            x + CIRCLE_RADIUS * 0.552f, y - CIRCLE_RADIUS,
            x + CIRCLE_RADIUS, y - CIRCLE_RADIUS * 0.552f,
            x + CIRCLE_RADIUS, y
        );
        contentStream.stroke();
    }

    private float calculateTextWidth(String text) {
        return text.length() * FONT_SIZE * 0.5f;
    }

    private void drawTreeToPDF(PDPageContentStream contentStream, PDPage page, Node node, float x, float y, float xOffset, float totalHeight, int depth) throws IOException {
        if (node == null) return;
        
        String nodeText = node.isLeaf ? formatSubset(node.subset) : node.chosenElement.toString();
        float textWidth = calculateTextWidth(nodeText);
        
        // Check if node would go beyond page boundaries
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        
        if (x - CIRCLE_RADIUS < PAGE_MARGIN || x + CIRCLE_RADIUS > pageWidth - PAGE_MARGIN) {
            x = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageWidth - PAGE_MARGIN - CIRCLE_RADIUS, x));
        }
        
        if (y - CIRCLE_RADIUS < PAGE_MARGIN || y + CIRCLE_RADIUS > pageHeight - PAGE_MARGIN) {
            y = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageHeight - PAGE_MARGIN - CIRCLE_RADIUS, y));
        }
        
        // Draw the circle
        drawCircle(contentStream, x, y);
        
        // Draw the text centered in the circle
        contentStream.beginText();
        contentStream.newLineAtOffset(x - textWidth/2, y - FONT_SIZE/3);
        contentStream.showText(nodeText);
        contentStream.endText();
        
        if (!node.isLeaf) {
            float leftHeight = calculateTotalHeight(node.children[0]);
            float rightHeight = calculateTotalHeight(node.children[1]);
            
            // Calculate the required space for each child node
            float leftNodeWidth = calculateNodeWidth(node.children[0].isLeaf ? 
                formatSubset(node.children[0].subset) : 
                node.children[0].chosenElement.toString());
            float rightNodeWidth = calculateNodeWidth(node.children[1].isLeaf ? 
                formatSubset(node.children[1].subset) : 
                node.children[1].chosenElement.toString());
            
            // Calculate base spacing that increases with depth to prevent overlap
            float baseSpacing = MIN_BRANCH_SPACING * (1 + (depth * 0.15f));
            
            // Calculate minimum required spacing based on node sizes
            float minRequiredSpacing = Math.max(
                leftNodeWidth + rightNodeWidth + (4 * CIRCLE_RADIUS),
                (leftHeight + rightHeight) * 0.6f
            );
            
            // Use the larger of base spacing and minimum required spacing
            float dynamicSpacing = Math.max(baseSpacing, minRequiredSpacing);
            
            // Calculate dynamic horizontal spacing that decreases with depth
            float currentHorizontalSpacing = Math.max(
                INITIAL_HORIZONTAL_SPACING * (float)Math.pow(HORIZONTAL_DECREASE_FACTOR, depth),
                MIN_HORIZONTAL_SPACING
            );
            
            // Apply compression if we're at or beyond max depth
            if (depth >= MAX_DEPTH) {
                currentHorizontalSpacing *= COMPRESSION_FACTOR;
                dynamicSpacing *= COMPRESSION_FACTOR;
            }
            
            // Apply additional depth-based scaling
            float depthScale = (float)Math.pow(DEPTH_SCALING_FACTOR, depth);
            currentHorizontalSpacing *= depthScale;
            dynamicSpacing *= depthScale;
            
            // Calculate angle reduction based on depth
            float angleFactor = INITIAL_ANGLE * (float)Math.pow(ANGLE_DECREASE_FACTOR, depth);
            
            // Apply additional angle reduction for leaf nodes
            if (node.children[0] != null && node.children[0].isLeaf) {
                angleFactor *= LEAF_ANGLE_FACTOR;
            }
            
            if (node.children[0] != null) {
                float leftChildX = x + currentHorizontalSpacing;
                float leftChildY = y - dynamicSpacing * angleFactor;
                
                // Ensure child position is within page boundaries
                leftChildX = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageWidth - PAGE_MARGIN - CIRCLE_RADIUS, leftChildX));
                leftChildY = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageHeight - PAGE_MARGIN - CIRCLE_RADIUS, leftChildY));
                
                // Set color for exclusion branch (red)
                contentStream.setStrokingColor(EXCLUSION_COLOR[0], EXCLUSION_COLOR[1], EXCLUSION_COLOR[2]);
                
                // Draw line from parent circle to child circle
                contentStream.moveTo(x + CIRCLE_RADIUS, y);
                contentStream.lineTo(leftChildX - CIRCLE_RADIUS, leftChildY);
                contentStream.stroke();
                
                drawTreeToPDF(contentStream, page, node.children[0], leftChildX, leftChildY, currentHorizontalSpacing, leftHeight, depth + 1);
            }
            
            // Reset angle factor for right child
            angleFactor = INITIAL_ANGLE * (float)Math.pow(ANGLE_DECREASE_FACTOR, depth);
            
            // Apply additional angle reduction for leaf nodes
            if (node.children[1] != null && node.children[1].isLeaf) {
                angleFactor *= LEAF_ANGLE_FACTOR;
            }
            
            if (node.children[1] != null) {
                float rightChildX = x + currentHorizontalSpacing;
                float rightChildY = y + dynamicSpacing * angleFactor;
                
                // Ensure child position is within page boundaries
                rightChildX = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageWidth - PAGE_MARGIN - CIRCLE_RADIUS, rightChildX));
                rightChildY = Math.max(PAGE_MARGIN + CIRCLE_RADIUS, Math.min(pageHeight - PAGE_MARGIN - CIRCLE_RADIUS, rightChildY));
                
                // Set color for inclusion branch (green)
                contentStream.setStrokingColor(INCLUSION_COLOR[0], INCLUSION_COLOR[1], INCLUSION_COLOR[2]);
                
                // Draw line from parent circle to child circle
                contentStream.moveTo(x + CIRCLE_RADIUS, y);
                contentStream.lineTo(rightChildX - CIRCLE_RADIUS, rightChildY);
                contentStream.stroke();
                
                drawTreeToPDF(contentStream, page, node.children[1], rightChildX, rightChildY, currentHorizontalSpacing, rightHeight, depth + 1);
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

    public void saveReverseTreeToPDF(String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            
            // Calculate total number of layers (depth + 1)
            int totalLayers = elements.size() + 1;
            
            // Calculate width of each vertical section
            float sectionWidth = (pageWidth - (2 * PAGE_MARGIN)) / totalLayers;
            
            // Calculate positions for each layer
            List<List<Node>> layers = new ArrayList<>();
            List<List<Float>> layerPositions = new ArrayList<>();
            
            // Initialize layers
            for (int i = 0; i < totalLayers; i++) {
                layers.add(new ArrayList<>());
                layerPositions.add(new ArrayList<>());
            }
            
            // Collect nodes for each layer
            collectNodesByLayer(root, 0, layers);
            
            // Calculate positions for each layer
            for (int layer = 0; layer < totalLayers; layer++) {
                List<Node> nodes = layers.get(layer);
                int totalNodes = nodes.size();
                
                // Calculate vertical spacing for this layer
                float availableHeight = pageHeight - (2 * PAGE_MARGIN);
                float verticalSpacing = availableHeight / (totalNodes + 1);
                
                // Calculate x position for this layer
                float x = PAGE_MARGIN + (layer * sectionWidth) + (sectionWidth / 2);
                
                // Calculate y positions for nodes in this layer
                for (int i = 0; i < totalNodes; i++) {
                    float y = PAGE_MARGIN + ((i + 1) * verticalSpacing);
                    layerPositions.get(layer).add(y);
                }
            }
            
            // Draw all nodes
            for (int layer = 0; layer < totalLayers; layer++) {
                List<Node> nodes = layers.get(layer);
                List<Float> positions = layerPositions.get(layer);
                
                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    float x = PAGE_MARGIN + (layer * sectionWidth) + (sectionWidth / 2);
                    float y = positions.get(i);
                    
                    // Draw the circle
                    drawCircle(contentStream, x, y);
                    
                    // Draw the text centered in the circle
                    String nodeText = node.isLeaf ? formatSubset(node.subset) : node.chosenElement.toString();
                    float textWidth = calculateTextWidth(nodeText);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x - textWidth/2, y - FONT_SIZE/3);
                    contentStream.showText(nodeText);
                    contentStream.endText();
                    
                    // Draw connections to children if not a leaf
                    if (!node.isLeaf && layer < totalLayers - 1) {
                        // Find children positions in next layer
                        int leftChildIndex = i * 2;
                        int rightChildIndex = i * 2 + 1;
                        
                        if (leftChildIndex < layerPositions.get(layer + 1).size()) {
                            float childX = PAGE_MARGIN + ((layer + 1) * sectionWidth) + (sectionWidth / 2);
                            float leftChildY = layerPositions.get(layer + 1).get(leftChildIndex);
                            
                            // Set color for exclusion branch (red)
                            contentStream.setStrokingColor(EXCLUSION_COLOR[0], EXCLUSION_COLOR[1], EXCLUSION_COLOR[2]);
                            
                            // Draw line from parent circle to child circle
                            contentStream.moveTo(x + CIRCLE_RADIUS, y);
                            contentStream.lineTo(childX - CIRCLE_RADIUS, leftChildY);
                            contentStream.stroke();
                        }
                        
                        if (rightChildIndex < layerPositions.get(layer + 1).size()) {
                            float childX = PAGE_MARGIN + ((layer + 1) * sectionWidth) + (sectionWidth / 2);
                            float rightChildY = layerPositions.get(layer + 1).get(rightChildIndex);
                            
                            // Set color for inclusion branch (green)
                            contentStream.setStrokingColor(INCLUSION_COLOR[0], INCLUSION_COLOR[1], INCLUSION_COLOR[2]);
                            
                            // Draw line from parent circle to child circle
                            contentStream.moveTo(x + CIRCLE_RADIUS, y);
                            contentStream.lineTo(childX - CIRCLE_RADIUS, rightChildY);
                            contentStream.stroke();
                        }
                    }
                }
            }
            
            contentStream.close();
            document.save(filename);
        }
    }

    private void collectNodesByLayer(Node node, int layer, List<List<Node>> layers) {
        if (node == null) return;
        
        // Add node to its layer
        layers.get(layer).add(node);
        
        if (!node.isLeaf) {
            collectNodesByLayer(node.children[0], layer + 1, layers);
            collectNodesByLayer(node.children[1], layer + 1, layers);
        }
    }
} 
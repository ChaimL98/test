package nl.blitz.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * NQueensSubsetTree class solves the N-Queens problem and visualizes the solutions.
 * The N-Queens problem involves placing N queens on an N×N chessboard such that
 * no two queens threaten each other (no two queens share the same row, column, or diagonal).
 */
public class NQueensSubsetTree {
    private int boardSize;                    // Size of the chessboard (N x N)
    private List<List<Integer>> solutions;    // List to store all valid solutions
    private static final float PAGE_MARGIN = 30f;     // Margin around the page
    private static final float SQUARE_SIZE = 40f;     // Size of each chessboard square
    private static final float FONT_SIZE = 12f;       // Font size for text
    private static final float[] QUEEN_COLOR = {0f, 0f, 0f};    // Black color for queens
    private static final float[] BOARD_COLOR = {0.8f, 0.8f, 0.8f};  // Light gray for board

    /**
     * Constructor initializes the N-Queens solver with a given board size.
     * @param boardSize The size of the chessboard (N x N)
     */
    public NQueensSubsetTree(int boardSize) {
        this.boardSize = boardSize;
        this.solutions = new ArrayList<>();
        findSolutions();
    }

    /**
     * Initiates the search for all valid N-Queens solutions.
     * This method starts the recursive backtracking process from the first row.
     */
    private void findSolutions() {
        List<Integer> currentSolution = new ArrayList<>();
        solveNQueens(0, currentSolution);
    }

    /**
     * Recursive backtracking method to find all valid queen placements.
     * @param row Current row being processed
     * @param currentSolution List storing the column positions of queens in each row
     */
    private void solveNQueens(int row, List<Integer> currentSolution) {
        // Base case: if all rows are filled, we found a solution
        if (row == boardSize) {
            solutions.add(new ArrayList<>(currentSolution));
            return;
        }

        // Try placing a queen in each column of the current row
        for (int col = 0; col < boardSize; col++) {
            if (isSafe(row, col, currentSolution)) {
                currentSolution.add(col);
                solveNQueens(row + 1, currentSolution);
                currentSolution.remove(currentSolution.size() - 1);
            }
        }
    }

    /**
     * Checks if placing a queen at the given position is safe.
     * @param row Row where queen is to be placed
     * @param col Column where queen is to be placed
     * @param currentSolution Current partial solution
     * @return true if the position is safe, false otherwise
     */
    private boolean isSafe(int row, int col, List<Integer> currentSolution) {
        // Check if this column is already used by another queen
        if (currentSolution.contains(col)) {
            return false;
        }

        // Check diagonals for any attacking queens
        for (int i = 0; i < currentSolution.size(); i++) {
            int prevCol = currentSolution.get(i);
            if (Math.abs(row - i) == Math.abs(col - prevCol)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generates a PDF file containing all found solutions.
     * Each solution is displayed as a chessboard with queens marked.
     * @param filename Path where the PDF will be saved
     * @throws IOException If there's an error creating or writing to the PDF file
     */
    public void saveSolutionsToPDF(String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            
            // Calculate board size and position to center it on the page
            float boardTotalSize = boardSize * SQUARE_SIZE;
            float startX = (pageWidth - boardTotalSize) / 2;
            float startY = (pageHeight - boardTotalSize) / 2;
            
            // Draw each solution on the PDF
            for (int solIndex = 0; solIndex < solutions.size(); solIndex++) {
                List<Integer> solution = solutions.get(solIndex);
                
                // Draw the chessboard with queens
                drawBoard(contentStream, startX, startY, solution);
                
                // Add solution number above the board
                contentStream.beginText();
                contentStream.newLineAtOffset(startX, startY - 20);
                contentStream.showText("Solution " + (solIndex + 1));
                contentStream.endText();
                
                // Move to next solution position
                startY += boardTotalSize + 50;
                
                // Create new page if current page is full
                if (startY + boardTotalSize > pageHeight - PAGE_MARGIN) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                    startY = PAGE_MARGIN;
                }
            }
            
            contentStream.close();
            document.save(filename);
        }
    }

    /**
     * Draws a single chessboard with queens placed according to the solution.
     * @param contentStream PDF content stream to draw on
     * @param startX Starting X coordinate of the board
     * @param startY Starting Y coordinate of the board
     * @param solution List of column positions for queens in each row
     * @throws IOException If there's an error drawing to the PDF
     */
    private void drawBoard(PDPageContentStream contentStream, float startX, float startY, List<Integer> solution) throws IOException {
        // Draw squares
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                float x = startX + col * SQUARE_SIZE;
                float y = startY + row * SQUARE_SIZE;
                
                // Draw square border
                contentStream.setStrokingColor(0f, 0f, 0f); // Black border
                contentStream.setLineWidth(0.5f);
                contentStream.addRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                contentStream.stroke();
                
                // Fill square with alternating colors
                if ((row + col) % 2 == 0) {
                    contentStream.setNonStrokingColor(BOARD_COLOR[0], BOARD_COLOR[1], BOARD_COLOR[2]);
                } else {
                    contentStream.setNonStrokingColor(1f, 1f, 1f); // White
                }
                contentStream.addRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                contentStream.fill();
                
                // Draw queen if present
                if (solution.get(row) == col) {
                    // Draw a diamond shape for the queen
                    float centerX = x + SQUARE_SIZE / 2;
                    float centerY = y + SQUARE_SIZE / 2;
                    float size = SQUARE_SIZE * 0.4f;
                    
                    contentStream.setStrokingColor(QUEEN_COLOR[0], QUEEN_COLOR[1], QUEEN_COLOR[2]);
                    contentStream.setLineWidth(2f);
                    contentStream.setNonStrokingColor(QUEEN_COLOR[0], QUEEN_COLOR[1], QUEEN_COLOR[2]);
                    
                    // Draw diamond
                    contentStream.moveTo(centerX, centerY - size);
                    contentStream.lineTo(centerX + size, centerY);
                    contentStream.lineTo(centerX, centerY + size);
                    contentStream.lineTo(centerX - size, centerY);
                    contentStream.closePath();
                    contentStream.fill();
                    
                    // Draw crown on top
                    contentStream.setStrokingColor(1f, 1f, 1f); // White for crown
                    contentStream.setLineWidth(1f);
                    
                    float crownY = centerY - size;
                    float crownWidth = size * 1.2f;
                    
                    // Draw crown base
                    contentStream.moveTo(centerX - crownWidth/2, crownY);
                    contentStream.lineTo(centerX + crownWidth/2, crownY);
                    
                    // Draw crown points
                    for (int i = 0; i < 3; i++) {
                        float pointX = centerX - crownWidth/2 + (crownWidth * i / 2);
                        contentStream.lineTo(pointX, crownY - size * 0.3f);
                        contentStream.lineTo(pointX + crownWidth/2, crownY);
                    }
                    
                    contentStream.stroke();
                }
            }
        }
    }

    /**
     * Returns the total number of solutions found.
     * @return Number of valid N-Queens solutions
     */
    public int getSolutionCount() {
        return solutions.size();
    }

    /**
     * Main method to demonstrate the N-Queens solver.
     * Creates an 8x8 chessboard solution and saves it to a PDF file.
     */
    public static void main(String[] args) {
        int boardSize = 8; // Standard 8x8 chessboard
        NQueensSubsetTree nQueens = new NQueensSubsetTree(boardSize);
        
        try {
            // Get the current working directory
            Path currentPath = Paths.get("").toAbsolutePath();
            System.out.println("Current working directory: " + currentPath);
            
            // Create output directory if it doesn't exist
            Path outputDir = currentPath.resolve("output");
            if (!outputDir.toFile().exists()) {
                System.out.println("Creating output directory: " + outputDir);
                Files.createDirectories(outputDir);
            }
            
            String pdfPath = outputDir.resolve("n_queens_solutions.pdf").toString();
            System.out.println("Attempting to save PDF to: " + pdfPath);
            
            // Check if file exists and delete it
            Path pdfFilePath = Paths.get(pdfPath);
            if (Files.exists(pdfFilePath)) {
                System.out.println("Existing PDF file found, deleting...");
                Files.delete(pdfFilePath);
                System.out.println("Existing PDF file deleted.");
            }
            
            // Save solutions to PDF
            System.out.println("Generating PDF with solutions...");
            nQueens.saveSolutionsToPDF(pdfPath);
            System.out.println("PDF generation completed.");
            
            // Verify the file was created
            if (Files.exists(pdfFilePath)) {
                System.out.println("Successfully saved PDF to: " + pdfPath);
                System.out.println("File size: " + Files.size(pdfFilePath) + " bytes");
            } else {
                System.err.println("Error: PDF file was not created!");
            }
            
            // Print statistics
            System.out.println("\nStatistics:");
            int totalSolutions = nQueens.getSolutionCount();
            System.out.println("Total number of solutions: " + totalSolutions);
            
        } catch (IOException e) {
            System.err.println("Error saving solutions to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
package nl.blitz.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubsetTreeController {

    @GetMapping("/api/subset-tree/{size}")
    public ResponseEntity<InputStreamResource> getSubsetTreePdf(@PathVariable int size) throws IOException {
        // Create output directory if it doesn't exist
        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Generate the subset tree
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4).subList(0, size);
        SubsetTree subsetTree = new SubsetTree(numbers);

        // Generate PDF to memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String pdfPath = outputDir.resolve("subset_tree_size_" + size + ".pdf").toString();
        subsetTree.saveTreeToPDF(pdfPath);

        // Read the generated PDF
        byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=subset_tree.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(pdfBytes)));
    }
} 
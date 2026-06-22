package com.zerotrust.document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.io.IOException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('role-admin') or hasAuthority('role signer')")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        String userId = token.getName();
        String docId = documentService.save(file, userId);
        return ResponseEntity.ok(docId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role-reader') or hasAuthority('role signer') or hasAuthority('role-admin')")
    public ResponseEntity<byte[]> download(@PathVariable String id) throws IOException {
        byte[] pdf = documentService.get(id);
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .body(pdf);
    }

    @PostMapping("/merge")
    @PreAuthorize("hasAuthority('role-admin')")
    public ResponseEntity<byte[]> merge(
            @RequestParam String docId1,
            @RequestParam String docId2) {
        byte[] merged = documentService.merge(docId1, docId2);
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .body(merged);
    }

    @PostMapping("/{id}/split")
    @PreAuthorize("hasAuthority('role-admin')")
    public ResponseEntity<byte[]> split(
            @PathVariable String id,
            @RequestParam int page) {
        byte[] split = documentService.split(id, page);
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .body(split);
    }
}
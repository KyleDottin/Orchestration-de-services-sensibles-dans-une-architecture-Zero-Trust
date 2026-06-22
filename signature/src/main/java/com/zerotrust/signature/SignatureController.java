package com.zerotrust.signature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/signatures")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    // Signer un document
    @PostMapping("/sign/{documentId}")
    @PreAuthorize("hasAuthority('role signer') or hasAuthority('role-admin')")
    public ResponseEntity<String> sign(
            @PathVariable String documentId,
            @RequestBody byte[] documentBytes) throws Exception {
        
        // On génère la signature à partir des octets reçus
        String generatedSignature = signatureService.sign(documentBytes);
        return ResponseEntity.ok(generatedSignature);
    }

    // Vérifier une signature
    @PostMapping("/verify/{documentId}")
    @PreAuthorize("hasAuthority('role-reader') or hasAuthority('role signer') or hasAuthority('role-admin')")
    public ResponseEntity<Boolean> verify(
            @PathVariable String documentId,
            @RequestHeader("X-Signature") String signature, // Reçu proprement de l'interface HTML
            @RequestBody byte[] documentBytes) throws Exception {
        
        boolean valid = signatureService.verify(documentBytes, signature);
        return ResponseEntity.ok(valid);
    }
}
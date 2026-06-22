package com.zerotrust.signature;

import org.springframework.stereotype.Service;
import java.security.*;
import java.util.Base64;

@Service
public class SignatureService {

    private final KeyPair keyPair;

    // Au démarrage du service, on génère une paire de clés RSA 2048 bits
    public SignatureService() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();
    }

    // Signe les bytes du document avec la clé privée
    // Retourne la signature encodée en base64
    public String sign(byte[] documentBytes) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(documentBytes);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    // Vérifie que la signature correspond bien au document
    // avec la clé publique
    public boolean verify(byte[] documentBytes, String signatureBase64) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update(documentBytes);
        return signature.verify(Base64.getDecoder().decode(signatureBase64));
    }
}
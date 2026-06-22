package com.zerotrust.document;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DocumentService {

    @Autowired
    private PdfUtils pdfUtils;

    // Dossier où les PDFs sont stockés
    private final String STORAGE_DIR = "C:/temp/documents/";

    public String save(MultipartFile file, String userId) throws IOException {
        // Crée le dossier s'il n'existe pas
        Files.createDirectories(Paths.get(STORAGE_DIR));

        // Génère un ID unique pour ce document
        String docId = UUID.randomUUID().toString();

        // Sauvegarde le fichier sur disque
        Path destination = Paths.get(STORAGE_DIR + docId + ".pdf");
        Files.write(destination, file.getBytes());

        return docId;
    }

    public byte[] get(String id) throws IOException {
        Path filePath = Paths.get(STORAGE_DIR + id + ".pdf");

        if (!Files.exists(filePath)) {
            throw new RuntimeException("Document introuvable : " + id);
        }

        return Files.readAllBytes(filePath);
    }

    public byte[] merge(String docId1, String docId2) {
        try {
            byte[] pdf1 = get(docId1);
            byte[] pdf2 = get(docId2);
            return pdfUtils.merge(pdf1, pdf2);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la fusion", e);
        }
    }

    public byte[] split(String id, int page) {
        try {
            byte[] pdfBytes = get(id);
            return pdfUtils.split(pdfBytes, page);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du split", e);
        }
    }
}
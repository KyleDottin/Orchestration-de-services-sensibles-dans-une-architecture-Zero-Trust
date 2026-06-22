package com.zerotrust.document;

import org.springframework.stereotype.Component;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer; 

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfUtils {

    public byte[] merge(byte[] pdf1, byte[] pdf2) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // PDFBox 3 demande un objet RandomAccessRead, on utilise RandomAccessReadBuffer
        merger.addSource(new RandomAccessReadBuffer(pdf1));
        merger.addSource(new RandomAccessReadBuffer(pdf2));
        
        merger.setDestinationStream(output);
        merger.mergeDocuments(null); // Requis sous PDFBox 3
        
        return output.toByteArray();
    }

    public byte[] split(byte[] pdfBytes, int fromPage) throws IOException {
        // PDDocument.load(byte[]) n'existe plus, on utilise Loader.loadPDF()
        try (PDDocument original = Loader.loadPDF(pdfBytes);
             PDDocument split = new PDDocument()) {
             
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            // Logique de sécurité pour éviter de sortir des limites du document
            if (fromPage > 0 && fromPage <= original.getNumberOfPages()) {
                split.addPage(original.getPage(fromPage - 1));
            }
            
            split.save(output);
            return output.toByteArray();
        }
    }
}
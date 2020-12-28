package com.importexport.demo;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PDFUtil {
    private final Document doc = new Document(PageSize.A4, 50, 50, 50, 50);

    public ByteArrayInputStream createPdf(Code code) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        try {
            String[] arry = code.getCode().split("(?!<\\+>)[;](?=<\\+>)");
            for (int i = 0; i < arry.length; i++) {
                Pattern pattern = Pattern.compile("([<+>]+)[\\w\\S].*([<+>])");
                Matcher matcher = pattern.matcher(arry[i]);
                while (matcher.find()) {
                    ExtractToken(matcher.group());
                }
            }
        } finally {
            if (doc != null) {
                doc.close();
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void ExtractToken(String group) {
        Pattern pattern = Pattern.compile("<key-[A-z]+>|<\\/key-[A-z]+>");
        doc.open();
        Matcher matcher = pattern.matcher(group);
        if (matcher.find()) {
            switch (matcher.group(0)) {
                case "<key-sentence>":
                    appendSentence(group);
                    break;
                case "<key-paragraph>":
                    appendParagraph(group);
                    break;
            }
        }
    }

    private void appendParagraph(String group) {
        try {
            Pattern pattern = Pattern.compile("(?!=<key-sentence>)[A-z0-9!\"£$%^&*(){}\\[\\]@;:?/`¬,()~|#\\s+=]+(?=<\\/key-sentence>)");
            String[] arr = group.split("(?!<\\/key-paragraph>)[~\\s]+(?=<key\\-paragraph>)");
            for (int i = 0; i < arr.length; i++) {
                Paragraph paragraph = new Paragraph();
                if (i != 0) {
                    doc.add(new Paragraph("\n"));
                }
                try {
                    Matcher matcher = pattern.matcher(arr[i]);
                    while (matcher.find()) {
                        String val = matcher.group();
                        Chunk chunk = new Chunk(matcher.group());
                        paragraph.add(chunk);
                    }
                    doc.add(paragraph);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println(group);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void appendSentence(String group) {
        Pattern pattern = Pattern.compile("(?!=<key-sentence>)[A-z0-9!\"£$%^&*(){}\\[\\]@;:?/`¬,()~|#\\s]+(?=<\\/key-sentence>)");
        String[] arr = group.split("(?!>)[~ ]+(?=<)");
        for (int i = 0; i < arr.length; i++) {
            try {
                Matcher matcher = pattern.matcher(arr[i]);
                while (matcher.find()) {
                    doc.add(new Phrase(matcher.group()));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String PDFReader(InputStream file) throws IOException {
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        RandomAccessBufferedFileInputStream t = new RandomAccessBufferedFileInputStream(file);
        PDFParser parser = new PDFParser(t);
        parser.parse();
        try (COSDocument cosDoc = parser.getDocument()) {
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            String parsedText = pdfStripper.getText(pdDoc);
            return (parsedText);
        }
    }

}

package com.importexport.demo;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
                case "<key-table>":
                    appendTable(group);
                    break;
            }
        }
    }

    private void appendTable(String group) {
        try {
           /* Pattern pattern = Pattern.compile("(?!=<key-paragraph>)[A-z0-9!\"£$%^&*(){}\\[\\]@;:?/`¬,()~|#\\s+=]+(?=<\\/key-paragraph>)");
            String[] arr = group.split("(?!<\\/key-table>)[~\\s]+(?=<key\\-table>)");
            PdfPTable table = new PdfPTable(1); // 3 columns.
            table.setWidthPercentage(100); //Width 100%
            table.setSpacingBefore(10f); //Space before table
            table.setSpacingAfter(10f); //Space after table

            //Set Column widths
            float[] columnWidths = {1f, 1f, 1f};
            table.setWidths(columnWidths);
            for (int i = 0; i < arr.length; i++) {
                Paragraph paragraph = new Paragraph();
                try {
                    Matcher matcher = pattern.matcher(arr[i]);
                    while (matcher.find()) {

                        String val = matcher.group();
                        paragraph.add(val);
                        PdfPCell cell1 = new PdfPCell(new Paragraph(paragraph));
                        cell1.setBorderColor(BaseColor.BLUE);
                        cell1.setPaddingLeft(10);
                        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell1);
                    }
                    doc.add(table);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }*

            */

            PdfPTable table = new PdfPTable(3); // 3 columns.
            table.setWidthPercentage(100); //Width 100%
            table.setSpacingBefore(10f); //Space before table
            table.setSpacingAfter(10f); //Space after table

            //Set Column widths
            float[] columnWidths = {1f, 1f, 1f};
            table.setWidths(columnWidths);

            PdfPCell cell1 = new PdfPCell(new Paragraph("1"));
            cell1.setBorderColor(BaseColor.BLUE);
            cell1.setPaddingLeft(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell2 = new PdfPCell(new Paragraph("2"));
            cell2.setBorderColor(BaseColor.GREEN);
            cell2.setPaddingLeft(10);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell3 = new PdfPCell(new Paragraph("3"));
            cell3.setBorderColor(BaseColor.RED);
            cell3.setPaddingLeft(10);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

            //To avoid having the cell border and the content overlap, if you are having thick cell borders
            //cell1.setUserBorderPadding(true);
            //cell2.setUserBorderPadding(true);
            //cell3.setUserBorderPadding(true);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);

            doc.add(table);
            } catch (DocumentException documentException) {
            documentException.printStackTrace();
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

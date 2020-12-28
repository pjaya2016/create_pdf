package com.importexport.demo;

import com.itextpdf.text.DocumentException;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf/convertor")
public class PDFController {

    @PostMapping(value = "/create")
    public ResponseEntity<byte[]> sendPdf(@RequestBody Code code) throws IOException, DocumentException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ByteArrayInputStream t = new PDFUtil().createPdf(code);
        byte[] targetArray = IOUtils.toByteArray(t);
        ResponseEntity<byte[]> response = new ResponseEntity<>(targetArray, headers, HttpStatus.OK);
        return response;

    }

    @PostMapping
    public String ReadPDF(@RequestParam("file") MultipartFile file) throws IOException {
        return new PDFUtil().PDFReader(file.getInputStream());
    }

}

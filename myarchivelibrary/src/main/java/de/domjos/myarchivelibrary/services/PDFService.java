package de.domjos.myarchivelibrary.services;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PDFService {
    public static final String H1 = "Header 1", H2 = "Header 2", H3 = "Header 3", H4 = "Header 4", H5 = "Header 5", P = "CONTENT";
    public static final String LEFT = "LEFT", RIGHT = "RIGHT", CENTER = "CENTER", TOP = "TOP", BOTTOM = "BOTTOM";

    private Document document;
    private final Map<String, Font> fonts;
    private final Map<String, Integer> positions;

    public PDFService(String file) throws Exception {
        this.fonts = new LinkedHashMap<>();
        this.fonts.put(PDFService.H1, new Font(Font.FontFamily.HELVETICA, 72f, Font.BOLDITALIC));
        this.fonts.put(PDFService.H2, new Font(Font.FontFamily.HELVETICA, 64f, Font.BOLDITALIC));
        this.fonts.put(PDFService.H3, new Font(Font.FontFamily.HELVETICA, 56f, Font.BOLD));
        this.fonts.put(PDFService.H4, new Font(Font.FontFamily.HELVETICA, 48f, Font.BOLD));
        this.fonts.put(PDFService.H5, new Font(Font.FontFamily.HELVETICA, 24f, Font.BOLD));
        this.fonts.put(PDFService.P, new Font(Font.FontFamily.HELVETICA, 24f, Font.NORMAL));

        this.positions = new LinkedHashMap<>();
        this.positions.put(PDFService.LEFT, Paragraph.ALIGN_LEFT);
        this.positions.put(PDFService.RIGHT, Paragraph.ALIGN_RIGHT);
        this.positions.put(PDFService.CENTER, Paragraph.ALIGN_CENTER);
        this.positions.put(PDFService.TOP, PdfPCell.ALIGN_TOP);
        this.positions.put(PDFService.BOTTOM, PdfPCell.ALIGN_BOTTOM);


        this.document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(this.document, new FileOutputStream(file));
        pdfWriter.setPageEvent(new PDFService.HeaderFooter(PDFService.P));
        this.document.open();
    }

    public void addHeadPage(byte[] icon, String title, String subTitle) throws Exception {
        this.addParagraph(title, PDFService.H1, PDFService.CENTER, 40f);
        this.addImage(icon, PDFService.CENTER, 10f);
        this.addParagraph(subTitle, PDFService.H3, PDFService.CENTER, 30f);
        this.newPage();
    }

    public void addTable(Map<String, Float> columns, List<List<String>> content, int headerColor, int contentColor, float padding) throws Exception {
        float[] columnSizes = new float[columns.values().size()];
        int i = 0;
        for(Float size : columns.values()) {
            columnSizes[i] = size;
            i++;
        }

        PdfPTable pdfPTable = new PdfPTable(columnSizes);
        pdfPTable.setPaddingTop(padding);
        for(String columnHeader : columns.keySet()) {
            PdfPCell cell = new PdfPCell(new Phrase(columnHeader, this.fonts.get(PDFService.H5)));
            cell.setBackgroundColor(new BaseColor(headerColor));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPadding(8.0f);
            pdfPTable.addCell(cell);
        }

        for(List<String> row : content) {
            for(String cellContent : row) {
                PdfPCell cell = new PdfPCell(new Phrase(cellContent, this.fonts.get(PDFService.P)));
                cell.setBackgroundColor(new BaseColor(contentColor));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setPadding(8.0f);
                pdfPTable.addCell(cell);
            }
        }
        this.document.add(pdfPTable);
    }

    public void addParagraph(String content, String font, String position, float padding) throws Exception {
        Paragraph paragraph = new Paragraph(content, this.fonts.get(font));

        Integer pos = this.positions.get(position);
        if(pos != null) {
            paragraph.setAlignment(pos);
        }

        paragraph.setPaddingTop(padding);
        this.document.add(paragraph);
    }

    public void addImage(byte[] imageContent, String position, float padding) throws Exception {
        Image image = Image.getInstance(imageContent);
        image.setPaddingTop(padding);

        Integer pos = this.positions.get(position);
        if(pos != null) {
            image.setAlignment(pos);
        }

        this.document.add(image);
    }

    public void newPage() {
        this.document.newPage();
    }

    public void close() {
        this.document.close();
    }

    private class HeaderFooter extends PdfPageEventHelper {
        private String font;

        HeaderFooter(String font) {
            this.font = font;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getPageSize();
            try {
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("(c) 2020 MyArchiveMobile", fonts.get(this.font)), 10, 18, 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.valueOf(writer.getPageNumber()), fonts.get(this.font)), rect.getWidth() - 10, 18, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

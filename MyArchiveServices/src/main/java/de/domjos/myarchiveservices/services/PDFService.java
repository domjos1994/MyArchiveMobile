/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchiveservices.services;

import android.content.Context;

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

import de.domjos.customwidgets.utils.MessageHelper;

@SuppressWarnings("WeakerAccess")
public class PDFService {
    public static final String H1 = "Header 1", H2 = "Header 2", H3 = "Header 3", H4 = "Header 4", H5 = "Header 5", P = "CONTENT";
    public static final String LEFT = "LEFT", RIGHT = "RIGHT", CENTER = "CENTER", TOP = "TOP", BOTTOM = "BOTTOM";

    private Document document;
    private final Map<String, Font> fonts;
    private final Map<String, Integer> positions;
    private final int icon;
    private final Context context;

    public PDFService(String file, int icon, Context context) {
        this.fonts = new LinkedHashMap<>();
        this.positions = new LinkedHashMap<>();
        this.icon = icon;
        this.context = context;

        try {
            this.fonts.put(PDFService.H1, new Font(Font.FontFamily.HELVETICA, 72f, Font.BOLDITALIC));
            this.fonts.put(PDFService.H2, new Font(Font.FontFamily.HELVETICA, 64f, Font.BOLDITALIC));
            this.fonts.put(PDFService.H3, new Font(Font.FontFamily.HELVETICA, 56f, Font.BOLD));
            this.fonts.put(PDFService.H4, new Font(Font.FontFamily.HELVETICA, 48f, Font.BOLD));
            this.fonts.put(PDFService.H5, new Font(Font.FontFamily.HELVETICA, 24f, Font.BOLD));
            this.fonts.put(PDFService.P, new Font(Font.FontFamily.HELVETICA, 24f, Font.NORMAL));


            this.positions.put(PDFService.LEFT, Paragraph.ALIGN_LEFT);
            this.positions.put(PDFService.RIGHT, Paragraph.ALIGN_RIGHT);
            this.positions.put(PDFService.CENTER, Paragraph.ALIGN_CENTER);
            this.positions.put(PDFService.TOP, PdfPCell.ALIGN_TOP);
            this.positions.put(PDFService.BOTTOM, PdfPCell.ALIGN_BOTTOM);


            this.document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, new FileOutputStream(file));
            pdfWriter.setPageEvent(new PDFService.HeaderFooter(PDFService.P));
            this.document.open();
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.icon, this.context);
        }
    }

    public void addHeadPage(byte[] icon, String title, String subTitle) {
        try {
            this.addParagraph(title, PDFService.H3, PDFService.CENTER, 40f);
            this.addImage(icon, PDFService.CENTER, 256f, 256f, 10f);
            this.addParagraph(subTitle, PDFService.H3, PDFService.CENTER, 30f);
            this.newPage();
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.icon, this.context);
        }
    }

    public void addTable(Map<String, Float> columns, List<List<String>> content, int headerColor, int contentColor, float padding) {
        try {
            float[] columnSizes = new float[columns.values().size()];
            int i = 0;
            for(Float size : columns.values()) {
                columnSizes[i] = size;
                i++;
            }

            PdfPTable pdfPTable = new PdfPTable(columnSizes);
            pdfPTable.setSpacingBefore(padding);
            pdfPTable.setSpacingAfter(padding);
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
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.icon, this.context);
        }
    }

    public void addParagraph(String content, String font, String position, float padding) {
        try {
            Paragraph paragraph = new Paragraph(content, this.fonts.get(font));

            Integer pos = this.positions.get(position);
            if(pos != null) {
                paragraph.setAlignment(pos);
            }

            paragraph.setSpacingBefore(padding);
            paragraph.setSpacingAfter(padding);
            this.document.add(paragraph);
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.icon, this.context);
        }
    }

    public void addImage(byte[] imageContent, String position, float maxWidth, float maxHeight, float padding) {
        try {
            Image image = Image.getInstance(imageContent);
            float imgWidth = image.getWidth();
            float imgHeight = image.getHeight();

            float width = imgWidth;
            float height = imgHeight;
            if(maxHeight<imgHeight) {
                float factor = maxHeight /imgHeight;
                width = imgWidth * factor;
                height = maxHeight;
            } else if(maxWidth<imgWidth) {
                float factor = maxWidth /imgWidth;
                width = maxWidth;
                height = imgHeight * factor;
            }

            image.scaleAbsolute(width, height);
            image.setSpacingBefore(padding);
            image.setSpacingAfter(padding);

            Integer pos = this.positions.get(position);
            if(pos != null) {
                image.setAlignment(pos);
            }

            this.document.add(image);
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.icon, this.context);
        }
    }

    public void newPage() {
        this.document.newPage();
    }

    public void close() {
        this.document.close();
    }

    private class HeaderFooter extends PdfPageEventHelper {
        private final String font;

        HeaderFooter(String font) {
            this.font = font;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getPageSize();

            int width = Math.round(rect.getWidth());
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("(c) 2020 MyArchiveMobile", fonts.get(this.font)), 10, 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.valueOf(writer.getPageNumber()), fonts.get(this.font)), width - 10, 18, 0);
        }
    }
}

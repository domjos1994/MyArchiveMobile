/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
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

package de.domjos.myarchivemobile.helper;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;

public class PDFReaderHelper {
    private final PdfRenderer pdfRenderer;
    private int pageNumber;
    private PdfRenderer.Page page;

    public PDFReaderHelper(String path) throws Exception {
        this.pageNumber = 1;

        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
        this.pdfRenderer = new PdfRenderer(fileDescriptor);
        this.openNewPage(this.pageNumber);
    }

    public void openPage(int page) {
        this.openNewPage(page);
    }

    public Bitmap getPage() {
        Bitmap bitmap = Bitmap.createBitmap(this.page.getWidth(), this.page.getHeight(), Bitmap.Config.ARGB_8888);
        this.page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        return bitmap;
    }

    public int getPagesCount() {
        return this.pdfRenderer.getPageCount();
    }

    public int getCurrentPageNumber() {
        return this.pageNumber;
    }

    public void close() {
        this.pdfRenderer.close();
    }

    private void openNewPage(int page) {
        if(this.page != null) {
            this.page.close();
        }
        this.pageNumber = page;
        this.page = this.pdfRenderer.openPage(this.pageNumber);
    }
}

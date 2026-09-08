package com.library.model.book;

import java.util.List;

/**
 * Concrete implementation representing a digital E-Book.
 */
public class EBook extends Book {
    private double fileSizeMB;
    private String format; // PDF, EPUB, MOBI
    private String downloadUrl;

    public EBook(String isbn, String title, List<String> authors, String publisher,
                 int publicationYear, int pages, BookCategory category, int totalCopies,
                 double fileSizeMB, String format, String downloadUrl) {
        super(isbn, title, authors, publisher, publicationYear, pages, category, totalCopies);
        this.fileSizeMB = fileSizeMB;
        this.format = (format != null) ? format : "PDF";
        this.downloadUrl = (downloadUrl != null) ? downloadUrl : "";
    }

    public double getFileSizeMB() {
        return fileSizeMB;
    }

    public void setFileSizeMB(double fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String getBookType() {
        return "E-Book";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("File Size: %.1f MB | Format: %s | URL: %s",
                fileSizeMB, format, downloadUrl.isEmpty() ? "N/A" : downloadUrl);
    }
}

package com.library.model.book;

import java.util.List;

/**
 * Concrete implementation representing an AudioBook.
 */
public class AudioBook extends Book {
    private int durationMinutes;
    private String narrator;
    private String audioFormat; // MP3, AAC, FLAC

    public AudioBook(String isbn, String title, List<String> authors, String publisher,
                     int publicationYear, int pages, BookCategory category, int totalCopies,
                     int durationMinutes, String narrator, String audioFormat) {
        super(isbn, title, authors, publisher, publicationYear, pages, category, totalCopies);
        this.durationMinutes = durationMinutes;
        this.narrator = (narrator != null) ? narrator : "Unknown Narrator";
        this.audioFormat = (audioFormat != null) ? audioFormat : "MP3";
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNarrator() {
        return narrator;
    }

    public void setNarrator(String narrator) {
        this.narrator = narrator;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public String getBookType() {
        return "Audiobook";
    }

    @Override
    public String getSpecificDetails() {
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return String.format("Duration: %dh %dm (%d mins) | Narrator: %s | Format: %s",
                hours, mins, durationMinutes, narrator, audioFormat);
    }
}

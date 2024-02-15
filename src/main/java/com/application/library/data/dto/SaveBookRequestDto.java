package com.application.library.data.dto;

import java.util.Objects;

public class SaveBookRequestDto {
    private String name;
    private String author;
    private String isbn;
    private Integer pageCount;
    private String publisher;
    private String publishedAt;
    private String language;
    private String description;
    private String imageUrl;
    private Integer totalCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveBookRequestDto that = (SaveBookRequestDto) o;
        return Objects.equals(name, that.name) && Objects.equals(author, that.author) && Objects.equals(isbn, that.isbn) && Objects.equals(pageCount, that.pageCount) && Objects.equals(publisher, that.publisher) && Objects.equals(publishedAt, that.publishedAt) && Objects.equals(language, that.language) && Objects.equals(description, that.description) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(totalCount, that.totalCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, isbn, pageCount, publisher, publishedAt, language, description, imageUrl, totalCount);
    }
}

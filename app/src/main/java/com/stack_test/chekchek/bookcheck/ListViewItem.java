package com.stack_test.chekchek.bookcheck;

import java.io.Serializable;

public class ListViewItem implements Serializable {
    public String BookimageURL;
    public String Title ;
    public String Author ;
    public String Description;
    public String ISBN;
    public boolean Available;
    public ListViewItem(String bookimageURL, String title, String author, String description, String ISBN, boolean available){
        this.BookimageURL = bookimageURL;
        this.Title = title;
        this.Author = author;
        this.Description = description;
        this.ISBN = ISBN;
        this.Available = available;
    }
}

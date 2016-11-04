package de.digitalcollections.commons.yaml.examples;

import org.joda.time.DateTime;
import static de.digitalcollections.commons.yaml.StringRepresentations.stringRepresentationOf;

public class Book {

  private Person author;

  private String title;

  private DateTime published;

  private Book() {}

  public Book(Person author, String title, DateTime published) {
    this.author = author;
    this.title = title;
    this.published = published;
  }

  public Person getAuthor() {
    return author;
  }

  public void setAuthor(Person author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DateTime getPublished() {
    return published;
  }

  public void setPublished(DateTime published) {
    this.published = published;
  }

  @Override
  public String toString() {
    return stringRepresentationOf(this);
  }

}

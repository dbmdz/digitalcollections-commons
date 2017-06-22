package de.digitalcollections.commons.yaml.examples;

import static de.digitalcollections.commons.yaml.StringRepresentations.stringRepresentationOf;
import org.joda.time.LocalDateTime;

public class Person {

  private String firstName;

  private String lastName;

  private LocalDateTime bornAt;

  private Person() {
  }

  public Person(String firstName, String lastName, LocalDateTime bornAt) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.bornAt = bornAt;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDateTime getBornAt() {
    return bornAt;
  }

  public void setBornAt(LocalDateTime bornAt) {
    this.bornAt = bornAt;
  }

  @Override
  public String toString() {
    return stringRepresentationOf(this);
  }

}

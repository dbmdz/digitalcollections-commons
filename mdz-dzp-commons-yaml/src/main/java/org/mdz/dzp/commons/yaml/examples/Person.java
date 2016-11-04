package org.mdz.dzp.commons.yaml.examples;

import org.joda.time.DateTime;
import static org.mdz.dzp.commons.yaml.StringRepresentations.stringRepresentationOf;

public class Person {

  private String firstName;

  private String lastName;

  private DateTime bornAt;

  private Person() {}

  public Person(String firstName, String lastName, DateTime bornAt) {
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

  public DateTime getBornAt() {
    return bornAt;
  }

  public void setBornAt(DateTime bornAt) {
    this.bornAt = bornAt;
  }

  @Override
  public String toString() {
    return stringRepresentationOf(this);
  }

}

package org.mdz.dzp.commons.yaml.examples;

import static org.assertj.core.api.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Test;

public class ExampleTest {

  @Test
  public void toStringShouldSerializeBook() {
    Person arkadiStrugatzki = new Person("Arkadi", "Strugatzki", DateTime.parse("1925-08-28"));
    Book roadsidePicknic = new Book(arkadiStrugatzki, "Roadside Picknic", DateTime.parse("1971"));

    System.out.println("roadsidePicknic.toString()");
    System.out.println(roadsidePicknic.toString());

    assertThat(roadsidePicknic.toString())
        .isEqualTo("!!org.mdz.dzp.commons.yaml.examples.Book {author: {bornAt: !!timestamp '1925-08-27T23:00:00Z', firstName: Arkadi, lastName: Strugatzki}, published: !!timestamp '1970-12-31T23:00:00Z', title: Roadside Picknic}");
  }

}

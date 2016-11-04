package org.mdz.dzp.commons.yaml.examples;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mdz.dzp.commons.yaml.StringRepresentations;
import static org.mdz.dzp.commons.yaml.StringRepresentations.fromStringRepresetation;

public class BenchmarkTest {

  private static final int COLUMN_WIDTH = 18;

  @Test
  public void benchmark() {
    Person arkadiStrugatzki = new Person("Arkadi", "Strugatzki", DateTime.parse("1925-08-28"));
    Book roadsidePicknic = new Book(arkadiStrugatzki, "Roadside Picknic", DateTime.parse("1971"));

    int repeats = 3;
    int numberOfIterations = 5000;

    measure("Book", roadsidePicknic, repeats, numberOfIterations);
    measure("Person", arkadiStrugatzki, repeats, numberOfIterations);
  }

  private static String format(double duration) {
    String string = String.format("%.1f µs ", duration);
    if (string.length() < COLUMN_WIDTH) {
      return String.format("%10s", string);
    }
    return null;
  }

  private void measure(String info, Object thing, int repeats, int numberOfIterations) {
    System.out.print(info + "\t");
    for (int k = 0; k < repeats; k++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < numberOfIterations; i++) {
        thing.toString();
      }
      double duration = 1.0 * (System.currentTimeMillis() - start) / numberOfIterations * 1000;
      System.out.print(format(duration));
    }
    System.out.println();
  }

}

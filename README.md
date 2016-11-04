# DigitalCollections Common Java Libraries
[![Build Status](https://travis-ci.org/dbmdz/digitalcollections-commons.svg?branch=master)](https://travis-ci.org/dbmdz/digitalcollections-commons)


## DigitalCollections Commons XML

Commons XML contains several utilities for processing XML files. Most important is `XPathWrapper`, which allows easy querying of XML files:

```
// ... get a org.w3c.dom.Document for your XML as 'doc'.
XPathWrapper wrapper = new XPathWrapper(doc);
Number id = wrapper.asNumber("//tei:classCode[@scheme='project']/tei:idno").intValue(), 12345)
```

## DigitalCollections Commons YAML: Effiecient toString() implementations

The main purpose of `de.digitalcollections.commons.yaml` is to provide easy to use `toString()`-implementations using YAML. This is particularily useful because one can use any String representation from a log file can be used to recreate the desired object in a unit test. Additionally, any `toString()`-implemetation via this library is idependent from the Object implemetation, so you don't have to change `toString()` if the object changes.

Example:

```java
package de.digitalcollections.commons.yaml.examples;

import org.joda.time.DateTime;
import static de.digitalcollections.commons.yaml.StringRepresentations.stringRepresentationOf;

public class Book {

  private Person author;

  private String title;

  private DateTime published;

  private Book() {}

  // [...] Omitted other constructors and getters/setters.

  @Override
  public String toString() {
    return stringRepresentationOf(this);
  }

}
```

Using with some Data (for details see `de.digitalcollections.commons.yaml.examples`):

```java
Person arkadiStrugatzki = new Person("Arkadi", "Strugatzki", DateTime.parse("1925-08-28"));
Book roadsidePicknic = new Book(arkadiStrugatzki, "Roadside Picknic", DateTime.parse("1971"));

System.out.println(roadsidePicknic.toString());
```

```
!!org.mdz.dzp.commons.yaml.examples.Book {author: {bornAt: !!timestamp '1925-08-27T23:00:00Z', firstName: Arkadi, lastName: Strugatzki}, published: !!timestamp '1970-12-31T23:00:00Z', title: Roadside Picknic}
```

Some Benchmarks on serialization speed. Shown is mean execution time of one toString() call (see source code for details):

```
Running de.digitalcollections.commons.yaml.examples.BenchmarkTest
Book    93,6 µs   49,0 µs   34,4 µs
Person  22,4 µs   21,0 µs   28,6 µs
```

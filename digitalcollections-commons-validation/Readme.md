# DigitalCollections Commons Validation

Commons Validation contains utilities to validate data:

```java
import de.digitalcollections.commons.validation.StringAssertions;
import static de.digitalcollections.commons.validation.StringAssertions.AS_OFTEN_AS;

class Example {
  public boolean validate(String value) {
    StringAssertions input = new StringAssertions("name: 'Alice'");
    return input.doesNotContain("\"")
      && input.contains(":")
      && input.contains("a", AS_OFTEN_AS, "e");
  }  
}
```


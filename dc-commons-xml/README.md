# DigitalCollections Commons XML

Commons XML contains several utilities for processing XML files. Most important is `XPathWrapper`, which allows easy querying of XML files:

```
// ... get a org.w3c.dom.Document for your XML as 'doc'.
XPathWrapper wrapper = new XPathWrapper(doc);
Number id = wrapper.asNumber("//tei:classCode[@scheme='project']/tei:idno").intValue(), 12345)
```


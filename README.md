[![Build Status](https://travis-ci.org/prefixcommons/curie-util.svg?branch=master)](https://travis-ci.org/prefixcommons/curie-util)

# curie-util
Java utility library to translate CURIEs to IRIs and vice-versa.

# Usage

## Create the CurieUtil object

### From a java map object:

```java
Map<String, String> map = new HashMap<>();
map.put("", "http://x.org/");
map.put("A", "http://x.org/a_");
map.put("B", "http://x.org/B_");
map.put("CC", "http://x.org/C_C");
map.put("C", "http://x.org/C_");
CurieUtil curieUtil = new CurieUtil(map);
```

### From a json-ld file:

```java
CurieUtil curieUtil = CurieUtil.fromJsonLdFile("path/to/file");
```

## Translates CURIEs and IRIs

Both methods will return an Option:


```java
curieUtil.getCurie("http://x.org/a_foo"); // Optional.of("A:foo")
curieUtil.getIri("A:foo"); // Optional.of("http://x.org/a_foo")
curieUtil.getCurie("none"); // Optional.absent();
```

# Genesis - It cannot dance, it cannot sing, but it generates serious code!

## What is Genesis?
Genesis is an extendible code generator.
It is integrated into your project as Java annotation processor and invokes arbitrary scripts which do the actual generation using a simple API.
The scripts can be written in any language supported by JSR-223 (e.g. javascript, groovy, ...).

## License
Genesis is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Getting started
Add the following dependency to your project:
```xml
<dependency>
    <groupId>de.hasait.genesis</groupId>
    <artifactId>genesis-processor</artifactId>
    <version>0.4.0</version>
    <scope>provided</scope>
</dependency>
```

Annotate classes:
```
@Genesis(execute = "script:metaData.js")
public class Foo {
    [...]
}
```

The script [metaData.js](processor/src/main/resources/genesis/metaData.js) is integrated into genesis and can be used as a starting point. For each annotated class it generates a class with suffix _MD containing metadata, e.g. property names.

## Project status
Early phase - expect major changes while the usage and API stabilizes.

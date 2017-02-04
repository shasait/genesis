# Genesis - It cannot dance, it cannot sing, but it generates serious code!

## What is Genesis?
Genesis is a collection of java code generators, which can be integrated into your project as java annotation processor.
It also contains a base module which simplifies writing custom annotation processors. 

## License
Genesis is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## ScriptGen
[![Maven Central](https://img.shields.io/maven-central/v/de.hasait.genesis/genesis-scriptgen.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.hasait.genesis%22%20AND%20a%3A%22genesis-scriptgen%22)

The Genesis ScriptGen code generator delegates the actual generation to a script. The scripts can be written in any language supported by JSR-223 (e.g. javascript, groovy, ...).
The script can either create code using simple string operations or by using a full fledged object model. 

### Getting started
Add as dependency to your project - click the badge for details.

Annotate classes:
```
@ScriptGen(script = "metaData.js")
public class Foo {
    [...]
}
```

The script [metaData.js](scriptgen/src/main/resources/genesis/metaData.js) is integrated and can be used as starting point.
For each annotated class it generates a class with suffix _MD containing metadata, e.g. property names.

## MetaGen
[![Maven Central](https://img.shields.io/maven-central/v/de.hasait.genesis/genesis-metagen.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.hasait.genesis%22%20AND%20a%3A%22genesis-metagen%22)

The Genesis MetaGen code generator generates metadata classes, i.e. a class containing constants with property names and types.
 
### How to use
Add as dependency to your project - click the badge for details.

Annotate classes:
```
@MetaGen
public class Foo {
    [...]
}
```

That's it.


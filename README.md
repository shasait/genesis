# Genesis - It cannot dance, it cannot sing, but it generates serious code!

## What is Genesis?
Genesis is a collection of java code generators, which can be integrated into your project as java annotation processor.
It also contains a base module which simplifies writing custom annotation processors. 

## License
Genesis is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## ScriptGen
The Genesis ScriptGen code generator delegates the actual generation to a script. The scripts can be written in any language supported by JSR-223 (e.g. javascript, groovy, ...).
The script can either create code using simple string operations or by using a full fledged object model. 

### Getting started
Add the following dependency to your project:
```xml
<dependency>
    <groupId>de.hasait.genesis</groupId>
    <artifactId>genesis-scriptgen</artifactId>
    <version>0.5.0</version>
    <scope>provided</scope>
</dependency>
```

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
The Genesis MetaGen code generator generates metadata classes, i.e. a class containing constants with property names and types.
 
### How to use
Add the following dependency to your project:
```xml
<dependency>
    <groupId>de.hasait.genesis</groupId>
    <artifactId>genesis-metagen</artifactId>
    <version>0.5.0</version>
    <scope>provided</scope>
</dependency>
```

Annotate classes:
```
@MetaGen
public class Foo {
    [...]
}
```

That's it.

## Project status
Early phase - expect major changes while the usage and API stabilizes.

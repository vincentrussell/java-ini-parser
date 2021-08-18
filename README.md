# java-ini-parser [![Maven Central](https://img.shields.io/maven-central/v/com.github.vincentrussell/java-ini-parser.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.vincentrussell%22%20AND%20a:%22java-ini-parser%22) [![Build Status](https://travis-ci.org/vincentrussell/java-ini-parser.svg?branch=master)](https://travis-ci.org/vincentrussell/java-ini-parser)

java-ini-parser helps you parse ini files in java.   

## Maven

Add a dependency to `com.github.vincentrussell:java-ini-parser`. 

```
<dependency>
   <groupId>com.github.vincentrussell</groupId>
   <artifactId>java-ini-parser</artifactId>
   <version>1.0</version>
</dependency>
```

## Requirements
- JDK 1.8 or higher

## Running it from Java

```
 IniParser iniParser = new IniParser(new FileInputStream("samples/sample2.ini"));
```

## Available options

### Get the sections

```
 IniParser iniParser = new IniParser(new FileInputStream("samples/sample2.ini"));
 iniParser.getSections();
```

### Get the keys

```
 IniParser iniParser = new IniParser(new FileInputStream("samples/sample2.ini"));
 iniParser.getKeys("FTPS")
```

### Get a value

```
 IniParser iniParser = new IniParser(new FileInputStream("samples/sample2.ini"));
 iniParser.getValue("String", "string")
```


### Cast a number to a particular type

Be careful!  Precision can be lost here.
```
 IniParser iniParser = new IniParser(new FileInputStream("samples/sample2.ini"));
  iniParser.getValue("Numbers", "long", int.class)
```

# Change Log

## [1.0](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.0) (2021-08-17)

**Bugs:**

- N/A
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
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini");
```

## Available options

### Get the sections

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Collection<String> sections = ini.getSections();
```

### Get the keys

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Collection<String> keys = ini.getKeys("FTPS")
```

### Get a value

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Object value = ini.getValue("String", "string")
```

### Get a section as Map

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Map<String, Object> value = ini.getSection("FTP")
```

### Cast a number to a particular type

Be careful!  Precision can be lost here.
```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 int value = ini.getValue("Numbers", "long", int.class)
```

### Put a value

```
 Ini ini = new Ini();
 ini.putValue("section", "key", "value")
```

### Write ini to file

```
 Ini ini = new Ini();
 ini.putValue("section", "key", "value")
 ini.store(new FileWriter("/tmp/file.ini"), "some comments at the top of the file");
```

# Change Log

## [1.1](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.1) (2021-09-20)

**Enhancements:**

- Created getSectionWithKeysWithPrefix and getSectionWithKeysWithRegex
- Added the ability to remove sections and remove values from ini.

## [1.0](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.0) (2021-08-20)

**Bugs:**

- N/A
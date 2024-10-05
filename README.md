# java-ini-parser [![Maven Central](https://img.shields.io/maven-central/v/com.github.vincentrussell/java-ini-parser.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.vincentrussell%22%20AND%20a:%22java-ini-parser%22) [![Build Status](https://travis-ci.org/vincentrussell/java-ini-parser.svg?branch=master)](https://travis-ci.org/vincentrussell/java-ini-parser)

java-ini-parser helps you parse ini files in java.   

## Maven

Add a dependency to `com.github.vincentrussell:java-ini-parser`. 

```
<dependency>
   <groupId>com.github.vincentrussell</groupId>
   <artifactId>java-ini-parser</artifactId>
   <version>1.6</version>
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

### Has a key

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 boolean has = ini.hasKey("String", "string")
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

### Put multiple key/value pairs into a section

```
 Ini ini = new Ini();
 Ini ini2 = new Ini();
 ini2.putValue("Char", "charKey", "b");
 ini2.putValue("Char", "characterKey", "z");
 ini2.putValue("String", "string", "Hello");
 ini.putValues("Char", ini2.getSection("Char"));
```

### Merge two ini files

```
 Ini ini = new Ini();
 Ini ini2 = new Ini();
 ini2.putValue("String", "user2", "Henry2");
 ini.merge(ini2);
```



### Write ini to file

```
 Ini ini = new Ini();
 ini.putValue("section", "key", "value")
 ini.store(new FileWriter("/tmp/file.ini"), "some comments at the top of the file");
```

### remove key from a section

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Object value = ini.removeSectionKey("String", "string")
```
### remove a section

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Object value = ini.removeSection("String")
```

### get section entries that have keys that start with prefix

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Map<String, Object> result = ini.getSectionWithKeysWithPrefix("Sample", "my.port.");
```
### get section entries that have keys that match a regex

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Map<String, Object> result = ini.getSectionWithKeysWithRegex("Sample", "^my\\.port\\.[\\d]{1}");
```

### get section entries that have keys that matches a filter

```
 Ini ini = new Ini();
 ini.load(new FileInputStream("samples/sample2.ini"));
 Map<String, Object> result = ini.getSectionWithKeysThatMatchFunction("Sample", entry -> Double.class.isInstance(entry.getValue()));
```

### multiline support

```
[group]
key=value
multilineKey = \
    this \
    is \
    a \
    multi-line \
    value
anotherKey = value
```

### string interpolation support from variables defined in ini section, system properties or environment variables

```
[section]
variable=some value
sysProperty=wouldn't you like to know that ${some.sys.property}
varKey=value is ${variable}
envVarKey=value is ${ENV_VAR1}
```

# Change Log

## [1.7](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.7) (2024-10-05)

**Enhancements:**

- N/A

**Bugs:**

- Fixed null pointer exception when using getValue with casting

## [1.6](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.6) (2023-10-28)

**Enhancements:**

- added the ability to merge two Ini files
- added the ability to put an entire map into an ini section (merge section)

**Bugs:**

- Fixed issues with writing out multiline comments


## [1.5](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.5) (2023-03-10)

**Enhancements:**

- added hasKey function

**Bugs:**

- Booleans not handled properly when calling ini.getValue("Boolean", "key", Boolean.class)
- Characters not handled properly when calling ini.getValue("Char", "charKey", char.class)

## [1.4](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.4) (2022-12-29)

**Enhancements:**

- Internal ini structure is now backed by LinkedHashMap instead of Hashmap to maintain insert order
- created Map<String, Object> getSectionWithKeysThatMatchFunction(String section, Predicate<Map.Entry<String, Object>> filter) method
- multiline support with ending lines with '\\'
- string interpolation support

**Bugs:**

- Escaped semicolons and pound signs not working properly

## [1.3](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.3) (2022-08-21)

**Enhancements:**

- Support for inline comments with ; and #
- Support for special characters that can be found in ini files like \b,\f,\r\t,etc

## [1.2](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.2) (2022-07-20)

**Enhancements:**

- Upgraded commons-io

## [1.1](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.1) (2021-09-06)

**Enhancements:**

- Created getSectionWithKeysWithPrefix and getSectionWithKeysWithRegex
- Added the ability to remove sections and remove values from ini.

## [1.0](https://github.com/vincentrussell/java-ini-parser/tree/java-ini-parser-1.0) (2021-08-20)

**Bugs:**

- N/A

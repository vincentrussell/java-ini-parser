package com.github.vincentrussell.ini;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.text.StringSubstitutor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 * This class is responsible for converting input streams into maps of maps that represent ini files.
 */
public class Ini {

    private static final String NO_SECTION = "_NO_SECTION";
    private static Pattern SECTION_PATTERN  = Pattern.compile( "\\s*\\[([^]]*)\\]\\s*" );
    private static Pattern  KEY_VALUE_PATTER = Pattern.compile( "\\s*([^=]*)=(.*)" );
    private static Pattern COMMENT_LINE = Pattern.compile("^[;|#].*");
    private Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();


    /**
     * default constructor
     */
    public Ini() {

    }

    /**
     * default constructor with an {@link InputStream}
     * @param inputStream the ini file as an input stream
     * @throws IOException thrown when there is an error processing the ini.
     */
    public void load(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new FileNotFoundException("inputStream is null");
        }
        MutableObject<String> section = new MutableObject<>(NO_SECTION);
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            parseIniFile(section, bufferedReader);
        }
    }

    /**
     * default constructor with {@link File}
     * @param file the ini file
     * @throws IOException thrown when there is an error processing the ini.
     */
    public void load(final File file) throws IOException {
        load(new FileInputStream(file));
    }

    /**
     * default constructor with {@link String}
     * @param string the contents of the ini file
     * @throws IOException thrown when there is an error processing the ini.
     */
    public void load(final String string) throws  IOException {
        load(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * merge this {@link Ini} with another {@link Ini}.  If there are any merge conflicts the passed in {@link Ini}
     * will take precedence.
     * @param ini  the ini to merge
     */
    public void merge(final Ini ini) {
        Collection<String> sectionKeys = firstNonNull(ini.getSections(), Collections.emptyList());
        for (String sectionKey : sectionKeys) {
            Map<String, Object> section = firstNonNull(ini.getSection(sectionKey), Collections.emptyMap());
            Set<Map.Entry<String, Object>> entrySet = section.entrySet();
            for (Map.Entry<String, Object> entry : entrySet ) {
                putValue(sectionKey, entry.getKey(), entry.getValue());
            }
        }
    }

    private void parseIniFile(final MutableObject<String> section,
                              final BufferedReader bufferedReader) throws IOException {

        final Map<String, Object> variables = new HashMap<>();
        variables.putAll(System.getenv());
        variables.putAll(new HashMap<String, Object>((Map) System.getProperties()));

        String line = null;
        String multilineValue = null;
        String key = null;
        Map<String, StringSubstitutor> stringStringSubstitutorPerSection = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null ) {

            final Matcher commentMatcher = COMMENT_LINE.matcher(line);
            if (commentMatcher.matches()) {
                continue;
            }

            final Matcher sectionMather = SECTION_PATTERN.matcher(line);
            if (sectionMather.matches()) {
                section.setValue(sectionMather.group(1).trim());
                continue;
            }

            if (line != null) {
                line = line.replaceAll("[^\\\\]{1}#.+", "")
                        .replaceAll("[^\\\\]{1};.+", "")
                        .replaceAll("\\\\([;#]{1})", "$1");
            }

            if (StringUtils.isEmpty(line)) {
                continue;
            }

            final Matcher keyValueMatcher = KEY_VALUE_PATTER.matcher(line);
            if (keyValueMatcher.matches()) {
                key = keyValueMatcher.group(1).trim();
                String value = handleEscapedAndSpecialCharacters(keyValueMatcher.group(2).trim()).replaceAll("\\\\$", "\n");
                multilineValue = value;
                if (line.endsWith("\\")) {
                    continue;
                }
            } else if (line.endsWith("\\")) {
                multilineValue+= line.replaceAll("\\\\$", "\n");
                continue;
            } else if (multilineValue != null){
                multilineValue+= line.replaceAll("\\\\$", "\n");
            }


            if (StringUtils.isNotEmpty(key)) {
                Object normalizedValue = normalizeValue(multilineValue);

                if (String.class.isInstance(normalizedValue) && line.contains("${")) {
                    StringSubstitutor substitutor = stringStringSubstitutorPerSection.computeIfAbsent(
                            section.getValue(), s -> {
                                StringSubstitutor stringSubstitutor = new StringSubstitutor(
                                        new DelegateMapWrapper(variables, getMapForSection(section)));
                                stringSubstitutor.setEnableSubstitutionInVariables(true);
                                return stringSubstitutor;
                            });

                    normalizedValue = substitutor.replace(normalizedValue.toString());
                }

                getMapForSection(section).put(key, normalizedValue);
            }
            key = null;
            multilineValue = null;
        }
    }

    private Map<String, Object> getMapForSection(final MutableObject<String> section) {
        return resultMap.computeIfAbsent(
                section.getValue(), s1 -> new LinkedHashMap<>());
    }

    private String handleEscapedAndSpecialCharacters(final String string) {
        return string.replaceAll("^\"(.*)\"$", "$1")
                .replaceAll("^'(.*)'$", "$1")
                .replaceAll("\\\\\"", "\"")
                .replaceAll("\\\\\'", "\'")
                .replaceAll("\\\\\\\\", "\\\\")
                .replaceAll("\\\\t", "\t")
                .replaceAll("\\\\r", "\r")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\0", "\0")
                .replaceAll("\\\\b", "\b")
                .replaceAll("\\\\f", "\f")
                .replaceAll("\\\\#", "#")
                .replaceAll("\\\\=", "=")
                .replaceAll("\\\\:", ":");
    }

    private Object normalizeValue(final String value) {
        if (NumberUtils.isCreatable(value)) {
            try {
                return NumberFormat.getInstance().parse(value);
            } catch (ParseException e) {
                return value;
            }
        }
        return value;
    }

     /**
     * return true if the section and section key exists, false otherwise
     * @param section the desired section
     * @param key the key in the section
     * @return true if the section and section key exists, false otherwise
     */
    public boolean hasKey(final String section, final String key) {
        return (resultMap.containsKey(section) && resultMap.get(section).containsKey(key));
    }
    
    /**
     * return a value from the nested structure as an object
     * @param section the desired section
     * @param key the desired key in the section
     * @return the value from the nested structure and cast it to the specified type.
     */
    public Object getValue(final String section, final String key) {
        return getValue(section, key, Object.class);
    }

    /**
     * return a value from the nested structure and cast it to the specified type.
     * @param section the desired section
     * @param key the key in the section
     * @param type the desired type
     * @param <T> the generic for the type
     * @return the value from the nested structure and cast it to the specified type.
     */
    public <T> T getValue(final String section, final String key, final Class<T> type) {
        return cast(resultMap.getOrDefault(section, new LinkedHashMap<>()).get(key), type);
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(final Object o, final Class<T> type) {
        if (o == null) {
            return null;
        }
        if (type.isInstance(o)) {
            return (T) o;
        } else if (String.class.equals(type)) {
            return (T) o.toString();
        } else if (o != null && ClassUtils.isPrimitiveWrapper(o.getClass())
                && o.getClass().equals(ClassUtils.primitiveToWrapper(type))) {
            return (T) o;
        } else if (Number.class.isInstance(o)) {
            if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
                return (T) (Long) ((Number)o).longValue();
            } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
                return (T) (Integer) ((Number)o).intValue();
            } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
                return (T) (Double) ((Number)o).doubleValue();
            } else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
                return (T) (Float) ((Number)o).floatValue();
            } else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
                return (T) (Short) ((Number)o).shortValue();
            } else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
                return (T) (Byte) ((Number) o).byteValue();
            }
        } else if (o != null && (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))) {
            return (T) Boolean.valueOf(o.toString());
        } else if (o != null && Character.class.isAssignableFrom(type)) {
            return (T) Character.valueOf(o.toString().charAt(0));
        } else if (o != null && char.class.isAssignableFrom(type)) {
            return (T) Character.valueOf(o.toString().charAt(0));
        }
        return (T) o;
    }

    /**
     * get the sections from the ini file.
     * @return the sections as a collection.
     */
    public Collection<String> getSections() {
        return resultMap.keySet();
    }

    /**
     * get the keys from a particular section.
     * @param section the desired section
     * @return the keys for a section or an empty collection.
     */
    public Collection<String> getKeys(final String section) {
        return resultMap.getOrDefault(section, new LinkedHashMap<>()).keySet();
    }

    /**
     * return the section as a map.
     * @param section the desired section
     * @return null if not found
     */
    public Map<String, Object> getSection(final String section) {
        Map<String, Object> map = resultMap.get(section);
        if (map == null) {
            return null;
        }
        return Collections.unmodifiableMap(map);
    }
    /**
     * return the section as a map; sorted by key
     * @param section the desired section
     * @return null if not found
     */
    public Map<String, Object> getSectionSortedByKey(final String section) {
        Map<String, Object> map = resultMap.get(section);
        if (map == null) {
            return null;
        }
        return Collections.unmodifiableMap(new TreeMap<>(map));
    }


    /**
     * get a subset of a section where all the keys match the provided prefix
     * @param section the desired section
     * @param prefix that the key starts with
     * @return a subset of a section where all the keys match the provided prefix
     */
    public Map<String, Object> getSectionWithKeysWithPrefix(final String section, final String prefix) {
        return getSectionWithKeysThatMatchFunction(section, entry -> entry.getKey().startsWith(prefix));
    }


    /**
     * get a subset of a section where all the keys match the provided filter
     * @param section
     * @param filter the filter that the Map.Entry in she specified section must match
     * @return
     */
    public Map<String, Object> getSectionWithKeysThatMatchFunction(final String section,
                                                       final Predicate<Map.Entry<String, Object>> filter) {
        final Map<String, Object> stringObjectMap = firstNonNull(resultMap.get(section), new LinkedHashMap<>());
        return stringObjectMap.entrySet().stream()
                .filter(map -> filter.test(map))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }


    /**
     * get a subset of a section where all the keys match the provided prefix
     * @param section the desired section
     * @param regex that matches the key
     * @return a subset of a section where all the keys match the provided prefix
     */
    public Map<String, Object> getSectionWithKeysWithRegex(final String section, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        return getSectionWithKeysThatMatchFunction(section, entry -> pattern.matcher(entry.getKey()).matches());
    }

    /**
     * store new values in the ini
     * @param section the desired section
     * @param key the key in the section
     * @param value the value to store for that key
     */
    public void putValue(final String section, final String key, final Object value) {
        resultMap.computeIfAbsent(section, s -> new LinkedHashMap<>()).put(key, value);
    }

    /**
     * put multiple key/value pairs into a section.
     * @param sectionEntries
     */
    public void putValues(final String sectionKey, Map<String, Object> sectionEntries) {
        resultMap.computeIfAbsent(sectionKey, s -> new LinkedHashMap<>()).putAll(sectionEntries);
    }

    /**
     * store the ini to an outputstream
     * @param outputStream the outputstream to write to
     * @param comments the comments to put at the top of the file
     * @throws IOException if there is an error writing to the outputstream
     */
    public void store(final OutputStream outputStream, final String comments) throws IOException {
        store(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)),
                comments);
    }

    /**
     * store the ini to a writer
     * @param writer the writer to use
     * @param comments commments to put at the top of the file.
     * @throws IOException if there is an error writing to the writer
     */
    public void store(final Writer writer, final String comments) throws IOException {
        try (BufferedWriter bufferedWriter = (writer instanceof BufferedWriter)
                ? (BufferedWriter) writer : new BufferedWriter(writer)) {
            doStore(bufferedWriter, comments);
        }
    }

    private void doStore(final BufferedWriter bufferedWriter, final String comments) throws IOException {
        writeComments(bufferedWriter, comments);
        for (Map.Entry<String, Map<String, Object>> section : resultMap.entrySet()) {
            bufferedWriter.write("[" + section.getKey() + "]");
            bufferedWriter.newLine();
            for (Map.Entry<String, Object> sectionEntry : section.getValue().entrySet()) {
                bufferedWriter.write(sectionEntry.getKey() + " = ");
                bufferedWriter.write(sectionEntry.getValue().toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
        }
    }

    private static void writeComments(final BufferedWriter bw, final String comments)
            throws IOException {
        if (StringUtils.isNotEmpty(comments)) {
            try (BufferedReader bufferedReader = new BufferedReader(new StringReader(comments))) {
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    bw.write("#");
                    IOUtils.write(line, bw);
                    bw.newLine();
                }
            }
        }
    }

    /**
     * Removes the specified section.
     *
     * The ini object will not contain a mapping for the specified section once the call returns.
     * @param section the section to remove
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    public Map<String, Object> removeSection(final String section) {
        return resultMap.remove(section);
    }

    /**
     * Removes the specified key in the specified section.
     * The ini object will not contain a mapping for the specified section once the call returns.
     * @param section the section to remove
     * @param key the key in that section to remove
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    public Object removeSectionKey(final String section, final String key) {
        return resultMap.getOrDefault(section, new LinkedHashMap<>()).remove(key);
    }
}

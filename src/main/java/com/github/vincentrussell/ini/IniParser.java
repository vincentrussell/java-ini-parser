package com.github.vincentrussell.ini;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableObject;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniParser {

    private static final String NO_SECTION = "_NO_SECTION";
    private static Pattern SECTION_PATTERN  = Pattern.compile( "\\s*\\[([^]]*)\\]\\s*" );
    private static Pattern  KEY_VALUE_PATTER = Pattern.compile( "\\s*([^=]*)=(.*)" );
    private static Pattern COMMENT_LINE = Pattern.compile("^[;|#].*");

    private Map<String, Map<String, Object>> resultMap = new HashMap<>();

    public IniParser(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new FileNotFoundException("inputStream is null");
        }
        MutableObject<String> section = new MutableObject<>(NO_SECTION);
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            parseIniFile(section, bufferedReader);
        }
    }

    private void parseIniFile(final MutableObject<String> section,
                              final BufferedReader bufferedReader) throws IOException {
        String line;
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

            final Matcher keyValueMatcher = KEY_VALUE_PATTER.matcher(line);
            if (keyValueMatcher.matches()) {
                final String key = keyValueMatcher.group(1).trim();
                final String value = keyValueMatcher.group(2).trim();

                resultMap.computeIfAbsent(section.getValue(), s -> new HashMap<>()).put(key, normalizeValue(value));
                continue;
            }
        }
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

    public Object getValue(final String section, final String key) {
        return getValue(section, key, Object.class);
    }

    public <T> T getValue(final String section, final String key, final Class<T> type) {
        return cast(resultMap.getOrDefault(section, new HashMap<>()).get(key), type);
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object o, Class<T> type) {
        if (type.isInstance(o)) {
            return (T) o;
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
                return (T) (Byte) ((Number)o).byteValue();
            }
        }
        return (T) o;
    }

    public Collection<String> getSections() {
        return resultMap.keySet();
    }

    public Collection<String> getKeys(String section) {
        return resultMap.getOrDefault(section, new HashMap<>()).keySet();
    }
}

package com.github.vincentrussell.ini;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DelegateMapWrapper extends AbstractMap<String, Object> {

    final Map<String, Object>[] sourceMaps;

    public DelegateMapWrapper(final Map<String, Object>... sourceMaps) {
        this.sourceMaps = sourceMaps;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entrySet = new HashSet<>();
        for (Map<String, Object> map : sourceMaps) {
            entrySet.addAll(map.entrySet());
        }
        return entrySet;
    }
}

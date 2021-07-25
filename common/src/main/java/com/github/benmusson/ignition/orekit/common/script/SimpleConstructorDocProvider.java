package com.github.benmusson.ignition.orekit.common.script;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SimpleConstructorDocProvider implements ScriptConstructorDocProvider {

    public String getConstructorDescription(String s, Constructor<?> c) {
        String className = s.substring(s.lastIndexOf(46) + 1);
        return String.format("Creates a new %s instance.", className);
    }

    public Map<String, String> getParameterDescriptions(String s, Constructor<?> c) {
        Map<String, String> params = new LinkedHashMap<>();
        for (Parameter p: c.getParameters()) {
            params.put(p.getType().getSimpleName() + " " + p.getType().getSimpleName().toLowerCase(Locale.ROOT), "no description");
        }
        return params;
    }

    public String getReturnValueDescription(String s, Constructor<?> c) {
        String className = s.substring(s.lastIndexOf(46) + 1);
        return String.format("A new %s instance.", className);
    }
}

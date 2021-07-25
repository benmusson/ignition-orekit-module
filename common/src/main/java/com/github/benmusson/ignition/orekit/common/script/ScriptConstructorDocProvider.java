package com.github.benmusson.ignition.orekit.common.script;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

public interface ScriptConstructorDocProvider {

    ScriptConstructorDocProvider NO_DOC_PROVIDER = new ScriptConstructorDocProvider() {
        public String getReturnValueDescription(String path, Constructor<?> c) {
            return "";
        }

        public Map<String, String> getParameterDescriptions(String path, Constructor<?> c) {
            return Collections.emptyMap();
        }

        public String getConstructorDescription(String path, Constructor<?> c) {
            return "";
        }
    };

    String getConstructorDescription(String path, Constructor<?> c);

    Map<String, String> getParameterDescriptions(String path, Constructor<?> c);

    String getReturnValueDescription(String path, Constructor<?> c);
}

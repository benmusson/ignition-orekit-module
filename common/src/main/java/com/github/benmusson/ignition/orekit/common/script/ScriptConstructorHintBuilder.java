package com.github.benmusson.ignition.orekit.common.script;

import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunctionHint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Map;

public class ScriptConstructorHintBuilder {
    public static final String RIGHTWARDS_ARROW = "→";

    public ScriptConstructorHintBuilder() {

    }

    public static ScriptFunctionHint build(String path, Constructor<?> c, ScriptConstructorDocProvider docProvider) {
        return new ScriptConstructorHintBuilder.Hint(
                getAutocompleteText(path, c),
                getConstructorSignature(path, c),
                docProvider.getConstructorDescription(path, c),
                docProvider.getParameterDescriptions(path, c),
                docProvider.getReturnValueDescription(path, c));
    }

    private static String getAutocompleteText(String path, Constructor<?> c) {
        return path;
    }

    private static String getConstructorSignature(String path, Constructor<?> c) {
        Annotation[][] parameterAnnotations = c.getParameterAnnotations();
        Class<?>[] parameterTypes = c.getParameterTypes();
        int parameterCount = parameterTypes.length;
        StringBuilder constructorSignature = new StringBuilder();
        String className = getAutocompleteText(path, c);
        className = className.substring(className.lastIndexOf(46) + 1);
        constructorSignature.append(className);
        constructorSignature.append("(");

        for(int i = 0; i < parameterCount; ++i) {
            Annotation[] annotations = parameterAnnotations[i];
            ScriptArg pAnnotation = (ScriptArg)findAnnotation(annotations, ScriptArg.class);
            if (pAnnotation != null) {
                String parameterName = pAnnotation.value();
                constructorSignature.append(parameterName);
            } else {
                constructorSignature.append(parameterTypes[i].getSimpleName());
            }

            constructorSignature.append(", ");
        }

        if (parameterCount > 0) {
            constructorSignature.setLength(constructorSignature.length() - 2);
        }

        constructorSignature.append(")");
        return constructorSignature.toString();
    }

    private static <T extends Annotation> T findAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation a: annotations) {
            if (a.annotationType() == clazz) {
                return (T) a;
            }
        }

        return null;
    }

    private static class Hint implements ScriptFunctionHint {
        final String autocompleteText;
        final String methodSignature;
        final String methodDescription;
        final Map<String, String> parameterDescriptions;
        final String returnValueDescription;

        public Hint(String autocompleteText, String methodSignature, String methodDescription, Map<String, String> parameterDescriptions, String returnValueDescription) {
            this.autocompleteText = autocompleteText;
            this.methodSignature = methodSignature;
            this.methodDescription = methodDescription;
            this.parameterDescriptions = parameterDescriptions;
            this.returnValueDescription = returnValueDescription;
        }

        public String getAutocompleteText() {
            return this.autocompleteText;
        }

        public String getMethodSignature() {
            return this.methodSignature;
        }

        public String getMethodDescription() {
            return this.methodDescription;
        }

        public Map<String, String> getParameterDescriptions() {
            return this.parameterDescriptions;
        }

        public String getReturnValueDescription() {
            return this.returnValueDescription;
        }

        public String toString() {
            return this.autocompleteText;
        }
    }

}

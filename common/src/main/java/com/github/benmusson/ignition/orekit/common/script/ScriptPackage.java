package com.github.benmusson.ignition.orekit.common.script;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptPackage {
    private final String packagePath;
    private final List<String> blacklist;
    private final ClassLoader classLoader;
    private final ScriptConstructorDocProvider docProvider;

    private ScriptPackage(ScriptPackageBuilder builder) {
        this.packagePath = builder.packagePath;
        this.blacklist = builder.blacklist;
        this.classLoader = builder.classLoader;
        this.docProvider = builder.docProvider;

    }

    public String getPackagePath() {
        return packagePath;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ScriptConstructorDocProvider getDocProvider() {
        return docProvider;
    }

    public ImmutableSet<ClassPath.ClassInfo> getClasses() throws IOException {
        List<ClassPath.ClassInfo> classes = new ArrayList<>();
        for (ClassPath.ClassInfo c: this.getAllClasses()) {
            if (!this.getBlacklist().contains(c.getName())) {
                classes.add(c);
            }
        }
        return ImmutableSet.<ClassPath.ClassInfo>builder().addAll(classes).build();
    }

    public ImmutableSet<ClassPath.ClassInfo> getAllClasses() throws IOException {
        return ClassPath.from(this.getClassLoader()).getTopLevelClassesRecursive(this.getPackagePath());
    }

    public static class ScriptPackageBuilder {
        private String packagePath;
        private List<String> blacklist = Collections.emptyList();
        private ClassLoader classLoader = ScriptPackage.class.getClassLoader();
        private ScriptConstructorDocProvider docProvider = ScriptConstructorDocProvider.NO_DOC_PROVIDER;

        public ScriptPackageBuilder() {

        }

        public ScriptPackage build() {
            return new ScriptPackage(this);
        }

        public ScriptPackageBuilder packagePath(String packagePath) {
            this.packagePath = packagePath;
            return this;
        }

        public ScriptPackageBuilder blacklist(List<String> blacklist) {
            this.blacklist = blacklist;
            return this;
        }

        public ScriptPackageBuilder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public ScriptPackageBuilder docProvider(ScriptConstructorDocProvider docProvider) {
            this.docProvider = docProvider;
            return this;
        }

    }

}

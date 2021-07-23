package com.github.benmusson.ignition.orekit.gateway.script;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import org.python.core.Py;
import org.python.core.PyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ScriptManagerPackageMounter {

    private final static Logger logger = LoggerFactory.getLogger(ScriptManagerPackageMounter.class);

    public static void addScriptPackage(ScriptManager manager, String path, String packagePath, List<String> blacklist) {
        addScriptPackage(manager, path, packagePath, ScriptManagerPackageMounter.class.getClassLoader(), blacklist);
    };

    public static void addScriptPackage(ScriptManager manager, String path, String packagePath, ClassLoader classLoader, List<String> blacklist) {
        logger.info(String.format("Mounting package %s to namespace %s...", packagePath, path));

        Integer failures = 0;

        try {
            ImmutableSet<ClassPath.ClassInfo> classSet = ClassPath.from(classLoader).getTopLevelClassesRecursive(packagePath);
            for (ClassPath.ClassInfo classInfo: classSet) {
                if (!blacklist.contains(classInfo.getName())) {

                    String namespace = classInfo.getName().replaceAll(packagePath, path);

                    try {
                        Class<?> clazz =  Class.forName(classInfo.getName(), true, classLoader);
                        PyObject pyObj = Py.java2py(clazz);
                        addModuleObject(manager, namespace, pyObj);

                        logger.trace(String.format("Loaded class %s to %s", classInfo.getSimpleName(), namespace));
                    } catch (Exception e) {
                        failures++;
                        logger.warn(String.format("Exception occured loading class %s to %s.", classInfo.getSimpleName(), namespace), e);
                    }
                }
            }

            if (failures > 0) {
                logger.warn(String.format("Failed to mount %d packages from %s to namespace %s.", failures, packagePath, path));
            } else {
                logger.info(String.format("Successfully mounted package %s to namespace %s.", packagePath, path));
            }

        } catch (Exception e) {
            logger.error(String.format("Unrecoverable exception occurred mounting package %s to namespace %s.", packagePath, path));
        }
    }

    public static void addModuleObject(ScriptManager manager, String path, PyObject pyObj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> params[] = {String.class, PyObject.class};
        Object[] obj = {path, pyObj};

        Method method = ScriptManager.class.getDeclaredMethod("addModuleObject", params);
        method.setAccessible(true);
        method.invoke(manager, obj);
    }
}

package com.github.benmusson.ignition.orekit.common.script;

import com.google.common.reflect.ClassPath;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunctionHint;
import org.python.core.PyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExtendedScriptManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    ScriptManager manager;

    public ExtendedScriptManager(ScriptManager manager) {
        this.manager = manager;
    }

    public void addScriptPackage(ScriptPackage scriptPackage, String path) {
        logger.trace(String.format("Mounting package %s to namespace %s...",
                scriptPackage.getPackagePath(),
                path));

        int failures = 0;

        try {
            for (ClassPath.ClassInfo classInfo: scriptPackage.getClasses()) {

                String namespace = classInfo.getName().replaceAll(
                        scriptPackage.getPackagePath(), path);

                try {
                    Class<?> clazz = Class.forName(classInfo.getName(), true, scriptPackage.getClassLoader());
                    this.addScriptClass(namespace, clazz, scriptPackage.getDocProvider());

                } catch (Exception e) {
                    failures++;
                    logger.warn(String.format("Exception occurred loading class %s to %s.",
                            classInfo.getSimpleName(), namespace), e);
                }

            }

            if (failures > 0) {
                logger.warn(String.format("Failed to mount %d packages from %s to namespace %s.",
                        failures, scriptPackage.getPackagePath(), path));
            } else {
                logger.trace(String.format("Successfully mounted package %s to namespace %s.",
                        scriptPackage.getPackagePath(), path));
            }

        } catch (Exception e) {
            logger.error(String.format("Unrecoverable exception occurred mounting package %s to namespace %s.",
                    scriptPackage.getPackagePath(), path));
        }

    }



    public abstract void addScriptClass(String namespace, Class<?> clazz, ScriptConstructorDocProvider docProvider)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    
    protected void addModuleObject(String path, PyObject pyObj)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] params = {String.class, PyObject.class};
        Object[] obj = {path, pyObj};

        Method method = ScriptManager.class.getDeclaredMethod("addModuleObject", params);
        method.setAccessible(true);
        method.invoke(manager, obj);

    }

    public void addHint(String path, Constructor<?> c, ScriptConstructorDocProvider docProvider)
        throws NoSuchFieldException, IllegalAccessException {
        String root = null;
        String[] pathElements = path.split("\\.");
        if (pathElements.length > 1) {
            root = pathElements[0];
        }

        Field f = ScriptManager.class.getDeclaredField("hints");
        f.setAccessible(true);

        Map<String, List<ScriptFunctionHint>> hints = (Map<String, List<ScriptFunctionHint>>) f.get(manager);

        List<ScriptFunctionHint> hintsForRoot = hints.computeIfAbsent(root, (k) -> new ArrayList<>());
        hintsForRoot.add(ScriptConstructorHintBuilder.build(path, c, docProvider));

    }
}

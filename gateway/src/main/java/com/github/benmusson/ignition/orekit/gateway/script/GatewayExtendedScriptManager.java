package com.github.benmusson.ignition.orekit.gateway.script;

import com.github.benmusson.ignition.orekit.common.script.ExtendedScriptManager;
import com.github.benmusson.ignition.orekit.common.script.ScriptConstructorDocProvider;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import org.python.core.Py;
import org.python.core.PyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class GatewayExtendedScriptManager extends ExtendedScriptManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GatewayExtendedScriptManager(ScriptManager manager) {
        super(manager);
    }

    @Override
    public void addScriptClass(String namespace, Class<?> clazz, ScriptConstructorDocProvider docProvider)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PyObject pyObj = Py.java2py(clazz);
        addModuleObject(namespace, pyObj);

        logger.trace(String.format("Loaded class %s to %s", clazz.getName(), namespace));

    }
}

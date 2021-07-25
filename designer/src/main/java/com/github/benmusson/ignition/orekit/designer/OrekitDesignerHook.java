package com.github.benmusson.ignition.orekit.designer;

import com.github.benmusson.ignition.orekit.client.data.ClientDataProviderManager;
import com.github.benmusson.ignition.orekit.client.script.ClientDataScriptModule;
import com.github.benmusson.ignition.orekit.client.script.ClientExtendedScriptManager;
import com.github.benmusson.ignition.orekit.common.script.ExtendedScriptManager;
import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.github.benmusson.ignition.orekit.common.script.ScriptPackage;
import com.github.benmusson.ignition.orekit.common.script.SimpleConstructorDocProvider;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class OrekitDesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DesignerContext context;
    private DefaultDataProviderManager manager;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) {
        try {
            this.context = context;
            manager = ClientDataProviderManager.get(context);
            manager.addDefaultProviders();

            logger.info("Orekit module setup.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            manager.removeDefaultProviders();

            logger.info("Orekit module stopped.");
        } catch (Exception e) {
            logger.error("Error shutting down Orekit module.", e);
        }
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);

        manager.addScriptModule(
                "system.orekit",
                new ClientDataScriptModule(context),
                new PropertiesFileDocProvider()
        );

        ExtendedScriptManager extendedManager = new ClientExtendedScriptManager(manager);
        extendedManager.addScriptPackage(
                new ScriptPackage.ScriptPackageBuilder()
                        .packagePath("org.orekit")
                        .blacklist(Collections.singletonList("org.orekit.compiler.plugin.DefaultDataContextPlugin"))
                        .docProvider(new SimpleConstructorDocProvider())
                        .classLoader(this.getClass().getClassLoader())
                        .build(),
                "system.orekit");

        extendedManager.addScriptPackage(
                new ScriptPackage.ScriptPackageBuilder()
                        .packagePath("org.hipparchus")
                        .docProvider(new SimpleConstructorDocProvider())
                        .classLoader(this.getClass().getClassLoader())
                        .build(),
                "system.hipparchus");

    }
}

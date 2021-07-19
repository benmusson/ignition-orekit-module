package com.github.benmusson.ignition.orekit.designer;

import com.github.benmusson.ignition.orekit.client.data.ClientDataProviderManager;
import com.github.benmusson.ignition.orekit.client.script.ClientDataScriptModule;
import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }
}

package com.github.benmusson.ignition.orekit.designer;

import com.github.benmusson.ignition.orekit.client.data.ClientDataContext;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrekitDesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DesignerContext context;
    private ClientDataContext dataContext;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) {
        try {
            this.context = context;
            reloadConfig(context);

            logger.info("Orekit module setup.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }
    }

    public void reloadConfig(ClientContext context) {
        this.dataContext = new ClientDataContext(context);
    }

    @Override
    public void shutdown() {
        try {

            logger.info("Orekit module stopped.");
        } catch (Exception e) {
            logger.error("Error shutting down Orekit module.", e);
        }
    }
}

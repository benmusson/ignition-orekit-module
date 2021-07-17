package com.github.benmusson.ignition.orekit.client;

import com.github.benmusson.ignition.orekit.client.data.ClientDataContext;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Hook for projects which target Vision
 *
 * @since <DATE>
 */
public class OrekitClientHook extends AbstractClientModuleHook {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ClientContext context;

    public ClientDataContext dataContext;

    @Override
    public void startup(ClientContext context, LicenseState activationState) {
        try {
            this.context = context;
            init();

            logger.info("Orekit module setup.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }

    }

    public void init() {
        reloadConfig(this.context);
    }

    public void reloadConfig(ClientContext context) {
        try {
            this.dataContext = new ClientDataContext(context);

        } catch (Exception e) {
            logger.error("Error starting up Orekit module.", e);
        }
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

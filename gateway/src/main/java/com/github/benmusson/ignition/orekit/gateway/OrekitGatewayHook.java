package com.github.benmusson.ignition.orekit.gateway;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.benmusson.ignition.orekit.common.api.v1.EndpointProvider;
import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.github.benmusson.ignition.orekit.gateway.api.RouteHandlerMounter;
import com.github.benmusson.ignition.orekit.gateway.api.v1.GatewayRouteHandler;
import com.github.benmusson.ignition.orekit.gateway.data.GatewayDataProviderManager;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfigurationPage;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrekitGatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DefaultDataProviderManager manager;
    private RouteHandlerMounter api;

    @Override
    public void setup(GatewayContext context) {
        try {
            this.manager = new GatewayDataProviderManager(context);
            this.api = new GatewayRouteHandler(context);

            BundleUtil.get().addBundle("orekit", this.getClass(), "orekit");
            context.getSchemaUpdater().updatePersistentRecords(OrekitInternalConfiguration.META);

            logger.info("Orekit module setup.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }
    }

    @Override
    public void startup(LicenseState activationState) {
        try {
            manager.addDefaultProviders();

            logger.info("Orekit module started.");
        } catch (Exception e) {
            logger.error("Error starting up Orekit module.", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            manager.removeDefaultProviders();

            BundleUtil.get().removeBundle("orekit");

            logger.info("Orekit module stopped.");
        } catch (Exception e) {
            logger.error("Error stopping Orekit module.", e);
        }
    }

    @Override
    public List<? extends IConfigTab> getConfigPanels() {
       return Collections.singletonList(OrekitInternalConfigurationPage.CONFIG_TAB);
    }

    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(OrekitInternalConfigurationPage.OREKIT_CATEGORY);
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }

    @Override
    public boolean isMakerEditionCompatible() {
        return true;
    }

    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of(EndpointProvider.MOUNT_ALIAS);
    }

    @Override
    public void mountRouteHandlers(RouteGroup routes) {
        api.mountRouteHandlers(routes);
    }
}

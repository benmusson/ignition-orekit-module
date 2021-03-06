package com.github.benmusson.ignition.orekit.gateway;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.benmusson.ignition.orekit.common.api.v1.EndpointProvider;
import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.github.benmusson.ignition.orekit.common.script.ExtendedScriptManager;
import com.github.benmusson.ignition.orekit.common.script.ScriptPackage;
import com.github.benmusson.ignition.orekit.gateway.api.RouteHandlerMounter;
import com.github.benmusson.ignition.orekit.gateway.api.v1.GatewayRouteHandler;
import com.github.benmusson.ignition.orekit.gateway.data.GatewayDataProviderManager;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfigurationPage;
import com.github.benmusson.ignition.orekit.gateway.script.GatewayExtendedScriptManager;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
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
            logger.trace("Orekit module beginning setup...");

            logger.trace("Creating new data provider manager...");
            this.manager = new GatewayDataProviderManager(context);

            logger.trace("Creating new route handler...");
            this.api = new GatewayRouteHandler(context);

            BundleUtil.get().addBundle("orekit", this.getClass(), "orekit");
            context.getSchemaUpdater().updatePersistentRecords(OrekitInternalConfiguration.META);

            logger.trace("Orekit module setup complete.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }
    }

    @Override
    public void startup(LicenseState activationState) {
        try {
            logger.trace("Orekit module starting...");

            logger.trace("Adding default providers...");
            manager.addDefaultProviders();

            logger.info("Orekit module started.");
        } catch (Exception e) {
            logger.error("Error starting up Orekit module.", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            logger.trace("Orekit module stopping...");

            logger.trace("Removing default providers...");
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

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);

        ExtendedScriptManager extendedManager = new GatewayExtendedScriptManager(manager);
        extendedManager.addScriptPackage(
                new ScriptPackage.ScriptPackageBuilder()
                        .packagePath("org.orekit")
                        .blacklist(Collections.singletonList("org.orekit.compiler.plugin.DefaultDataContextPlugin"))
                        .build(),
                "system.orekit");

        extendedManager.addScriptPackage(
                new ScriptPackage.ScriptPackageBuilder()
                        .packagePath("org.hipparchus")
                        .build(),
                "system.hipparchus");
    }
}

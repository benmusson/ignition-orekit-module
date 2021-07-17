package com.github.benmusson.ignition.orekit.gateway;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.benmusson.ignition.orekit.common.api.v1.OrekitWebAPIAccess;
import com.github.benmusson.ignition.orekit.gateway.api.RouteHandlerMounter;
import com.github.benmusson.ignition.orekit.gateway.api.v1.OrekitWebEndpoint;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfigurationPage;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.execution.ExecutionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IRecordListener;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which is instantiated by the Ignition platform when the module is loaded in the gateway scope.
 */
public class OrekitGatewayHook extends AbstractGatewayModuleHook implements IRecordListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GatewayContext context;
    private ExecutionManager executor;

    private RouteHandlerMounter api;

    /**
     * Called to before startup. This is the chance for the module to add its extension points and update persistent
     * records and schemas. None of the managers will be started up at this point, but the extension point managers will
     * accept extension point types.
     */
    @Override
    public void setup(GatewayContext context) {
        this.context = context;
        this.executor = context.getExecutionManager();
        this.api = new OrekitWebEndpoint(context);

        BundleUtil.get().addBundle("orekit", this.getClass(), "orekit");

        OrekitInternalConfiguration.META.addRecordListener(this);

        try {
            context.getSchemaUpdater().updatePersistentRecords(OrekitInternalConfiguration.META);

            logger.info("Orekit module setup.");
        } catch (Exception e) {
            logger.error("Error setting up Orekit module.", e);
        }
    }

    /**
     * Called to initialize the module. Will only be called once. Persistence interface is available, but only in
     * read-only mode.
     */
    @Override
    public void startup(LicenseState activationState) {
        try {
            init();

            logger.info("Orekit module started.");
        } catch (Exception e) {
            logger.error("Error starting up Orekit module.", e);
        }
    }

    /**
     * Called to shutdown this module. Note that this instance will never be started back up - a new one will be created
     * if a restart is desired
     */
    @Override
    public void shutdown() {
        try {
            BundleUtil.get().removeBundle("orekit");


            logger.info("Orekit module stopped.");
        } catch (Exception e) {
            logger.error("Error stopping Orekit module.", e);
        }
    }

    private void init() {
        OrekitInternalConfiguration config = OrekitInternalConfiguration.getConfig(context);

        if (config == null) {
            config = context.getPersistenceInterface().createNew(OrekitInternalConfiguration.META);
            config.installDefaultValues();
            context.getPersistenceInterface().save(config);
        }

        reloadConfig(config);
    }

    /**
     * A list (may be null or empty) of panels to display in the config section. Note that any config panels that are
     * part of a category that doesn't exist already or isn't included in {@link #getConfigCategories()} will
     * <i>not be shown</i>.
     */
    @Override
    public List<? extends IConfigTab> getConfigPanels() {
       return Collections.singletonList(OrekitInternalConfigurationPage.CONFIG_TAB);
    }

    /**
     * A list (may be null or empty) of custom config categories needed by any panels returned by  {@link
     * #getConfigPanels()}
     */
    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(OrekitInternalConfigurationPage.OREKIT_CATEGORY);
    }

    /**
     * @return {@code true} if this is a "free" module, i.e. it does not participate in the licensing system. This is
     * equivalent to the now defunct FreeModule attribute that could be specified in module.xml.
     */
    @Override
    public boolean isFreeModule() {
        return true;
    }

    @Override
    public boolean isMakerEditionCompatible() { return true; }

    private void reloadConfig(PersistentRecord persistentRecord) {
        try {
            OrekitInternalConfiguration config = (OrekitInternalConfiguration) persistentRecord;

        } catch (Exception e) {
            logger.error("Error starting up Orekit module.", e);
        }
    }

    @Override
    public void recordUpdated(PersistentRecord persistentRecord) {
        reloadConfig(persistentRecord);
    }

    @Override
    public void recordAdded(PersistentRecord persistentRecord) {
        reloadConfig(persistentRecord);
    }

    @Override
    public void recordDeleted(KeyValue keyValue) {

    }

    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of(OrekitWebAPIAccess.MOUNT_ALIAS);
    }

    @Override
    public void mountRouteHandlers(RouteGroup routes) {
        api.mountRouteHandlers(routes);
    }
}

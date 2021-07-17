package com.github.benmusson.ignition.orekit.client.data;

import com.github.benmusson.ignition.orekit.client.api.v1.OrekitWebAPIClient;
import com.inductiveautomation.ignition.client.model.ClientContext;
import org.orekit.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataContext {
    public static final DataProvidersManager MANAGER = DataContext.getDefault().getDataProvidersManager();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OrekitWebAPIClient client;

    public ClientDataContext(ClientContext context) {
        this.client = new OrekitWebAPIClient(context);
        reloadProviders();
    }

    public void reloadProviders() {
        logger.trace("Reloading client data providers.");
        MANAGER.clearProviders();
        MANAGER.addProvider(new GatewayProviderCrawler(client));
    }
}

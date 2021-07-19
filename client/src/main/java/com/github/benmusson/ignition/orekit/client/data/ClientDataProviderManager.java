package com.github.benmusson.ignition.orekit.client.data;

import com.github.benmusson.ignition.orekit.client.api.v1.OrekitAPIClient;
import com.github.benmusson.ignition.orekit.client.api.v1.GatewayEndpointCrawler;
import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.inductiveautomation.ignition.client.model.ClientContext;
import org.orekit.data.*;

import java.util.ArrayList;
import java.util.List;

public class ClientDataProviderManager implements DefaultDataProviderManager {

    private final OrekitAPIClient client;
    private final GatewayEndpointCrawler gatewayCrawler;

    public static ClientDataProviderManager get(ClientContext context) {
        return new ClientDataProviderManager(context);
    }

    ClientDataProviderManager(ClientContext context) {
        this.client = new OrekitAPIClient(context);
        this.gatewayCrawler = new GatewayEndpointCrawler(client);
    }

    @Override
    public List<DataProvider> getDefaultProviders() {
        List<DataProvider> defaults = new ArrayList<>();
        defaults.add(this.gatewayCrawler);
        return defaults;
    }
}

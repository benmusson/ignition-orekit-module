package com.github.benmusson.ignition.orekit.gateway.data;

import com.github.benmusson.ignition.orekit.common.data.DefaultDataProviderManager;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.orekit.data.DataProvider;
import org.orekit.data.DirectoryCrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GatewayDataProviderManager implements DefaultDataProviderManager {

    private final GatewayContext context;

    public GatewayDataProviderManager(GatewayContext context) {
        this.context = context;
    }

    @Override
    public List<DataProvider> getDefaultProviders() {
        List<DataProvider> defaults = new ArrayList<>();
        OrekitInternalConfiguration config = OrekitInternalConfiguration.getConfig(context);

        for (String path: config.getGatewayDataPaths()) {
            defaults.add(new DirectoryCrawler(new File(path)));
        }
        return defaults;
    }
}

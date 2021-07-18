package com.github.benmusson.ignition.orekit.client.api.v1;

import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import org.orekit.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.regex.Pattern;

public class GatewayEndpointCrawler implements DataProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OrekitAPIClient client;

    public GatewayEndpointCrawler(OrekitAPIClient client) {
        this.client = client;
    }

    @Override
    @Deprecated
    public boolean feed(Pattern supported, DataLoader visitor) {
        return feed(supported, visitor, DataContext.getDefault().getDataProvidersManager());
    }

    @Override
    public boolean feed(Pattern supported, DataLoader visitor, DataProvidersManager manager) {
        try {
            return throwableFeed(supported, visitor, manager);
        } catch (Exception e) {
            logger.error("Error feeding data provider.", e);
            return false;
        }
    }

    private boolean throwableFeed(Pattern supported, DataLoader visitor, DataProvidersManager manager)
            throws InterruptedException, IOException, ParseException {

        JsonArray files = client.getDirectory();

        if (files == null) {
            return false;

        } else {
            boolean loaded = false;
            for (JsonElement element : files) {
                JsonObject jo = element.getAsJsonObject();

                if (visitor.stillAcceptsData()) {
                    String name = jo.get("name").getAsString();
                    NamedData data = new NamedData(name, () -> {
                        try {
                            return new FileInputStream(client.getFile(name));
                        } catch (InterruptedException e) {
                            logger.error("Error reading file", e);
                            return null;
                        }
                    });
                    data = manager.applyAllFilters(data);

                    if (supported.matcher(data.getName()).matches()) {
                        InputStream is = data.getStreamOpener().openStream();

                        visitor.loadData(is, name);
                        loaded = true;

                        is.close();
                    }
                }
            }
            return loaded;
        }
    }
}


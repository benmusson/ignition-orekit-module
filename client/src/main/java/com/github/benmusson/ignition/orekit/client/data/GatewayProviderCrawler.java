package com.github.benmusson.ignition.orekit.client.data;

import com.github.benmusson.ignition.orekit.client.api.v1.OrekitWebAPIClient;
import org.json.JSONException;
import org.orekit.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

public class GatewayProviderCrawler implements DataProvider {

    private OrekitWebAPIClient client;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public GatewayProviderCrawler(OrekitWebAPIClient client) {
        this.client = client;
    }

    /**
     * @param supported
     * @param visitor
     * @deprecated
     */
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
            throws InterruptedException, JSONException, IOException, ParseException {

        List<String> files = client.listFiles();

        if (files == null) {
            return false;

        } else {

            boolean loaded = false;

            for (String name : files) {

                if (visitor.stillAcceptsData()) {
                    NamedData data = new NamedData(name, () -> {
                        try {
                            return client.getFile(name);
                        } catch (InterruptedException e) {
                            return null;
                        }
                    });
                    data = manager.applyAllFilters(data);

                    if (supported.matcher(data.getName()).matches()) {
                        InputStream is = data.getStreamOpener().openStream();

                        try {
                            visitor.loadData(is, name);
                            loaded = true;

                        } finally {
                            is.close();
                        }
                    }
                }
            }

            return loaded;
        }
    }

}


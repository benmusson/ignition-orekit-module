package com.github.benmusson.ignition.orekit.common.script;

import org.orekit.data.*;

import java.io.IOException;
import java.net.MalformedURLException;

public interface DataScriptModule {

    void clearCache() throws IOException;

    void addProvider(DataProvider dp);

    void removeProvider(DataProvider dp);

    void clearProviders();

    void addDefaultProviders();

    void removeDefaultProviders();

}

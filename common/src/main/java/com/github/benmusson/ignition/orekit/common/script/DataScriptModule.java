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

    DirectoryCrawler newDirectoryCrawler(String root);

    ZipJarCrawler newZipJarCrawler(String path);

    NetworkCrawler newNetworkCrawler(String url) throws MalformedURLException;
}

package com.github.benmusson.ignition.orekit.client.script;

import com.github.benmusson.ignition.orekit.client.data.ClientDataProviderManager;
import com.github.benmusson.ignition.orekit.client.data.ClientFileCache;
import com.github.benmusson.ignition.orekit.common.script.DataScriptModule;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction;
import org.orekit.data.DataProvider;

import java.io.IOException;

public class ClientDataScriptModule implements DataScriptModule {

    public final static String BUNDLE_PREFIX = "ClientDataScriptModule";
    static {
        BundleUtil.get().addBundle(
                ClientDataScriptModule.class.getSimpleName(),
                ClientDataScriptModule.class.getClassLoader(),
                ClientDataScriptModule.class.getName().replace('.', '/')
        );
    }

    private final ClientFileCache cache;
    private final ClientDataProviderManager manager;

    public ClientDataScriptModule(ClientContext context) {
        this.cache = ClientFileCache.get(context);
        this.manager = ClientDataProviderManager.get(context);
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void clearCache() throws IOException {
        cache.clear();
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void addProvider(
            @ScriptArg("dp") DataProvider dp) {
        manager.addProvider(dp);
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void removeProvider(
            @ScriptArg("dp") DataProvider dp) {
        manager.removeProvider(dp);
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void clearProviders() {
        manager.clearProviders();
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void addDefaultProviders() {
        manager.addDefaultProviders();
    }

    @Override
    @ScriptFunction(docBundlePrefix = BUNDLE_PREFIX)
    public void removeDefaultProviders() {
        manager.removeDefaultProviders();
    }


}

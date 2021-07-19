package com.github.benmusson.ignition.orekit.common.data;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvider;
import org.orekit.data.DataProvidersManager;

import java.util.List;

public interface DefaultDataProviderManager {

    DataProvidersManager MANAGER = DataContext.getDefault().getDataProvidersManager();

    List<DataProvider> getDefaultProviders();

    default void addDefaultProviders() {
        for (DataProvider dp: this.getDefaultProviders()) {
            MANAGER.addProvider(dp);
        }
    }

    default void removeDefaultProviders() {
        for (DataProvider dp: this.getDefaultProviders()) {
            MANAGER.removeProvider(dp);
        }
    }

    default void addProvider(DataProvider dp) {
        MANAGER.addProvider(dp);
    }

    default void removeProvider(DataProvider dp) {
        MANAGER.removeProvider(dp);
    }

    default void clearProviders() {
        MANAGER.clearProviders();
    }
}
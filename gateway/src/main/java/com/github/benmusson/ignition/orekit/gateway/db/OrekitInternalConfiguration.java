package com.github.benmusson.ignition.orekit.gateway.db;

import com.inductiveautomation.ignition.gateway.localdb.persistence.*;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import simpleorm.dataset.SQuery;

public class OrekitInternalConfiguration extends PersistentRecord {
        public static final RecordMeta<OrekitInternalConfiguration> META = new RecordMeta<>(
            OrekitInternalConfiguration.class,
            "orekit_config");

    public static final IdentityField Id = new IdentityField(META, "id");
    public static final StringField DataPaths = new StringField(META, "data_paths");

    static {
        DataPaths.setDefault("");
        DataPaths.setWide();
    }

    public static final Category OrekitDataServerCategory =
            new Category("OrekitInternalConfiguration.Category.DataServer", 125)
                    .include(DataPaths);

    private static final String PATH_DELIMITER = ";";

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public String[] getGatewayDataPaths() {
        String gatewayDataPaths = getString(DataPaths);
        if (gatewayDataPaths != null) {
            return getString(DataPaths).split(PATH_DELIMITER);
        } else {
            return null;
        }
    }

    public static OrekitInternalConfiguration getConfig(GatewayContext context) {
        SQuery<OrekitInternalConfiguration> query = new SQuery<>(OrekitInternalConfiguration.META);
        return context.getPersistenceInterface().queryOne(query);
    }
}

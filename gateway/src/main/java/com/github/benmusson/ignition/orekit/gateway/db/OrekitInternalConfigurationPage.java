package com.github.benmusson.ignition.orekit.gateway.db;

import com.inductiveautomation.ignition.gateway.model.IgnitionWebApp;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Application;
import org.apache.wicket.model.Model;

public class OrekitInternalConfigurationPage extends RecordEditForm {
    public static final ConfigCategory OREKIT_CATEGORY =
            new ConfigCategory("orekit", "orekit.Config.MenuTitle", 700);
    public static final IConfigTab CONFIG_TAB = DefaultConfigTab.builder()
            .category(OREKIT_CATEGORY)
            .name("orekit")
            .i18n("orekit.Config.Settings.MenuTitle")
            .page(OrekitInternalConfigurationPage.class)
            .terms("orekit")
            .build();

    public OrekitInternalConfigurationPage(IConfigPage configPage) {
        super(configPage, null, Model.of("Orekit Configuration"), (((IgnitionWebApp) Application.get()).getContext())
                .getPersistenceInterface().find(
                        OrekitInternalConfiguration.META, 1L));
    }

    @Override
    public Pair<String, String> getMenuLocation() {
        return CONFIG_TAB.getMenuLocation();
    }
}

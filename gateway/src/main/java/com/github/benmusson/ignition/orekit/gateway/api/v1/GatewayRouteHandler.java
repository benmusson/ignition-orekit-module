package com.github.benmusson.ignition.orekit.gateway.api.v1;

import com.github.benmusson.ignition.orekit.common.api.v1.EndpointProvider;
import com.github.benmusson.ignition.orekit.gateway.api.RouteHandlerMounter;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.gateway.dataroutes.HttpMethod;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

public class GatewayRouteHandler implements RouteHandlerMounter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GatewayContext context;
    private final EndpointProvider provider;

    public GatewayRouteHandler(GatewayContext context) {
        this.context = context;
        this.provider = new GatewayEndpointProvider();
    }


    @Override
    public void mountRouteHandlers(RouteGroup routes) {
        routes.newRoute(provider.getDirectoryEndpoint())
                .handler((request, response) -> this.getDirectory())
                .method(HttpMethod.GET)
                .type("application/json")
                .mount();

        routes.newRoute(provider.getDirectoryEndpoint(":name"))
                .handler((request, response) -> this.getDirectory(request.getParameter("name")))
                .method(HttpMethod.GET)
                .type("application/json")
                .mount();

        routes.newRoute(provider.getFileEndpoint(":name"))
                .handler((request, response) -> {
                    try {
                        File f = this.getFile(request.getParameter("name"));

                        response.setHeader("Content-Disposition",
                                "attachment;filename="+f.getName());
                        response.setContentLength((int) f.length());

                        logger.trace(String.format("Beginning to stream file %s to client.",
                                f.getName()
                        ));

                        Files.copy(f.toPath(), response.getOutputStream());

                    } catch (Exception e) {
                        logger.error("Exception occurred providing Orekit file.", e);
                    }
                    return null;
                })
                .method(HttpMethod.GET)
                .type("application/octet-stream")
                .mount();
    }

    private JsonArray getDirectory() {
        JsonArray array = new JsonArray();
        for(String path: OrekitInternalConfiguration.getConfig(context).getGatewayDataPaths()) {
            try {
                File f = new File(path);
                array.addAll(this.getDirectory(f));
            } catch (Exception e) {
                logger.error("Exception occurred reading Orekit data files.", e);
            }
        }
        return array;
    }

    private JsonArray getDirectory(File file) {
        JsonArray array = new JsonArray();
        for(File f:file.listFiles()) {
            if(f.isDirectory()) {
                array.addAll(this.getDirectory(f));

            } else {
                array.add(this.getFileProperties(f));
            }
        }
        return array;
    }

    private JsonObject getDirectory(String name) {
        for(JsonElement je: this.getDirectory()) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.get("name").getAsString().equals(name)) {
                return jo;
            }
        }
        return null;
    }

    private JsonObject getFileProperties(File f) {
        JsonObject jo = new JsonObject();
        jo.addProperty("name", f.getName());
        jo.addProperty("modified", f.lastModified());
        jo.addProperty("size", f.length());
        return jo;
    }

    private File getFile(String name) {
        for(String path: OrekitInternalConfiguration.getConfig(context).getGatewayDataPaths()) {
            try {
                File f = this.getFile(new File(path), name);
                if (f != null) {
                    return f;
                }
            } catch (Exception e) {
                logger.error("Exception occurred finding Orekit data file.", e);
            }
        }
        return null;
    }

    private File getFile(File root, String name) {
        logger.trace(String.format("Searching folder %s for file %s", root, name));
        for(File f:root.listFiles()) {
            if (f.isDirectory()) {
                File found = this.getFile(f, name);
                if (found != null) {
                    return found;
                }
            } else if (f.getName().equals(name)) {
                logger.trace(String.format("Found file %s in folder %s", name, root));
                return f;
            }
        }
        logger.trace(String.format("File %s not found in folder %s", name, root));
        return null;
    }
}

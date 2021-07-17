package com.github.benmusson.ignition.orekit.gateway.api.v1;

import com.github.benmusson.ignition.orekit.common.api.v1.OrekitWebAPIAccess;
import com.github.benmusson.ignition.orekit.gateway.api.RouteHandlerMounter;
import com.github.benmusson.ignition.orekit.gateway.db.OrekitInternalConfiguration;
import com.inductiveautomation.ignition.common.gson.Gson;
import com.inductiveautomation.ignition.gateway.dataroutes.HttpMethod;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class OrekitWebEndpoint implements RouteHandlerMounter, OrekitWebAPIAccess {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GatewayContext context;

    public OrekitWebEndpoint(GatewayContext context) {
        this.context = context;
    }

    @Override
    public String getApiEndpoint() {
        return ROOT_PATH;
    }

    public void mountRouteHandlers(RouteGroup routes) {
        routes.newRoute(getDirectoryEndpoint())
                .handler((request, response) -> new Gson().toJson(getDirectory()))
                .method(HttpMethod.GET)
                .type("application/json")
                .mount();

        routes.newRoute(getFileEndpoint(":name"))
                .handler((request, response) -> {
                    try (OutputStream os = response.getOutputStream()){
                        File f = getFile(request.getParameter("name"));

                        response.setHeader("Content-Disposition",
                                "attachment;filename="+f.getName());
                        response.setContentLength((int) f.length());
                        response.setContentType("application/octet-stream");

                        logger.info(String.format("Beginning to stream file %s to client.", f.getName()));

                        Files.copy(f.toPath(), os);
                        os.flush();

                    } catch (Exception e) {
                        logger.error("Failed to supply file.", e);
                    }
                    return null;
                })
                .method(HttpMethod.GET)
                .mount();

    }

    public ArrayList<String> getDirectory() {
        ArrayList<String> files = new ArrayList<>();
        for(String path: OrekitInternalConfiguration.getConfig(context).getGatewayDataPaths()) {
            try {
                File f = new File(path);
                files.addAll(getDirectory(f));

            } catch (Exception e) {
                logger.error("Exception reading Orekit data files.", e);
            }
        }

        return files;
    }

    private ArrayList<String> getDirectory(File file) {
        ArrayList<String> files = new ArrayList<>();
        for(File f:file.listFiles()) {
            if(f.isDirectory()) {
                try {
                    files.addAll(getDirectory(f));
                } catch (Exception e) {
                }
            } else {
                files.add(f.getName());
            }
        }
        return files;
    }

    private File getFile(String name) {
        for(String path: OrekitInternalConfiguration.getConfig(context).getGatewayDataPaths()) {
            try {
                File f = getFile(new File(path), name);
                if (f != null) {
                    return f;
                }

            } catch (Exception e) {
                logger.error("Exception reading Orekit data files.", e);
            }
        }
        return null;
    }

    private File getFile(File root, String name) {
        logger.trace(String.format("Searching folder %s for file %s", root, name));
        for(File f:root.listFiles()) {
            if (f.isDirectory()) {
                File found = getFile(f, name);
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

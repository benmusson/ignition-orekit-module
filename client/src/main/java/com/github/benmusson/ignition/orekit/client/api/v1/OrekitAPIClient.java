package com.github.benmusson.ignition.orekit.client.api.v1;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class OrekitAPIClient {

    private final ClientContext context;
    private final HttpClient client;
    private final ClientEndpointProvider provider;

    public OrekitAPIClient(ClientContext context) {
        this.context = context;
        this.provider = new ClientEndpointProvider(context);
        this.client = HttpClient.newBuilder().build();
    }

    public Path getCacheFolder() {
        Path p = context.getLaunchContext().getGwCacheDir().toPath().resolve(
                ClientEndpointProvider.MOUNT_ALIAS);
        p.toFile().mkdir();
        return p;
    }

    public JsonArray getDirectory() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(provider.getDirectoryEndpoint()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        return new JsonParser().parse(response.body()).getAsJsonArray();
    }

    public JsonObject getDirectory(String name) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(provider.getDirectoryEndpoint(name)))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        return new JsonParser().parse(response.body()).getAsJsonObject();
    }

    public File getFile(String name) throws IOException, InterruptedException {
        JsonObject properties = this.getDirectory(name);

        File f = getCacheFolder().resolve(name).toFile();
        if (f.exists()) {
            if (f.lastModified() == properties.get("modified").getAsLong()) {
                return f;
            } else {
                f.delete();
            }
        }

        HttpRequest req = HttpRequest.newBuilder(
                URI.create(provider.getFileEndpoint(name)))
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
        Files.copy(response.body(), f.toPath());
        f.setLastModified(properties.get("modified").getAsLong());
        return f;

    }
}

package com.github.benmusson.ignition.orekit.client.api.v1;

import com.github.benmusson.ignition.orekit.client.data.ClientFileCache;
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
import java.util.Optional;

public class OrekitAPIClient {

    private final HttpClient client;
    private final ClientEndpointProvider provider;
    private final ClientFileCache cache;

    public OrekitAPIClient(ClientContext context) {
        this.client = HttpClient.newBuilder().build();
        this.provider = new ClientEndpointProvider(context);
        this.cache = ClientFileCache.get(context);
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

        Optional<File> cachedFile = cache.retrieve(name);
        if (cachedFile.isPresent()) {
            if (cachedFile.get().lastModified() == properties.get("modified").getAsLong()) {
                return cachedFile.get();
            } else {
                cache.remove(name);
            }
        }

        HttpRequest req = HttpRequest.newBuilder(
                URI.create(provider.getFileEndpoint(name)))
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
        File f = cache.store(name, response.body());
        f.setLastModified(properties.get("modified").getAsLong());
        return f;

    }
}

package com.github.benmusson.ignition.orekit.client.api.v1;

import com.github.benmusson.ignition.orekit.common.api.v1.OrekitWebAPIAccess;
import com.inductiveautomation.ignition.client.model.ClientContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OrekitWebAPIClient implements OrekitWebAPIAccess {

    private final HttpClient client;
    private final ClientContext context;

    public OrekitWebAPIClient(ClientContext context) {
        this.context = context;
        client = HttpClient.newBuilder().build();
    }

    public String getHostAddress() {
        return context.getLaunchContext().getGatewayAddress().toString();
    }

    public String getApiEndpoint() {
        return getHostAddress() + MOUNT_POINT + ROOT_PATH;
    }

    public List<String> listFiles() throws IOException, InterruptedException, JSONException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(getDirectoryEndpoint()))
                .GET()
                .build();

        HttpResponse response = client.send(req, HttpResponse.BodyHandlers.ofString());
        JSONArray json = new JSONArray(new JSONTokener(response.body().toString()));

        List<String> list = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            list.add(json.getString(i));
        }
        return list;
    }

    public InputStream getFile(String name) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(getFileEndpoint(name)))
                .GET()
                .build();

        HttpResponse response = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
        return (InputStream) response.body();
    }

}

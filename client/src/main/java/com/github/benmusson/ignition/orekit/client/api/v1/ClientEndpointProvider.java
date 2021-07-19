package com.github.benmusson.ignition.orekit.client.api.v1;

import com.github.benmusson.ignition.orekit.common.api.v1.EndpointProvider;
import com.inductiveautomation.ignition.client.model.ClientContext;

public class ClientEndpointProvider implements EndpointProvider {

    private final ClientContext context;

    public ClientEndpointProvider(ClientContext context) {
        this.context = context;
    }

    public String getHostAddress() {
        return context.getLaunchContext().getGatewayAddress().toString();
    }

    @Override
    public String getApiEndpoint() {
        return getHostAddress() + MOUNT_POINT + ROOT_PATH;
    }
}

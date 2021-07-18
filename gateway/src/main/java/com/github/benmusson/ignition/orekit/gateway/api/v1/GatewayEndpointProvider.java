package com.github.benmusson.ignition.orekit.gateway.api.v1;

import com.github.benmusson.ignition.orekit.common.api.v1.EndpointProvider;

public class GatewayEndpointProvider implements EndpointProvider {

    @Override
    public String getApiEndpoint() {
        return ROOT_PATH;
    }

}

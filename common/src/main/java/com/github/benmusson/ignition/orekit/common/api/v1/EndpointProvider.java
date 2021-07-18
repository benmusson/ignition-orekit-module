package com.github.benmusson.ignition.orekit.common.api.v1;

public interface EndpointProvider {

    String MOUNT_ALIAS = "orekit";

    String MOUNT_POINT = "/data/orekit";
    String ROOT_PATH = "/api/v1";

    String DIRECTORY_ENDPOINT = "/directory";
    String FILE_ENDPOINT = "/file";

    String getApiEndpoint();

    default String getApiEndpoint(String endpoint) {
        return getApiEndpoint() + endpoint;
    }

    default String getDirectoryEndpoint() {
        return getApiEndpoint(DIRECTORY_ENDPOINT);
    }

    default String getDirectoryEndpoint(String name) {
        return getDirectoryEndpoint() + "/" + name;
    }

    default String getFileEndpoint() {
        return getApiEndpoint(FILE_ENDPOINT);
    }

    default String getFileEndpoint(String name) {
        return getFileEndpoint() + "/" + name;
    }
}

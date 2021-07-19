package com.github.benmusson.ignition.orekit.common.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface FileCache {

    void clear() throws IOException;

    void remove(String name);

    boolean exists(String name);

    File store(String name, InputStream is) throws IOException;

    Optional<File> retrieve(String name);
}

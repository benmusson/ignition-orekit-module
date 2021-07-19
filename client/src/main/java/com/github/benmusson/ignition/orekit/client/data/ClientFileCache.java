package com.github.benmusson.ignition.orekit.client.data;

import com.github.benmusson.ignition.orekit.common.data.FileCache;
import com.inductiveautomation.ignition.client.model.ClientContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ClientFileCache implements FileCache {

    private final ClientContext context;

    ClientFileCache(ClientContext context) {
        this.context = context;
    }

    public static ClientFileCache get(ClientContext context) {
        return new ClientFileCache(context);
    }

    private Path getFolder() {
        Path p = context.getLaunchContext().getGwCacheDir().toPath().resolve("orekit");
        p.toFile().mkdir();
        return p;
    }


    @Override
    public void clear() throws IOException {
        FileUtils.forceDelete(this.getFolder().toFile());
    }

    @Override
    public void remove(String name) {
        Optional<File> f = this.retrieve(name);
        if (f.isPresent()) {
            f.get().delete();
        }
    }

    @Override
    public boolean exists(String name) {
        return this.getFolder().resolve(name).toFile().exists();
    }

    @Override
    public File store(String name, InputStream is) throws IOException {
        File f = this.getFolder().resolve(name).toFile();
        Files.copy(is, f.toPath());
        return f;
    }

    @Override
    public Optional<File> retrieve(String name) {
        File f = this.getFolder().resolve(name).toFile();
        if (f.exists()) {
            return Optional.of(f);
        }
        return Optional.empty();
    }
}

package com.asolutions.scmsshd.keyprovider;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class DefaultKeyPairProvider extends StreamKeyPairProvider {

    @Override
    protected List<InputStream> getStreams() {
        return Arrays.asList(
            getStream("/META-INF/keys/ssh_host_rsa_key"),
            getStream("/META-INF/keys/ssh_host_dsa_key")
        );
    }

    protected InputStream getStream(String path) {
        return getClass().getResourceAsStream(path);
    }
}

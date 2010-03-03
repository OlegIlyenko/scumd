package com.asolutions.scmsshd.keyprovider;

import org.apache.sshd.common.keyprovider.AbstractKeyPairProvider;
import org.apache.sshd.common.util.SecurityUtils;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public abstract class StreamKeyPairProvider extends AbstractKeyPairProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private PasswordFinder passwordFinder;

    public StreamKeyPairProvider() {}

    public StreamKeyPairProvider(PasswordFinder passwordFinder) {
        this.passwordFinder = passwordFinder;
    }

    public PasswordFinder getPasswordFinder() {
        return passwordFinder;
    }

    public void setPasswordFinder(PasswordFinder passwordFinder) {
        this.passwordFinder = passwordFinder;
    }

    protected KeyPair[] loadKeys() {
        if (!SecurityUtils.isBouncyCastleRegistered()) {
            throw new IllegalStateException("BouncyCastle must be registered as a JCE provider");
        }

        List<KeyPair> keys = new ArrayList<KeyPair>();

        for (InputStream stream : getStreams()) {
            try {
                PEMReader r = new PEMReader(new InputStreamReader(stream), passwordFinder);
                try {
                    Object o = r.readObject();
                    if (o instanceof KeyPair) {
                        keys.add((KeyPair) o);
                    }
                } finally {
                    r.close();
                }
            } catch (Exception e) {
                log.info("Unable to read key from stream.", e);
            }
        }

        return keys.toArray(new KeyPair[keys.size()]);
    }

    protected abstract List<InputStream> getStreams();

}

package com.asolutions.scmsshd.keyprovider;

import org.bouncycastle.openssl.PasswordFinder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class StringKeyPairProvider extends StreamKeyPairProvider {

    private List<String> stringKeys;

    public StringKeyPairProvider() {}

    public StringKeyPairProvider(List<String> stringKeys) {
        this.stringKeys = stringKeys;
    }

    public StringKeyPairProvider(List<String> stringKeys, PasswordFinder passwordFinder) {
        this.stringKeys = stringKeys;
        setPasswordFinder(passwordFinder);
    }

    public List<String> getStringKeys() {
        return stringKeys;
    }

    public void setStringKeys(List<String> stringKeys) {
        this.stringKeys = stringKeys;
    }

    @Override
    protected List<InputStream> getStreams() {
        return convertStrings(stringKeys);
    }

    protected List<InputStream> convertStrings(List<String> keys) {
        List<InputStream> streams = new ArrayList<InputStream>();

        for (String key : keys) {
            streams.add(new ByteArrayInputStream(key.getBytes()));
        }

        return streams;
    }

}

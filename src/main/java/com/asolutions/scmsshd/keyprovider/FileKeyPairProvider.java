package com.asolutions.scmsshd.keyprovider;

import org.bouncycastle.openssl.PasswordFinder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class FileKeyPairProvider extends StreamKeyPairProvider {

    private List<String> fileKeys;

    public FileKeyPairProvider() {}

    public FileKeyPairProvider(List<String> fileKeys) {
        this.fileKeys = fileKeys;
    }

    public FileKeyPairProvider(List<String> fileKeys, PasswordFinder passwordFinder) {
        this.fileKeys = fileKeys;
        setPasswordFinder(passwordFinder);
    }

    public List<String> getFileKeys() {
        return fileKeys;
    }

    public void setFileKeys(List<String> fileKeys) {
        this.fileKeys = fileKeys;
    }

    @Override
    protected List<InputStream> getStreams() {
        return convertFiles(fileKeys);
    }

    protected List<InputStream> convertFiles(List<String> keys) {
        List<InputStream> streams = new ArrayList<InputStream>();

        try {
            for (String key : keys) {
                streams.add(new FileInputStream(key));
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return streams;
    }

}
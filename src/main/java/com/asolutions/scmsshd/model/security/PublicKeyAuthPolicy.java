package com.asolutions.scmsshd.model.security;

import com.asolutions.scmsshd.util.CryptoUtil;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class PublicKeyAuthPolicy implements AuthPolicy {

    private List<PublicKey> publicKeys;

    private List<String> publicKeyStrings;

    public List<PublicKey> getPublicKeys() {
        if ((publicKeys == null || publicKeys.size() == 0) && publicKeyStrings != null) {
            publicKeys = readKeys(publicKeyStrings);
        }
        
        return publicKeys;
    }

    private List<PublicKey> readKeys(List<String> stringKeys) {
        List<PublicKey> keys = new ArrayList<PublicKey>();

        for (String stringKey : stringKeys) {
            keys.add(CryptoUtil.readPublicKey(stringKey));
        }

        return keys;
    }

    public void setPublicKeys(List<PublicKey> publicKeys) {
        this.publicKeys = publicKeys;
    }

    public void setPublicKeyAsStrings(List<String> publicKeyStrings) {
        this.publicKeyStrings = publicKeyStrings;
    }

    public List<String> getPublicKeyAsStrings() {
        return  this.publicKeyStrings;
    }
}

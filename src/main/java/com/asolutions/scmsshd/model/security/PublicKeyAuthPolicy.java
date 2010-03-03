package com.asolutions.scmsshd.model.security;

import com.asolutions.scmsshd.util.CryptoUtil;

import java.security.PublicKey;

/**
 * @author Oleg Ilyenko
 */
public class PublicKeyAuthPolicy implements AuthPolicy {

    private PublicKey publicKey;

    private String publicKeyString;

    public PublicKey getPublicKey() {
        if (publicKey == null && publicKeyString != null) {
            publicKey = CryptoUtil.readPublicKey(publicKeyString);
        }
        
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPublicKeyAsString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }
}

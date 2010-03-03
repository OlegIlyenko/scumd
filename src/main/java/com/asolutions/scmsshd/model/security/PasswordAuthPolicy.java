package com.asolutions.scmsshd.model.security;

/**
 * @author Oleg Ilyenko
 */
public class PasswordAuthPolicy implements AuthPolicy {

    public static enum EncodingAlgorithm {
        none, md5("MD5"), sha1("SHA-1");

        private String algorithm;

        private EncodingAlgorithm() {}

        private EncodingAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }

    private String password;

    private EncodingAlgorithm encodingAlgorithm = EncodingAlgorithm.none;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EncodingAlgorithm getEncodingAlgorithm() {
        return encodingAlgorithm;
    }

    public void setEncodingAlgorithm(EncodingAlgorithm encodingAlgorithm) {
        this.encodingAlgorithm = encodingAlgorithm;
    }
}

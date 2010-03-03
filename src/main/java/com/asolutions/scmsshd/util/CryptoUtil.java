package com.asolutions.scmsshd.util;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author Oleg Ilyenko
 */
public abstract class CryptoUtil {

    public static enum SshPublicKeyType {
        RSA("ssh-rsa"),
        DSA("ssh-dss");

        private String marker;

        private SshPublicKeyType(String marker) {
            this.marker = marker;
        }

        public String getMarker() {
            return marker;
        }
    }

    public static String calculateChecksum(String text, String algorithm) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance(algorithm);
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }

        return buf.toString();
    }

    public static PublicKey readPublicKey(String publicKeyString) {
        PublicKey publicKey = readSshPublicKey(publicKeyString);

        if (publicKey == null) {
            PEMReader reader = new PEMReader(new StringReader(publicKeyString));
            try {
                Object pk = reader.readObject();

                if (!(pk instanceof PublicKey)) {
                    throw new IllegalArgumentException("Invaid public key provided: " + publicKeyString);
                }

                publicKey = (PublicKey) pk;
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid public key: " + publicKeyString, e);
            }
        }

        return publicKey;
    }

    public static PublicKey readSshPublicKey(String publicKey) {
        String[] parts = publicKey.split(" ");

        if (parts.length < 2) {
            return null;
        }

        SshPublicKeyType type = null;
        for (SshPublicKeyType t : SshPublicKeyType.values()) {
            if (t.getMarker().equals(parts[0])) {
                type = t;
                break;
            }
        }

        if (type == null) {
            return null;
        }

        return readSshPublicKey(Base64.decode(parts[1]), type);
    }

    public static PublicKey readSshPublicKey(byte[] publicKey, SshPublicKeyType type) {
        ByteArrayInputStream publicKeyStream = new ByteArrayInputStream(publicKey);

        try {
            String realType = readSshString(publicKeyStream);
            if (!realType.equals(type.getMarker())) {
                throw new IllegalStateException("Invalid key type!");
            }

            switch (type) {
                case RSA:
                    BigInteger e = readSshInt(publicKeyStream);
                    BigInteger n = readSshInt(publicKeyStream);
                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(n, e));
                case DSA:
                    BigInteger p = readSshInt(publicKeyStream);
                    BigInteger q = readSshInt(publicKeyStream);
                    BigInteger g = readSshInt(publicKeyStream);
                    BigInteger y = readSshInt(publicKeyStream);
                    return KeyFactory.getInstance("DSA").generatePublic(new DSAPublicKeySpec(y, p, q, g));
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read SSH public key", e);
        }
    }

    private static BigInteger readSshInt(InputStream stream) throws IOException {
        return new BigInteger(readNextPart(stream));
    }

    private static String readSshString(InputStream stream) throws IOException {
        return new String(readNextPart(stream), "UTF-8");
    }

    private static byte[] readNextPart(InputStream stream) throws IOException {
        if (stream.available() < 4) {
            throw new IllegalStateException("Not enough butes");
        }

        int length = (int) (stream.read() << 24 & 0xff000000L | stream.read() << 16 & 0x00ff0000L | stream.read() << 8 & 0x0000ff00L | stream.read() & 0x000000ffL);
        byte[] buffer = new byte[length];
        stream.read(buffer, 0, length);

        return buffer;
    }
}

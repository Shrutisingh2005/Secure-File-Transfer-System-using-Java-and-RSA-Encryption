import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAUtil {
    private BigInteger n, d, e;
    private int bitlen = 1024;

    public RSAUtil() {
        SecureRandom r = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitlen / 2, r);
        BigInteger q = BigInteger.probablePrime(bitlen / 2, r);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.valueOf(65537); // common choice

        // Compute d, modular inverse of e mod phi
        d = e.modInverse(phi);
    }

    // Public key: (e, n)
    public BigInteger getE() { return e; }
    public BigInteger getN() { return n; }

    // Private key: (d, n)
    public BigInteger getD() { return d; }

    public byte[] encrypt(byte[] message) {
        BigInteger m = new BigInteger(message);
        BigInteger c = m.modPow(e, n);
        return c.toByteArray();
    }

    public byte[] decrypt(byte[] encrypted) {
        BigInteger c = new BigInteger(encrypted);
        BigInteger m = c.modPow(d, n);
        return m.toByteArray();
    }
}

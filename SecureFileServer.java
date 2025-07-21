import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SecureFileServer {
    private static final int PORT = 9999;
    private static RSAUtil rsa;

    public static void main(String[] args) throws IOException {
        rsa = new RSAUtil();
        System.out.println("Server started. Public Key (e,n):");
        System.out.println("e = " + rsa.getE());
        System.out.println("n = " + rsa.getN());

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Waiting for client connection...");

        while (true) {
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client connected.");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                // First receive length of encrypted file
                int length = dis.readInt();
                byte[] encryptedData = new byte[length];
                dis.readFully(encryptedData);

                // Decrypt
                byte[] decryptedData = rsa.decrypt(encryptedData);

                // Save to file
                try (FileOutputStream fos = new FileOutputStream("received_decrypted.txt")) {
                    fos.write(decryptedData);
                }
                System.out.println("File received and decrypted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

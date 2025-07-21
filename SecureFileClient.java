import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;

public class SecureFileClient extends JFrame {
    private JTextField txtPublicKeyE;
    private JTextField txtPublicKeyN;
    private JButton btnSelectFile;
    private JLabel lblStatus;

    private File selectedFile;

    public SecureFileClient() {
        setTitle("Secure File Transfer Client");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        add(new JLabel("Enter Server Public Key (e):"));
        txtPublicKeyE = new JTextField();
        add(txtPublicKeyE);

        add(new JLabel("Enter Server Public Key (n):"));
        txtPublicKeyN = new JTextField();
        add(txtPublicKeyN);

        btnSelectFile = new JButton("Select Text File");
        btnSelectFile.addActionListener(e -> selectFile());
        add(btnSelectFile);

        lblStatus = new JLabel("Status: Waiting for file selection.");
        add(lblStatus);

        setVisible(true);
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            lblStatus.setText("Selected: " + selectedFile.getName());
            try {
                sendFile();
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void sendFile() throws Exception {
        if (selectedFile == null) return;

        BigInteger e = new BigInteger(txtPublicKeyE.getText().trim());
        BigInteger n = new BigInteger(txtPublicKeyN.getText().trim());

        // Read file bytes
        byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

        // Encrypt
        BigInteger message = new BigInteger(fileBytes);
        BigInteger encrypted = message.modPow(e, n);
        byte[] encryptedBytes = encrypted.toByteArray();

        // Send to server
        try (Socket socket = new Socket("localhost", 9999);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeInt(encryptedBytes.length);
            dos.write(encryptedBytes);
            dos.flush();
            lblStatus.setText("File sent and encrypted successfully.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SecureFileClient::new);
    }
}

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;

public class LogSender {
    public static void main(String[] args) {
        try {
            // Load the client certificate (PKCS12)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream("/etc/nginx/certs/java-app.p12"), "changeme".toCharArray());
            kmf.init(ks, "changeme".toCharArray());

            System.out.println("Client certificate loaded successfully.");

            // Load the truststore (root CA certificate)
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            ts.load(new FileInputStream("/etc/nginx/certs/truststore.jks"), "changeme".toCharArray());
            tmf.init(ts);

            System.out.println("Truststore loaded successfully.");

            // Initialize the SSLContext with both KeyManagers and TrustManagers
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            System.out.println("SSLContext initialized successfully.");

            // Create the SSL socket and connect to NGINX
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket("localhost", 5000);

            System.out.println("Connected to NGINX on port 5000.");

            // Send the log message
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("Log message from Java app\n");
            writer.flush();

            System.out.println("Log message sent successfully.");

            writer.close();
            socket.close();

            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

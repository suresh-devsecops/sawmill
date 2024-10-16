import ssl
import socket

# Create SSL context for secure connection
context = ssl.create_default_context(ssl.Purpose.SERVER_AUTH, cafile="rootCA.crt")
context.load_cert_chain(certfile="python-app.crt", keyfile="python-app.key")

try:
    # Establish TCP connection to the NGINX server
    with socket.create_connection(('localhost', 5000)) as sock:
        print("Connection established with NGINX server")

        # Wrap the connection with SSL
        with context.wrap_socket(sock, server_hostname="localhost") as ssock:
            print("SSL handshake successful")

            # Send log message to NGINX
            ssock.sendall(b'Log message from Python app\n')
            print("Log message sent")
except Exception as e:
    print(f"An error occurred: {e}")

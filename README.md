### Steps Overview:
1. Generate client certificates for Java and Python applications.
2. Configure NGINX for mTLS to accept logs and forward them via Syslog to Sawmill.
3. Configure Java and Python applications to send logs using mTLS.
4. Setup Syslog to receive logs from NGINX and forward them to Sawmill.

Generate Certificates for mTLS 

We need separate client certificates for Java and Python, and server certificates for NGINX.

###Root CA Generation

```
openssl genrsa -out rootCA.key 2048
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 1024 -out rootCA.crt -subj "/C=US/ST=State/L=City/O=Org/OU=OrgUnit/CN=rootCA" 
```
Java Client Certificate

### Generate a private key for the Java app
```
openssl genrsa -out java-app.key 2048
```

### Generate a CSR
```
openssl req -new -key java-app.key -out java-app.csr -subj "/C=US/ST=State/L=City/O=Org/OU=JavaApp/CN=java-app"
```
### Sign the CSR with the Root CA
```
openssl x509 -req -in java-app.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out java-app.crt -days 500 -sha256
```
Python Client Certificate

### Generate a private key for the Python app
```
openssl genrsa -out python-app.key 2048
```
### Generate a CSR
```
openssl req -new -key python-app.key -out python-app.csr -subj "/C=US/ST=State/L=City/O=Org/OU=PythonApp/CN=python-app"
```
### Sign the CSR with the Root CA
```
openssl x509 -req -in python-app.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out python-app.crt -days 500 -sha256
```
NGINX Server Certificate

### Generate a private key for the NGINX server
```
openssl genrsa -out nginx.key 2048
```
### Generate a CSR for NGINX
```
openssl req -new -key nginx.key -out nginx.csr -subj "/C=US/ST=State/L=City/O=Org/OU=Server/CN=localhost"
```
### Sign the CSR with the Root CA
```
openssl x509 -req -in nginx.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out nginx.crt -days 500 -sha256
```
### Convert the .crt and .key into a PKCS12 format for Java.
 ```
 openssl pkcs12 -export -in java-app.crt -inkey java-app.key -out java-app.p12 -name java-app -CAfile rootCA.crt -caname rootCA
 ```
 

### write a sample python, java code to generate logs and send to NGINX with mTLS authentication 
```
LogSender.java
```
### compile java code
```
javac send-logs
```
### run java code 
```
java send-logs
```
### expected output
```
[root@ip-172-18-159-178 certs]# java send-logs
Client certificate loaded successfully.
Truststore loaded successfully.
SSLContext initialized successfully.
Connected to NGINX on port 5000.
Log message sent successfully.
Connection closed.
[root@ip-172-18-159-178 certs]#
```
### run python code
```
python3 send-logs.py
```
### exepected output 
```
[root@ip-172-18-159-178 certs]# python3 send_logs.py
Connection established with NGINX server
SSL handshake successful
Log message sent
```
### Configure rsyslog 
Enable TCP 
```


module(load="imtcp")   # For TCP Syslog
input(type="imtcp" port="8988")

# Forward logs to Sawmill
*.* @@localhost:8988
```
restart rsyslog
```
systemctl restart rsyslog
```

### Configure Sawmill
Download sawmill 
```
wget http://www.sawmill.net//download/sawmill/8.8.1.1/sawmill8.8.1.1_x64_linux-es7.tar.gz
```
untar and run 
```
[root@ip-172-18-159-178 RH9]# /etc/nginx/sawmill/sawmill -sh 172.18.159.178 -ws t
Sawmill 8.8.1.1; Copyright (c) 1996-2024 Flowerfire, Inc.
Web server running; browse http://172.18.159.178:8988/ to use Sawmill.
To run on a different IP address, use "sawmill -sh ip-addr -ws t"
```


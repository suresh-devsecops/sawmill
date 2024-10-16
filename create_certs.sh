#Root CA Generation

openssl genrsa -out rootCA.key 2048
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 1024 -out rootCA.crt -subj "/C=US/ST=State/L=City/O=Org/OU=OrgUnit/CN=rootCA"
#Java Client Certificate

## Generate a private key for the Java app
openssl genrsa -out java-app.key 2048

## Generate a CSR
openssl req -new -key java-app.key -out java-app.csr -subj "/C=US/ST=State/L=City/O=Org/OU=JavaApp/CN=java-app"
## Sign the CSR with the Root CA
openssl x509 -req -in java-app.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out java-app.crt -days 500 -sha256
#Python Client Certificate

## Generate a private key for the Python app
openssl genrsa -out python-app.key 2048
# Generate a CSR
openssl req -new -key python-app.key -out python-app.csr -subj "/C=US/ST=State/L=City/O=Org/OU=PythonApp/CN=python-app"
## Sign the CSR with the Root CA
openssl x509 -req -in python-app.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out python-app.crt -days 500 -sha256
#NGINX Server Certificate

## Generate a private key for the NGINX server
openssl genrsa -out nginx.key 2048
## Generate a CSR for NGINX
openssl req -new -key nginx.key -out nginx.csr -subj "/C=US/ST=State/L=City/O=Org/OU=Server/CN=localhost"
## Sign the CSR with the Root CA
openssl x509 -req -in nginx.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out nginx.crt -days 500 -sha256

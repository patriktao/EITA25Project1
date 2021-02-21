#!/usr/bin/env bash

# Standardize behaviour a bit. Fail on undefined variables etc
set -o errexit
set -o nounset

# Step 1 - Create a self-signed certificate for the CA.
openssl req -x509 
  -newkey rsa:2048 
  -days 365 
  -keyout ca-key.pem -out ca-cert.pem 
  -passout pass:password 
  -subj "/C=SE/ST=Lund/L=Lund/O=LTH/OU=Education/CN=CA"

# Step 2 - Create truststore for client, including the CA-cert created above
yes | keytool 
  -import -file ca-cert.pem 
  -keystore clienttruststore 
  -alias clienttruststore 
  -storepass password


## Generate Client Certificate
## ---------------------------

# Step 3 - Generate keystore and key pair
yes | keytool -genkeypair 
  -keystore clientkeystore 
  -alias client 
  -storepass password -keypass password 
  -dname "CN=Oscar Cederb erg (os7138ce-s)/Elna Seyer (el3888se-s)/Patrik Tao (pa0676ta-s)/Gabriel Borglund (ga4112bo-s)" -keyalg rsa

# Step 4 - Create a Certificate Signing Request for our client key pair
yes | keytool -certreq 
  -keystore clientkeystore 
  -alias client 
  -keyalg rsa -file client.csr 
  -storepass password

# Step 5 - Sign CSR with CA certificate
openssl x509 -req -in client.csr 
  -CA ca-cert.pem -CAkey ca-key.pem 
  -CAcreateserial 
  -passin pass:password 
  -out client-cert.pem

# Manually create certificate chain by appending CA to Client cert
type ca-cert.pem >> client-cert.pem

# Step 6a - Import CA certificate to clientkeystore
yes | keytool -importcert -trustcacerts -file ca-cert.pem 
  -keystore clientkeystore 
  -storepass password 
  -alias rootCA

# Step 6b - Import certifiacte signed by CA
# Make sure to give it the same name to replace keypair.
yes | keytool -importcert -file client-cert.pem 
  -keystore clientkeystore 
  -storepass password 
  -alias client


## Generate Server Certificate
## ---------------------------

# Step 3 - Generate keystore and key pair
yes | keytool -genkeypair 
  -keystore serverkeystore 
  -alias server 
  -storepass password -keypass password 
  -keyalg rsa
  -dname "CN=MyServer"

# Step 4 - Create a Certificate Signing Request for our server key pair
yes | keytool -certreq 
  -keystore serverkeystore 
  -alias server 
  -keyalg rsa -file server.csr 
  -storepass password

# Step 5 - Sign CSR with CA certificate
openssl x509 -req -in server.csr 
  -CA ca-cert.pem -CAkey ca-key.pem 
  -CAcreateserial 
  -passin pass:password 
  -out server-cert.pem

# Manually create certificate chain by appending CA to Client cert
type ca-cert.pem >> server-cert.pem

# Step 6a - Import CA certificate to serverkeystore
yes | keytool -importcert -file ca-cert.pem 
  -keystore serverkeystore 
  -storepass password 
  -alias rootCA

# Step 6b - Import certifiacte signed by CA
# Make sure to give it the same name to replace keypair.
yes | keytool -importcert -file server-cert.pem 
  -keystore serverkeystore 
  -storepass password 
  -alias server

# Step 10 - Create truststore for server, including the CA-cert created above
yes | keytool 
  -import -file ca-cert.pem 
  -keystore servertruststore 
  -alias servertruststore 
  -storepass password

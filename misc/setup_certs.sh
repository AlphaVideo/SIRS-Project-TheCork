#!/bin/bash

# Create Root CA key and self sign
openssl genrsa -out root-ca.key
openssl req -new -key root-ca.key -out root-ca.csr
openssl x509 -req -days 120 -in root-ca.csr -signkey root-ca.key -out root-ca.crt
echo 01 > root-ca.srl

# Create API key and sign with Root CA key
openssl genrsa -out api.key
openssl req -new -key api.key -out api.csr
openssl x509 -req -days 120 -in api.csr -CA root-ca.crt -CAkey root-ca.key -out api.crt

#openssl x509 -outform der -in root-ca.crt -out root-ca.der

# Export API key and certificate chain to api.p12
openssl pkcs12 -export -in api.crt -inkey api.key -out api.p12 -name API -CAfile root-ca.crt -caname RootCA -chain

#keytool -import -alias RootCA -keystore api.jks -file root-ca.der
#keytool -importkeystore -deststorepass sirssirs -destkeypass sirssirs -destkeystore api.jks -srckeystore api.p12 -srcstoretype PKCS12 -srcstorepass sirssirs -alias API

cp api.p12 ../thecork-api/src/main/resources/apicert.p12

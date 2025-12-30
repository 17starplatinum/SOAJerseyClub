#!/bin/bash
set -e

CA_ALIAS="root-ca"
CA_PASSWORD="changeit"
KEYSTORE_PASSWORD="changeit"
VALIDITY_DAYS=3650

SERVICES=("config-server" "discovery-service" "api-gateway" "hero-service" "human-being-service")
# бля впизду всё это
keytool -genkeypair \
  -alias $CA_ALIAS \
  -keyalg RSA \
  -keysize 4096 \
  -validity $VALIDITY_DAYS \
  -dname "CN=SOA Root CA, OU=ITMO, O=SOA, L=SPB, ST=RU, C=RU" \
  -keystore ca-keystore.p12 \
  -storepass $CA_PASSWORD \
  -keypass $CA_PASSWORD \
  -storetype PKCS12 \
  -ext BasicConstraints:critical=ca:true \
  -ext KeyUsage:critical=keyCertSign,cRLSign

keytool -exportcert \
  -alias $CA_ALIAS \
  -keystore ca-keystore.p12 \
  -storepass $CA_PASSWORD \
  -file ca.crt \
  -rfc

for SERVICE in "${SERVICES[@]}"; do

  keytool -genkeypair \
    -alias "$SERVICE" \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY_DAYS \
    -dname "CN=$SERVICE, OU=Services, O=SOA, L=SPB, ST=RU, C=RU" \
    -keystore "$SERVICE"-keystore.p12 \
    -storepass $KEYSTORE_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -storetype PKCS12 \
    -ext "san=dns:localhost,dns:$SERVICE,ip:127.0.0.1"

  keytool -certreq \
    -alias "$SERVICE" \
    -keystore "$SERVICE"-keystore.p12 \
    -storepass $KEYSTORE_PASSWORD \
    -file "$SERVICE".csr

  keytool -gencert \
    -alias $CA_ALIAS \
    -keystore ca-keystore.p12 \
    -storepass $CA_PASSWORD \
    -infile "$SERVICE".csr \
    -outfile "$SERVICE".crt \
    -validity $VALIDITY_DAYS \
    -ext "san=dns:localhost,dns:$SERVICE,ip:127.0.0.1" \
    -ext KeyUsage:critical=digitalSignature,keyEncipherment \
    -ext ExtendedKeyUsage=serverAuth,clientAuth \
    -rfc

  keytool -importcert \
    -alias $CA_ALIAS \
    -keystore "$SERVICE"-keystore.p12 \
    -storepass $KEYSTORE_PASSWORD \
    -file ca.crt \
    -noprompt

  keytool -importcert \
    -alias "$SERVICE" \
    -keystore "$SERVICE"-keystore.p12 \
    -storepass $KEYSTORE_PASSWORD \
    -file "$SERVICE".crt

  keytool -importcert \
    -alias $CA_ALIAS \
    -keystore "$SERVICE"-truststore.p12 \
    -storepass $KEYSTORE_PASSWORD \
    -file ca.crt \
    -noprompt \
    -storetype PKCS12
done

cp ca-keystore.p12 truststore-common.p12

mkdir -p hero-module/keystores payara-module/keystores

for SERVICE in config-server discovery-service api-gateway hero-service; do
  cp $SERVICE-keystore.p12 hero-module/keystores/
  cp $SERVICE-truststore.p12 hero-module/keystores/
done
cp ca.crt hero-module/keystores/

cp human-being-service-keystore.p12 payara-module/keystores/
cp human-being-service-truststore.p12 payara-module/keystores/
cp ca.crt payara-module/keystores/

for SERVICE in "${SERVICES[@]}"; do
  keytool -list -v -keystore "$SERVICE"-keystore.p12 -storepass $KEYSTORE_PASSWORD | grep -E "Alias|Owner|Issuer"
done

echo "1. Copy hero-module/keystores/* to HeroModule/*/src/main/resources/keystore/"
echo "2. Copy payara-module/keystores/* to HumanBeingModule config"
echo "3. Update application configurations"

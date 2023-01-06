#delete database if exists
sudo mysql -u sirs -psirs -e "DROP DATABASE IF EXISTS thecork;"

#create database
sudo mysql -u sirs -psirs -e "CREATE DATABASE thecork;"

# table creation
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE restaurant(name VARCHAR(32) NOT NULL, PRIMARY KEY(name));"
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE client(username VARCHAR(32) NOT NULL, pass_hash CHAR(64) NOT NULL, pass_salt CHAR(10) NOT NULL, wallet VARCHAR(28) NOT NULL, auth_token CHAR(32),token_exp_time TIMESTAMP, PRIMARY KEY(username));"
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE reservation(clientUsername VARCHAR(32) NOT NULL, restaurantName VARCHAR(32) NOT NULL, time DATETIME NOT NULL, nPeople INT NOT NULL, PRIMARY KEY(clientUsername,restaurantName,time), FOREIGN KEY (clientUsername) REFERENCES client(username), FOREIGN KEY (restaurantName) REFERENCES restaurant(name));"
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE staff(username VARCHAR(32) NOT NULL, restaurantName VARCHAR(32) NOT NULL, pass_hash CHAR(64) NOT NULL, pass_salt CHAR(10) NOT NULL, auth_token CHAR(32),token_exp_time TIMESTAMP, PRIMARY KEY(username), FOREIGN KEY(restaurantName) REFERENCES restaurant(name));"
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE giftcard(id INT UNSIGNED UNIQUE AUTO_INCREMENT NOT NULL, nonce CHAR(32) NOT NULL, owner VARCHAR(32), value VARCHAR(28) NOT NULL, PRIMARY KEY(id), FOREIGN KEY (owner) REFERENCES client(username));"

sudo mysql -u sirs -psirs thecork -e "CREATE TABLE client_ivs(username VARCHAR(32), iv CHAR(24), FOREIGN KEY (username) REFERENCES client(username));"
sudo mysql -u sirs -psirs thecork -e "CREATE TABLE giftcard_ivs(id INT UNSIGNED AUTO_INCREMENT NOT NULL, iv CHAR(24), FOREIGN KEY (id) REFERENCES giftcard(id));"

#
# populate tables
#

# restaurant
sudo mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('McDonalds');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Kasarao');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Udon');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Modesta da Pampulha');"

# client - passwords hashed with Sha256 and salted with 10 character long suffixation
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Eduu', '47406eb8081eab32005cb1d7a599b4930951dca1756314bb4f3deff83d3c0937', 's4lt3dG00d', 'JCmA/zFZgLIXRQxw5q0HhOE=', NULL, NULL);" #pass = getInTheRobot
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Tomas', '407f95bbb48e67e2519d1ffdd3e82c5bf19b395d568f5b513cadcfbdcc31d900', 'a8racada8r', 'x5ehqGV01JImwd8dI33wFrc=', NULL, NULL);" #pass = daikonlegs
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Lopes', '2ab21cf9256fa523534fce1dccf090f007b9771c4d0a6b9c3db8e6e8d8df4a86', 'sheepless0', '49j9lNOhVUIPS6GHrcDouc0=', NULL, NULL);" #pass = cherryl0ve
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('user', '71049f0dada4ddb3e3aa27f8c601d238b076fbed005e587b83899fe7e0e560a2', 'g3n3r1cdud', 'lnwYnpbj++q+dasQmqTlkF0=', NULL, NULL);" #pass = password

# staff - passwords hashed with Sha256 and salted with 10 character long suffixation
sudo mysql -u sirs -psirs thecork -e "INSERT INTO staff VALUES ('mcadmin', 'McDonalds', '6814b9dfd1d1191d8ee4e7114f70a0de09c1075438bd4b4c59ef6c743b26f9bb', '1234fries5', NULL, NULL);" #pass = admin
sudo mysql -u sirs -psirs thecork -e "INSERT INTO staff VALUES ('Duarte', 'Modesta da Pampulha', 'a8ec9d72a2bf62779f7f4197c11b1696ee685d1811c01a387caf7430813ffa27', 'croquetes1', NULL, NULL);" #pass = maria123

# giftcards
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard VALUES (0, '0748d697590086e39bbde2ec93009495', NULL, '8vpSCglSQ9GB5RXPHG1mb5fr');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard VALUES (0, '1a88bd55bf5d333996369efd27aebe20', NULL, '1oR5uD5Xq00gi0lwQ3d+pveU');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard VALUES (0, 'cb33ba6f7b097bebf4d2ebf6430c2841', NULL, 'pOpizzV9xPlhWKkps+jXCEvc');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard VALUES (0, 'd7d5f80f1c73091c1a108e64f24514a5', NULL, 'B6nlg2AV42NZapKy1+rMm6sSUg==');"

sudo mysql -u sirs -psirs thecork -e "INSERT INTO client_ivs VALUES ('Eduu',  'ftMPYfS7wZTkD+WJLf6Mow==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client_ivs VALUES ('Lopes', 'AKXKOfiyGuoWKMWVOmHJqw==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client_ivs VALUES ('Tomas', 'issRZj585pi+2P5pnCbJAw==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO client_ivs VALUES ('user',  'XH8EOF0fYBdlPkslZLrC1Q==');"

sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard_ivs VALUES (0, '5UFXnPuUkpzfDkcNo42BNQ==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard_ivs VALUES (0, 'VUAPCw3iYJpXSkdcNRQzLA==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard_ivs VALUES (0, 'hZ+q8shPyu6nvYlEzLm6DA==');"
sudo mysql -u sirs -psirs thecork -e "INSERT INTO giftcard_ivs VALUES (0, 'iFh7k5mtuV5vc5CrFDRY2g==');"

# other important commands
#  -Delete a Database
#  DROP DATABASE dbname;
#
#  -Delete a User
#  DROP USER 'thecork'@'localhost';


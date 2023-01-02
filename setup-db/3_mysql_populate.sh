#create database
mysql -u sirs -psirs -e "CREATE DATABASE thecork;"

# table creation
mysql -u sirs -psirs thecork -e "CREATE TABLE restaurant(name VARCHAR(32) NOT NULL, PRIMARY KEY(name));"
mysql -u sirs -psirs thecork -e "CREATE TABLE client(username VARCHAR(32) NOT NULL, pass_hash CHAR(64) NOT NULL, pass_salt CHAR(10) NOT NULL, wallet INT NOT NULL, auth_token_nonce CHAR(16),token_exp_date DATETIME, PRIMARY KEY(username));"
mysql -u sirs -psirs thecork -e "CREATE TABLE reservation(clientUsername VARCHAR(32) NOT NULL, restaurantName VARCHAR(32) NOT NULL, time DATETIME NOT NULL, nPeople INT NOT NULL, PRIMARY KEY(clientUsername,restaurantName,time), FOREIGN KEY (clientUsername) REFERENCES client(username), FOREIGN KEY (restaurantName) REFERENCES restaurant(name));"
mysql -u sirs -psirs thecork -e "CREATE TABLE staff(username VARCHAR(32) NOT NULL, restaurantName VARCHAR(32) NOT NULL, pass_hash CHAR(64) NOT NULL, pass_salt CHAR(10) NOT NULL, auth_token_nonce CHAR(16),token_exp_date DATETIME, PRIMARY KEY(username), FOREIGN KEY(restaurantName) REFERENCES restaurant(name));"
mysql -u sirs -psirs thecork -e "CREATE TABLE giftcards(id INT UNSIGNED UNIQUE AUTO_INCREMENT NOT NULL, nonce INT(16) NOT NULL, owner VARCHAR(32) NOT NULL, PRIMARY KEY(id,nonce), FOREIGN KEY (owner) REFERENCES client(username));"

#
# populate tables
#

# restaurant
mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('McDonalds');"
mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Kasarao');"
mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Udon');"
mysql -u sirs -psirs thecork -e "INSERT INTO restaurant VALUES ('Modesta da Pampulha');"

# client - passwords hashed with Sha256 and salted with 10 character long suffixation
mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Eduu', '47406eb8081eab32005cb1d7a599b4930951dca1756314bb4f3deff83d3c0937', 's4lt3dG00d', 0, NULL, NULL);" #pass = getInTheRobot
mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Tomas', '407f95bbb48e67e2519d1ffdd3e82c5bf19b395d568f5b513cadcfbdcc31d900', 'a8racada8r', 0, NULL, NULL);" #pass = daikonlegs
mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('Lopes', '2ab21cf9256fa523534fce1dccf090f007b9771c4d0a6b9c3db8e6e8d8df4a86', 'sheepless0', 0, NULL, NULL);" #pass = cherryl0ve
mysql -u sirs -psirs thecork -e "INSERT INTO client VALUES ('user', '71049f0dada4ddb3e3aa27f8c601d238b076fbed005e587b83899fe7e0e560a2', 'g3n3r1cdud', 0, NULL, NULL);" #pass = password

# staff - passwords hashed with Sha256 and salted with 10 character long suffixation
mysql -u sirs -psirs thecork -e "INSERT INTO staff VALUES ('mcadmin', 'McDonalds', '6814b9dfd1d1191d8ee4e7114f70a0de09c1075438bd4b4c59ef6c743b26f9bb', '1234fries5', NULL, NULL);" #pass = admin
mysql -u sirs -psirs thecork -e "INSERT INTO staff VALUES ('Duarte', 'Modesta da Pampulha', 'a8ec9d72a2bf62779f7f4197c11b1696ee685d1811c01a387caf7430813ffa27', 'croquetes1', NULL, NULL);" #pass = maria123


# other important commands
#  -Delete a Database
#  DROP DATABASE dbname;
#
#  -Delete a User
#  DROP USER 'thecork'@'localhost';


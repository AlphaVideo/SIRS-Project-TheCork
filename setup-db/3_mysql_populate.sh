#create database
mysql -u sirs -psirs -e "CREATE DATABASE thecork;"

mysql -u sirs -psirs thecork -e "CREATE TABLE restaurant(name VARCHAR(50) NOT NULL, PRIMARY KEY(name));"
mysql -u sirs -psirs thecork -e "CREATE TABLE client(username VARCHAR(50) NOT NULL, pass_hash VARCHAR(50) NOT NULL, PRIMARY KEY(username));"
mysql -u sirs -psirs thecork -e "CREATE TABLE reservation(clientUsername VARCHAR(50) NOT NULL, restaurantName VARCHAR(50) NOT NULL, time DATETIME NOT NULL, nPeople VARCHAR(50) NOT NULL, PRIMARY KEY(clientUsername,restaurantName,time), FOREIGN KEY (clientUsername) REFERENCES client(username), FOREIGN KEY (restaurantName) REFERENCES restaurant(name));"
mysql -u sirs -psirs thecork -e "CREATE TABLE staff(username VARCHAR(50) NOT NULL, restaurantName VARCHAR(50) NOT NULL, pass_hash VARCHAR(50) NOT NULL, PRIMARY KEY(username), FOREIGN KEY(restaurantName) REFERENCES restaurant(name));"


# other important commands
#  -Delete a Database
#  DROP DATABASE dbname;
#
#  -Delete a User
#  DROP USER 'thecork'@'localhost';


#install mysql
sudo apt install mysql-server

#create privileged user
sudo mysql -u root
CREATE USER 'sirs'@'localhost' IDENTIFIED BY 'sirs';
GRANT ALL PRIVILEGES ON * . * TO 'sirs'@'localhost';

#leave mysql
\q

#create database
mysql -u sirs -p
CREATE DATABASE thecork;

USE thecork;

CREATE TABLE restaurant(name VARCHAR(50) NOT NULL, PRIMARY KEY(name));
CREATE TABLE client(username VARCHAR(50) NOT NULL, pass_hash VARCHAR(50) NOT NULL, PRIMARY KEY(username));
CREATE TABLE reservation(clientUsername VARCHAR(50) NOT NULL, restaurantName VARCHAR(50) NOT NULL, time DATETIME NOT NULL, nPeople VARCHAR(50) NOT NULL, PRIMARY KEY(clientUsername,restaurantName,time), FOREIGN KEY (clientUsername) REFERENCES client(username), FOREIGN KEY (restaurantName) REFERENCES restaurant(name));
CREATE TABLE staff(username VARCHAR(50) NOT NULL, restaurantName VARCHAR(50) NOT NULL, pass_hash VARCHAR(50) NOT NULL, PRIMARY KEY(username), FOREIGN KEY(restaurantName) REFERENCES restaurant(name));

# other important commands
#  -Delete a Database
#  DROP DATABASE dbname;
#
#  -Delete a User
#  DROP USER 'thecork'@'localhost';

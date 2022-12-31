#install mysql
sudo apt install mysql-server


#create privileged user
sudo mysql -u root -e "CREATE USER 'sirs'@'localhost' IDENTIFIED BY 'sirs'; GRANT ALL PRIVILEGES ON * . * TO 'sirs'@'localhost';"
sudo mysql -u root -e "CREATE USER 'sirs'@'10.1.1.2' IDENTIFIED BY 'sirs'; GRANT ALL PRIVILEGES ON * . * TO 'sirs'@'10.1.1.2';"
sudo cp ./my.cnf /etc/
sudo systemctl restart mysql

echo "Use your preferred text editor to open the mysqld.cnf file. (/etc/mysql/mysql.conf.d/mysqld.cnf)
   Scroll down to the *bind-address* line and change the IP address to 0.0.0.0 .
   Apply the changes made to the MySQL config file by restarting the MySQL service\n"

echo "Then run `sudo systemctl restart mysql`"

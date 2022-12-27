**For TheCork-06 VM1:**

*Interfaces:*

`enp0s3`: Internal Network connected to ```sw-lan```

(don't forget to generate new MAC addresses!)
<hr/>

Instructions:
1) In `/etc/netplan/`, replace with the given `01-network-manager-all.yaml`
2) Afterwards, run the following commands in order:


```
sudo netplan try
sudo netplan apply
```


3) Use your preferred text editor to open the mysqld.cnf file. (/etc/mysql/mysql.conf.d/mysqld.cnf)
   Scroll down to the *bind-address* line and change the IP address to 0.0.0.0 .
   Apply the changes made to the MySQL config file by restarting the MySQL service:
   ```
   sudo systemctl restart mysql
   ```

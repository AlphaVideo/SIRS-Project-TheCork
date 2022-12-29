sudo cp ./01-network-manager-all.yaml /etc/netplan/
sudo cp ./sysctl.conf /etc/
sudo cp ./before.rules /etc/ufw/

sudo netplan try
sudo netplan apply
sudo ufw default deny INCOMING
sudo ufw default deny ROUTED
sudo ufw route allow in on enp0s8 from 10.1.1.2 out on enp0s3 to 10.0.0.1 port 3306   #API to DB requests
sudo ufw route allow in on enp0s9 out on enp0s8 to 10.1.1.2 port 8443                 #Client accessing port 8443 gets routed to API
sudo ufw route allow in on enp0s3 out on enp0s10                                      #NAT for DB
sudo ufw route allow in on enp0s8 out on enp0s10                                      #NAT for API
sudo ufw reload

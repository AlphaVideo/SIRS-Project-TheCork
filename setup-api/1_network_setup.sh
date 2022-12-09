sudo cp ./01-network-manager-all.yaml /etc/netplan/
sudo cp ./sysctl.conf /etc/
sudo cp ./before.rules /etc/ufw/

sudo netplan try
sudo netplan apply
sudo ufw enable
sudo ufw default deny INCOMING
sudo ufw default allow ROUTED        #so VM1 has net access
sudo ufw allow to any port 8080
sudo ufw reload

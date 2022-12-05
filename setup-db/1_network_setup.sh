echo "VM-DB" | sudo tee /etc/hostname

sudo cp ./01-network-manager-all.yaml /etc/netplan/

sudo netplan try
sudo netplan apply
sudo ufw enable
sudo ufw default deny INCOMING
sudo ufw default deny ROUTED
sudo ufw allow from 10.0.0.2 to any port 3306
sudo ufw reload

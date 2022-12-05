echo "VM-Client" | sudo tee /etc/hostname

sudo cp ./01-network-manager-all.yaml /etc/netplan/

sudo netplan try
sudo netplan apply

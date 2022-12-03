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
sudo ufw enable
sudo ufw default deny INCOMING
sudo ufw default deny ROUTED
sudo ufw allow from 10.0.0.2 to any port 3306
sudo ufw reload 
```

Uncomplicated Firewall is persistent, and will keep changes through reboot.
To deactive run `sudo ufw disable`.

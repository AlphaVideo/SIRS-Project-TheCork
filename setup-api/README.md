**For TheCork-06 VM2:**

*Interfaces:*

`enp0s3`: Internal Network connected to ```sw-lan``` \
`enp0s8`: Internal Network connected to ```sw-wan``` \
`enp0s9`: NAT

(don't forget to generate new MAC addresses!)
<hr/>

Instructions:
1) In `/etc/netplan/`, replace with the given `01-network-manager-all.yaml`
2) In `/etc/`, replace with the given `sysctl.conf`
3) In `/etc/ufw/`, replace with the give `before.rules`
4) Afterwards, run the following commands in order:


```
sudo netplan try
sudo netplan apply
sudo ufw enable
sudo ufw default deny INCOMING
sudo ufw default allow ROUTED        #so VM1 has net access
sudo ufw allow http
sudo ufw allow https
sudo ufw reload
```

Uncomplicated Firewall is persistent, and will keep changes through reboot.
To deactive run `sudo ufw disable`.

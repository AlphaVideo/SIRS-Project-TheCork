**For TheCork-06 VM2:**

*Interfaces:*

`enp0s3`: Internal Network connected to ```sw-lan``` \
`enp0s8`: Internal Network connected to ```sw-dmz``` \
`enp0s9`: Internal Network connected to ```sw-wan``` \
`enp0s10`: NAT

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
sudo ufw default deny INCOMING
sudo ufw default deny ROUTED
sudo ufw route allow in on enp0s8 from 10.1.1.2 out on enp0s3 to 10.0.0.1 port 3306   #API to DB requests
sudo ufw route allow in on enp0s9 from any port 8443 out on enp0s8 to 10.1.1.2        #Client accessing port 8443 gets routed to API
sudo ufw route allow in on enp0s3 out on enp0s10                                      #NAT for DB
sudo ufw route allow in on enp0s8 out on enp0s10                                      #NAT for API
sudo ufw reload
```

Uncomplicated Firewall is persistent, and will keep changes through reboot.
To deactive run `sudo ufw disable`.

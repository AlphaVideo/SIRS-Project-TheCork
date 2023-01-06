**For TheCork-06 VM2:**

*Interfaces:*

`enp0s3`: Internal Network connected to ```sw-dmz``` \

(don't forget to generate new MAC addresses!)
<hr/>

Instructions (Scripts):
1) Run the scripts in the following order:
   - 1_network_setup.sh
   - 2_install_maven.sh
   
Instructions (Manual):
1) In `/etc/netplan/`, replace with the given `01-network-manager-all.yaml`
2) Afterwards, run the following commands in order:

```
sudo netplan try
sudo netplan apply
```

3) Install the latest version of `maven`

This directory stores hashes of the disks.\
Due to the fact our machines were cloned through link cloning, they utilize a main disk and snapshots to store data, as follows:

### Base vdi
`c60ea41baf087f8c17e255eb1403dd444a530d8e816b1857486caa2dcd939370`  SEED-Ubuntu20.04.vdi
### VM-API snapshots
`5a5044f1a06581060e380587f5bc2b25f8275bcae398c1b2df12b8ca66295052` {cc36212e-6214-4ad2-82c5-b0b75569634a}.vdi
### VM-Client
`846fdf5073cbedb1fd2d74d826e8c3460914f3bac0f638ec4cb5b6c635d2be2e`  {2778b542-22ab-4915-8aea-53e7e31b71ae}.vdi
### VM-DB
`bbcff642ba89da27cc54eb6b8dc5e22e70fab9b56f6d28f39af223f186ff3797`  {75f8a684-83d8-45d2-adf2-95ad132df5a6}.vdi

An exception to this is the router machine, which has the disk:
# VM-Router
`902003445118f632b0aaf00e854a017bc51c2e4bf8740634f88a2ff8c2c1b5c3`  VM-Firewall.vdi

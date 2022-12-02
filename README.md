# The Cork&trade;
## Infrastructure
... notes regarding infrastructure ...

## How to start service
After setting everything up, run `mvn spring-boot:run` in directory `thecork-api/`

## Project strucutre
Organization of project folders:

- `setup-api/`: scripts to be run inside VM2 to setup before starting service
- `setup-db/`: scripts to be run inside VM1 to setup before starting service
- `thecork-api/`: code regarding REST API for VM2

Note: setup scripts should be divided in different files. They should cover:

- IP and network configuration (iptables)
- Firewall configuration
- Database setup (on VM1)

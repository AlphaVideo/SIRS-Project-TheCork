# The Cork&trade;

Project done for the Network and Computer Security 2022/2023 course at Instituto Superior Técnico.

All virtual machines used in this service are recommended to run on **Linux, Ubuntu 64-bit** operating systems.

## Initial setup
To run, 4 different virtual machines with the recommended OS are required. \
On each of them, run a separate `setup` folder to get started. More details on infrastructure can be found in each of them.\
Once all 4 different roles are properly setup (DB, API, Router and Client), proceed to the section "How to Start and Utilize Service"

## How to Start and Utilize Service

**Startup:**
- `Router VM:` Make sure the Firewall is properly up and running with `sudo ufw status`
- `DB VM:` Guarantee that the MySQL service is up and running (e.g log-in to the mysql root user with `sudo mysql -u root`)
- `API VM:` Inside the directory `thecork-api/`, execute the command `mvn spring-boot:run`
- `Client VM:` Execute the client script by running the command `python3 client.py` inside the directory `thecork-client/`

Once done, the client script is relatively straightforward. Some of the users the client can login to are as follows:
**Customer**
- `Username:` Eduu  ;  `Password:` getInTheRobot
- `Username:` Tomas ;  `Password:` daikonlegs
- `Username:` Lopes ;  `Password:` cherryl0ve
- `Username:` user  ;  `Password:` password

**Staff**
- `Username:` Duarte  ;  `Password:` maria123
- `Username:` mcadmin ;  `Password:` admin

## Project structure
Organization of project folders:

- `example-runs/`: wireshark capture of simple operations
- `hashes/`: hashes of virtual disks
- `misc/`: certificate files and keys for the API
- `msql-keys/:`: certificate files and keys for the DB
- `setup-client/`: scripts to be run inside a VM to setup the client before starting service
- `setup-router/`: scripts to be run inside a VM to setup the router before starting service
- `setup-api/`: scripts to be run inside a VM to setup the api before starting service
- `setup-db/`: scripts to be run inside a VM to setup the db before starting service
- `thecork-api/`: code regarding REST API for service functionality. This is where you'll startup TheCork live service.
- `thecork-client/`: code regarding the CLI client application to connect to the service.

***Notes:***
- Follow the file `setup-db/3_mysql_populate.sh` to check the used Database structure

import urllib3
import requests
import sslkeylog
from datetime import datetime
from enum import Enum

# This is only to supress certificate warnings
urllib3.disable_warnings(urllib3.exceptions.SecurityWarning)
print("Certificate warnings currently surpressed.\n")

# This is just for debug purposes
sslkeylog.set_keylog('sslkeys_{}.log'.format(datetime.now().strftime("%Y-%m-%d_%H-%M-%S")))

#
# Variables and such
#
token = None

#
# Data types and such
#
class AppMode(Enum):
    CLIENT = 1
    STAFF = 2

def connection_test():
    resp = requests.get("https://192.168.1.3:8443", verify="root-ca.crt")

    print("Status: {} {}".format(resp.status_code, resp.reason))
    print("Headers: {}".format(resp.headers))
    print("\nContent:\n{}".format(resp.text))


def login():
    print("Logging in as " + ("client." if mode == AppMode.CLIENT else "staff."))

    while True:
        username = input("Please insert username.\n> ")
        if len(username) > 32:
            print("Username cannot be more than 32 characters.")
            continue

        password = input("Please insert password.\n> ")
        if len(password) > 32:
            print("Password cannot be more than 32 characters.")
            continue
        
        break
    
    if mode == AppMode.CLIENT:
        resp = requests.post("https://192.168.1.3:8443/client_login", verify="root-ca.crt", json={"user": username, "pass": password} )
    else:
        resp = requests.post("https://192.168.1.3:8443/staff_login", verify="root-ca.crt", json={"user": username, "pass": password} )
    
    

        
    


    
        



#---------------------------------------------------------------------------------------------------/
#                                       * ~ * Main * ~ *                                           /
#-------------------------------------------------------------------------------------------------/

print("TheCork CLI App offers both a customer and a staff interface. Please select which mode to run on:")
print("1) Customer mode\n2) Staff mode")

#Obtain mode for CLI and login
mode_not_selected = True
while mode_not_selected:
    try:
        mode_input = int(input("> "))
    except ValueError:
        print("Please select a valid number. (1 or 2)")
    else:
        if mode_input not in [1, 2]:
            print("Please select a valid app mode. (1 or 2)")
        else:
            mode_not_selected = False

mode = AppMode(mode_input)

#Authenticate and gain valid token
login()


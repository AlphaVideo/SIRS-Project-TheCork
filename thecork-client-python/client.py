import urllib3
import requests
import sslkeylog
from datetime import datetime
from enum import Enum
import json

# This is only to supress certificate warnings
urllib3.disable_warnings(urllib3.exceptions.SecurityWarning)
print("Certificate warnings currently surpressed.\n")

# This is just for debug purposes
sslkeylog.set_keylog('sslkeys_{}.log'.format(datetime.now().strftime("%Y-%m-%d_%H-%M-%S")))

#
# Variables and such
#
auth_token = None
operation = 0

#
# Data types and such
#
class AppMode(Enum):
    CUSTOMER = 1
    STAFF = 2


def print_http_response(resp):
    jsonObj = json.loads(resp.text)
    print(json.dumps(jsonObj, indent=2))
    print()

def connection_test():
    resp = requests.get("https://192.168.1.3:8443", verify="root-ca.crt")
    print_http_response(resp)

def login():
    print("Logging in as " + ("customer." if mode == AppMode.CUSTOMER else "staff."))

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
    
    if mode == AppMode.CUSTOMER:
        resp = requests.post("https://192.168.1.3:8443/login/customer", verify="root-ca.crt", params={"user": username, "pass": password} )
    else:
        resp = requests.post("https://192.168.1.3:8443/login/staff", verify="root-ca.crt", params={"user": username, "pass": password} )

    data = resp.json()

    token = None
    if data["status"] == "OK":
        token = data["auth_token"]

    print_http_response(resp)
    
    #Change global token
    return token

def is_valid_date(year, month, day):
    day_count_for_month = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    if year%4==0 and (year%100 != 0 or year%400==0):
        day_count_for_month[2] = 29
    return (1 <= month <= 12 and 1 <= day <= day_count_for_month[month])
    

def reservation():    
    while True:
        restaurant = input("Please insert the name of the restaurant where you would like to book a reservation.\n> ")
        if len(restaurant) > 32:
            print("Restaurant name cannot be more than 32 characters.")
            continue

        nPeople = int(input("Please insert the number of people you would like to book for.\n> "))
        if nPeople < 1:
            print("Number of people must be a positive number.")
            continue

        date = input("Please insert the date you would like to book for. (Please use the format YYYY-MM-DD)\n> ")
        date_parse = date.split('-')
        if len(date_parse) != 3:
            print("Date inserted isn't valid.")
            continue

        year = date_parse[0]
        month = date_parse[1]
        day = date_parse[2]

        if not is_valid_date(int(year), int(month), int(day)):
            print("Date inserted isn't valid.")
            continue

        time = input("Please insert the time you would like to book for. (Please use the format HH:MM)\n> ")
        time_parse = time.split(':')
        if len(time_parse) != 2:
            print("Time inserted isn't valid.")
            continue

        hour = int(time_parse[0])
        minute = int(time_parse[1])

        if hour > 23 or hour < 0 or minute > 59 or minute < 0:
            print("Time inserted isn't valid.")
            continue
            
        break

    datetime = date + " " + time + ":00"

    resp = requests.post("https://192.168.1.3:8443/reservation/create", verify="root-ca.crt", params={"auth_token": auth_token, "restaurant": restaurant, "nPeople": nPeople, "datetime": datetime} )

    print_http_response(resp)


def buy_giftcard():  
    print("Please select the value of the giftcard you wish to buy:")
    print("1) 10€\n2) 25€\n3) 50€\n4) 100€")

    value_not_selected = True
    while value_not_selected:
        try:
            value = int(input("> "))
        except ValueError:
            print("Please select a valid number. (1, 2, 3 or 4)")
        else:
            if value not in [1, 2, 3, 4]:
                print("Please select a valid number. (1, 2, 3 or 4)")
            else:
                value_not_selected = False

    resp = requests.post("https://192.168.1.3:8443/buy_giftcard", verify="root-ca.crt", params={"auth_token": auth_token, "value": value} )

    print_http_response(resp)
    
def redeem_giftcard():

    while True:
        giftcard_id = int(input("Please insert the giftcard id.\n> "))
        if giftcard_id < 1:
            print("Giftcard id must be a positive integer.")
            continue

        giftcard_nonce = int(input("Please insert the giftcard code.\n> "))
        if len(giftcard_nonce) != 16:
            print("Giftcard code must be 16 numbers long.")
            continue
            
        break

    resp = requests.post("https://192.168.1.3:8443/giftcard/redeem", verify="root-ca.crt", params={"auth_token": auth_token, "id": giftcard_id, "nonce": giftcard_nonce} )

    print_http_response(resp)

def gift_giftcard():

    while True:
        target = input("Please insert the username of the user you want to give a giftcard to.\n> ")
        if len(target) > 32:
            print("Username cannot be more than 32 characters.")
            continue

        giftcard_id = int(input("Please insert the giftcard id.\n> "))
        if giftcard_id < 1:
            print("Giftcard id must be a positive integer.")
            continue

        giftcard_nonce = input("Please insert the giftcard code.\n> ")
        if len(giftcard_nonce) != 316:
            print("Giftcard code must be 16 characters long.")
            continue
            
        break

    resp = requests.post("https://192.168.1.3:8443/giftcard/give", verify="root-ca.crt", params={"auth_token": auth_token, "target": target, "id": giftcard_id, "nonce": giftcard_nonce} )

    print_http_response(resp)

def create_giftcard():

    print("Please select the value of the giftcard you wish to create:")
    print("1) 10€\n2) 25€\n3) 50€\n4) 100€")

    value = None
    values = {1: 10, 2: 25, 3: 50, 4: 100}
    value_not_selected = True
    while value_not_selected:
        try:
            value_choice = int(input("> "))
        except ValueError:
            print("Please select a valid number. (1, 2, 3 or 4)")
        else:
            if value_choice not in [1, 2, 3, 4]:
                print("Please select a valid number. (1, 2, 3 or 4)")
            else:
                value = values[value_choice]
                value_not_selected = False

    resp = requests.post("https://192.168.1.3:8443/giftcard/create", verify="root-ca.crt", params={"auth_token": auth_token, "value": value} )

    print_http_response(resp)

def check_balance():
    resp = requests.post("https://192.168.1.3:8443/check_balance", verify="root-ca.crt", params={"auth_token": auth_token} )

    print_http_response(resp)




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
auth_token = login()

if mode == AppMode.CUSTOMER:
    while operation != 6:
        print("Select Operation:")
        print("1) Book a Reservation")
        print("2) Buy a Giftcard")
        print("3) Redeem a Giftcard")
        print("4) Gift a Giftcard")
        print("5) Check Current Balance")
        print("6) Quit")

        #Obtain Operation
        operation_not_selected = True
        while operation_not_selected:
            try:
                operation = int(input("> "))
            except ValueError:
                print("Please select a valid number. (1, 2, 3, 4, 5 or 6)")
            else:
                if operation not in [1, 2, 3, 4, 5, 6]:
                    print("Please select a valid operation. (1, 2, 3, 4, 5 or 6)")
                else:
                    operation_not_selected = False

        if operation == 1:
            reservation()

        if operation == 2:
            buy_giftcard()

        if operation == 3:
            redeem_giftcard()

        if operation == 4:
            gift_giftcard()

        if operation == 5:
            check_balance()

if mode == AppMode.STAFF:
    while operation != 2:
        print("Select Operation:")
        print("1) Create a Giftcard")
        print("2) Quit")

        #Obtain Operation
        operation_not_selected = True
        while operation_not_selected:
            try:
                operation = int(input("> "))
            except ValueError:
                print("Please select a valid number. (1 or 2)")
            else:
                if operation not in [1, 2]:
                    print("Please select a valid operation. (1 or 2)")
                else:
                    operation_not_selected = False

        if operation == 1:
                create_giftcard()


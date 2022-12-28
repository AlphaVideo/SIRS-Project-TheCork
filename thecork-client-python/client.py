import requests
import sslkeylog
from datetime import datetime

# This is only to supress certificate warnings
import urllib3
urllib3.disable_warnings(urllib3.exceptions.SecurityWarning)
print("A warning was supressed\n")

# This is just for debug purposes
sslkeylog.set_keylog('sslkeys_{}.log'.format(datetime.now().strftime("%Y-%m-%d_%H-%M-%S")))


resp = requests.get("https://192.168.1.3:8443", verify="root-ca.crt")

print("Status: {} {}".format(resp.status_code, resp.reason))
print("Headers: {}".format(resp.headers))
print("\nContent:\n{}".format(resp.text))

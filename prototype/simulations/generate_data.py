#! /usr/bin/python

import json
import time
import random
import sys
from signal import signal, SIGPIPE, SIG_DFL
signal(SIGPIPE, SIG_DFL)

ITERATIONS = 3
MAX_SPEED = 160; #km / hr
COND_BND = 3; # bounds 
MAX_STEER_ANGLE = 360;

json_dat = []
odometer = [0]
vehicle_speed = [0]
steering_wheel = [0]
air_conditioning = [0]

itera = 0
while True:
    vs = (random.randint(0, MAX_SPEED) + vehicle_speed[-1]) / 2
    if vs < MAX_SPEED/2:
        vs = vs*(random.randint(0,MAX_SPEED/2)>vs)  
    vehicle_speed.append(vs)

    if random.randint(0,100) > 90:
        ac = random.randint(-COND_BND, COND_BND)
    else:
        ac = air_conditioning[-1]
    air_conditioning.append(ac)

    odo = odometer[-1] + vs
    odometer.append(odo)

    sw = random.normalvariate(0, 0.25)*MAX_STEER_ANGLE
  
    #print str(vs) + " " + str(ac) + " " + str(odo) + " " + str(sw)
    json_pic = {"v":vs, "ac":ac, "odo":odo, "sw":sw}
    print json.dumps(json_pic)
    sys.stdout.flush()
    json_dat.append(json_pic)
    time.sleep(abs(random.normalvariate(0,0.5)))
    itera = itera + 1

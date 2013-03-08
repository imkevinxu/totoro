#!/usr/bin/python

import sys
import subprocess

p = subprocess.Popen("""python -c '
from time import sleep ; import sys
for i in range(3):
    sleep(1)
    print "Hello", i
    sys.stdout.flush()
'""", shell = True, stdout = subprocess.PIPE)

while True:
    inline = p.stdout.readline()
    if not inline:
        break
    sys.stdout.write(inline)
    sys.stdout.flush()

print "Done"

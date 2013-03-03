#! /usr/bin/python

import csv, random, time

cr = csv.reader(open('../media/data/trackLog-2013-Feb-28_23-13-08.csv', 'rb'))

for row in cr:
    print row
    time.sleep(abs(random.normalvariate(0,0.5)))

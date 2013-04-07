from django import forms
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.core import serializers
from django.core.context_processors import csrf
from django.core.mail import send_mail
from django.http import HttpResponse, HttpResponseRedirect
from coffin.shortcuts import render_to_response, get_object_or_404, render, \
    redirect, render_to_string
from django.template import loader, RequestContext
from django.views.decorators.csrf import csrf_exempt
from django.db.models import Avg
from django.db.models import *

from facebook.models import *
from prototype_app.models import *
from prototype_app.model_forms import *
from prototype_app.forms import *
from utils import views as util_views
from datetime import datetime

import csv, random, time

try:
    import json
except ImportError:
    import simplejson as json


def home(request):
    facebook_profile = None
    #return render(request, 'home.html', locals())
    return render_to_response('home.html', { 'facebook_profile': None }, context_instance=RequestContext(request))

#@login_required
def dashboard(request):
    # facebook_profile = request.user.get_profile().get_facebook_profile()
    facebook_profile = {'id': "1374900452", "name": "Kevin Xu", "username": "imkevinxu"}
    # match_user_profile(facebook_profile['id'])
    #read_csv()
    if len(DriveData.objects.all()) == 0:
        read_csv()
    #dates hard coded for now
    start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')

    # Averages (output is a number)
    avg_speed = get_average(facebook_profile['id'], 'vehicle_speed', start_time, end_time)
    avg_mpg = get_average(facebook_profile['id'], 'mpg', start_time, end_time)
    avg_altitude = get_average(facebook_profile['id'], 'altitude', start_time, end_time)

    # Change over time charts (output is a list)

    #engine_rev_data is a tuple: (timestamp, number of revs/min)
    engine_rev_data = graph_engine_rpm(facebook_profile['id'], 'engine_rpm', start_time, end_time)

    #throttle_position_data is a tuple: (timestamp, position of throttle)
    throttle_position_data = graph_throttle(facebook_profile['id'], 'pedal_force', start_time, end_time)

    # Using util scoring functions

    # Scores how much your engine idles (0 - 100)
    engine_idle_score = util_views.score_idling(get_list(facebook_profile['id'], 'engine_rpm', start_time, end_time))
    pedal_score = get_average(facebook_profile['id'], 'pedal_force', start_time, end_time)

    # Scores how much you 'think ahead', e.g. pressing the throttle ahead of a hill
    think_ahead_score = util_views.score_thinking_ahead(
        get_list(facebook_profile['id'], 'pedal_force', start_time, end_time),
        get_list(facebook_profile['id'], 'altitude', start_time, end_time))

    # data from the last trip
    last_trip = get_last_trip(facebook_profile['id'], start_time, end_time)
    last_trip_date = last_trip[0]
    last_trip_duration = ("%d" % (last_trip[1].seconds/3600), "%d" % (last_trip[1].seconds%3600/60))
    last_trip_fuel = last_trip[2]

    return render(request, 'dashboard.html', locals())
    #return render_to_response('index.html',  {'facebook_profile': facebook_profile}, context_instance=RequestContext(request))

#def match_user_profile(id):
def scores(request):
    try:
        if 'fbid' in request.GET:
            fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
            fbid = fb.get_facebook_profile()['id']
            first_name = fb.get_facebook_profile()['name'].split()[0]
            highscore = fb.highscore
            #friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore } for user in FacebookProfile.objects.all() if user != fb]
            # friends = [1, 2, 3]
            friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore } for user in FacebookProfile.objects.all()]
            results = json.dumps({'friends' : friends }, ensure_ascii=False)

            #results = json.dumps({ 'fbid' : fbid, 'first_name' : first_name, 'highscore' : highscore, 'friends' : friends }, ensure_ascii=False)
            return HttpResponse(results, mimetype='application/json')
    except FacebookProfile.DoesNotExist:
        pass
    return redirect('home')

def data(request):
    try:
        fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
        fb.highscore = request.GET['data']
        fb.save()
        if 'fbid' in request.GET:
            fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
            fbid = fb.get_facebook_profile()['id']
            first_name = fb.get_facebook_profile()['name'].split()[0]
            friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore } for user in FacebookProfile.objects.all()]
            results = json.dumps({'friends' : friends }, ensure_ascii=False)
            return HttpResponse(results, mimetype='application/json')
    except FacebookProfile.DoesNotExist:
        pass
    return redirect('home')

def read_csv():
    # DriveData.objects.all().delete()
    cr = csv.reader(open('media/data/trackLog-2013-Feb-28_23-13-08.csv', 'rb'))
    counter = 0
    for row in cr:
        if counter != 0:
            new_row = DriveData()
            new_row.timestamp = datetime.strptime(row[0], '%a %b %d %H:%M:%S %Z %Y')
            new_row.vehicle_speed = row[4]
            new_row.air_conditioning = row[13] #data
            new_row.pedal_force = row[18]
            new_row.mpg = row[14]
            new_row.altitude = row[6]
            new_row.engine_rpm = row[15]
            new_row.fuel_used = row[16]
            new_row.save()
        counter+=1

def generate_dashboard(request):
    username = request.user.username
    #hard coded for now
    start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    avg_speed = get_average(username, 'vehicle_speed', start_time, end_time)
    return render(request, 'dashboard.html', {'vehicle_speed' : avg_speed})

def get_average(username, metric, start_time, end_time):
    formatted_metric = metric + "__avg"
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).aggregate(Avg(metric))
    formatted_result = "{0:.2f}".format(result[formatted_metric])
    return formatted_result

#####Temporary testing of some scoring functions #####

PEDAL_WEIGHTING_FACTOR = 1
BRAKING_PRESSURE_WEIGHTING_FACTOR = 1
AIR_CONDITIONING_WEIGHTING_FACTOR = 1
SUN_INTENSITY_WEIGHTING_FACTOR = 1

# Given a list of values, returns the average value of the function.
def average_linear_value(list):
	reduce(lambda x,y:x+y,list)
    #return integrate_linear(list) / len(list)

def integrate_quadratic(list):
    if len(list) == 1:
        return list[0] * list[0]
    ret = 0
    for i in range(0, len(list) - 1):
        a = list[i]
        b = list[i+1] - list[i]
        ret = ret + (a * (a + b)) + (b * b) / 3.
    return ret

# Given a list of values, returns the average value of the square of the function,
# after normalizing for length of the trip.
def average_quadratic_value(list):
    return integrate_quadratic(list) / (len(list) * len(list))

def score_pedal_force(pedal_force_list):
    return max(0.0, 100 - PEDAL_WEIGHTING_FACTOR * average_quadratic_value(pedal_force_list))


#### Charts and graphs ####
def graph_engine_rpm(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp')
    l = []
    for e in result:
        l.append((e.timestamp, e.engine_rpm))
    return l

def graph_throttle(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp')
    l = []
    for e in result:
        l.append((e.timestamp, e.pedal_force))
    return l

def get_list(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp').values(metric)
    l = []
    for e in result:
        l.append(e[metric])
    return l

def get_last_trip(username, start_time, end_time):
    duration = end_time - start_time
    date = end_time
    fuel_query = DriveData.objects.filter(timestamp = end_time).values('fuel_used')[:1]
    fuel = 0
    for e in fuel_query:
        fuel = e['fuel_used']
    return(date, duration, fuel)

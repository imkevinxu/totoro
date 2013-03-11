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

from prototype_app.models import *
from prototype_app.model_forms import *
from prototype_app.forms import *
from utils import views as util_views
from datetime import datetime

import csv, random, time

def home(request):
    facebook_profile = None
    return render(request, 'home.html', locals())
    #return render_to_response('home.html', { 'facebook_profile': None }, context_instance=RequestContext(request))

#@login_required
def dashboard(request):
    facebook_profile = request.user.get_profile().get_facebook_profile()
    #match_user_profile(facebook_profile['id'])
    #read_csv()
    if len(DriveData.objects.all()) == 0:
        read_csv()
    #dates hard coded for now
    start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    avg_speed = get_average(facebook_profile['id'], 'vehicle_speed', start_time, end_time)
    avg_mpg = get_average(facebook_profile['id'], 'mpg', start_time, end_time)
    pedal_score = get_average(facebook_profile['id'], 'pedal_force', start_time, end_time)

    avg_altitude = get_average(facebook_profile['id'], 'altitude', start_time, end_time)
    #ac_list = list(DriveData.values_list('air_conditioning', flat=True))
    #ac_score = score_air_conditioning(ac_list)
    #pedal_list = list(DriveData.objects.only("pedal_force"))
    #throttle_score = score_pedal_force(pedal_list)

    #engine_score = score_idling(engine_rpm_list)

    return render(request, 'dashboard.html', locals())
    #return render_to_response('index.html',  {'facebook_profile': facebook_profile}, context_instance=RequestContext(request))

#def match_user_profile(id):


def read_csv():
    #DriveData.objects.all().delete()
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

def score_air_conditioning(air_conditoning_list):
    return max(0.0, 100 - AIR_CONDITIONING_WEIGHTING_FACTOR * average_quadratic_value(air_conditoning_list))

def score_idling(engine_speed_list):
    idling_penalty = 0
    curr_streak = 0
    for number in engine_speed_list:
        if number < IDLING_ENGINE_THRESHOLD:
            curr_streak = curr_streak + 1
        else:
            curr_streak = 0
        idling_penalty = idling_penalty + curr_streak
    score = 100. * (len(engine_speed_list) - idling_penalty) / len(engine_speed_list)
    if score < 0:
        score = 0.0
    return score


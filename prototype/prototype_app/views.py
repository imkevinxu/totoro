from django import forms
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.core import serializers
from django.core.context_processors import csrf
from django.core.mail import send_mail
from django.http import HttpResponse, HttpResponseRedirect
from coffin.shortcuts import get_object_or_404, \
   redirect
from django.shortcuts import render, render_to_response
from django.template import loader, RequestContext
from django.views.decorators.csrf import csrf_exempt
from django.db.models import Avg
from django.db.models import *

from prototype_app.models import *
from prototype_app.model_forms import *
from prototype_app.forms import *
from datetime import datetime

import csv, random, time

#@login_required
def index(request):
	facebook_profile = request.user.get_profile().get_facebook_profile()
	print facebook_profile.id
	read_csv()
	#dates hard coded for now
	start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
	end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
	avg_speed = get_average(facebook_profile.id, 'vehicle_speed', start_time, end_time)

	return render(request, 'dashboard.html', locals())
	#return render_to_response('index.html',  {'facebook_profile': facebook_profile}, context_instance=RequestContext(request))

def read_csv():
	cr = csv.reader(open('media/data/trackLog-2013-Feb-28_23-13-08.csv', 'rb'))
	for row in cr:
		new_row = DriveData()
   		print row

def home(request):
	return render(request, 'home.html', {'facebook_profile': None})
	#return render_to_response('home.html', { 'facebook_profile': None }, context_instance=RequestContext(request))

def generate_dashboard(request):
	username = request.user.username
	#hard coded for now
	start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
	end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
	avg_speed = get_average(username, 'vehicle_speed', start_time, end_time)
	return render(request, 'dashboard.html', {'vehicle_speed' : avg_speed})

def get_average(username, metric, start_time, end_time):
	print username
	result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time, user_id__exact=username).aggregate(Avg(metric))
	return result 

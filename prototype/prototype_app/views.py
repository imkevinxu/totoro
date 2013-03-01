from django import forms
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.core import serializers
from django.core.context_processors import csrf
from django.core.mail import send_mail
from django.http import HttpResponse, HttpResponseRedirect
from coffin.shortcuts import get_object_or_404, render, \
    redirect
from django.shortcuts import render_to_response
from django.template import loader, RequestContext
from django.views.decorators.csrf import csrf_exempt

from prototype_app.models import *
from prototype_app.model_forms import *
from prototype_app.forms import *

#@login_required
def index(request):
	#facebook_profile = request.user.get_profile().get_facebook_profile()
	#return render_to_response('index.html', { 'facebook_profile': None }, context_instance=RequestContext(request))
    return render(request, 'index.html', locals())

def home(request):
	return render_to_response('home.html', { 'facebook_profile': None }, context_instance=RequestContext(request))
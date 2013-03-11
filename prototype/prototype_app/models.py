from django.db import models
from django.contrib.auth.models import User
from django.contrib import admin
from django import forms

import datetime
import os

class Base(models.Model):
    created_at  = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now_add=True, auto_now=True)

    class Meta:
        abstract = True

class User(models.Model):
    created_at = models.DateTimeField(auto_now_add=True)
    name = models.CharField(max_length=256)
    user_id = models.IntegerField(max_length=100)

class DriveData(models.Model):
    timestamp = models.DateTimeField()
    user_id = models.IntegerField(max_length=100, default=0)
    odometer = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    vehicle_speed = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    steering_wheel_angle = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    air_conditioning = models.IntegerField(max_length=10, default=0)
    pedal_force = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    mpg = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    altitude = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    engine_rpm = models.DecimalField(max_digits=10, decimal_places=2, default=0)

# class Data(Base):
#     data = models.CharField(blank=True, null=True, max_length=255)

#     def __unicode__(self):
#         return u'%s' % (self.data)

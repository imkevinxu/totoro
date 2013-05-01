import json, urllib

from django.db import models
from django.contrib.auth.models import User

class FacebookProfile(models.Model):
    user = models.OneToOneField(User)
    facebook_id = models.BigIntegerField()
    access_token = models.CharField(max_length=150)
    highscore = models.FloatField(null=True, blank=True, default=0)
    currentscore = models.FloatField(null=True, blank=True, default=0)
    rpm = models.FloatField(null=True, blank=True, default=0)
    mpg = models.FloatField(null=True, blank=True, default=0)
    speed = models.FloatField(null=True, blank=True, default=0)
    maf = models.FloatField(null=True, blank=True, default=0)
    runsec = models.FloatField(null=True, blank=True, default=0)
    warmups = models.FloatField(null=True, blank=True, default=0)
    barometer = models.FloatField(null=True, blank=True, default=0)
    ambienttemp = models.FloatField(null=True, blank=True, default=0)
    throttle = models.FloatField(null=True, blank=True, default=0)


    def get_facebook_profile(self):
        fb_profile = urllib.urlopen('https://graph.facebook.com/me?access_token=%s' % self.access_token)
        return json.load(fb_profile)

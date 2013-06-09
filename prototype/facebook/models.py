import json, urllib

from django.db import models
from django.contrib.auth.models import User

from prototype_app.models import Drive

class FacebookProfile(models.Model):
    created_at  = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now_add=True, auto_now=True)
    user = models.OneToOneField(User)
    facebook_id = models.BigIntegerField()
    access_token = models.CharField(max_length=255)
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
    coins = models.IntegerField(null=True, blank=True, default=0)

    def get_facebook_profile(self):
        fb_profile = urllib.urlopen('https://graph.facebook.com/me?access_token=%s' % self.access_token)
        return json.load(fb_profile)

    def _average_mpg(self):
        drives = Drive.objects.filter(fb=self)
        return sum([d.mpg for d in drives]) / len(drives)

    average_mpg = property(_average_mpg)
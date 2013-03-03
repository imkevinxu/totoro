from django.conf.urls.defaults import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    url(r'^generate$', 'simulations.views.generate'),
    url(r'^admin/', include(admin.site.urls)),
)

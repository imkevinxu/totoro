from coffin.conf.urls.defaults import *
from coffin.shortcuts import redirect
from django.contrib.auth.views import logout

from prototype.jinja2 import login

def smartlogin(request, **kwargs):
    if request.user.is_authenticated() and 'next' not in request.GET:
        return redirect('index')
    return login(request, **kwargs)

urlpatterns = patterns('prototype_app.views',
    url(r'^$', 'index', name='index'),
    url(r'^login/$', smartlogin, kwargs=dict(template_name='login.html'), name='login'),
    url(r'^logout/$', logout, kwargs=dict(next_page='/'), name='logout'),

)

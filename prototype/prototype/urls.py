from coffin.conf.urls.defaults import *
from django.contrib import admin
from django.views.generic import TemplateView
from django.conf import settings

admin.autodiscover()

urlpatterns = patterns('',
    ## Admin
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),
    url(r'^admin/', include(admin.site.urls)),

    ## App URL include
    url(r'', include('prototype_app.appurls')),

    # Simulation URL include
    url(r'simulations/', include('simulations.urls')),

    ## Static Links
    # url(r'^', TemplateView.as_view(template_name='index.html'), name='index'),

)

# Development AND Production are using media AND static files
urlpatterns = patterns('',
    url(r'^static/(?P<path>.*)$', 'django.views.static.serve', { 'document_root': settings.STATIC_ROOT }),
    url(r'^media/(?P<path>.*)$', 'django.views.static.serve', { 'document_root': settings.MEDIA_ROOT }),
) + urlpatterns

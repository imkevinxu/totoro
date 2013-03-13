from prototype_app.models import *
from django.contrib import admin

class DriveDataAdmin(admin.ModelAdmin):
    list_display = ('timestamp', 'user_id', 'odometer', 'vehicle_speed', 'steering_wheel_angle', 'air_conditioning', 'pedal_force', 'mpg', 'altitude', 'engine_rpm', 'fuel_used')
    # list_filter = ('created_at',)
    # ordering = ['-created_at']
    # search_fields = ['data']

admin.site.register(DriveData, DriveDataAdmin)
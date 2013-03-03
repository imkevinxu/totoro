# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'DriveData.mpg'
        db.add_column('prototype_app_drivedata', 'mpg',
                      self.gf('django.db.models.fields.DecimalField')(default=0, max_digits=10, decimal_places=2),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'DriveData.mpg'
        db.delete_column('prototype_app_drivedata', 'mpg')


    models = {
        'prototype_app.drivedata': {
            'Meta': {'object_name': 'DriveData'},
            'air_conditioning': ('django.db.models.fields.IntegerField', [], {'max_length': '10'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'mpg': ('django.db.models.fields.DecimalField', [], {'max_digits': '10', 'decimal_places': '2'}),
            'odometer': ('django.db.models.fields.DecimalField', [], {'max_digits': '10', 'decimal_places': '2'}),
            'steering_wheel_angle': ('django.db.models.fields.DecimalField', [], {'max_digits': '10', 'decimal_places': '2'}),
            'timestamp': ('django.db.models.fields.DateTimeField', [], {}),
            'user_id': ('django.db.models.fields.IntegerField', [], {'max_length': '100'}),
            'vehicle_speed': ('django.db.models.fields.DecimalField', [], {'max_digits': '10', 'decimal_places': '2'})
        },
        'prototype_app.user': {
            'Meta': {'object_name': 'User'},
            'created_at': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '256'}),
            'user_id': ('django.db.models.fields.IntegerField', [], {'max_length': '100'})
        }
    }

    complete_apps = ['prototype_app']
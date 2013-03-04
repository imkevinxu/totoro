# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'User'
        db.create_table('prototype_app_user', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('created_at', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=256)),
            ('user_id', self.gf('django.db.models.fields.IntegerField')(max_length=100)),
        ))
        db.send_create_signal('prototype_app', ['User'])

        # Adding model 'DriveData'
        db.create_table('prototype_app_drivedata', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('timestamp', self.gf('django.db.models.fields.DateTimeField')()),
            ('user_id', self.gf('django.db.models.fields.IntegerField')(max_length=100)),
            ('odometer', self.gf('django.db.models.fields.DecimalField')(max_digits=10, decimal_places=2)),
            ('vehicle_speed', self.gf('django.db.models.fields.DecimalField')(max_digits=10, decimal_places=2)),
            ('steering_wheel_angle', self.gf('django.db.models.fields.DecimalField')(max_digits=10, decimal_places=2)),
            ('air_conditioning', self.gf('django.db.models.fields.IntegerField')(max_length=10)),
        ))
        db.send_create_signal('prototype_app', ['DriveData'])


    def backwards(self, orm):
        # Deleting model 'User'
        db.delete_table('prototype_app_user')

        # Deleting model 'DriveData'
        db.delete_table('prototype_app_drivedata')


    models = {
        'prototype_app.drivedata': {
            'Meta': {'object_name': 'DriveData'},
            'air_conditioning': ('django.db.models.fields.IntegerField', [], {'max_length': '10'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
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

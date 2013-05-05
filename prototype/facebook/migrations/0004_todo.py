# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'FacebookProfile.rpm'
        db.add_column('facebook_facebookprofile', 'rpm',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.mpg'
        db.add_column('facebook_facebookprofile', 'mpg',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.speed'
        db.add_column('facebook_facebookprofile', 'speed',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.maf'
        db.add_column('facebook_facebookprofile', 'maf',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.runsec'
        db.add_column('facebook_facebookprofile', 'runsec',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.warmups'
        db.add_column('facebook_facebookprofile', 'warmups',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.barometer'
        db.add_column('facebook_facebookprofile', 'barometer',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.ambienttemp'
        db.add_column('facebook_facebookprofile', 'ambienttemp',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)

        # Adding field 'FacebookProfile.throttle'
        db.add_column('facebook_facebookprofile', 'throttle',
                      self.gf('django.db.models.fields.FloatField')(default=0, null=True, blank=True),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'FacebookProfile.rpm'
        db.delete_column('facebook_facebookprofile', 'rpm')

        # Deleting field 'FacebookProfile.mpg'
        db.delete_column('facebook_facebookprofile', 'mpg')

        # Deleting field 'FacebookProfile.speed'
        db.delete_column('facebook_facebookprofile', 'speed')

        # Deleting field 'FacebookProfile.maf'
        db.delete_column('facebook_facebookprofile', 'maf')

        # Deleting field 'FacebookProfile.runsec'
        db.delete_column('facebook_facebookprofile', 'runsec')

        # Deleting field 'FacebookProfile.warmups'
        db.delete_column('facebook_facebookprofile', 'warmups')

        # Deleting field 'FacebookProfile.barometer'
        db.delete_column('facebook_facebookprofile', 'barometer')

        # Deleting field 'FacebookProfile.ambienttemp'
        db.delete_column('facebook_facebookprofile', 'ambienttemp')

        # Deleting field 'FacebookProfile.throttle'
        db.delete_column('facebook_facebookprofile', 'throttle')


    models = {
        'auth.group': {
            'Meta': {'object_name': 'Group'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        'auth.permission': {
            'Meta': {'ordering': "('content_type__app_label', 'content_type__model', 'codename')", 'unique_together': "(('content_type', 'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': "orm['contenttypes.ContentType']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Group']", 'symmetrical': 'False', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        'facebook.facebookprofile': {
            'Meta': {'object_name': 'FacebookProfile'},
            'access_token': ('django.db.models.fields.CharField', [], {'max_length': '150'}),
            'ambienttemp': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'barometer': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'currentscore': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'facebook_id': ('django.db.models.fields.BigIntegerField', [], {}),
            'highscore': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'maf': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'mpg': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'rpm': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'runsec': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'speed': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'throttle': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'}),
            'user': ('django.db.models.fields.related.OneToOneField', [], {'to': "orm['auth.User']", 'unique': 'True'}),
            'warmups': ('django.db.models.fields.FloatField', [], {'default': '0', 'null': 'True', 'blank': 'True'})
        }
    }

    complete_apps = ['facebook']
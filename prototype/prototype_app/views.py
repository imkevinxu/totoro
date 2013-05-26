from django import forms
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.core import serializers
from django.core.context_processors import csrf
from django.core.mail import send_mail
from django.http import HttpResponse, HttpResponseRedirect
from coffin.shortcuts import render_to_response, get_object_or_404, render, \
    redirect, render_to_string
from django.template import loader, RequestContext
from django.views.decorators.csrf import csrf_exempt
from django.db.models import Avg
from django.db.models import *

from facebook.models import *
from prototype_app.models import *
from prototype_app.model_forms import *
from prototype_app.forms import *
from utils import views as util_views
from datetime import datetime

import csv, random, time

try:
    import json
except ImportError:
    import simplejson as json

log = json.loads('{"trips":[{"currentSpeed": 0.2277019044, "acceleration": 0.2277019044, "airConditioning": 0.0000000000, "steering": 15.6732017951, "odometer": 0.0000632505, "brakingPressure": 0.0000000000, "pedalForce": 0.2277019044, "altitude": 6.1828550528, "fuelUsage": 0.0000000052, "mpg": 12199.1855325786, "tripLength": 1.0000000000, "ecoScore": 95.9774064400 }, {"currentSpeed": 0.5753530802, "acceleration": 0.3476511758, "airConditioning": 0.0000000000, "steering": 38.4983946760, "odometer": 0.0002230708, "brakingPressure": 0.0000000000, "pedalForce": 0.1199492714, "altitude": -16.0134453092, "fuelUsage": 0.0000000383, "mpg": 5826.1393334197, "tripLength": 2.0000000000, "ecoScore": 93.6727053895 }, {"currentSpeed": 1.2805930543, "acceleration": 0.7052399741, "airConditioning": 0.9594769618, "steering": 20.0083290587, "odometer": 0.0005787911, "brakingPressure": 0.0000000000, "pedalForce": 0.3575887982, "altitude": -11.0291519594, "fuelUsage": 0.0000002023, "mpg": 2861.3393526909, "tripLength": 3.0000000000, "ecoScore": 94.9838447141 }, {"currentSpeed": 3.1593460301, "acceleration": 1.8787529758, "airConditioning": 0.7747689736, "steering": 28.5128387809, "odometer": 0.0014563872, "brakingPressure": 0.0000000000, "pedalForce": 1.1735130018, "altitude": -0.9891559984, "fuelUsage": 0.0000012004, "mpg": 1213.2248106322, "tripLength": 4.0000000000, "ecoScore": 89.5144625980 }, {"currentSpeed": 5.0967584132, "acceleration": 1.9374123831, "airConditioning": 0.0000000000, "steering": 8.2008223105, "odometer": 0.0028721535, "brakingPressure": 0.0000000000, "pedalForce": 0.0586594072, "altitude": -16.2719817269, "fuelUsage": 0.0000037981, "mpg": 756.2037517167, "tripLength": 5.0000000000, "ecoScore": 93.3825236246 }, {"currentSpeed": 7.4169553879, "acceleration": 2.3201969747, "airConditioning": 1.2860969231, "steering": 6.7976409215, "odometer": 0.0049324189, "brakingPressure": 0.0000000000, "pedalForce": 0.3827845917, "altitude": -31.4621331983, "fuelUsage": 0.0000092992, "mpg": 530.4107427913, "tripLength": 6.0000000000, "ecoScore": 91.9533702620 }, {"currentSpeed": 9.9902636908, "acceleration": 2.5733083029, "airConditioning": 1.0671861318, "steering": 14.7536579666, "odometer": 0.0077074921, "brakingPressure": 0.0000000000, "pedalForce": 0.2531113282, "altitude": -12.4878641788, "fuelUsage": 0.0000192798, "mpg": 399.7707341406, "tripLength": 7.0000000000, "ecoScore": 89.5208196426 }, {"currentSpeed": 12.6693614135, "acceleration": 2.6790977227, "airConditioning": 0.6377768177, "steering": 17.7364530440, "odometer": 0.0112267592, "brakingPressure": 0.0000000000, "pedalForce": 0.1057894197, "altitude": 22.6901544238, "fuelUsage": 0.0000353311, "mpg": 317.7589778044, "tripLength": 8.0000000000, "ecoScore": 88.6097846088 }, {"currentSpeed": 15.5061239576, "acceleration": 2.8367625441, "airConditioning": 0.2716944752, "steering": 16.0361500555, "odometer": 0.0155340158, "brakingPressure": 0.0000000000, "pedalForce": 0.1576648214, "altitude": -3.7238377155, "fuelUsage": 0.0000593750, "mpg": 261.6253505185, "tripLength": 9.0000000000, "ecoScore": 87.9443427982 }, {"currentSpeed": 18.3548162828, "acceleration": 2.8486923252, "airConditioning": 0.0000000000, "steering": 9.0238724674, "odometer": 0.0206325759, "brakingPressure": 0.0000000000, "pedalForce": 0.0119297812, "altitude": 45.8677657409, "fuelUsage": 0.0000930650, "mpg": 221.7007772269, "tripLength": 10.0000000000, "ecoScore": 88.8809742286 }, {"currentSpeed": 21.3714407801, "acceleration": 3.0166244973, "airConditioning": 0.0000000000, "steering": 3.6122851997, "odometer": 0.0265690872, "brakingPressure": 0.0000000000, "pedalForce": 0.1679321721, "altitude": -13.6965623805, "fuelUsage": 0.0001387388, "mpg": 191.5043520883, "tripLength": 11.0000000000, "ecoScore": 88.9946394777 }, {"currentSpeed": 24.5896169640, "acceleration": 3.2181761838, "airConditioning": 0.1343538164, "steering": 12.4597313925, "odometer": 0.0333995364, "brakingPressure": 0.0000000000, "pedalForce": 0.2015516865, "altitude": -3.3570166838, "fuelUsage": 0.0001992037, "mpg": 167.6652048158, "tripLength": 12.0000000000, "ecoScore": 86.1053199281 }, {"currentSpeed": 27.9467733882, "acceleration": 3.3571564242, "airConditioning": 0.0374372818, "steering": 19.8538262854, "odometer": 0.0411625290, "brakingPressure": 0.0000000000, "pedalForce": 0.1389802404, "altitude": -2.5499979615, "fuelUsage": 0.0002773060, "mpg": 148.4372329481, "tripLength": 13.0000000000, "ecoScore": 84.2710530031 }, {"currentSpeed": 31.5283920295, "acceleration": 3.5816186413, "airConditioning": 1.1140174744, "steering": 28.1961194099, "odometer": 0.0499204157, "brakingPressure": 0.0000000000, "pedalForce": 0.2244622171, "altitude": -3.9718504641, "fuelUsage": 0.0003767099, "mpg": 132.5168641359, "tripLength": 14.0000000000, "ecoScore": 81.8506969363 }, {"currentSpeed": 35.1944129017, "acceleration": 3.6660208722, "airConditioning": 1.1629987446, "steering": 30.6062583020, "odometer": 0.0596966415, "brakingPressure": 0.0000000000, "pedalForce": 0.0844022308, "altitude": -28.2964624213, "fuelUsage": 0.0005005746, "mpg": 119.2562389884, "tripLength": 15.0000000000, "ecoScore": 81.0273973868 }, {"currentSpeed": 38.9629504300, "acceleration": 3.7685375283, "airConditioning": 2.1296671332, "steering": 26.1130721289, "odometer": 0.0705196833, "brakingPressure": 0.0000000000, "pedalForce": 0.1025166561, "altitude": -10.9667347726, "fuelUsage": 0.0006523857, "mpg": 108.0950735035, "tripLength": 16.0000000000, "ecoScore": 80.6869523627 }, {"currentSpeed": 43.0853493329, "acceleration": 4.1223989030, "airConditioning": 2.1322964717, "steering": 27.5149516684, "odometer": 0.0824878358, "brakingPressure": 0.0000000000, "pedalForce": 0.3538613747, "altitude": 7.1360192597, "fuelUsage": 0.0008380205, "mpg": 98.4317682957, "tripLength": 17.0000000000, "ecoScore": 77.7160478834 }, {"currentSpeed": 47.4160412652, "acceleration": 4.3306919323, "airConditioning": 2.5650373014, "steering": 25.5052217133, "odometer": 0.0956589584, "brakingPressure": 0.0000000000, "pedalForce": 0.2082930293, "altitude": 14.8289250401, "fuelUsage": 0.0010628486, "mpg": 90.0024351584, "tripLength": 18.0000000000, "ecoScore": 76.1858009199 }, {"currentSpeed": 51.7891961891, "acceleration": 4.3731549239, "airConditioning": 3.4996780799, "steering": 23.5428692590, "odometer": 0.1100448462, "brakingPressure": 0.0000000000, "pedalForce": 0.0424629917, "altitude": 4.0144837387, "fuelUsage": 0.0013310606, "mpg": 82.6745549874, "tripLength": 19.0000000000, "ecoScore": 76.0233399768 }, {"currentSpeed": 56.2072759934, "acceleration": 4.4180798043, "airConditioning": 1.9462229733, "steering": 20.4145782376, "odometer": 0.1256579785, "brakingPressure": 0.0000000000, "pedalForce": 0.0449248803, "altitude": -0.4954009608, "fuelUsage": 0.0016469864, "mpg": 76.2956975310, "tripLength": 20.0000000000, "ecoScore": 75.9622307076 }, {"currentSpeed": 60.7039131491, "acceleration": 4.4966371557, "airConditioning": 2.3076954831, "steering": 21.1066165807, "odometer": 0.1425201766, "brakingPressure": 0.0000000000, "pedalForce": 0.0785573514, "altitude": 0.1736877592, "fuelUsage": 0.0020154829, "mpg": 70.7126683971, "tripLength": 21.0000000000, "ecoScore": 75.1855757024 }, {"currentSpeed": 65.2336127826, "acceleration": 4.5296996335, "airConditioning": 0.5635067125, "steering": 23.2546783883, "odometer": 0.1606406246, "brakingPressure": 0.0000000000, "pedalForce": 0.0330624778, "altitude": -25.3594342156, "fuelUsage": 0.0024410254, "mpg": 65.8086667867, "tripLength": 22.0000000000, "ecoScore": 74.6594746064 }, {"currentSpeed": 69.7642898128, "acceleration": 4.5306770302, "airConditioning": 0.0000000000, "steering": 12.0085269377, "odometer": 0.1800195940, "brakingPressure": 0.0000000000, "pedalForce": 0.0009773967, "altitude": -5.8838718432, "fuelUsage": 0.0029277310, "mpg": 61.4877512892, "tripLength": 23.0000000000, "ecoScore": 76.0076334929 }, {"currentSpeed": 74.2951787515, "acceleration": 4.5308889387, "airConditioning": 0.6366352283, "steering": 8.9651187762, "odometer": 0.2006571436, "brakingPressure": 0.0000000000, "pedalForce": 0.0002119085, "altitude": 12.1892182999, "fuelUsage": 0.0034797083, "mpg": 57.6649317832, "tripLength": 24.0000000000, "ecoScore": 76.4768646064 }, {"currentSpeed": 78.8223507218, "acceleration": 4.5271719703, "airConditioning": 1.6835779897, "steering": 13.5558108873, "odometer": 0.2225522410, "brakingPressure": 0.0037169684, "pedalForce": 0.0000000000, "altitude": 12.8137394799, "fuelUsage": 0.0041010046, "mpg": 54.2677370862, "tripLength": 25.0000000000, "ecoScore": 75.8228922197 }, {"currentSpeed": 83.2732797741, "acceleration": 4.4509290523, "airConditioning": 0.0000000000, "steering": 7.4021981746, "odometer": 0.2456837076, "brakingPressure": 0.0762429180, "pedalForce": 0.0000000000, "altitude": -8.6206313285, "fuelUsage": 0.0047944485, "mpg": 51.2433714698, "tripLength": 26.0000000000, "ecoScore": 77.4680892686 }, {"currentSpeed": 87.6914466651, "acceleration": 4.4181668910, "airConditioning": 0.9117583439, "steering": -4.8332412837, "odometer": 0.2700424428, "brakingPressure": 0.0327621612, "pedalForce": 0.0000000000, "altitude": -27.1886722481, "fuelUsage": 0.0055634275, "mpg": 48.5388623577, "tripLength": 27.0000000000, "ecoScore": 78.2813027660 }, {"currentSpeed": 92.0754551334, "acceleration": 4.3840084683, "airConditioning": 2.7633663965, "steering": 4.0402819932, "odometer": 0.2956189581, "brakingPressure": 0.0341584227, "pedalForce": 0.0000000000, "altitude": 12.4446060238, "fuelUsage": 0.0064112165, "mpg": 46.1096516743, "tripLength": 28.0000000000, "ecoScore": 78.7703846225 }, {"currentSpeed": 96.4410120352, "acceleration": 4.3655569018, "airConditioning": 2.2416048800, "steering": 0.8405285237, "odometer": 0.3224081281, "brakingPressure": 0.0184515665, "pedalForce": 0.0000000000, "altitude": -8.1227462470, "fuelUsage": 0.0073413033, "mpg": 43.9170148492, "tripLength": 29.0000000000, "ecoScore": 80.0251032284 }, {"currentSpeed": 100.7438656166, "acceleration": 4.3028535814, "airConditioning": 1.5489401452, "steering": 5.8357698015, "odometer": 0.3503925353, "brakingPressure": 0.0627033204, "pedalForce": 0.0000000000, "altitude": -5.3819101649, "fuelUsage": 0.0083562360, "mpg": 41.9318620868, "tripLength": 30.0000000000, "ecoScore": 79.0694707239 }, {"currentSpeed": 104.9999417439, "acceleration": 4.2560761273, "airConditioning": 1.3256588253, "steering": 17.9494418044, "odometer": 0.3795591857, "brakingPressure": 0.0467774541, "pedalForce": 0.0000000000, "altitude": 3.9963114572, "fuelUsage": 0.0094587348, "mpg": 40.1279024049, "tripLength": 31.0000000000, "ecoScore": 77.6490354866 }, {"currentSpeed": 109.2507677917, "acceleration": 4.2508260478, "airConditioning": 0.0000000000, "steering": 5.7377906240, "odometer": 0.4099066212, "brakingPressure": 0.0052500795, "pedalForce": 0.0000000000, "altitude": 0.3813293543, "fuelUsage": 0.0106523078, "mpg": 38.4805460996, "tripLength": 32.0000000000, "ecoScore": 79.5351091889 }, {"currentSpeed": 113.4613981708, "acceleration": 4.2106303792, "airConditioning": 2.0169115332, "steering": -2.4811391447, "odometer": 0.4414236763, "brakingPressure": 0.0401956686, "pedalForce": 0.0000000000, "altitude": 7.4037052698, "fuelUsage": 0.0119396567, "mpg": 36.9712201918, "tripLength": 33.0000000000, "ecoScore": 80.6953636542 }, {"currentSpeed": 117.6242711152, "acceleration": 4.1628729443, "airConditioning": 2.2061679722, "steering": -12.5139023060, "odometer": 0.4740970849, "brakingPressure": 0.0477574348, "pedalForce": 0.0000000000, "altitude": -21.6060251012, "fuelUsage": 0.0133232036, "mpg": 35.5843158426, "tripLength": 34.0000000000, "ecoScore": 79.1328804830 }, {"currentSpeed": 121.7398120714, "acceleration": 4.1155409562, "airConditioning": 2.9851929956, "steering": -8.5722681954, "odometer": 0.5079136994, "brakingPressure": 0.0473319881, "pedalForce": 0.0000000000, "altitude": 37.8711640079, "fuelUsage": 0.0148052618, "mpg": 34.3062964236, "tripLength": 35.0000000000, "ecoScore": 80.1343729904 }, {"currentSpeed": 125.5879099138, "acceleration": 3.8480978424, "airConditioning": 4.6421689121, "steering": -0.3520357029, "odometer": 0.5427992299, "brakingPressure": 0.2674431138, "pedalForce": 0.0000000000, "altitude": 12.7549450310, "fuelUsage": 0.0163824941, "mpg": 33.1328811568, "tripLength": 36.0000000000, "ecoScore": 84.5796879407 }, {"currentSpeed": 129.4351728417, "acceleration": 3.8472629279, "airConditioning": 5.2614114646, "steering": 14.5437818487, "odometer": 0.5787534446, "brakingPressure": 0.0008349145, "pedalForce": 0.0000000000, "altitude": -27.2160383361, "fuelUsage": 0.0180578405, "mpg": 32.0499809953, "tripLength": 37.0000000000, "ecoScore": 81.3849369051 }, {"currentSpeed": 132.3448758976, "acceleration": 2.9097030560, "airConditioning": 5.3040617446, "steering": 5.8899908356, "odometer": 0.6155159101, "brakingPressure": 0.9375598719, "pedalForce": 0.0000000000, "altitude": -7.0767729773, "fuelUsage": 0.0198093571, "mpg": 31.0719780913, "tripLength": 38.0000000000, "ecoScore": 88.2825653094 }, {"currentSpeed": 134.8950243338, "acceleration": 2.5501484361, "airConditioning": 5.8721396531, "steering": 17.6537199537, "odometer": 0.6529867502, "brakingPressure": 0.3595546198, "pedalForce": 0.0000000000, "altitude": 5.4908831764, "fuelUsage": 0.0216290239, "mpg": 30.1903014289, "tripLength": 39.0000000000, "ecoScore": 89.2486268929 }, {"currentSpeed": 137.2735418414, "acceleration": 2.3785175076, "airConditioning": 6.6135353975, "steering": 5.7059731745, "odometer": 0.6911182896, "brakingPressure": 0.1716309285, "pedalForce": 0.0000000000, "altitude": -27.7478929681, "fuelUsage": 0.0235134264, "mpg": 29.3924959264, "tripLength": 40.0000000000, "ecoScore": 91.9488808102 }, {"currentSpeed": 139.3405239034, "acceleration": 2.0669820620, "airConditioning": 6.2879325444, "steering": -12.3096561718, "odometer": 0.7298239907, "brakingPressure": 0.3115354456, "pedalForce": 0.0000000000, "altitude": -15.5472207976, "fuelUsage": 0.0254550046, "mpg": 28.6711396588, "tripLength": 41.0000000000, "ecoScore": 92.1888373305 }, {"currentSpeed": 141.0311716741, "acceleration": 1.6906477707, "airConditioning": 6.9815485467, "steering": -17.2134655906, "odometer": 0.7689993162, "brakingPressure": 0.3763342913, "pedalForce": 0.0000000000, "altitude": 4.5102542934, "fuelUsage": 0.0274439837, "mpg": 28.0206884201, "tripLength": 42.0000000000, "ecoScore": 92.9394994569 }, {"currentSpeed": 142.2734733208, "acceleration": 1.2423016468, "airConditioning": 6.1742358358, "steering": -25.1717332029, "odometer": 0.8085197254, "brakingPressure": 0.4483461239, "pedalForce": 0.0000000000, "altitude": -38.7895306022, "fuelUsage": 0.0294681578, "mpg": 27.4370637767, "tripLength": 43.0000000000, "ecoScore": 93.3494187311 }, {"currentSpeed": 143.1723263443, "acceleration": 0.8988530234, "airConditioning": 7.8965056108, "steering": -17.2417981597, "odometer": 0.8482898161, "brakingPressure": 0.3434486234, "pedalForce": 0.0000000000, "altitude": 13.4811860192, "fuelUsage": 0.0315179893, "mpg": 26.9144648614, "tripLength": 44.0000000000, "ecoScore": 94.9992266593 }, {"currentSpeed": 143.7979744539, "acceleration": 0.6256481097, "airConditioning": 8.4532017533, "steering": -38.5867982629, "odometer": 0.8882336979, "brakingPressure": 0.2732049137, "pedalForce": 0.0000000000, "altitude": -15.3145606713, "fuelUsage": 0.0335857751, "mpg": 26.4467232366, "tripLength": 45.0000000000, "ecoScore": 93.3763448868 }, {"currentSpeed": 144.2925968282, "acceleration": 0.4946223742, "airConditioning": 7.2290856008, "steering": -30.5215448473, "odometer": 0.9283149748, "brakingPressure": 0.1310257355, "pedalForce": 0.0000000000, "altitude": 18.3553293953, "fuelUsage": 0.0356678104, "mpg": 26.0266880397, "tripLength": 46.0000000000, "ecoScore": 94.2284685477 }, {"currentSpeed": 144.5903251064, "acceleration": 0.2977282783, "airConditioning": 4.9839889577, "steering": -40.1063048856, "odometer": 0.9684789540, "brakingPressure": 0.1968940959, "pedalForce": 0.0000000000, "altitude": -1.9818945773, "fuelUsage": 0.0377584466, "mpg": 25.6493325456, "tripLength": 47.0000000000, "ecoScore": 93.5707709397 }, {"currentSpeed": 144.8016529975, "acceleration": 0.2113278910, "airConditioning": 4.2260101895, "steering": -38.5861949059, "odometer": 1.0087016354, "brakingPressure": 0.0864003873, "pedalForce": 0.0000000000, "altitude": -9.2724093331, "fuelUsage": 0.0398551985, "mpg": 25.3091609964, "tripLength": 48.0000000000, "ecoScore": 93.7429168181 }, {"currentSpeed": 144.3846924268, "acceleration": -0.4169605707, "airConditioning": 3.1169718037, "steering": -32.3514666467, "odometer": 1.0488084944, "brakingPressure": 0.6282884617, "pedalForce": 0.0000000000, "altitude": 16.0174262036, "fuelUsage": 0.0419398924, "mpg": 25.0074197464, "tripLength": 49.0000000000, "ecoScore": 93.8902943095 }, {"currentSpeed": 143.6186342741, "acceleration": -0.7660581527, "airConditioning": 3.9585711275, "steering": -44.7593656266, "odometer": 1.0887025594, "brakingPressure": 0.3490975820, "pedalForce": 0.0000000000, "altitude": 6.4813176929, "fuelUsage": 0.0440025237, "mpg": 24.7418209027, "tripLength": 50.0000000000, "ecoScore": 92.6803666277 }, {"currentSpeed": 142.7682361285, "acceleration": -0.8503981456, "airConditioning": 4.6183096684, "steering": -44.2586582145, "odometer": 1.1283604028, "brakingPressure": 0.0843399929, "pedalForce": 0.0000000000, "altitude": 51.9057490799, "fuelUsage": 0.0460408006, "mpg": 24.5078362822, "tripLength": 51.0000000000, "ecoScore": 92.6235049566 }, {"currentSpeed": 141.8698131801, "acceleration": -0.8984229484, "airConditioning": 4.2385670605, "steering": -41.3398081876, "odometer": 1.1677686842, "brakingPressure": 0.0480248029, "pedalForce": 0.0000000000, "altitude": -42.9593983465, "fuelUsage": 0.0480535050, "mpg": 24.3014257774, "tripLength": 52.0000000000, "ecoScore": 92.7631213962 }, {"currentSpeed": 115.6643298066, "acceleration": -26.2054833735, "airConditioning": 3.8835951976, "steering": -37.2834143978, "odometer": 1.1998976647, "brakingPressure": 25.3070604250, "pedalForce": 0.0000000000, "altitude": -4.9413796793, "fuelUsage": 0.0493913287, "mpg": 24.2936907495, "tripLength": 53.0000000000, "ecoScore": 0.0000000000 }, {"currentSpeed": 63.7969694463, "acceleration": -51.8673603604, "airConditioning": 3.3418458043, "steering": -42.9713777343, "odometer": 1.2176190452, "brakingPressure": 25.6618769869, "pedalForce": 0.0000000000, "altitude": -30.0053801949, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 54.0000000000, "ecoScore": 0.0000000000 }, {"currentSpeed": 0.0000000000, "acceleration": 0.0000000000, "airConditioning": 3.7625275366, "steering": -50.4604235953, "odometer": 1.2176190452, "brakingPressure": 0.0000000000, "pedalForce": 0.0000000000, "altitude": 8.6208817955, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 55.0000000000, "ecoScore": 92.8964499301 }, {"currentSpeed": 0.0000000000, "acceleration": 0.0000000000, "airConditioning": 4.2071376495, "steering": -46.0318851279, "odometer": 1.2176190452, "brakingPressure": 0.0000000000, "pedalForce": 0.0000000000, "altitude": -31.4613086249, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 56.0000000000, "ecoScore": 93.2153198213 }, {"currentSpeed": 0.0000000000, "acceleration": 0.0000000000, "airConditioning": 3.1327645696, "steering": -42.5604961010, "odometer": 1.2176190452, "brakingPressure": 0.0000000000, "pedalForce": 0.0000000000, "altitude": -19.9621600088, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 57.0000000000, "ecoScore": 93.4761594056 }, {"currentSpeed": 0.0000000000, "acceleration": 0.0000000000, "airConditioning": 2.5497107465, "steering": -17.6100422222, "odometer": 1.2176190452, "brakingPressure": 0.0000000000, "pedalForce": 0.0000000000, "altitude": 1.0348028149, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 58.0000000000, "ecoScore": 95.8035679176 }, {"currentSpeed": 0.0000000000, "acceleration": 0.0000000000, "airConditioning": 2.2906146686, "steering": -25.1376883215, "odometer": 1.2176190452, "brakingPressure": 0.0000000000, "pedalForce": 0.0000000000, "altitude": -23.2735403544, "fuelUsage": 0.0497983340, "mpg": 24.4509996015, "tripLength": 59.0000000000, "ecoScore": 94.9862500739 }, {"currentSpeed": 0.5728918719, "acceleration": 0.5728918719, "airConditioning": 2.4023474287, "steering": -45.4959495308, "odometer": 1.2177781818, "brakingPressure": 0.0000000000, "pedalForce": 0.5728918719, "altitude": 36.6836235782, "fuelUsage": 0.0497983668, "mpg": 24.4541791061, "tripLength": 60.0000000000, "ecoScore": 92.7387003366 }, {"currentSpeed": 1.4849569758, "acceleration": 0.9120651039, "airConditioning": 2.5347160841, "steering": -39.1215987559, "odometer": 1.2181906698, "brakingPressure": 0.0000000000, "pedalForce": 0.3391732320, "altitude": 11.7488372968, "fuelUsage": 0.0497985873, "mpg": 24.4623539497, "tripLength": 61.0000000000, "ecoScore": 92.8743931592 }, {"currentSpeed": 2.6947266865, "acceleration": 1.2097697107, "airConditioning": 3.7873231046, "steering": -38.3299695789, "odometer": 1.2189392050, "brakingPressure": 0.0000000000, "pedalForce": 0.2977046068, "altitude": 20.2085158974, "fuelUsage": 0.0497993135, "mpg": 24.4770282830, "tripLength": 62.0000000000, "ecoScore": 92.3189520534 }, {"currentSpeed": 4.2509558241, "acceleration": 1.5562291377, "airConditioning": 5.9026853949, "steering": -42.4465995425, "odometer": 1.2201200261, "brakingPressure": 0.0000000000, "pedalForce": 0.3464594270, "altitude": -13.0145588057, "fuelUsage": 0.0498011206, "mpg": 24.4998508527, "tripLength": 63.0000000000, "ecoScore": 91.0214584239 }, {"currentSpeed": 6.0363419165, "acceleration": 1.7853860924, "airConditioning": 7.5298618794, "steering": -36.3005158987, "odometer": 1.2217967877, "brakingPressure": 0.0000000000, "pedalForce": 0.2291569547, "altitude": -0.5827679489, "fuelUsage": 0.0498047643, "mpg": 24.5317251225, "tripLength": 64.0000000000, "ecoScore": 90.7753718567 }, {"currentSpeed": 8.2731587108, "acceleration": 2.2368167943, "airConditioning": 7.5676082520, "steering": -47.0958694357, "odometer": 1.2240948874, "brakingPressure": 0.0000000000, "pedalForce": 0.4514307019, "altitude": 10.8666530992, "fuelUsage": 0.0498116088, "mpg": 24.5744900908, "tripLength": 65.0000000000, "ecoScore": 88.0420106753 }, {"currentSpeed": 10.7529364919, "acceleration": 2.4797777812, "airConditioning": 5.8939245048, "steering": -45.3802393173, "odometer": 1.2270818142, "brakingPressure": 0.0000000000, "pedalForce": 0.2429609868, "altitude": -16.2498201743, "fuelUsage": 0.0498231714, "mpg": 24.6287375946, "tripLength": 66.0000000000, "ecoScore": 87.0998744790 }, {"currentSpeed": 13.3526373771, "acceleration": 2.5997008851, "airConditioning": 5.7900639476, "steering": -59.0097119617, "odometer": 1.2307908801, "brakingPressure": 0.0000000000, "pedalForce": 0.1199231040, "altitude": 4.1984496859, "fuelUsage": 0.0498410007, "mpg": 24.6943452856, "tripLength": 67.0000000000, "ecoScore": 85.5580527109 }, {"currentSpeed": 16.4219395485, "acceleration": 3.0693021714, "airConditioning": 3.8092063028, "steering": -63.6802442707, "odometer": 1.2353525300, "brakingPressure": 0.0000000000, "pedalForce": 0.4696012863, "altitude": 58.0607498550, "fuelUsage": 0.0498679687, "mpg": 24.7724654223, "tripLength": 68.0000000000, "ecoScore": 82.4958349414 }, {"currentSpeed": 19.4938362520, "acceleration": 3.0718967035, "airConditioning": 2.8548829195, "steering": -44.3278137821, "odometer": 1.2407674845, "brakingPressure": 0.0000000000, "pedalForce": 0.0025945321, "altitude": -11.3456252794, "fuelUsage": 0.0499059697, "mpg": 24.8621055385, "tripLength": 69.0000000000, "ecoScore": 83.9055370048 }, {"currentSpeed": 22.5990933354, "acceleration": 3.1052570834, "airConditioning": 1.3897897295, "steering": -51.6805528161, "odometer": 1.2470450104, "brakingPressure": 0.0000000000, "pedalForce": 0.0333603799, "altitude": -8.7682073234, "fuelUsage": 0.0499570416, "mpg": 24.9623470811, "tripLength": 70.0000000000, "ecoScore": 83.1684225670 }, {"currentSpeed": 25.8263078967, "acceleration": 3.2272145612, "airConditioning": 0.0000000000, "steering": -59.7480585527, "odometer": 1.2542189848, "brakingPressure": 0.0000000000, "pedalForce": 0.1219574778, "altitude": -21.2288099680, "fuelUsage": 0.0500237414, "mpg": 25.0724745977, "tripLength": 71.0000000000, "ecoScore": 81.8535853917 }, {"currentSpeed": 29.2288383642, "acceleration": 3.4025304675, "airConditioning": 0.0000000000, "steering": -54.3971744153, "odometer": 1.2623381066, "brakingPressure": 0.0000000000, "pedalForce": 0.1753159063, "altitude": 6.2280085684, "fuelUsage": 0.0501091739, "mpg": 25.1917565004, "tripLength": 72.0000000000, "ecoScore": 81.0419539514 }, {"currentSpeed": 32.8038128447, "acceleration": 3.5749744805, "airConditioning": 0.0000000000, "steering": -56.4749953960, "odometer": 1.2714502768, "brakingPressure": 0.0000000000, "pedalForce": 0.1724440129, "altitude": -26.9878943541, "fuelUsage": 0.0502167829, "mpg": 25.3192300221, "tripLength": 73.0000000000, "ecoScore": 79.6994447835 }, {"currentSpeed": 36.6142288622, "acceleration": 3.8104160176, "airConditioning": 0.0000000000, "steering": -62.3090176392, "odometer": 1.2816208960, "brakingPressure": 0.0000000000, "pedalForce": 0.2354415371, "altitude": -22.9132617445, "fuelUsage": 0.0503508431, "mpg": 25.4538120528, "tripLength": 74.0000000000, "ecoScore": 77.5740724851 }, {"currentSpeed": 40.4734174730, "acceleration": 3.8591886108, "airConditioning": 0.0430457652, "steering": -60.9462211620, "odometer": 1.2928635119, "brakingPressure": 0.0000000000, "pedalForce": 0.0487725932, "altitude": -9.5090108473, "fuelUsage": 0.0505146528, "mpg": 25.5938314913, "tripLength": 75.0000000000, "ecoScore": 77.2997411685 }, {"currentSpeed": 44.4061904183, "acceleration": 3.9327729453, "airConditioning": 0.0000000000, "steering": -66.4441727283, "odometer": 1.3051985648, "brakingPressure": 0.0000000000, "pedalForce": 0.0735843345, "altitude": -8.5954794400, "fuelUsage": 0.0507118438, "mpg": 25.7375490091, "tripLength": 76.0000000000, "ecoScore": 76.3815690202 }, {"currentSpeed": 48.4056760815, "acceleration": 3.9994856632, "airConditioning": 1.1735633113, "steering": -74.3174986312, "odometer": 1.3186445860, "brakingPressure": 0.0000000000, "pedalForce": 0.0667127179, "altitude": -5.2018945909, "fuelUsage": 0.0509461547, "mpg": 25.8831033041, "tripLength": 77.0000000000, "ecoScore": 75.3830577709 }, {"currentSpeed": 52.4734787640, "acceleration": 4.0678026825, "airConditioning": 2.2450986656, "steering": -82.7770088448, "odometer": 1.3332205523, "brakingPressure": 0.0000000000, "pedalForce": 0.0683170192, "altitude": -8.3971655611, "fuelUsage": 0.0512215013, "mpg": 26.0285332838, "tripLength": 78.0000000000, "ecoScore": 74.3544753678 }, {"currentSpeed": 56.5658581385, "acceleration": 4.0923793745, "airConditioning": 1.8500693586, "steering": -87.0735952381, "odometer": 1.3489332907, "brakingPressure": 0.0000000000, "pedalForce": 0.0245766921, "altitude": 3.3012406841, "fuelUsage": 0.0515414710, "mpg": 26.1718042858, "tripLength": 79.0000000000, "ecoScore": 73.9210928724 }, {"currentSpeed": 60.6655863196, "acceleration": 4.0997281811, "airConditioning": 2.3682047157, "steering": -81.2173667755, "odometer": 1.3657848424, "brakingPressure": 0.0000000000, "pedalForce": 0.0073488066, "altitude": -29.6263604281, "fuelUsage": 0.0519095023, "mpg": 26.3108830157, "tripLength": 80.0000000000, "ecoScore": 74.1801606028 }, {"currentSpeed": 64.7627358239, "acceleration": 4.0971495043, "airConditioning": 3.2601222997, "steering": -84.4016288935, "odometer": 1.3837744913, "brakingPressure": 0.0025786768, "pedalForce": 0.0000000000, "altitude": 19.9350239734, "fuelUsage": 0.0523289235, "mpg": 26.4437790582, "tripLength": 81.0000000000, "ecoScore": 74.0263300078 }, {"currentSpeed": 68.8423174643, "acceleration": 4.0795816404, "airConditioning": 4.1326274292, "steering": -73.7549400037, "odometer": 1.4028973572, "brakingPressure": 0.0175678639, "pedalForce": 0.0000000000, "altitude": -14.9352019030, "fuelUsage": 0.0528028500, "mpg": 26.5685916237, "tripLength": 82.0000000000, "ecoScore": 74.7689385853 }, {"currentSpeed": 72.9166508782, "acceleration": 4.0743334139, "airConditioning": 5.0547553841, "steering": -77.9327904620, "odometer": 1.4231519825, "brakingPressure": 0.0052482265, "pedalForce": 0.0000000000, "altitude": 16.9088295019, "fuelUsage": 0.0533345338, "mpg": 26.6834990790, "tripLength": 83.0000000000, "ecoScore": 74.5718520321 }, {"currentSpeed": 76.9254199856, "acceleration": 4.0087691074, "airConditioning": 4.2973105998, "steering": -98.2546675396, "odometer": 1.4445201547, "brakingPressure": 0.0655643065, "pedalForce": 0.0000000000, "altitude": 4.9417443443, "fuelUsage": 0.0539262858, "mpg": 26.7869394986, "tripLength": 84.0000000000, "ecoScore": 74.0171391596 }, {"currentSpeed": 80.8846877924, "acceleration": 3.9592678068, "airConditioning": 5.0858387833, "steering": -103.5826730417, "odometer": 1.4669881235, "brakingPressure": 0.0495013006, "pedalForce": 0.0000000000, "altitude": 29.8980670931, "fuelUsage": 0.0545805191, "mpg": 26.8775040781, "tripLength": 85.0000000000, "ecoScore": 74.1465198153 }, {"currentSpeed": 84.8301850292, "acceleration": 3.9454972368, "airConditioning": 4.3975967468, "steering": -100.6558706984, "odometer": 1.4905520638, "brakingPressure": 0.0137705699, "pedalForce": 0.0000000000, "altitude": 14.8176568745, "fuelUsage": 0.0553001351, "mpg": 26.9538593623, "tripLength": 86.0000000000, "ecoScore": 74.4003090031 }, {"currentSpeed": 88.7489602921, "acceleration": 3.9187752629, "airConditioning": 4.8032495290, "steering": -93.5630825742, "odometer": 1.5152045528, "brakingPressure": 0.0267219740, "pedalForce": 0.0000000000, "altitude": -21.2349361324, "fuelUsage": 0.0560877729, "mpg": 27.0148817640, "tripLength": 87.0000000000, "ecoScore": 74.9703801829 }, {"currentSpeed": 92.5878373901, "acceleration": 3.8388770980, "airConditioning": 5.0726675567, "steering": -89.8952914444, "odometer": 1.5409233965, "brakingPressure": 0.0798981648, "pedalForce": 0.0000000000, "altitude": -35.7001288898, "fuelUsage": 0.0569450236, "mpg": 27.0598429483, "tripLength": 88.0000000000, "ecoScore": 75.7811998301 }, {"currentSpeed": 96.1956922534, "acceleration": 3.6078548633, "airConditioning": 4.3457322382, "steering": -93.2414158029, "odometer": 1.5676444221, "brakingPressure": 0.2310222347, "pedalForce": 0.0000000000, "altitude": 3.3338815139, "fuelUsage": 0.0578703848, "mpg": 27.0888888763, "tripLength": 89.0000000000, "ecoScore": 77.3148938597 }, {"currentSpeed": 99.6721825013, "acceleration": 3.4764902478, "airConditioning": 5.2473465663, "steering": -92.4645156951, "odometer": 1.5953311395, "brakingPressure": 0.1313646155, "pedalForce": 0.0000000000, "altitude": -23.3375963113, "fuelUsage": 0.0588638392, "mpg": 27.1020572599, "tripLength": 90.0000000000, "ecoScore": 78.2959015291 }, {"currentSpeed": 102.5076750728, "acceleration": 2.8354925715, "airConditioning": 4.9349577243, "steering": -84.7446951616, "odometer": 1.6238054937, "brakingPressure": 0.6409976763, "pedalForce": 0.0000000000, "altitude": -8.0748046602, "fuelUsage": 0.0599146215, "mpg": 27.1019903464, "tripLength": 91.0000000000, "ecoScore": 82.4909218241 }, {"currentSpeed": 105.2666953693, "acceleration": 2.7590202965, "airConditioning": 3.4122623704, "steering": -87.3294488554, "odometer": 1.6530462424, "brakingPressure": 0.0764722750, "pedalForce": 0.0000000000, "altitude": 3.4266878216, "fuelUsage": 0.0610227292, "mpg": 27.0890250808, "tripLength": 92.0000000000, "ecoScore": 83.0423371146 }, {"currentSpeed": 107.4892536477, "acceleration": 2.2225582783, "airConditioning": 3.5448063803, "steering": -90.4771836538, "odometer": 1.6829043684, "brakingPressure": 0.5364620182, "pedalForce": 0.0000000000, "altitude": -35.6867162292, "fuelUsage": 0.0621781232, "mpg": 27.0658598580, "tripLength": 93.0000000000, "ecoScore": 85.3938959731 }, {"currentSpeed": 109.5044200054, "acceleration": 2.0151663577, "airConditioning": 3.0509500556, "steering": -92.8666593958, "odometer": 1.7133222628, "brakingPressure": 0.2073919206, "pedalForce": 0.0000000000, "altitude": 24.5497139040, "fuelUsage": 0.0633772450, "mpg": 27.0337131759, "tripLength": 94.0000000000, "ecoScore": 86.2934494391 }, {"currentSpeed": 111.4588439637, "acceleration": 1.9544239583, "airConditioning": 2.3921282337, "steering": -79.1329516312, "odometer": 1.7442830528, "brakingPressure": 0.0607423994, "pedalForce": 0.0000000000, "altitude": -3.3499048742, "fuelUsage": 0.0646195524, "mpg": 26.9931156854, "tripLength": 95.0000000000, "ecoScore": 87.2843324869 }, {"currentSpeed": 113.2955477978, "acceleration": 1.8367038341, "airConditioning": 2.2644684239, "steering": -68.4809649196, "odometer": 1.7757540383, "brakingPressure": 0.1177201242, "pedalForce": 0.0000000000, "altitude": -4.4310924692, "fuelUsage": 0.0659031405, "mpg": 26.9449077099, "tripLength": 96.0000000000, "ecoScore": 88.3495650096 }, {"currentSpeed": 114.8982772952, "acceleration": 1.6027294974, "airConditioning": 3.4856437005, "steering": -62.6669201720, "odometer": 1.8076702265, "brakingPressure": 0.2339743367, "pedalForce": 0.0000000000, "altitude": -7.4110163071, "fuelUsage": 0.0672233019, "mpg": 26.8905301476, "tripLength": 97.0000000000, "ecoScore": 89.5022053995 }, {"currentSpeed": 116.4754330002, "acceleration": 1.5771557050, "airConditioning": 2.6283118028, "steering": -71.4351175336, "odometer": 1.8400245134, "brakingPressure": 0.0255737925, "pedalForce": 0.0000000000, "altitude": 25.8318031332, "fuelUsage": 0.0685799546, "mpg": 26.8303548085, "tripLength": 98.0000000000, "ecoScore": 89.0606333453 }, {"currentSpeed": 117.7057418770, "acceleration": 1.2303088768, "airConditioning": 3.5852972836, "steering": -79.9950944542, "odometer": 1.8727205528, "brakingPressure": 0.3468468282, "pedalForce": 0.0000000000, "altitude": 7.8676818979, "fuelUsage": 0.0699654187, "mpg": 26.7663738326, "tripLength": 99.0000000000, "ecoScore": 89.5006157726 }, {"currentSpeed": 118.9053461069, "acceleration": 1.1996042299, "airConditioning": 3.3926244864, "steering": -70.7638800116, "odometer": 1.9057498156, "brakingPressure": 0.0307046470, "pedalForce": 0.0000000000, "altitude": 7.1605032501, "fuelUsage": 0.0713792669, "mpg": 26.6989267281, "tripLength": 100.0000000000, "ecoScore": 90.1487937829 } ] }')

def home(request):
    facebook_profile = None
    #return render(request, 'home.html', locals())
    return render_to_response('home.html', { 'facebook_profile': None }, context_instance=RequestContext(request))

def analytics(request):
    # facebook_profile = request.user.get_profile().get_facebook_profile()
    facebook_profile = {'id': "1374900452", "name": "Kevin Xu", "username": "imkevinxu"}
    return render_to_response('analytics.html', locals(), context_instance=RequestContext(request))

#@login_required
def dashboard(request):
    ecoscore = [e['ecoScore'] for e in log['trips']]
    totals = [e['currentSpeed'] for e in log['trips']]
    fuels = [e['fuelUsage'] for e in log['trips']]


    facebook_profile = request.user.get_profile().get_facebook_profile()
    # facebook_profile = {'id': "1374900452", "name": "Kevin Xu", "username": "imkevinxu"}
    # match_user_profile(facebook_profile['id'])
    #read_csv()
    if len(DriveData.objects.all()) == 0:
        read_csv()
    #dates hard coded for now
    start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')

    # Averages (output is a number)
    avg_speed = get_average(facebook_profile['id'], 'vehicle_speed', start_time, end_time)
    avg_mpg = get_average(facebook_profile['id'], 'mpg', start_time, end_time)
    avg_altitude = get_average(facebook_profile['id'], 'altitude', start_time, end_time)

    # Change over time charts (output is a list)

    #engine_rev_data is a tuple: (timestamp, number of revs/min)
    engine_rev_data = graph_engine_rpm(facebook_profile['id'], 'engine_rpm', start_time, end_time)

    #throttle_position_data is a tuple: (timestamp, position of throttle)
    throttle_position_data = graph_throttle(facebook_profile['id'], 'pedal_force', start_time, end_time)

    # Using util scoring functions

    # Scores how much your engine idles (0 - 100)
    engine_idle_score = util_views.score_idling(get_list(facebook_profile['id'], 'engine_rpm', start_time, end_time))
    pedal_score = get_average(facebook_profile['id'], 'pedal_force', start_time, end_time)

    # Scores how much you 'think ahead', e.g. pressing the throttle ahead of a hill
    think_ahead_score = util_views.score_thinking_ahead(
        get_list(facebook_profile['id'], 'pedal_force', start_time, end_time),
        get_list(facebook_profile['id'], 'altitude', start_time, end_time))

    # data from the last trip
    last_trip = get_last_trip(facebook_profile['id'], start_time, end_time)
    last_trip_date = last_trip[0]
    last_trip_duration = ("%d" % (last_trip[1].seconds/3600), "%d" % (last_trip[1].seconds%3600/60))
    last_trip_fuel = last_trip[2]

    friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'username' : user.get_facebook_profile()['username'], 'first_name' : user.get_facebook_profile()['name'], 'highscore' : user.highscore } for user in FacebookProfile.objects.all()]

    return render(request, 'dashboard.html', locals())
    #return render_to_response('index.html',  {'facebook_profile': facebook_profile}, context_instance=RequestContext(request))

#def match_user_profile(id):
def scores(request):
    try:
        if 'fbid' in request.GET:
            fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
            fbid = fb.get_facebook_profile()['id']
            first_name = fb.get_facebook_profile()['name'].split()[0]
            highscore = fb.highscore
            #friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore } for user in FacebookProfile.objects.all() if user != fb]
            # friends = [1, 2, 3]
            friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore } for user in FacebookProfile.objects.all()]
            results = json.dumps({'friends' : friends }, ensure_ascii=False)

            #results = json.dumps({ 'fbid' : fbid, 'first_name' : first_name, 'highscore' : highscore, 'friends' : friends }, ensure_ascii=False)
            return HttpResponse(results, mimetype='application/json')
    except FacebookProfile.DoesNotExist:
        pass
    return redirect('home')

def data(request):
    try:
        fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
        if 'data' in request.GET:
            fb.highscore = request.GET['data']
        if 'currentscore' in request.GET:
            fb.currentscore = request.GET['currentscore']
        fb.save()
        if 'fbid' in request.GET:
            fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
            fbid = fb.get_facebook_profile()['id']
            first_name = fb.get_facebook_profile()['name'].split()[0]
            friends = [{ 'fbid' : user.get_facebook_profile()['id'], 'first_name' : user.get_facebook_profile()['name'].split()[0], 'highscore' : user.highscore, 'currentscore' : user.currentscore } for user in FacebookProfile.objects.all()]
            results = json.dumps({'friends' : friends }, ensure_ascii=False)
            return HttpResponse(results, mimetype='application/json')
    except FacebookProfile.DoesNotExist:
        pass
    return redirect('home')

from django.core import serializers

# api needs to accept batched json

def api(request):
    if 'fbid' not in request.GET:
        results = json.dumps({'error': 'No FB ID found'}, ensure_ascii=False)
    else:
        try:
            parameters = request.GET.copy()
            fbid = parameters.pop('fbid')[0]
            fb = FacebookProfile.objects.get(facebook_id=fbid)
            if len(parameters):
                if "mpgs" in parameters.keys():
                    mpgs = parameters.pop('mpgs')[0].split(',')
                    for mpg in mpgs:
                        d = Drive(fb=fb, mpg=mpg)
                        d.save()
                for key, value in parameters.items():
                    setattr(fb, key, value)
                fb.save()
            drives = json.loads(serializers.serialize("json", Drive.objects.filter(fb=fb), ensure_ascii=False))
            real_drives = [d['fields'] for d in drives]
            for d in real_drives:
                d.pop('fb')
            serial = json.loads(serializers.serialize("json", [fb], ensure_ascii=False))
            serial[0]['fields']['average_mpg'] = fb.average_mpg
            serial[0]['fields']['drives'] = real_drives
            serial[0]['fields'].pop('access_token')
            results = json.dumps(serial[0]['fields'], ensure_ascii=False)
        except FacebookProfile.DoesNotExist:
            results = json.dumps({'error': 'User not found'}, ensure_ascii=False)
    return HttpResponse(results, mimetype='application/json')

def getscore(request):
    try:
        if 'fbid' in request.GET:
            fb = FacebookProfile.objects.get(facebook_id=request.GET['fbid'])
            results = json.dumps({'fbid' : fb.get_facebook_profile()['id'], 'highscore' : fb.highscore, 'currentscore' : fb.currentscore }, ensure_ascii=False)
            return HttpResponse(results, mimetype='application/json')
    except FacebookProfile.DoesNotExist:
        pass
    return redirect('home')

def read_csv():
    # DriveData.objects.all().delete()
    cr = csv.reader(open('media/data/trackLog-2013-Feb-28_23-13-08.csv', 'rb'))
    counter = 0
    for row in cr:
        if counter != 0:
            new_row = DriveData()
            new_row.timestamp = datetime.strptime(row[0], '%a %b %d %H:%M:%S %Z %Y')
            new_row.vehicle_speed = row[4]
            new_row.air_conditioning = row[13] #data
            new_row.pedal_force = row[18]
            new_row.mpg = row[14]
            new_row.altitude = row[6]
            new_row.engine_rpm = row[15]
            new_row.fuel_used = row[16]
            new_row.save()
        counter+=1

def generate_dashboard(request):
    username = request.user.username
    #hard coded for now
    start_time = datetime.strptime('Thu Feb 28 23:13:32 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    end_time = datetime.strptime('Thu Feb 28 23:25:39 PST 2013', '%a %b %d %H:%M:%S %Z %Y')
    avg_speed = get_average(username, 'vehicle_speed', start_time, end_time)
    return render(request, 'dashboard.html', {'vehicle_speed' : avg_speed})

def get_average(username, metric, start_time, end_time):
    formatted_metric = metric + "__avg"
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).aggregate(Avg(metric))
    formatted_result = "{0:.2f}".format(result[formatted_metric])
    return formatted_result

#####Temporary testing of some scoring functions #####

PEDAL_WEIGHTING_FACTOR = 1
BRAKING_PRESSURE_WEIGHTING_FACTOR = 1
AIR_CONDITIONING_WEIGHTING_FACTOR = 1
SUN_INTENSITY_WEIGHTING_FACTOR = 1

# Given a list of values, returns the average value of the function.
def average_linear_value(list):
    reduce(lambda x,y:x+y,list)
    #return integrate_linear(list) / len(list)

def integrate_quadratic(list):
    if len(list) == 1:
        return list[0] * list[0]
    ret = 0
    for i in range(0, len(list) - 1):
        a = list[i]
        b = list[i+1] - list[i]
        ret = ret + (a * (a + b)) + (b * b) / 3.
    return ret

# Given a list of values, returns the average value of the square of the function,
# after normalizing for length of the trip.
def average_quadratic_value(list):
    return integrate_quadratic(list) / (len(list) * len(list))

def score_pedal_force(pedal_force_list):
    return max(0.0, 100 - PEDAL_WEIGHTING_FACTOR * average_quadratic_value(pedal_force_list))


#### Charts and graphs ####
def graph_engine_rpm(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp')
    l = []
    for e in result:
        l.append((e.timestamp, e.engine_rpm))
    return l

def graph_throttle(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp')
    l = []
    for e in result:
        l.append((e.timestamp, e.pedal_force))
    return l

def get_list(username, metric, start_time, end_time):
    result = DriveData.objects.filter(timestamp__gte=start_time, timestamp__lte=end_time).order_by('timestamp').values(metric)
    l = []
    for e in result:
        l.append(e[metric])
    return l

def get_last_trip(username, start_time, end_time):
    duration = end_time - start_time
    date = end_time
    fuel_query = DriveData.objects.filter(timestamp = end_time).values('fuel_used')[:1]
    fuel = 0
    for e in fuel_query:
        fuel = e['fuel_used']
    return(date, duration, fuel)

def mariokart(request):
    return render(request, 'mario.html', locals())
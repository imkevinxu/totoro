from django.shortcuts import render, render_to_response
# Create your views here.
def generate(request):
    return render(request, 'index.html', locals())
    
# Given an input list, this function generates the corresponding prefix sum list.
def generatePrefixSum(orig_list):
    prefix_list = list()
    prefix_list.append(0)
    for number in orig_list:
        prefix_list.append(prefix_list[-1] + number)
    return prefix_list
        
# Given a list of values for a function, approximates an integral for the function over the given range
def integrate_linear(list):
    if len(list) == 1:
        return list[0]
    ret = 0
    for number in list:
        ret = ret + number
    ret = ret - list[0] / 2.
    ret = ret - list[-1] / 2.
    return ret

# Given a list of values for a function, approximates an integral for the square of the function over the given range  
def integrate_quadratic(list):
    if len(list) == 1:
        return list[0] * list[0]
    ret = 0
    for i in range(0, len(list) - 1):
        a = list[i]
        b = list[i+1] - list[i]
        ret = ret + (a * (a + b)) + (b * b) / 3.
    return ret
    
IDLING_ENGINE_THRESHOLD = 1 # TODO (Nick): Make sure that this makes sense. Blocked on receiving data.
# This function will return a score from 0 to 100 indicating how much idling took place.
# Longer sequences of idling will be more strongly penalized than shorter bursts of idling.
# Score will be a real number between 0 and 100
def score_idling(engine_speed_list):
    idling_penalty = 0
    curr_streak = 0
    for number in engine_speed_list:
        if number < IDLING_ENGINE_THRESHOLD:
            curr_streak = curr_streak + 1
        else:
            curr_streak = 0
        idling_penalty = idling_penalty + curr_streak
    score = 100. * (len(engine_speed_list) - idling_penalty) / len(engine_speed_list)
    if score < 0:
        score = 0.0
    return score    

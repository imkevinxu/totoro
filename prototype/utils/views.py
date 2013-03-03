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
        
# Given a list of values for a function, approximates an integral for the function over the given range.
def integrate_linear(list):
    if len(list) == 1:
        return list[0]
    ret = 0
    for number in list:
        ret = ret + number
    ret = ret - list[0] / 2.
    ret = ret - list[-1] / 2.
    return ret

# Given a list of values for a function, approximates an integral for the square of the function over the given range.
# The point of being able to integrate the square of the function is that, because the function y=x^2 is convex,
# we can use the integral of a quadratic equation to more cleanly penalize higher values over lower values.
# Examples of this (TODO for Nick, implement these): Braking pressure, pedal force, air conditioning.
def integrate_quadratic(list):
    if len(list) == 1:
        return list[0] * list[0]
    ret = 0
    for i in range(0, len(list) - 1):
        a = list[i]
        b = list[i+1] - list[i]
        ret = ret + (a * (a + b)) + (b * b) / 3.
    return ret

OPTIMAL_SPEED = 60 # TODO (Nick): Verify that units of speed are miles per hour. Blocked on receiving data.
# This function will return a score from 0 to 100 indicating how much excessive speeding
# was taking place. We assume that the optimal mpg is attained when the car is driving
# at 60 mph - this is not an unreasonable assumption given other cars.
# Score will be a real number between 0 and 100.
def score_engine_overspeeding(vehicle_speed_list):
    speed_penalty = 0
    curr_streak = 0
    for number in vehicle_speed_list:
        if number > OPTIMAL_SPEED:
            curr_streak = curr_streak + 1
        else:
            curr_streak = 0
        speed_penalty = speed_penalty + curr_streak
    score = 100. * (len(vehicle_speed_list) - speed_penalty) / len(vehicle_speed_list)
    if score < 0:
        score = 0.0
    return score

IDLING_ENGINE_THRESHOLD = 1 # TODO (Nick): Make sure that this makes sense. Blocked on receiving data.
# This function will return a score from 0 to 100 indicating how much idling took place.
# Longer sequences of idling will be more strongly penalized than shorter bursts of idling.
# Score will be a real number between 0 and 100.
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
   
# This function will return a score from 0 to 100 indicating how intelligent the
# driver is in terms of speeding up preemptively to avoid having to hold down the
# accelerator harder when going up hills
# This function will return a score between 0 and 100. Scores are likely to be
# very high, since the worst case happens when all the force is applied over
# all the gradient (a poor assumption). 
# ASSUMPTIONS: Positive gradient means uphill. Verifying this is blocked on seeing data.
# TODO (Nick): Make the formula more advanced than a simple dot product. Determine
# an appropriate lookahead range and more strongly penalize when drivers do not
# approach gradients properly. This may be blocked on doing an actual drive ourselves
# to see what is reasonable for lookahead range.
def score_thinking_ahead(pedal_force_list, gradient_list):
    assert len(pedal_force_list) == len(gradient_list)
    total_gradient = 0
    total_force = 0
    badness = 0
    for i in range(0, len(pedal_force_list)):
        badness = badness + pedal_force_list[i] * abs(gradient_list[i])
        total_force = total_force + pedal_force_list[i]
        total_gradient = total_gradient + abs(gradient_list[i])
    return 100. * (1. - badness / (total_force * 1. * total_gradient))



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
    
def integrate_quadratic(list):
    if len(list) == 1:
        return list[0] * list[0]
    ret = 0
    for i in range(0, len(list) - 1):
        a = list[i]
        b = list[i+1] - list[i]
        ret = ret + (a * (a + b)) + (b * b) / 3.
    return ret

from django.shortcuts import render, render_to_response
# Create your views here.
def generate(request):
    return render(request, 'index.html', locals())

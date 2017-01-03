#!/usr/bin/python2.7
# coding: utf-8

import sys
reload(sys)
sys.setdefaultencoding("utf-8")

import re
import json
import csv
from urllib2 import Request, urlopen, URLError, HTTPError

hostname = 'localhost'
username = 'livia'
password = 'livia'
database = 'mytastelist'

import urllib2
import psycopg2

def get_from_omdb(poster, back, titre, date) :
	#Gestion caracteres speciaux
	var = titre.replace(' ', '+')
	var = var.replace('ß', '%C3%9F')
	var = var.replace('ä', '%C3%A4')
	var = var.replace('?', '%3F')
	var = var.replace('ü', '%C3%BC')
	var = var.replace('$', '%245')
	var = var.replace('&', '%26')
	var = var.replace('à', '%C3%A0')
	var = var.replace('é', '%C3%A9')
	var = var.replace('è', '%C3%A8')
	var = var.replace('ù', '%C3%B9')
	var = var.replace(':', '%3A')
	var = var.replace(';', '%3B')
	var = var.replace(' s', '\'s')
	var = var.replace('ç', '%C3%A7')
	var = var.replace('½', '%C2%BD')
	try :
		if(poster != "") :
			poster = poster.replace('\'', '')
			poster = "https://image.tmdb.org/t/p/w500/" + poster
		if(back != "") :
			back = back.replace('\'', '')
			back = "https://image.tmdb.org/t/p/w500/" + back
		request = Request('http://www.omdbapi.com/?t=' + var + '&y=&plot=full&r=json')
		response = urlopen(request)
		kittens = response.read()
		liste = json.loads(kittens)
		imdb_id = liste['imdbID']
		actors = liste['Actors']
		genres = liste['Genre']
		date = date[0:4]
		plot = liste['Plot']
		titre = titre.replace('\'', ' ')
		plot = plot.replace('\'', ' ')
		genres = genres.replace('\'', ' ')
		actors = actors.replace('\'', ' ')
		if(actors == "" or plot == "" or genres == "" or titre == "" or date == ""):
			return
		c.writerow([imdb_id,titre,date,genres,plot,actors,1,0,'Ended', poster, back])
		print titre + " add in database"
	except urllib2.HTTPError, e :
		print e
	except KeyError, e :
		print "Film " + titre + " not found"
	return
 
def parcours_themoviesdb(nombre_page) :
	for j in range (1, nombre_page) :
		request = Request(url + "&page=" + str(j))
		response = urlopen(request)
		kittens = response.read()
		line = json.loads(kittens) 
		for item in line['results'] :
			try :
				if(int(item['release_date'][0:4]) > 2000) :
					poster = ""
					back = ""
					if(item['poster_path'] != None) : 
						poster = item['poster_path']
					if(item['backdrop_path'] != None) :
						back = item['backdrop_path']
					get_from_omdb(poster, back, item['title'], item['release_date'])
			except ValueError, e :
				print "No date"
		print j 


c = csv.writer(open("output_direct_2.csv", "wb"), delimiter ='|',quotechar ='"',quoting=csv.QUOTE_MINIMAL)

url = 'https://api.themoviedb.org/3/discover/movie?redits%3Frelease_date.gte=2000-01-01&api_key=2516f724443b0337df22a4c8c83ca547&sort_by=release_date%2Fcredits'
request = Request(url)
response = urlopen(request)
kittens = response.read()
line = json.loads(kittens) 

parcours_themoviesdb(line['total_pages'])

#cursor.execute("COPY (SELECT * FROM films_series) TO 'data.csv' format csv")
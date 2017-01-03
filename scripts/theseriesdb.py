#!/usr/bin/python2.7
# coding: utf-8

import sys
reload(sys)
sys.setdefaultencoding("utf-8")

import re
import json
from urllib2 import Request, urlopen, URLError, HTTPError

hostname = 'localhost'
username = 'livia'
password = 'livia'
database = 'mytastelist'

import psycopg2
import csv
 
def getNumbers(id) :
	url_serie = "https://api.themoviedb.org/3/tv/" + str(id) + "?api_key=2516f724443b0337df22a4c8c83ca547&language=en-US"
	request = Request(url_serie)
	response = urlopen(request)
	kittens = response.read()
	line = json.loads(kittens)
	return (line['number_of_episodes'], line['number_of_seasons'], line['status'])


def getCast(id) :
	url_cast = ""
	return "N/A"
	#TODO


def parcours_themoviesdb(nombre_page, dic) :
	for j in range (1, nombre_page) :
		request = Request(url + "&page=" + str(j))
		response = urlopen(request)
		kittens = response.read()
		line = json.loads(kittens) 
		for item in line['results'] :
			if item ['first_air_date'][0:4] > "2000" : 
				if item['overview'] != "" :
					genre_list = ""
					for genre in item['genre_ids'] :
						if(genre in dic) :
							genre_list += dic[genre] + " "
					(number_of_ep, number_of_sea, status) = getNumbers(item['id'])
					cast = getCast(item['id'])
					titre = item['name'].replace('\'', ' ')
					plot = item['overview'].replace('\'', ' ')
					plot = plot.replace('\n\n', ' ')
					plot = plot.replace('\n', ' ')
					genre_list = genre_list.replace('\'', ' ')
					poster = item['poster']
					back = item['backdrop_path']
					poster = poster.replace('\'', '')
					poster = "https://image.tmdb.org/t/p/w500/" + poster
					back = back = back.replace('\'', '')
					back = "https://image.tmdb.org/t/p/w500/" + back
					if plot == "" or genre_list == "" or titre == "" :
						print titre + " not found"
					else :
						c.writerow(['0',titre,str(item ['first_air_date'][0:4]),genre_list,plot,cast,number_of_ep,number_of_sea,status, poster, back])	
						print titre + " add in database"


def genre_equivalence():
	dic = dict()
	url = 'https://api.themoviedb.org/3/genre/movie/list?api_key=2516f724443b0337df22a4c8c83ca547&language=en-US'
	request = Request(url)
	response = urlopen(request)
	kittens = response.read()
	line = json.loads(kittens)
	for item in line['genres'] :
		dic[item['id']] = item['name']
	return dic



c = csv.writer(open("output_direct_series.csv", "wb"), delimiter ='|',quotechar ='"',quoting=csv.QUOTE_MINIMAL)
url = 'https://api.themoviedb.org/3/discover/tv?redits%3Frelease_date.gte=1980-01-01&api_key=2516f724443b0337df22a4c8c83ca547&sort_by=release_date%2Fcredits'
request = Request(url)
response = urlopen(request)
kittens = response.read()
line = json.loads(kittens) 

dic = genre_equivalence()

parcours_themoviesdb(line['total_pages'], dic)
# coding=utf-8
import MySQLdb

import sys

from flask import json
from flask import jsonify

reload(sys)
sys.setdefaultencoding('utf-8')


def algo_distance_user(id_user):
    db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")

    # PARTIE POUR RÉCUPÉRER L'ANNÉE DE L'USER MAIN
    requestUser = 'SELECT birth FROM users WHERE id_user = ' + str(id_user)
    cursor = db.cursor()
    cursor.execute(requestUser)
    result = cursor.fetchall()
    birthUser = result[0][0]

    # PARTIE POUR RÉCUPÉRER LES DATAS DE L'USER MAIN
    datasUser = []
    requestDatasUser = 'SELECT id_data FROM user_choices WHERE id_user IN (SELECT id_user FROM users WHERE id_user = ' + str(
        id_user) + ')'
    cursor = db.cursor()
    cursor.execute(requestDatasUser)
    result = cursor.fetchall()
    if result:
        for z in result:
            datasUser.append(z[0])

    # PARTIE POUR RÉCUPÉRER LES DATAS DES AUTRES USERS
    otheruser = []
    requestDatasOthers = "SELECT id_user, id_data FROM user_choices WHERE id_user IN (SELECT id_user FROM users WHERE (birth between DATE_SUB('" + str(
        birthUser) + "', INTERVAL 5 YEAR) and DATE_SUB('" + str(
        birthUser) + "', INTERVAL -5 YEAR)) AND id_user <> " + str(id_user) + ")"
    cursor = db.cursor()
    cursor.execute(requestDatasOthers)
    result = cursor.fetchall()
    i = 0
    if result:
        for z in result:
            otheruser.append((int(z[0]), int(z[1])))
    print otheruser
    # PARTIE POUR RÉCUPÉRER LES FILMS EN COMMUNS DES UTILISATEURS PROCHES
    retour, k = {}, 0

    for i in otheruser:
        movies = []
        movies.append(i[1])
        for j in otheruser:
            if (i[0] == j[0]):
                movies.append(j[1])
        if haveMoviesCommons(movies, datasUser):
            movie = get_movie_informations(i[1])
            retour[k] = movie
            k += 1
    print retour

    return jsonify(movies=json.dumps(retour), success="true")


def get_movie_informations(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT plot, name, id_data, poster, genre FROM datas WHERE id_data = " + str(id_data))
    row = cursor.fetchall()
    cursor.close()
    return {"name": (row[0][1]).decode('latin-1'), "poster": row[0][3], "id_data": row[0][2]}


def haveMoviesCommons(data_other, data_user):
    commons = 0
    for i in data_other:
        if data_user.__contains__(i):
            commons += 1
            if commons == 3:
                return True
    return False

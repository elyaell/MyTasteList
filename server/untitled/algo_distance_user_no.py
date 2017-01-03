import MySQLdb
import numpy as np

from flask import jsonify


def algo_distance_correlation(id_user):
    movie_not_seen = get_movie_not_saw(id_user)
    users = get_all_users()
    datas = get_all_datas()
    id_user = int(id_user)
    tab = list()
    for i in range(0, len(users)):
        movie = []
        for j in range(0, len(datas)):
            if user_has_data(users[i], datas[j]):
                movie.append(1)
            else:
                movie.append(0)
        tab.append(movie)
    for x in tab :
        print x
    E = np.array(tab)
    C = np.corrcoef(E)
    print C
    retour, l = {}, 0
    j = int(users.index(id_user))
    for x in movie_not_seen :
        correlation = 0
        for u in users:
            i = int(datas.index(x))
            k = int(users.index(u))
            correlation += C[k][j] * tab[k][i]
        if correlation >= 0:
            movies = get_movie_informations(x)
            retour[l] = movies
            l += 1
    print retour
    #for x in movie_not_seen:
    #    i = datas.index(x)
        #correlation = {"id" : , "correlation" : }
    return jsonify(movies=retour, success="true")

def get_movie_not_saw(id_user):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_data FROM user_choices WHERE id_user <>" + str(id_user) + " AND id_data NOT IN (SELECT id_data FROM user_choices WHERE id_user = " + str(id_user) + ")")
    row = cursor.fetchall()
    cursor.close()
    ret = []
    for result in row:
        ret.append(result[0])
    return ret


def user_has_data(id_user, id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT count(*) FROM user_choices WHERE id_data = " + str(id_data) + " AND id_user = " + str(id_user))
    row = cursor.fetchall()
    cursor.close()
    return row[0][0] == 1


def get_all_users():
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_user FROM users ORDER BY id_user DESC LIMIT 50")
    row = cursor.fetchall()
    cursor.close()
    ret = []
    for result in row:
        ret.append(int(result[0]))
    return ret


def get_all_datas():
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_data FROM datas")
    row = cursor.fetchall()
    cursor.close()
    ret = []
    for result in row:
        ret.append(int(result[0]))
    return ret



def get_movie_informations(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT plot, name, id_data, poster, genre FROM datas WHERE id_data = " + str(id_data))
    row = cursor.fetchall()
    cursor.close()
    return {"name": (row[0][1]).decode('latin-1'), "poster": row[0][3], "id_data": row[0][2]}


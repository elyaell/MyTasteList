from flask import Flask, jsonify, json
import MySQLdb
import algo_distance_tchebychev as adt
import algo_distance_cosinus as adc
import algo_distance_user as adu
import algo_distance_user_no as aduc
import algo_recommandation as adr


def login_user(username, password):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT * FROM users WHERE username = \'" + username + "\' AND password = \'" + password + "\'")
    row = cursor.fetchall()
    cursor.close()
    if len(row) == 0:
        return jsonify(success="false")
    else:
        return jsonify(success="true", username=row[0][1])


def register_user(username, password, email, birth):
    try:
        connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
        cursor = connexion.cursor()
        cursor.execute("SELECT * FROM users WHERE username = \'" + username + "\'")
        row = cursor.fetchall()
        if len(row) != 0:
            return jsonify(success="false")
        cursor.execute(
            "INSERT INTO users (username, password, email, birth) VALUES (\'" + username + "\',  \'" + password + "\',  \'" + email + "\',  \'" + birth + "\')")
        connexion.commit()
        return jsonify(success="true", username=username)
    except MySQLdb.IntegrityError:
        return jsonify(success="false")
    finally:
        cursor.close()


def get_movie_by_name(movie_name):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT name FROM datas WHERE name LIKE '" + movie_name + "%'")
    row = cursor.fetchall()
    cursor.close()
    if len(row) == 0:
        return jsonify(success="false")
    else:
        data = {}
        id = 0
        for elt in row:
            data[id] = elt[0].decode('latin-1')
            id += 1
        return jsonify(movies=json.dumps(data), success="true")


def add_movie_to_user(user, movie):
    try:
        id_data = get_movie_id(movie)
        id_user = get_user_id(user)
        connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
        cursor = connexion.cursor()
        cursor.execute("INSERT INTO user_choices VALUES (" + str(id_user) + ", " + str(id_data) + ")")
        connexion.commit()
        cursor.close()
        return jsonify(success="true")
    except MySQLdb.IntegrityError:
        return jsonify(success="false")
    finally:
        cursor.close()

def get_for_tf_movie(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT name, plot, poster, id_data FROM datas WHERE id_data = '" + str(id_data) + "'")
    row = cursor.fetchall()
    cursor.close()
    return (row[0][0], row[0][1], row[0][2], row[0][3])


def get_user_movies(user):
    id_user = get_user_id(user)
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_data FROM user_choices WHERE id_user = " + str(id_user))
    row = cursor.fetchall()
    data = {}
    id = 0
    for elt in row:
        movie = {}
        (name, poster, id_data) = get_viewing_movie(elt[0])
        movie["name"] = name.decode('latin-1')
        movie["poster"] = poster.decode('latin-1')
        movie["id_data"] = id_data
        data[id] = movie
        id += 1
    return jsonify(movies=json.dumps(data), success="true")


def get_user_id(user):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_user FROM users WHERE username LIKE '" + user + "'")
    print "SELECT id_user FROM users WHERE username LIKE '" + user + "'"
    row = cursor.fetchall()
    cursor.close()
    return row[0][0]


def get_movie_id(movie):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_data FROM datas WHERE name LIKE '" + movie + "'")
    row = cursor.fetchall()
    cursor.close()
    return row[0][0]


def get_viewing_movie(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT name, poster, id_data FROM datas WHERE id_data = '" + str(id_data) + "'")
    row = cursor.fetchall()
    cursor.close()
    return (row[0][0], row[0][1], row[0][2])


def get_movies_by_genre(genre):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT name, poster, id_data FROM datas WHERE genre LIKE '%" + genre + "%'")
    row = cursor.fetchall()
    cursor.close()
    data = {}
    id = 0
    for elt in row:
        movie = {"name": elt[0].decode('latin-1'), "poster": elt[1].decode('latin-1'), "id_data": elt[2]}
        data[id] = movie
        id += 1
    return jsonify(movies=json.dumps(data), success="true")


def get_movie_informations(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT name, year, genre, plot, actors, number_of_episodes, number_of_seasons, status, back FROM datas WHERE id_data = '" + str(
            id_data) + "'")
    row = cursor.fetchall()
    cursor.close()
    movie = {"name": row[0][0].decode('latin-1'), "year": row[0][1], "genre": row[0][2].decode('latin-1'),
             "plot": row[0][3].decode('latin-1'), "actors": row[0][4].decode('latin-1'),
             "number_of_episodes": row[0][5], "number_of_seasons": row[0][6], "status": row[0][7].decode('latin-1'),
             "back": row[0][8].decode('latin-1')}
    return jsonify(movies=json.dumps(movie), success="true")


def get_distance_by_tchebychev(id_data, name):
    id_user = get_user_id(name)
    print id_data
    return adt.tchebychev_init(id_data, id_user)


def get_distance_by_cosinus(id_data, name):
    id_user = get_user_id(name)
    return adc.cosinus_init(id_data, id_user)


def algo_distance_user(name):
    id_user = get_user_id(name)
    return adu.algo_distance_user(id_user)


def algo_distance_user_2(name):
    id_user = get_user_id(name)
    return aduc.algo_distance_correlation(id_user)


def recommandation(name):
    id_user = get_user_id(name)
    return adr.algo_recommandation(id_user)

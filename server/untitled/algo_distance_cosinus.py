import MySQLdb
from sklearn.feature_extraction.text import TfidfVectorizer

from flask import json
from flask import jsonify
from math import sqrt
import sys

reload(sys)
sys.setdefaultencoding('utf-8')


def cosinus_init(id_data, id_user):
    plots, names, ids, posters = list(), list(), list(), list()
    (plot, name, id, poster, genre) = get_movie_informations(id_data)
    plots.append(plot)
    names.append(name)
    ids.append(id)
    posters.append(poster)

    genre_final = genre.split(", ")
    var = " genre LIKE '" + genre_final[0] + "%' "
    for j in range(1, len(genre_final)):
        var += "OR genre LIKE '%" + genre_final[j] + "%'"

    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        'SELECT DISTINCT plot, name, id_data, poster FROM datas WHERE id_data <> ' + str(
            id_data) + ' AND ' + var + " AND id_data NOT IN (SELECT id_data FROM user_choices WHERE id_user = " + str(id_user) + ")")
    result = cursor.fetchall()
    if result:
        for z in result:
            plots.append(z[0].decode('latin-1'))
            names.append(z[1].decode('latin-1'))
            ids.append(int(z[2]))
            posters.append(z[3])
    (matrix, feature_names) = transform_text(tuple(plots))
    return cosinus(matrix, feature_names, names, ids, posters)


def get_movie_informations(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT plot, name, id_data, poster, genre FROM datas WHERE id_data = " + str(id_data))
    row = cursor.fetchall()
    cursor.close()
    return (row[0][0]).decode('latin-1'), (row[0][1]).decode('latin-1'), row[0][2], row[0][3], row[0][4]


def transform_text(pairs):
    tf = TfidfVectorizer(analyzer='word', stop_words='english')
    tfidf_matrix = tf.fit_transform(pairs)
    feature_names = tf.get_feature_names()
    return tfidf_matrix, feature_names


def get_order_similarity(tab):
    lng = len(tab)
    for i in range(lng) :
        k = i
        for j in range(i+1, lng) :
            if tab[k]['sim'] > tab[j]['sim'] :
                tmp = tab[k]
                tab[k], tab[j] = tab[j], tmp
    return tab



def cosinus(matrix, feature_names, names, ids, posters):
    dense = matrix.todense()
    text_1 = dense[0].tolist()[0]
    phrase_text_1 = [pair for pair in zip(range(0, len(text_1)), text_1) if pair[1] > 0]
    retour, tab, l = {}, {}, 0
    for i in range(1, len(dense)):
        text_2 = dense[i].tolist()[0]
        phrase_text_2 = [pair for pair in zip(range(0, len(text_2)), text_2) if pair[1] > 0]
        scalaire, scalaire_x, scalaire_y = 0, 0, 0
        for j in range(0, len(feature_names)):
            val_1, val_2 = 0, 0
            for elt in phrase_text_1:
                if elt[0] == j:
                    val_1 = round(elt[1], 3)
                    break
            for elt in phrase_text_2:
                if elt[0] == j:
                    val_2 = round(elt[1], 3)
                    break
            scalaire += (val_1 * val_2)
            scalaire_x += (val_1 * val_1)
            scalaire_y += (val_2 * val_2)
        normes = sqrt(scalaire_x * scalaire_y)
        similarity = 0
        if normes != 0 :
            similarity = scalaire / normes
            tab[l] = {"id": l, "sim" : similarity}
            l += 1
    tab = get_order_similarity(tab)
    l = 0
    fo = open("Cosinus.txt", "wb")
    for x in range(len(tab)-1, 0, -1):
        if tab[x]['sim'] > 0:
            i = int(tab[x]['id'])
            movie = {"name": names[i].decode('latin-1'), "poster": posters[i].decode('latin-1'), "id_data": ids[i]}
            retour[l] = movie
            l += 1
            print movie['name'] + " " + str(tab[x]['sim'])
        fo.write(names[int(tab[x]['id'])].decode('latin-1') + " " + str(tab[x]['sim']) + "\n")
    return jsonify(movies=json.dumps(retour), success="true")
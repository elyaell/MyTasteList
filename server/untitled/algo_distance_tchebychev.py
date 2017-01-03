import MySQLdb
from sklearn.feature_extraction.text import TfidfVectorizer

from flask import json
from flask import jsonify
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

def tchebychev_init(id_data, id_user):
    plots, names, ids, posters = list(), list(), list(), list()
    (plot, name, id, poster, genre) = get_movie_informations(id_data)
    plots.append(plot)
    names.append(name)
    ids.append(id)
    posters.append(poster)

    genre_final = genre.split(", ")
    var = " genre LIKE '%" + genre_final[0] + "%' "
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
    return tchebychev(matrix, feature_names, names, ids, posters)


def get_movie_informations(id_data):
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute(
        "SELECT plot, name, id_data, poster, genre FROM datas WHERE id_data = '" + str(id_data) + "'")
    row = cursor.fetchall()
    cursor.close()
    return row[0][0], row[0][1], row[0][2], row[0][3], row[0][4]


def transform_text(pairs):
    tf = TfidfVectorizer(analyzer='word', stop_words='english')
    tfidf_matrix = tf.fit_transform(pairs)
    feature_names = tf.get_feature_names()
    return tfidf_matrix, feature_names


def get_order_maxval(tab):
    lng = len(tab)
    for i in range(lng):
        k = i
        for j in range(i + 1, lng):
            if tab[k]['max_val'] > tab[j]['max_val']:
                tmp = tab[k]
                tab[k], tab[j] = tab[j], tmp
    return tab


def tchebychev(matrix, feature_names, names, ids, posters):
    dense = matrix.todense()
    text_1 = dense[0].tolist()[0]
    phrase_text_1 = [pair for pair in zip(range(0, len(text_1)), text_1) if pair[1] > 0]
    retour, tab, l = {}, {}, 0
    for i in range(1, len(dense)):
        max_val = 0
        text_2 = dense[i].tolist()[0]
        phrase_text_2 = [pair for pair in zip(range(0, len(text_2)), text_2) if pair[1] > 0]
        for j in range(0, len(feature_names)):
            val_1, val_2 = 0, 0
            for elt in phrase_text_1:
                if elt[0] == j:
                    val_1 = elt[1]
                    break
            for elt in phrase_text_2:
                if elt[0] == j:
                    val_2 = elt[1]
                    break
            tmp = round(abs(val_1 - val_2), 5)
            if tmp > max_val:
                max_val = tmp
        tab[l] = {"id": l, "max_val": max_val}
        l += 1
    # distance.append((ids[i], names[i], posters[i], max_val))
    tab = get_order_maxval(tab)
    l = 0
    fo = open("Tchebychev.txt", "wb")
    for x in range(0, len(tab)):
        if tab[x]['max_val'] < 0.5 :
            i = int(tab[x]['id'])
            movie = {"name": names[i].decode('latin-1'), "poster": posters[i].decode('latin-1'), "id_data": ids[i]}
            retour[l] = movie
            l += 1
        fo.write(names[int(tab[x]['id'])].decode('latin-1') + " " + str(tab[x]['max_val']) + "\n")
    return jsonify(movies=json.dumps(retour), success="true")

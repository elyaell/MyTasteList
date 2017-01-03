import MySQLdb
from sklearn.cluster import KMeans

import numpy as np

import api
import algo_distance_tchebychev as adt

def recommandation(id_user):
    plots, names, ids, posters = list(), list(), list(), list()
    connexion = MySQLdb.connect(host="localhost", user="root", passwd="root", db="projectbigdata")
    cursor = connexion.cursor()
    cursor.execute("SELECT id_data FROM user_choices WHERE id_user = " + str(id_user))
    row = cursor.fetchall()
    for elt in row:
        (name, plot, poster, id_data) = api.get_for_tf_movie(elt[0])
        plots.append(plots.decode('latin-1'))
        names.append(name.decode('latin-1'))
        ids.append(id)
        posters.append(poster)
    (matrix, feature) = adt.transform_text(plots)
    feat = np.array(feature)
    model = KMeans(10).fit(matrix)


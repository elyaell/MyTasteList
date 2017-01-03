from flask import *

import api

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/home/login', methods=['POST'])
def login_user():
    user = request.form['username']
    password = request.form['password']
    return api.login_user(user, password)


@app.route('/home/register', methods=['POST'])
def register_user():
    user = request.form['username']
    password = request.form['password']
    email = request.form['email']
    birth = request.form['birth']
    return api.register_user(user, password, email, birth)


@app.route('/home/get_movies_by_name', methods=['POST'])
def get_movie_by_name():
    movie_name = request.form['movie_name']
    return api.get_movie_by_name(movie_name)


@app.route('/home/add_movie', methods=['POST'])
def add_movie_to_user():
    user = request.form['username']
    movie = request.form['name_film']
    return api.add_movie_to_user(user, movie)


@app.route('/home/get_user_movies', methods=['POST'])
def get_user_movies():
    user = request.form['username']
    return api.get_user_movies(user)


@app.route('/home/get_movies_by_genre', methods=['POST'])
def get_movies_by_genre():
    genre = request.form['genre']
    return api.get_movies_by_genre(genre)


@app.route('/home/get_movie_informations', methods=['POST'])
def get_movie_informations():
    id_data = request.form['id_data']
    return api.get_movie_informations(id_data)


@app.route('/home/get_movie_by_Tchebychev', methods=['POST'])
def get_distance_by_tchebychev():
    id_data = request.form['id_film']
    username = request.form['name']
    return api.get_distance_by_tchebychev(id_data, username)


@app.route('/home/get_movie_by_Cosine', methods=['POST'])
def get_distance_by_cosine():
    id_data = request.form['id_film']
    username = request.form['name']
    return api.get_distance_by_cosinus(id_data, username)


@app.route('/home/get_distance_user_2', methods=['POST'])
def get_distance_by_user_no():
    name = request.form['name']
    return api.algo_distance_user_2(name)


@app.route('/home/get_distance_user', methods=['POST'])
def get_distance_user():
    name = request.form['name']
    return api.algo_distance_user(name)


if __name__ == '__main__':
    app.run(debug=True, host="")

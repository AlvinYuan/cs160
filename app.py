import sqlite3, json, os
from flask import Flask,request
app = Flask(__name__)

#prints logs to the console
import logging, sys
logging.basicConfig(stream=sys.stderr)

DATABASE = 'data.db'

def db_init():
    connection = sqlite3.connect(DATABASE)
    cur = connection.cursor()
    cur.execute('CREATE TABLE IF NOT EXISTS GarageSales (title TEXT, 
description TEXT, plannerId INT, startYear INT, startMonth INT, startDay 
INT, startHour INT, startMinute INT, endYear INT, endMonth INT, endDay 
INT, endHour INT, endMinute INT, location TEXT, latitude REAL, longitude 
REAL)')
    connection.commit()
    connection.close()

#routes 
-------------------------------------------------------------------

@app.route('/')
def index():
    return json.dumps({'message': 'hello world'})

@app.route('/clear', methods=['GET'])
def clear():
    #remove tables
    connection = sqlite3.connect(DATABASE)
    cur = connection.cursor()
    cur.execute('DROP TABLE IF EXISTS GarageSales')
    connection.commit()
    connection.close()
    return json.dumps({'message': 'deleted tables'})

@app.route('/sales', methods=['GET'])
def all():
    #get all sales and return in list
    resources = query_db('SELECT rowid,* FROM GarageSales')
    return json.dumps(resources)

@app.route('/sale/<id>', methods=['GET'])
def show(id):
    #get the sale with the id <id> from the database
    single_resource = query_db('SELECT * FROM GarageSales WHERE rowid = 
?', [id], one=True)
    return json.dumps(single_resource)

@app.route('/sale', methods=['POST'])
def add():
    #access message of the POST with request.form
    #then add a new item to the database
    #return the id of the new item
    params = request.form
    new_item_id = add_to_db('INSERT INTO GarageSales 
values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)', [params.get('title'), 
params.get('description'), int(params.get('plannerId')), 
int(params.get('startYear')), int(params.get('startMonth')), 
int(params.get('startDay')), int(params.get('startHour')), 
int(params.get('startMinute')), int(params.get('endYear')), 
int(params.get('endMonth')), int(params.get('endDay')), 
int(params.get('endHour')), int(params.get('endMinute')), 
params.get('location'), float(params.get('latitude')), 
float(params.get('longitude'))])

    return json.dumps({'id' : new_item_id})

"""
@app.route('/search', methods=['GET'])
def search():
    #access params in the GET query string with request.args
    #in this example we expect a url in the format /search?name=foo
    params = request.args
    matching_rows = query_db('SELECT * FROM messages WHERE name = ?', 
[params.get('name')])
    return json.dumps(matching_rows)

@app.route('/messages/<id>', methods=['POST'])
def update(id):
    #update resource with the id <id>
    params = request.form
    if (params['name']):
        update_db('UPDATE messages SET name = ? WHERE rowid = ?', 
[params['name'], id])
    if (params['comment']):
        update_db('UPDATE messages SET comment = ? WHERE rowid = ?', 
[params['comment'], id])
    return json.dumps({'success': True})

@app.route('/messages/<id>', methods=['DELETE'])
def delete(id):
    #delete record with id <id>
   update_db('DELETE FROM messages WHERE rowid = ?', [id])
   return json.dumps({'success': True})
"""

def connect_db():
    db_init()
    return sqlite3.connect(DATABASE)

def query_db(query, args=(), one=False):
    connection = connect_db()
    cur = connection.cursor().execute(query,args)
    if one:
        rows = cur.fetchone()
    else:
        rows = cur.fetchall()
    connection.close()
    return rows

def add_to_db(query, args=()):
    connection = connect_db()
    cur = connection.cursor().execute(query,args)
    connection.commit()
    id = cur.lastrowid
    connection.close()
    return id

def update_db(query, args=()):
    connection = connect_db()
    cur = connection.cursor().execute(query,args)
    connection.commit()
    connection.close()

if __name__ == '__main__':
    debug=True
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port)

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
REAL, mainPhotoId INT)')
    cur.execute('CREATE TABLE IF NOT EXISTS Users (email TEXT)')
    cur.execute('CREATE TABLE IF NOT EXISTS UserFollowed (userid INT, 
saleid INT)')
    cur.execute('CREATE TABLE IF NOT EXISTS UserPlanned (userid INT, 
saleid INT)')
    cur.execute('CREATE TABLE IF NOT EXISTS UserHidden (userid INT, 
saleid INT)')
    cur.execute('CREATE TABLE IF NOT EXISTS Messages (saleid INT, 
senderid INT, receiverid INT, subject TEXT, content TEXT)')
    cur.execute('CREATE TABLE IF NOT EXISTS Photos (bitmap TEXT, 
description TEXT)')
    cur.execute('CREATE TABLE IF NOT EXISTS SalePhotos (saleid INT, 
photoid INT)')
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
    cur.execute('DROP TABLE IF EXISTS Users')
    cur.execute('DROP TABLE IF EXISTS UserFollowed')
    cur.execute('DROP TABLE IF EXISTS UserPlanned')
    cur.execute('DROP TABLE IF EXISTS UserHidden')
    cur.execute('DROP TABLE IF EXISTS Messages')
    cur.execute('DROP TABLE IF EXISTS Photos')
    cur.execute('DROP TABLE IF EXISTS SalePhotos')
    connection.commit()
    connection.close()
    return json.dumps({'message': 'deleted tables'})

"""
SALES
"""

@app.route('/sales', methods=['GET'])
def allSales():
    #get all sales and return in list
    resources = query_db('SELECT rowid,* FROM GarageSales')
    return json.dumps(resources)

@app.route('/sale/<id>', methods=['GET'])
def showSale(id):
    #get the sale with the id <id> from the database
    single_resource = query_db('SELECT * FROM GarageSales WHERE rowid = 
?', [id], one=True)
    return json.dumps(single_resource)

@app.route('/sale', methods=['POST'])
def addSale():
    #access message of the POST with request.form
    #then add a new item to the database
    #return the id of the new item
    params = request.form
    new_item_id = add_to_db('INSERT INTO GarageSales 
values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)', [params.get('title'), 
params.get('description'), int(params.get('plannerId')), 
int(params.get('startYear')), int(params.get('startMonth')), 
int(params.get('startDay')), int(params.get('startHour')), 
int(params.get('startMinute')), int(params.get('endYear')), 
int(params.get('endMonth')), int(params.get('endDay')), 
int(params.get('endHour')), int(params.get('endMinute')), 
params.get('location'), float(params.get('latitude')), 
float(params.get('longitude')), int(params.get('mainPhotoId'))])
    return json.dumps({'id' : new_item_id})

"""
USERS
"""
@app.route('/users', methods=['GET'])
def allUsers():
    resources = query_db('SELECT rowid,* FROM Users')
    return json.dumps(resources)

@app.route('/user', methods=['POST'])
def addUser():
    #TODO: add password
    params = request.form
    user = query_db('SELECT rowid FROM Users WHERE email = ?', 
[params.get('email')], one=True)
    if user == None:
        id = add_to_db('INSERT INTO Users values(?)', 
[params.get('email')])
        new = True
    else:
        id = user[0];
        new = False
    return json.dumps({'id' : id, 'new' : new})

"""
Messages
"""
@app.route('/messages', methods=['GET'])
def allMessages():
    resources = query_db('SELECT rowid,* FROM Messages')
    return json.dumps(resources)

@app.route('/messages/<saleid>', methods=['GET'])
def getMessages(saleid):
    resources = query_db('SELECT * FROM Messages WHERE saleid = ?', 
[saleid])
    return json.dumps(resources)

@app.route('/message', methods=['POST'])
def addMessage():
    params = request.form
    new_item_id = add_to_db('INSERT INTO Messages values(?,?,?,?,?)', 
[int(params.get('saleid')), int(params.get('senderid')), 
int(params.get('receiverid')), params.get('subject'), 
params.get('content')])
    return json.dumps({'id' : new_item_id})

"""
Photos
"""
@app.route('/photos', methods=['GET'])
def allPhotos():
    resources = query_db('SELECT rowid,* FROM Photos')
    return json.dumps(resources)

@app.route('/photo', methods=['POST'])
def addPhoto():
    params = request.form
    new_item_id = add_to_db('INSERT INTO Photos values(?,?)', 
[params.get('bitmap'), params.get('description')])
    return json.dumps({'id' : new_item_id})

@app.route('/salephotos', methods=['GET'])
def allSalePhotos():
    resources = query_db('SELECT * FROM SalePhotos')
    return json.dumps(resources)

@app.route('/salephoto', methods=['POST'])
def addSalePhoto():
    params = request.form
    new_item_id = add_to_db('INSERT INTO SalePhotos values(?,?)', 
[int(params.get('saleid')), int(params.get('photoid'))])
    return json.dumps({'id' : new_item_id})

"""
@app.route('/delete/photos', methods=['GET'])
def deletePhotos():
    connection = sqlite3.connect(DATABASE)
    cur = connection.cursor()
    cur.execute('DROP TABLE IF EXISTS Photos')
    connection.commit()
    connection.close()
    return json.dumps({'message': 'deleted Photos'})
"""

"""
@app.route('/photos', methods=['GET'])
def allPhotos():
"""
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

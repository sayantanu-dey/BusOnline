import tornado.web
import tornado.ioloop
import json


class MyServer(tornado.web.RequestHandler):
    def get(self):
        with open("data.json",'r') as MyJson:
            data = json.load(MyJson)
        self.write(str(data))
    
    def put(self):
        gender, count = [int(x) for x in self.request.body.split()]   
        with open("data.json",'r') as MyJson:
            data = json.load(MyJson)
        if gender == 1:
            data['malecount'] = int(data['malecount']) + count
        else :
            data['femalecount'] = int(data['femalecount']) + count 

        with open("data.json",'w') as MyJson:
            json.dump(data,MyJson)
        with open("data.json",'r') as MyJson:
            data = json.load(MyJson)             
        self.write(str(data))
        

if (__name__ == "__main__"):
    app = tornado.web.Application([
        ("/", MyServer),
    ])
    
    app.listen(8080)
    print("Listening on port 8080")
    
    tornado.ioloop.IOLoop.instance().start()

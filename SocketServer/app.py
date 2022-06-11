from flask import Flask, request
from flask_api import status
import json 

from gestureRegognizer import *

app = Flask(__name__)

@app.route('/')
def greeting():
  return "Server for Gesture Recognition"

@app.route('/gesture', methods=['POST'])
def gestureRecognition():
  req = request.form
  if len(req) == 0:
    return 'No parameter'
  res = req
  # res = decideGesture(req)
  return res
  

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=3000, debug=True)
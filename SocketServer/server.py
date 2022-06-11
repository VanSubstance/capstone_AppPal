import socket
from _thread import *
import struct
import json

socketList = []

Host = '0.0.0.0'
Port = 4000

print('Socket Server for gesture recognition started')

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((Host, Port))
server_socket.listen()

def threaded(client_socket, addr):
  print('>> Connected by :', addr[0], ':', addr[1])

  while True:
    try:
      data = client_socket.recv(1024)

      if not data:
        print('>> Disconnected by ' + addr[0], ':', addr[1])
        break

      print(data)
      print(data.decode())

    except ConnectionResetError as e:
      print('>> Disconnected by ' + addr[0], ':', addr[1])
      break

  if client_socket in socketList :
    socketList.remove(client_socket)
    print('remove client list : ',len(socketList))

  client_socket.close()

try:
  while True:
    print('>> Wait')

    client_socket, addr = server_socket.accept()
    socketList.append(client_socket)
    start_new_thread(threaded, (client_socket, addr))
    print("참가자 수 : ", len(socketList))
        
except Exception as e :
  print ('에러는? : ',e)

finally:
    server_socket.close()
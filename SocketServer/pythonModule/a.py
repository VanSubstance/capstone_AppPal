import sys

def main(req):
  print(req[0] + req[1])
  sys.exit()
  
if __name__ == "__main__":
  main(sys.argv[1:])
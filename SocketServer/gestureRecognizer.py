import numpy as np
from tensorflow.keras.models import load_model
import ast

# Load model
# Location of model::
#   it is the path in perspective of location of executing this python file
model = load_model('models/model.h5')
actions = ['ONE', 'TWO', 'THREE','FOUR','FIVE','ZERO']
seq_length = 15

# coorListList structure::
#   ArrayList<{
#     x, y, z, visibility
#   }>
# length:: 5 --> gesture decision will be made in every 5 frames

def recognize(coorList):
  dummy = []
  # Attach frame to hand model
  # print(coorList)
  joint = np.zeros((21, 4))
  for j, lm in enumerate(coorList):
    # print(lm)
    joint[j] = [lm[0], lm[1], lm[2], lm[3]]

  # Compute angles between joints
  v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
  v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
  v = v2 - v1 # [20, 3]
  # Normalize v
  v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]

  # Get angle using arcos of dot product
  angle = np.arccos(np.einsum('nt,nt->n',
    v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:], 
    v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

  angle = np.degrees(angle) # Convert radian to degree

  d = np.concatenate([joint.flatten(), angle])
  dummy.append(d)
  dummy.append(d)
  dummy.append(d)

  input_data = np.expand_dims(np.array(dummy, dtype=np.float32), axis=0)

  y_pred = model.predict(input_data).squeeze()

  i_pred = int(np.argmax(y_pred))
  # conf = y_pred[i_pred]

  # if conf < 0.9:
  #   continue
  return actions[i_pred]

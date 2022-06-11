import json
import cv2
import mediapipe as mp
import numpy as np
from tensorflow.keras.models import load_model

# Load model
model = load_model('models/model.h5')
actions = ['pen', 'mask', 'hold']
seq_length = 3

# MediaPipe hands model
mp_hands = mp.solutions.hands
hands = mp_hands.Hands(
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5)


def recognize(frame):
  # Attach frame to hand model
  frame = cv2.flip(frame, 1)
  frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
  result = hands.process(frame)
  frame = cv2.cvtColor(frame, cv2.COLOR_RGB2BGR)

  if result.multi_hand_landmarks is not None:
    for res in result.multi_hand_landmarks:
      joint = np.zeros((21, 4))
      for j, lm in enumerate(res.landmark):
        joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

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

      input_data = np.expand_dims(np.array(d, dtype=np.float32), axis=0)

      y_pred = model.predict(input_data).squeeze()

      i_pred = int(np.argmax(y_pred))
      # conf = y_pred[i_pred]

      # if conf < 0.9:
      #   continue
  return i_pred

def decideGesture(frameArray):
  idxCnt = [0, 0, 0, 0, 0]
  for frame in frameArray:
    idxCnt[recognize(frame)] += 1
  return labels[idxCnt.index(max(idxCnt))]
// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.capstone.apppal;

import android.content.Context;
import android.util.Log;

import com.capstone.apppal.model.RoomData;
import com.capstone.apppal.model.Stroke;
import com.capstone.apppal.model.StrokeUpdate;
import com.capstone.apppal.utils.GlobalState;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A helper class to manage all communications with Firebase.
 */
/*package*/ class RoomManager {

  public interface StrokeUpdateListener {

    void onLineAdded(String uid, Stroke value);

    void onLineRemoved(String uid);

    void onLineUpdated(String uid, Stroke value);
  }

  private static final String TAG = "RoomManager";

  private static final String KEY_STROKES = "lines";

  boolean isHost = false;

  RoomData mRoomData;

  private List<String> partners = new ArrayList<>();

  private ChildEventListener lineListener;

  private Set<String> localStrokeUids = new HashSet<>();

  private final Map<String, StrokeUpdate> strokeQueue = Collections
    .synchronizedMap(new LinkedHashMap<String, StrokeUpdate>());

  private Map<String, StrokeUpdate> completedStrokeUpdates = Collections
    .synchronizedMap(new LinkedHashMap<String, StrokeUpdate>());

  private List<String> uploadingStrokes = new ArrayList<>();

  /**
   * Default constructor for the FirebaseManager class.
   *
   * @param context The application context.
   */
  public RoomManager(Context context) {
  }

  public void addStroke(String uid, Stroke stroke) {
    stroke.creator = uid;
    DatabaseReference strokeRef = GlobalState.currentRoomRef.child(KEY_STROKES).push();
    localStrokeUids.add(strokeRef.getKey());
    stroke.setFirebaseReference(strokeRef);
    updateStroke(stroke);
  }

  public void updateStroke(Stroke stroke) {
    if (!stroke.hasFirebaseReference()) {
      throw new Error("Cant update line missing firebase reference");
    }

    StrokeUpdate strokeUpdate = new StrokeUpdate(stroke, false);
    queueStrokeUpdate(strokeUpdate);
  }

  private void queueStrokeUpdate(StrokeUpdate strokeUpdate) {
    boolean shouldQueue;

    synchronized (strokeQueue) {
      shouldQueue = uploadingStrokes.size() > 0;
    }

    if (shouldQueue) {
      Log.d(TAG, "strokeQueue: queueStrokeUpdate: queuing");
      // add stroke update to queue
      synchronized (strokeQueue) {
        strokeQueue.put(strokeUpdate.stroke.getFirebaseKey(), strokeUpdate);
      }

    } else {
      Log.d(TAG, "strokeQueue: queueStrokeUpdate: perform update");
      doStrokeUpdate(strokeUpdate);
    }

  }

  private void doStrokeUpdate(final StrokeUpdate strokeUpdate) {
    if (strokeUpdate.remove) {
      strokeUpdate.stroke.removeFirebaseValue();
    } else {
      DatabaseReference.CompletionListener completionListener
        = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError databaseError,
                               DatabaseReference databaseReference) {
          synchronized (strokeQueue) {
            completedStrokeUpdates
              .put(strokeUpdate.stroke.getFirebaseKey(), strokeUpdate);
            uploadingStrokes.remove(strokeUpdate.stroke.getFirebaseKey());
            Iterator<Map.Entry<String, StrokeUpdate>> i = strokeQueue.entrySet().iterator();
            if (i.hasNext()) {
              Map.Entry<String, StrokeUpdate> entry
                = i.next();
              i.remove();
              queueStrokeUpdate(entry.getValue());
            }
          }
        }
      };
      synchronized (strokeQueue) {
        uploadingStrokes.add(strokeUpdate.stroke.getFirebaseKey());
        strokeUpdate.stroke.setFirebaseValue(strokeUpdate,
          completedStrokeUpdates.get(strokeUpdate.stroke.getFirebaseKey()),
          completionListener);
      }
    }
  }

  public void undoStroke(Stroke stroke) {
    if (GlobalState.currentRoomRef == null) {
      return;
    }
    if (stroke.hasFirebaseReference()) {
      StrokeUpdate strokeUpdate = new StrokeUpdate(stroke, true);
      doStrokeUpdate(strokeUpdate);
    }

  }

  public void clearStrokes(String uid) {
    if (GlobalState.currentRoomRef == null) {
      return;
    }
    GlobalState.currentRoomRef.child(KEY_STROKES).removeValue();
    synchronized (strokeQueue) {
      uploadingStrokes.clear();
      strokeQueue.clear();
    }
  }

  void setStrokesListener(final StrokeUpdateListener updateListener) {
    if (lineListener != null) {
      GlobalState.currentRoomRef.child(KEY_STROKES).removeEventListener(lineListener);
    }
    lineListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "LINE onChildAdded: " + dataSnapshot.getValue().toString());
        String uid = dataSnapshot.getKey();
        if (!localStrokeUids.contains(uid)) {
          if (updateListener != null) {
            Stroke stroke;
            try {
              stroke = dataSnapshot.getValue(Stroke.class);
            } catch (DatabaseException e) {
              // lines were cleared while someone was mid-stroke, a partial line was pushed
              // stroke does not have lineWidth or creator, ignore it
              return;
            }
            updateListener.onLineAdded(uid, stroke);
          }
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "LINE onChildChanged: ");
        String uid = dataSnapshot.getKey();
        if (!localStrokeUids.contains(uid)) {
          Stroke stroke;
          try {
            stroke = dataSnapshot.getValue(Stroke.class);
          } catch (DatabaseException e) {
            // lines were cleared while someone was mid-stroke, a partial line was pushed
            // stroke does not have lineWidth or creator, ignore it
            return;
          }
          if (updateListener != null) {
            updateListener.onLineUpdated(uid, stroke);
          }
        } else {
          try {
            dataSnapshot.getValue(Stroke.class);
          } catch (DatabaseException e) {
            // update for local line was pushed simultaneously with
            // a clear from another device. If this occurs,
            // we will not receive an onChildRemoved callback.
            // remove our local line
            if (updateListener != null) {
              updateListener.onLineRemoved(uid);
            }
          }
        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "LINE onChildRemoved: " + dataSnapshot.getValue().toString());
        String uid = dataSnapshot.getKey();
        if (localStrokeUids.contains(uid)) {
          localStrokeUids.remove(uid);
        }
        synchronized (strokeQueue) {
          if (strokeQueue.containsKey(uid)) {
            strokeQueue.remove(uid);
          }
          if (completedStrokeUpdates.containsKey(uid)) {
            completedStrokeUpdates.remove(uid);
          }
        }
        if (updateListener != null) {
          updateListener.onLineRemoved(uid);
        }
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    };
    GlobalState.currentRoomRef.child(KEY_STROKES).addChildEventListener(lineListener);
  }

  public void leaveRoom() {
    isHost = false;
    partners.clear();

    GlobalState.currentRoomRef = null;

    mRoomData = null;

    synchronized (strokeQueue) {
      strokeQueue.clear();
      completedStrokeUpdates.clear();
    }
    localStrokeUids.clear();
  }

  public void pauseListeners(String uid) {
    if (GlobalState.currentRoomRef != null) {
      // remove line listener
      if (lineListener != null) {
        GlobalState.currentRoomRef.child(KEY_STROKES).removeEventListener(lineListener);
        lineListener = null;
      }
    }
  }

  public void resumeListeners(String userUid, StrokeUpdateListener strokeUpdateListener) {
    if (GlobalState.currentRoomRef != null) {
      setStrokesListener(strokeUpdateListener);
    }
  }

}


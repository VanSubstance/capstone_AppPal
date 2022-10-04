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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.capstone.apppal.model.Stroke;
import com.capstone.apppal.utils.GlobalState;
import com.google.ar.core.Anchor;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Kat on 4/4/18.
 */

public class PairSessionManager {

    private static final String TAG = "PairSessionManager";

    enum PairedState {
        NOT_PAIRED, PAIRING, PAIRED, JOINING
    }

    PairedState mPairedOrPairing = PairedState.NOT_PAIRED;

    boolean mPartnerInFlow = false;

    RoomManager mRoomDbManager;

    String mUserUid = GlobalState.useruid;

    private FirebaseAuth mFirebaseAuth;

    private Anchor mAnchor;

    private String mAnchorId;

    private BroadcastReceiver mConnectivityBroadcastReceiver;


    public PairSessionManager(Context context) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRoomDbManager = createRoomManager(context);
    }

    public RoomManager createRoomManager(Context context) {
        return new RoomManager(context);
    }

    boolean mLogInInProgress = false;

    public void setStrokeListener(RoomManager.StrokeUpdateListener strokeListener) {
        mRoomDbManager.setStrokesListener(strokeListener);
    }

    public void addStroke(Stroke stroke) {
        mRoomDbManager.addStroke(mUserUid, stroke);
    }

    public void updateStroke(Stroke stroke) {
        mRoomDbManager.updateStroke(stroke);
    }

    public void undoStroke(Stroke stroke) {
        mRoomDbManager.undoStroke(stroke);
    }

    public void clearStrokes() {
        mRoomDbManager.clearStrokes(mUserUid);
    }

    void leaveRoom() {
        pauseListeners();

        if (mAnchor != null) {
            mAnchor.detach();
            mAnchor = null;
        }
        mAnchorId = null;

        mPairedOrPairing = PairedState.NOT_PAIRED;
        mPartnerInFlow = false;

        mRoomDbManager.leaveRoom();
    }

    void pauseListeners() {

        if (mConnectivityBroadcastReceiver != null) {
            Log.d(TAG, "setupConnectionBroadcastReceiver: PAUSE LISTENER");
            App.get().unregisterReceiver(mConnectivityBroadcastReceiver);
            mConnectivityBroadcastReceiver = null;
        }

        mRoomDbManager.pauseListeners(mUserUid);
    }

    public void resumeListeners(RoomManager.StrokeUpdateListener strokeUpdateListener) {
        mRoomDbManager.resumeListeners(mUserUid, strokeUpdateListener);
    }

}

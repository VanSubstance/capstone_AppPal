package com.example.apppal.Storage;

import com.example.apppal.VO.GestureType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GlobalState {
    public static GestureType currentGesture;
    public static ObjectInputStream is;
    public static ObjectOutputStream os;
}

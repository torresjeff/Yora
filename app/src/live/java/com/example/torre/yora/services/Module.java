package com.example.torre.yora.services;

import android.util.Log;

import com.example.torre.yora.infrastructure.YoraApplication;

public class Module
{
    public static void register (YoraApplication application)
    {
        Log.e("MODULE", "Live register method called");
    }
}
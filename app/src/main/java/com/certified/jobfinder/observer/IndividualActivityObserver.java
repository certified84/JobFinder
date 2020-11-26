package com.certified.jobfinder.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IndividualActivityObserver implements LifecycleObserver {
    private final String TAG = this.getClass().getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateEvent() {
        Log.d(TAG, "onCreateEvent: observer onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartEvent() {
        Log.d(TAG, "onStartEvent: observer onStart");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        if (user != null) {
            FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
            Log.d(TAG, "onStartEvent: AuthStateListener added");
            Log.d(TAG, "onStartEvent: authenticated with: " + user.getPhoneNumber());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStopEvent() {
        Log.d(TAG, "onStopEvent: observer onStop");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        if (mUser != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
            Log.d(TAG, "onStopEvent: AuthStateListener removed");
        }
    }
}

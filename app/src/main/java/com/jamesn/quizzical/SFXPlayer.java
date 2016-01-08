package com.jamesn.quizzical;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by James on 1/4/2016.
 */
// Object for handling SFX playback.
public class SFXPlayer {

    //Sound IDs for each sound resource
    public static int correct;
    public static int incorrect;
    public static int tick;

    SoundPool player;
    Object lock;
    Context mContext;

    public SFXPlayer(Context context){
        lock= new Object();
        player = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mContext=context;
    }

    //Loads all the sound assets into memory
    public void loadResources(){
        correct = player.load(mContext,R.raw.correct,2);
        incorrect = player.load(mContext,R.raw.incorrect,2);
        tick = player.load(mContext,R.raw.beep,1);
    }

    public void playSFX(int soundID){
        player.play(soundID,1f,1f,1,0,1f);
    }

}
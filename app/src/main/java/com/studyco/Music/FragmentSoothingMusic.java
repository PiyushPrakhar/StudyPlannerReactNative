package com.studyco.Music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

@SuppressLint("ValidFragment")
public class FragmentSoothingMusic extends Fragment {

    InformServer informServer;
    String ActivityTag = "Soothing Music Tab";

    Context c ;

    @SuppressLint("ValidFragment")
    public FragmentSoothingMusic(Context c){
        this.c = c;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_soothing_music, container, false);
    }
}

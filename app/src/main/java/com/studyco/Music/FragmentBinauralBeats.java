package com.studyco.Music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

@SuppressLint("ValidFragment")
public class FragmentBinauralBeats extends Fragment {

    InformServer informServer;
    Context c;
    String ActivityTag = "Binaural Beats Tab";

    @SuppressLint("ValidFragment")
    public FragmentBinauralBeats(Context c)
    {
        this.c = c;
    }


    ImageButton alpha , betaMid, betaLow , deltaLow , deltaMid , theta ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //  ' Inform server ' initialization

        informServer = new InformServer(c);

        View v ;
        v = inflater.inflate(R.layout.fragment_binaural_beats, container, false);

        alpha = (ImageButton)v.findViewById(R.id.alpha_button);
        betaLow =  (ImageButton)v.findViewById(R.id.beta_low_button);
        betaMid =  (ImageButton)v.findViewById(R.id.beta_mid_button);
        deltaLow =  (ImageButton)v.findViewById(R.id.delta_low_button);
        deltaMid = (ImageButton)v.findViewById(R.id.delta_mid_button);
        theta =  (ImageButton)v.findViewById(R.id.theta_button);


        final MediaPlayer alphaMediaPlayer = MediaPlayer.create(getActivity(), R.raw.alpha_8);
        final MediaPlayer betaLowMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beta_12);
        final MediaPlayer betaMidMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beta_18);
        final MediaPlayer deltaLowMediaPlayer = MediaPlayer.create(getActivity(), R.raw.delta_0_5);
        final MediaPlayer deltaMidMediaPlayer = MediaPlayer.create(getActivity(), R.raw.delta_2);
        final MediaPlayer thetaMediaPlayer = MediaPlayer.create(getActivity(), R.raw.theta_4);

        // alpha button
        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alphaMediaPlayer.isPlaying())
                {
                    alphaMediaPlayer.seekTo(0);
                    alphaMediaPlayer.pause();
                    alpha.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    alphaMediaPlayer.start();
                    alpha.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag, "Alpha track played");

                }

            }
        });

        // beta low button
        betaLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (betaLowMediaPlayer.isPlaying())
                {
                    betaLowMediaPlayer.seekTo(0);
                    betaLowMediaPlayer.pause();
                    betaLow.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    betaLowMediaPlayer.start();
                    betaLow.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag, "Low beta track played");
                }

            }
        });

         // beta mid button
        betaMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (betaMidMediaPlayer.isPlaying())
                {
                    betaMidMediaPlayer.seekTo(0);
                    betaMidMediaPlayer.pause();
                    betaMid.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    betaMidMediaPlayer.start();
                    betaMid.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag,"Mid beta track played");
                }

            }
        });

        // delta low button
        deltaLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deltaLowMediaPlayer.isPlaying())
                {
                    deltaLowMediaPlayer.seekTo(0);
                    deltaLowMediaPlayer.pause();
                    deltaLow.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    deltaLowMediaPlayer.start();
                    deltaLow.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag,"Low delta track played");
                }

            }
        });

        // delta mid button
        deltaMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deltaMidMediaPlayer.isPlaying())
                {
                    deltaMidMediaPlayer.seekTo(0);
                    deltaMidMediaPlayer.pause();
                    deltaMid.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    deltaMidMediaPlayer.start();
                    deltaMid.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag,"Mid delta track played");
                }

            }
        });

        // theta button
        theta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (thetaMediaPlayer.isPlaying())
                {
                    thetaMediaPlayer.seekTo(0);
                    thetaMediaPlayer.pause();
                    theta.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else
                {
                    thetaMediaPlayer.start();
                    theta.setImageResource(R.drawable.ic_pause_black_24dp);

                    // User monitor
                    informServer.informServerMethod(ActivityTag,"Theta track played");
                }

            }
        });

        return v;
    }

}

package com.janedoe.sos;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

    private Button helpButton;

    public HelpFragment() {
    }

    public static void runFadeOutAnimationOn(Activity ctx, View target) {
        AnimationSet s = new AnimationSet(false);

        Animation animation = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_out);
        Animation rotate_anim = AnimationUtils.loadAnimation(ctx, R.anim.rotate_around_center_point);
        Animation shrink_anim = AnimationUtils.loadAnimation(ctx, R.anim.shrink);
        s.addAnimation(animation);
        s.addAnimation(rotate_anim);
        s.addAnimation(shrink_anim);

        target.startAnimation(s);
        Timer t = new Timer();
        //t.schedule(clearAnimation(), 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        helpButton = (Button) view.findViewById(R.id.help_button);

        helpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_on_press);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.startAnimation(animation);
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    v.clearAnimation();
                }
                return false;
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runFadeOutAnimationOn(getActivity(), v);
                Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.anim_button);
            }

        });
        return view;
    }
}

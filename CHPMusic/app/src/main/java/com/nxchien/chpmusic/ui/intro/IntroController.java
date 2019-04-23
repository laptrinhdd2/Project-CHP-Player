package com.nxchien.chpmusic.ui.intro;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.ui.widget.fragmentnavigationcontroller.FragmentNavigationController;
import com.nxchien.chpmusic.ui.widget.fragmentnavigationcontroller.SupportFragment;

import static com.nxchien.chpmusic.ui.widget.fragmentnavigationcontroller.SupportFragment.PRESENT_STYLE_DEFAULT;

public class IntroController {
    private static final String TAG ="IntroController";

    FragmentNavigationController mNavigationController;

    public FragmentNavigationController getNavigationController() {
        return mNavigationController;
    }

    public IntroController() {

    }

    public void Init(AppCompatActivity activity, Bundle savedInstanceState) {
        initBackStack(activity, savedInstanceState);

    }

    private void initBackStack(AppCompatActivity activity, Bundle savedInstanceState) {
        FragmentManager fm = activity.getSupportFragmentManager();
        mNavigationController = FragmentNavigationController.navigationController(fm, R.id.back_wall_container);
        mNavigationController.setAbleToPopRoot(true);
        mNavigationController.setPresentStyle(PRESENT_STYLE_DEFAULT);
        mNavigationController.setDuration(250);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        mNavigationController.presentFragment(new IntroStepOneFragment());
    }
}

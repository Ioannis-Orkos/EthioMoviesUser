package com.ioannisnicos.ethiomoviesuser.utils;

import android.view.View;

public class SnackbarNoSwipeBehavior extends com.google.android.material.snackbar.BaseTransientBottomBar.Behavior {
                  @Override
    public boolean canSwipeDismissView(View child) {
        return false;
    }
}

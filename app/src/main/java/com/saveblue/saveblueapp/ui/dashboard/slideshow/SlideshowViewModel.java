package com.saveblue.saveblueapp.ui.dashboard.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Settings fragment not yet implemented");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
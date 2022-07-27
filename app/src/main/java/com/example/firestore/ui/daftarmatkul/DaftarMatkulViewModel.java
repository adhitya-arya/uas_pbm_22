package com.example.firestore.ui.daftarmatkul;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DaftarMatkulViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DaftarMatkulViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is daftar matkul fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
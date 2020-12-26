package com.certified.jobfinder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class RepoViewModel extends AndroidViewModel {

    private Repository mRepository;

    public RepoViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }
}

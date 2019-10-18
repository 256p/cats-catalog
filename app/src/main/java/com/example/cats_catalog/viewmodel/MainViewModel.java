package com.example.cats_catalog.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cats_catalog.App;
import com.example.cats_catalog.Errors;
import com.example.cats_catalog.data.ApiService;
import com.example.cats_catalog.data.models.Cat;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    @Inject
    ApiService apiService;
    private CompositeDisposable disposable;
    private MutableLiveData<List<Cat>> catsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> errorsLiveData = new MutableLiveData<>();

    public MainViewModel() {
        App.getComponent().inject(this);
        disposable = new CompositeDisposable();
        fetchCats();
    }

    public MutableLiveData<List<Cat>> getCatsLiveData() {
        return catsLiveData;
    }

    public MutableLiveData<Integer> getErrorsLiveData() {
        return errorsLiveData;
    }

    public void fetchCats() {
        disposable.add(apiService.getCats(40)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cats -> catsLiveData.postValue(cats),
                        throwable -> errorsLiveData.postValue(Errors.CANT_DOWNLOAD)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }
}
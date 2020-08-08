package com.bidstack.cat_gallery.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidstack.cat_gallery.App;
import com.bidstack.cat_gallery.Errors;
import com.bidstack.cat_gallery.data.ApiService;
import com.bidstack.cat_gallery.data.PreferencesHelper;
import com.bidstack.cat_gallery.data.models.Cat;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    @Inject
    public MainViewModel(ApiService apiService, PreferencesHelper preferencesHelper) {
        this.apiService = apiService;
        this.preferencesHelper = preferencesHelper;
        disposable = new CompositeDisposable();
        fetchCats();
    }

    private ApiService apiService;
    private PreferencesHelper preferencesHelper;
    private CompositeDisposable disposable;
    private MutableLiveData<List<Cat>> catsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> errorsLiveData = new MutableLiveData<>();

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

    public int getVoteCount() {
        return preferencesHelper.getVotesCount();
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

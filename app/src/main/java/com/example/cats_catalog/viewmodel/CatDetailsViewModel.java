package com.example.cats_catalog.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cats_catalog.App;
import com.example.cats_catalog.data.ApiService;
import com.example.cats_catalog.data.models.Cat;
import com.example.cats_catalog.data.models.Vote;
import com.example.cats_catalog.data.models.VoteResponse;
import com.example.cats_catalog.data.models.VoteType;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CatDetailsViewModel extends ViewModel {

    @Inject
    ApiService apiService;
    private CompositeDisposable disposable;
    private MutableLiveData<VoteResponse> voteResponseLiveData = new MutableLiveData<>();

    public CatDetailsViewModel() {
        App.getComponent().inject(this);
        disposable = new CompositeDisposable();
    }

    public MutableLiveData<VoteResponse> getVoteResponseLiveData() {
        return voteResponseLiveData;
    }

    public void sendVote(VoteType voteType, Cat cat) {
        Vote vote = new Vote(cat.getId(), voteType.asInteger());

        disposable.add(apiService.postVote(vote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voteResponse -> voteResponseLiveData.postValue(voteResponse),
                        throwable -> {}));
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

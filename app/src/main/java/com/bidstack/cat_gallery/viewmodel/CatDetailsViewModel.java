package com.bidstack.cat_gallery.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidstack.cat_gallery.App;
import com.bidstack.cat_gallery.data.ApiService;
import com.bidstack.cat_gallery.data.PreferencesHelper;
import com.bidstack.cat_gallery.data.models.Cat;
import com.bidstack.cat_gallery.data.models.Vote;
import com.bidstack.cat_gallery.data.models.VoteResponse;
import com.bidstack.cat_gallery.data.models.VoteType;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CatDetailsViewModel extends ViewModel {

    @Inject
    public CatDetailsViewModel(ApiService apiService, PreferencesHelper preferencesHelper) {
        this.apiService = apiService;
        this.preferencesHelper = preferencesHelper;
        disposable = new CompositeDisposable();
    }

    private ApiService apiService;
    private PreferencesHelper preferencesHelper;
    private CompositeDisposable disposable;
    private MutableLiveData<VoteResponse> voteResponseLiveData = new MutableLiveData<>();

    public MutableLiveData<VoteResponse> getVoteResponseLiveData() {
        return voteResponseLiveData;
    }

    public void sendVote(VoteType voteType, Cat cat) {
        Vote vote = new Vote(cat.getId(), voteType.asInteger());

        disposable.add(apiService.postVote(vote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voteResponse -> voteResponseLiveData.postValue(voteResponse),
                        throwable -> voteResponseLiveData.postValue(new VoteResponse(VoteResponse.MESSAGE_ERROR, 0))));
    }

    public int getVotesCount() {
        return preferencesHelper.getVotesCount();
    }

    public void reduceVotesCount() {
        preferencesHelper.setVotesCount(preferencesHelper.getVotesCount() - 1);
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

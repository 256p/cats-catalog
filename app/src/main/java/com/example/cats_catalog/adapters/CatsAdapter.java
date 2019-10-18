package com.example.cats_catalog.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cats_catalog.R;
import com.example.cats_catalog.data.models.Cat;
import com.example.cats_catalog.databinding.ItemCatBinding;

import java.util.List;

public class CatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Cat> cats;
    private OnCatClickListener listener;

    public interface OnCatClickListener {
        void onCatClick(Cat cat, ImageView imageView);
    }

    public CatsAdapter(List<Cat> cats, OnCatClickListener listener) {
        this.cats = cats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCatBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_cat, parent, false);
        return new CatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CatViewHolder) holder).bind(cats.get(position));
    }

    @Override
    public int getItemCount() {
        return cats != null ? cats.size() : 0;
    }

    public void updateData(List<Cat> cats) {
        this.cats = cats;
        notifyDataSetChanged();
    }

    class CatViewHolder extends RecyclerView.ViewHolder {

        private ItemCatBinding binding;

        CatViewHolder(ItemCatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(Cat cat) {
            binding.setCat(cat);
            ViewCompat.setTransitionName(binding.catImage, cat.getId());
            itemView.setOnClickListener(v -> listener.onCatClick(cat, binding.catImage));
        }
    }
}

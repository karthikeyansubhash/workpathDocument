// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.adapter;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.authorization.Permission;
import com.hp.workpath.api.authorization.SignInMethod;
import com.hp.workpath.sample.authorization.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableAdapter<E extends Parcelable> extends RecyclerView.Adapter<SelectableAdapter.ViewHolder<E>> {
    private final ArrayList<E> items;
    private final OnItemClickListener<E> listener;
    private final boolean visibleCheckBox;
    private final Set<Integer> checkedItems;

    public SelectableAdapter(ArrayList<E> items, ArrayList<E> selectedItems, OnItemClickListener<E> listener, boolean isVisibleCheckBox) {
        this.items = items;
        this.listener = listener;
        this.visibleCheckBox = isVisibleCheckBox;
        checkedItems = new HashSet<>();
        if (selectedItems != null)
            setSelectedItems(selectedItems);
    }

    @NonNull
    @Override
    public ViewHolder<E> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_selectable, parent, false);
        return new ViewHolder<>(view, checkedItems, visibleCheckBox);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<E> holder, int position) {
        E item = items.get(position);
        holder.bind(item, checkedItems, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<E> getSelectedItems() {
        List<E> selectedItems = new ArrayList<>();
        checkedItems.forEach(index -> selectedItems.add(items.get(index)));
        return selectedItems;
    }

    public void setSelectedItems(List<E> selectedItems) {
        for (E item : selectedItems) {
            int index = items.indexOf(item);
            if (index != -1) checkedItems.add(index);
        }
    }

    public static class ViewHolder<E> extends RecyclerView.ViewHolder {
        private final TextView uuidView;
        private final TextView nameView;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView, Set<Integer> checkedItems, boolean visibleCheckBox) {
            super(itemView);
            uuidView = itemView.findViewById(R.id.firstTextView);
            nameView = itemView.findViewById(R.id.secondTextView);
            checkBox = itemView.findViewById(R.id.selectableCheckBox);
            checkBox.setVisibility(visibleCheckBox ? View.VISIBLE : View.GONE);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) checkedItems.add(getAdapterPosition());
                else checkedItems.remove(getAdapterPosition());
            });
        }

        public void bind(E item, Set<Integer> checkedItems, OnItemClickListener<E> listener) {
            if (item instanceof Permission) {
                uuidView.setText(((Permission) item).getId());
                nameView.setText(((Permission) item).getLocalizedName().getValue());
            } else if (item instanceof SignInMethod) {
                uuidView.setText(((SignInMethod) item).getId());
                nameView.setText(((SignInMethod) item).getName());
            }
            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(item);
                else
                    checkBox.setChecked(!checkBox.isChecked());

            });
            checkBox.setChecked(checkedItems.contains(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener<E> {
        void onItemClick(E item);
    }
}

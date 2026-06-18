package com.hp.workpath.sample.authorization.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.authorization.Permission
import com.hp.workpath.api.authorization.SignInMethod
import com.hp.workpath.sample.authorization.R

class SelectableAdapter<E : Parcelable>(
    private val items: ArrayList<E>,
    selectedItems: ArrayList<E>?,
    private val listener: OnItemClickListener<E>?,
    private val visibleCheckBox: Boolean
) : RecyclerView.Adapter<SelectableAdapter.ViewHolder<E>>() {

    private val checkedItems: MutableSet<Int> = HashSet()

    init {
        selectedItems?.let { setSelectedItems(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<E> {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_selectable, parent, false)
        return ViewHolder(view, checkedItems, visibleCheckBox)
    }

    override fun onBindViewHolder(holder: ViewHolder<E>, position: Int) {
        val item = items[position]
        holder.bind(item, checkedItems, listener)
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedItems(): List<E> {
        return checkedItems.mapNotNull { index -> items.getOrNull(index) }
    }

    fun setSelectedItems(selectedItems: List<E>) {
        for (item in selectedItems) {
            val index = items.indexOf(item)
            if (index != -1) checkedItems.add(index)
        }
    }

    class ViewHolder<E>(
        itemView: View,
        private val checkedItems: MutableSet<Int>,
        visibleCheckBox: Boolean
    ) : RecyclerView.ViewHolder(itemView) {

        private val uuidView: TextView = itemView.findViewById(R.id.firstTextView)
        private val nameView: TextView = itemView.findViewById(R.id.secondTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.selectableCheckBox)

        init {
            checkBox.visibility = if (visibleCheckBox) View.VISIBLE else View.GONE
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.add(adapterPosition)
                } else {
                    checkedItems.remove(adapterPosition)
                }
            }
        }

        fun bind(item: E, checkedItems: Set<Int>, listener: OnItemClickListener<E>?) {
            when (item) {
                is Permission -> {
                    uuidView.text = item.id
                    nameView.text = item.localizedName.value
                }

                is SignInMethod -> {
                    uuidView.text = item.id
                    nameView.text = item.name
                }
            }
            itemView.setOnClickListener {
                if (listener != null) {
                    listener.onItemClick(item)
                } else {
                    checkBox.isChecked = !checkBox.isChecked
                }
            }
            checkBox.isChecked = checkedItems.contains(adapterPosition)
        }
    }

    fun interface OnItemClickListener<E> {
        fun onItemClick(item: E)
    }
}
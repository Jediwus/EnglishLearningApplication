package com.jediwus.learningapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.pojo.ItemModifyPhrase;

import java.util.List;

public class ModifyPhraseAdapter extends RecyclerView.Adapter<ModifyPhraseAdapter.MyViewHolder> {

    private final List<ItemModifyPhrase> mItemModifyPhraseList;
    private final Context mContext;
//    private OnItemClickListener mItemClickListener;
    // SparseArray是Android SDK中的一种数据结构，它类似于Map，可以将一个整数类型的key映射到一个任意类型的value上。
    // 与Map不同的是，SparseArray在内存使用和性能方面具有更好的表现，适合处理小数据量的情况。
    private final SparseArray<ItemModifyPhrase> mRemovedItems = new SparseArray<>();

    public ModifyPhraseAdapter(Context context, List<ItemModifyPhrase> dataList, RecyclerView recyclerView) {
        this.mContext = context;
        this.mItemModifyPhraseList = dataList;
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ItemModifyPhrase removedItem = mItemModifyPhraseList.remove(position);
                mRemovedItems.put(position, removedItem);
                notifyItemRemoved(position);
                showUndoSnackBar(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Drawable icon = ContextCompat.getDrawable(mContext, R.drawable.icon_delete);
                assert icon != null;
                int iconWidth = icon.getIntrinsicWidth() / 7;
                int iconHeight = icon.getIntrinsicHeight() / 7;
                int itemViewHeight = viewHolder.itemView.getHeight();
                int iconTop = viewHolder.itemView.getTop() + (itemViewHeight - iconHeight) / 2;
                int iconMargin = (itemViewHeight - iconHeight) / 4;
                int iconLeft = viewHolder.itemView.getRight() - iconMargin - iconWidth;
                int iconRight = viewHolder.itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconTop + iconHeight);
                icon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f;
            }
        });
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText editEn;
        TextInputEditText editCn;

        public MyViewHolder(View itemView) {
            super(itemView);
            editEn = itemView.findViewById(R.id.edit_text_item_modify_phrase_en);
            editCn = itemView.findViewById(R.id.edit_text_item_modify_phrase_cn);
        }

    }


    @NonNull
    @Override
    public ModifyPhraseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_modify_phrase, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holder.editEn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                ItemModifyPhrase itemModifyPhrase = new ItemModifyPhrase(
                        charSequence.toString().trim(),
                        mItemModifyPhraseList.get(position).getPhraseCn());
                mItemModifyPhraseList.set(position, itemModifyPhrase);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.editCn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                ItemModifyPhrase itemModifyPhrase = new ItemModifyPhrase(
                        mItemModifyPhraseList.get(position).getPhraseEn(),
                        charSequence.toString().trim());
                mItemModifyPhraseList.set(position, itemModifyPhrase);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ModifyPhraseAdapter.MyViewHolder holder, int position) {
        ItemModifyPhrase itemModifyPhrase = mItemModifyPhraseList.get(position);
        holder.editEn.setText(itemModifyPhrase.getPhraseEn());
        holder.editCn.setText(itemModifyPhrase.getPhraseCn());
    }

    @Override
    public int getItemCount() {
        return mItemModifyPhraseList.size();
    }

    public void removeItem(int position) {
        mRemovedItems.remove(position);
        notifyItemRemoved(position);
    }

    public void undoRemove(int position) {
        ItemModifyPhrase removedItem = mRemovedItems.get(position);
        mItemModifyPhraseList.add(position, removedItem);
        mRemovedItems.remove(position);
        notifyItemInserted(position);
    }

    private void showUndoSnackBar(int position) {
        View view = ((Activity) mContext).findViewById(R.id.recycler_modify_data_phrase);
//        ItemModifyPhrase itemModifyPhrase = mRemovedItems.get(position);
        Snackbar snackbar = Snackbar.make(view, "删除", Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> undoRemove(position));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    removeItem(position);
                }
            }
        });
        snackbar.show();
    }
}

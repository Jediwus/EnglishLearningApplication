package com.jediwus.learningapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.FavoritesDetailActivity;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.database.FavoritesLinkWord;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemWordList;

import org.litepal.LitePal;

import java.util.List;

public class FavoritesWordListAdapter extends RecyclerView.Adapter<FavoritesWordListAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<ItemWordList> mItemWordLists;
    private OnItemClickListener mItemClickListener;
    // SparseArray是Android SDK中的一种数据结构，它类似于Map，可以将一个整数类型的key映射到一个任意类型的value上。
    // 与Map不同的是，SparseArray在内存使用和性能方面具有更好的表现，适合处理小数据量的情况。
    private final SparseArray<ItemWordList> mRemovedItems = new SparseArray<>();

    public FavoritesWordListAdapter(Context context, List<ItemWordList> dataList, RecyclerView recyclerView) {
        this.mContext = context;
        this.mItemWordLists = dataList;
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ItemWordList removedItem = mItemWordLists.remove(position);
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
                int iconWidth = icon.getIntrinsicWidth() / 10;
                int iconHeight = icon.getIntrinsicHeight() / 10;
                int itemViewHeight = viewHolder.itemView.getHeight();
                int iconTop = viewHolder.itemView.getTop() + (itemViewHeight - iconHeight) / 2;
                int iconMargin = (itemViewHeight - iconHeight) / 2;
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        //        ImageView imgDelete;
        TextView textWord, textMean;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textWord = itemView.findViewById(R.id.text_i_f_w_l_word);
            textMean = itemView.findViewById(R.id.text_i_f_w_l_mean);
//            imgDelete = itemView.findViewById(R.id.img_i_f_w_l_delete);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                int position = getAdapterPosition();
                ItemWordList item = mItemWordLists.get(position);
                mItemClickListener.onItemClick(item);
            }
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorites_word_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        holder.view.setOnClickListener(view1 -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            WordDetailActivity.wordId = itemWordList.getWordId();
            Intent intent = new Intent(MyApplication.getContext(), WordDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
            MyApplication.getContext().startActivity(intent);
        });
/*
        holder.textWord.setOnClickListener(viewWord -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            MediaHelper.play(itemWordList.getWordName());
        });

        holder.textMean.setOnClickListener(viewMean -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            WordDetailActivity.wordId = itemWordList.getWordId();
            Intent intent = new Intent(MyApplication.getContext(), WordDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
            MyApplication.getContext().startActivity(intent);
        });

        holder.imgDelete.setOnClickListener(viewDelete -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            if (!itemWordList.isSearch()) {
                mItemWordLists.remove(position);
                notifyItemRemoved(position);
                notifyItemChanged(0, mItemWordLists.size());
                LitePal.deleteAll(FavoritesLinkWord.class,
                        "favoritesId = ? and wordId = ?",
                        FavoritesDetailActivity.currentFavoritesId + "",
                        itemWordList.getWordId() + "");
            }
        });*/

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemWordList itemWordList = mItemWordLists.get(position);
        holder.textMean.setText(itemWordList.getWordMean());
        holder.textWord.setText(itemWordList.getWordName());
//        Glide.with(MyApplication.getContext()).load(R.drawable.icon_delete).into(holder.imgDelete);
    }

    @Override
    public int getItemCount() {
        return mItemWordLists.size();
    }

    public void addItem(ItemWordList item) {
        mItemWordLists.add(item);
        notifyItemInserted(mItemWordLists.size() - 1);
    }

    public void removeItem(int position) {
        mRemovedItems.remove(position);
        notifyItemRemoved(position);
    }

    public void undoRemove(int position) {
        ItemWordList removedItem = mRemovedItems.get(position);
        mItemWordLists.add(position, removedItem);
        mRemovedItems.remove(position);
        notifyItemInserted(position);
    }

    public interface OnItemClickListener {
        void onItemClick(ItemWordList item);
    }

    private void showUndoSnackBar(int position) {
        View view = ((Activity) mContext).findViewById(R.id.favorites_detail_recycle_view);
        ItemWordList itemWordList = mRemovedItems.get(position);
        Snackbar snackbar = Snackbar.make(view, "将" + itemWordList.getWordName() + "移出单词夹", Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> undoRemove(position));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    removeItem(position);
                    LitePal.deleteAll(FavoritesLinkWord.class,
                            "favoritesId = ? and wordId = ?",
                            FavoritesDetailActivity.currentFavoritesId + "",
                            itemWordList.getWordId() + "");
                }
            }
        });
        snackbar.show();
    }
}

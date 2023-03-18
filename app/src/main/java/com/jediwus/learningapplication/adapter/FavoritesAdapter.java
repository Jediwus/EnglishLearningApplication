package com.jediwus.learningapplication.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.FavoritesDetailActivity;
import com.jediwus.learningapplication.database.Favorites;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemFavorites;

import org.litepal.LitePal;

import java.util.List;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<ItemFavorites> mItemFavoritesList;
    private OnItemClickListener mItemClickListener;
    // SparseArray是Android SDK中的一种数据结构，它类似于Map，可以将一个整数类型的key映射到一个任意类型的value上。
    // 与Map不同的是，SparseArray在内存使用和性能方面具有更好的表现，适合处理小数据量的情况。
    private final SparseArray<ItemFavorites> mRemovedItems = new SparseArray<>();

    public FavoritesAdapter(Context context, List<ItemFavorites> dataList, RecyclerView recyclerView) {
        mContext = context;
        mItemFavoritesList = dataList;
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ItemFavorites removedItem = mItemFavoritesList.remove(position);
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorites, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        holder.view.setOnClickListener(view1 -> {
            int position = holder.getAdapterPosition();
            ItemFavorites itemFavorites = mItemFavoritesList.get(position);
            if (itemFavorites.getWordNumbers() > 0) {
                Intent intent = new Intent(MyApplication.getContext(), FavoritesDetailActivity.class);
                FavoritesDetailActivity.currentFavoritesId = itemFavorites.getFavoritesId();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
            } else {
                Toast.makeText(MyApplication.getContext(), "空空如也啊~", Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemFavorites data = mItemFavoritesList.get(position);
        holder.textName.setText(data.getFavoritesName());
        holder.textRemark.setText(data.getFavoritesRemark());
        holder.textNum.setText(data.getWordNumbers() + "");
    }

    @Override
    public int getItemCount() {
        return mItemFavoritesList.size();
    }

    public void addItem(ItemFavorites item) {
        mItemFavoritesList.add(item);
        notifyItemInserted(mItemFavoritesList.size() - 1);
    }

    public void removeItem(int position) {
        mRemovedItems.remove(position);
        notifyItemRemoved(position);
    }

    public void undoRemove(int position) {
        ItemFavorites removedItem = mRemovedItems.get(position);
        mItemFavoritesList.add(position, removedItem);
        mRemovedItems.remove(position);
        notifyItemInserted(position);
    }

    public interface OnItemClickListener {
        void onItemClick(ItemFavorites item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        TextView textName, textRemark, textNum;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textName = itemView.findViewById(R.id.item_favorites_text_name);
            textRemark = itemView.findViewById(R.id.item_favorites_text_remark);
            textNum = itemView.findViewById(R.id.item_favorites_text_number);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                int position = getAdapterPosition();
                ItemFavorites item = mItemFavoritesList.get(position);
                mItemClickListener.onItemClick(item);
            }
        }

    }

    private void showUndoSnackBar(int position) {
        View view = ((Activity) mContext).findViewById(R.id.recycler_wordFavorites);
        Snackbar snackbar = Snackbar.make(view, R.string.item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> undoRemove(position));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    ItemFavorites itemFavorites = mRemovedItems.get(position);
                    removeItem(position);
                    LitePal.deleteAll(Favorites.class, "id = ?", itemFavorites.getFavoritesId() + "");
                }
            }
        });
        snackbar.show();
    }

}

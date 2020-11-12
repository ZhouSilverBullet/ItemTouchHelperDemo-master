package me.yokeyword.itemtouchhelperdemo.demochannel;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;

import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.MyTextViewHolder;
import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.OtherViewHolder;

/**
 * @Author zhouzhou
 * @Date :2020/11/11
 * @Version :1.0
 * @Brief :
 */

public interface IChannelController {
    boolean isEditMode();

    void startEditMode();

    void cancelEditMode();

    void onItemClick(View view, int position);

    RecyclerView getRecyclerView();

    void moveMyToOther(MyTextViewHolder myHolder);

    void moveOtherToMy(OtherViewHolder otherHolder);

    void moveOtherToMyWithDelay(OtherViewHolder otherHolder);

    void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY);

    List<ChannelEntity> getMyChannelItems();

    List<ChannelEntity> getOtherChannelItems();

    ItemTouchHelper getItemTouchHelper();

    int getItemCount();
}

package me.yokeyword.itemtouchhelperdemo.demochannel.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.yokeyword.itemtouchhelperdemo.demochannel.ChannelEntity;
import me.yokeyword.itemtouchhelperdemo.demochannel.IChannelController;

/**
 * @Author zhouzhou
 * @Date :2020/11/11
 * @Version :1.0
 * @Brief :
 */

public class BaseArticleViewHolder extends RecyclerView.ViewHolder {

    protected IChannelController mChannelController;

    public BaseArticleViewHolder(View itemView) {
        super(itemView);
    }

    public void setChannelController(IChannelController channelController) {
        mChannelController = channelController;
    }

    public void fillView(ChannelEntity entity, int realPosition) {

    }
}

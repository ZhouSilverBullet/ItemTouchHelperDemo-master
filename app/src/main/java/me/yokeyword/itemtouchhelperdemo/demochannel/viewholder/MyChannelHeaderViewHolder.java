package me.yokeyword.itemtouchhelperdemo.demochannel.viewholder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import me.yokeyword.itemtouchhelperdemo.R;
import me.yokeyword.itemtouchhelperdemo.demochannel.ChannelEntity;

/**
 * @Author zhouzhou
 * @Date :2020/11/12
 * @Version :1.0
 * @Brief :
 */
/**
 * 我的频道  标题部分
 */
public class MyChannelHeaderViewHolder extends BaseArticleViewHolder implements View.OnClickListener {
    private TextView tvBtnEdit;

    public MyChannelHeaderViewHolder(View itemView) {
        super(itemView);
        tvBtnEdit = (TextView) itemView.findViewById(R.id.tv_article_btn_edit);
        tvBtnEdit.setOnClickListener(this);
    }

    @Override
    public void fillView(@Nullable ChannelEntity entity, int realPosition) {
        if (mChannelController == null) {
            return;
        }
        if (mChannelController.isEditMode()) {
            tvBtnEdit.setText(R.string.finish);
        } else {
            tvBtnEdit.setText(R.string.edit);
        }
    }

    @Override
    public void onClick(View v) {
        if (mChannelController == null) {
            return;
        }
        if (!mChannelController.isEditMode()) {
            mChannelController.startEditMode();
            tvBtnEdit.setText(R.string.finish);
        } else {
            mChannelController.cancelEditMode();
            tvBtnEdit.setText(R.string.edit);
        }
    }
}
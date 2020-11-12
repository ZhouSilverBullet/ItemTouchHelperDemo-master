package me.yokeyword.itemtouchhelperdemo.demochannel.viewholder;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.yokeyword.itemtouchhelperdemo.R;
import me.yokeyword.itemtouchhelperdemo.demochannel.ChannelEntity;
import me.yokeyword.itemtouchhelperdemo.helper.OnDragVHListener;

import static me.yokeyword.itemtouchhelperdemo.demochannel.ChannelAdapter.COUNT_PRE_MY_HEADER;
import static me.yokeyword.itemtouchhelperdemo.demochannel.ChannelAdapter.COUNT_PRE_OTHER_HEADER;

/**
 * @Author zhouzhou
 * @Date :2020/11/11
 * @Version :1.0
 * @Brief :
 */

/**
 * 我的频道
 */
public class MyTextViewHolder extends BaseArticleViewHolder implements OnDragVHListener,
        View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private static final long SPACE_TIME = 100;

    private TextView textView;
    private ImageView imgEdit;

    public MyTextViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.tv_article_text);
        imgEdit = (ImageView) itemView.findViewById(R.id.iv_img_edit);

        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        textView.setOnTouchListener(this);
    }

    @Override
    public void fillView(ChannelEntity entity, int realPosition) {
        textView.setText(entity.getName());
        if (mChannelController == null) {
            return;
        }
        if (mChannelController.isEditMode()) {
            imgEdit.setVisibility(View.VISIBLE);
        } else {
            imgEdit.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * item 被选中时
     */
    @Override
    public void onItemSelected() {
        textView.setBackgroundResource(R.drawable.bg_channel_p);
    }

    /**
     * item 取消选中时
     */
    @Override
    public void onItemFinish() {
        textView.setBackgroundResource(R.drawable.bg_channel);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_article_text: {
                if (mChannelController == null) {
                    return;
                }
                int position = getAdapterPosition();
                List<ChannelEntity> mMyChannelItems = mChannelController.getMyChannelItems();
                if (mChannelController.isEditMode()) {
                    RecyclerView recyclerView = mChannelController.getRecyclerView();
                    View targetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
                    View currentView = recyclerView.getLayoutManager().findViewByPosition(position);
                    // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                    // 如果在屏幕内,则添加一个位移动画
                    if (recyclerView.indexOfChild(targetView) >= 0) {
                        int targetX, targetY;

                        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        int spanCount = ((GridLayoutManager) manager).getSpanCount();

                        // 移动后 高度将变化 (我的频道Grid 最后一个item在新的一行第一个)
                        if ((mMyChannelItems.size() - COUNT_PRE_MY_HEADER) % spanCount == 0) {
                            View preTargetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER - 1);
                            targetX = preTargetView.getLeft();
                            targetY = preTargetView.getTop();
                        } else {
                            targetX = targetView.getLeft();
                            targetY = targetView.getTop();
                        }

                        mChannelController.moveMyToOther(this);
                        mChannelController.startAnimation(recyclerView, currentView, targetX, targetY);

                    } else {
                        mChannelController.moveMyToOther(this);
                    }
                } else {
                    mChannelController.onItemClick(v, position - COUNT_PRE_MY_HEADER);
                }
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        if (mChannelController == null) {
            return true;
        }
        if (!mChannelController.isEditMode()) {
            mChannelController.startEditMode();

            // header 按钮文字 改成 "完成"
            RecyclerView recyclerView = mChannelController.getRecyclerView();
            View view = recyclerView.getChildAt(0);
            if (view == recyclerView.getLayoutManager().findViewByPosition(0)) {
                TextView tvBtnEdit = (TextView) view.findViewById(R.id.tv_article_btn_edit);
                tvBtnEdit.setText(R.string.finish);
            }
        }

        mChannelController.getItemTouchHelper().startDrag(this);
        return true;
    }

    public long startTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mChannelController == null) {
            return true;
        }
        if (mChannelController.isEditMode()) {
            switch (MotionEventCompat.getActionMasked(event)) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                        mChannelController.getItemTouchHelper().startDrag(this);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    startTime = 0;
                    break;
            }
        }
        return false;
    }
}

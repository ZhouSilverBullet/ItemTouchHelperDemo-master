package me.yokeyword.itemtouchhelperdemo.demochannel.viewholder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import me.yokeyword.itemtouchhelperdemo.R;
import me.yokeyword.itemtouchhelperdemo.demochannel.ChannelEntity;

import static me.yokeyword.itemtouchhelperdemo.demochannel.ChannelAdapter.COUNT_PRE_MY_HEADER;
import static me.yokeyword.itemtouchhelperdemo.demochannel.ChannelAdapter.COUNT_PRE_OTHER_HEADER;

/**
 * @Author zhouzhou
 * @Date :2020/11/12
 * @Version :1.0
 * @Brief :
 */
/**
 * 其他频道
 */
public class OtherViewHolder extends BaseArticleViewHolder implements View.OnClickListener {
    private TextView textView;

    public OtherViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.tv_article_text);
        textView.setOnClickListener(this);
    }

    @Override
    public void fillView(ChannelEntity entity, int realPosition) {
        textView.setText(entity.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_article_text: {
                if (mChannelController == null) {
                    return;
                }
                int itemCount = mChannelController.getItemCount();
                List<ChannelEntity> mMyChannelItems = mChannelController.getMyChannelItems();
                RecyclerView recyclerView = mChannelController.getRecyclerView();
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                int currentPosition = getAdapterPosition();
                // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                View currentView = manager.findViewByPosition(currentPosition);
                // 目标位置的前一个item  即当前MyChannel的最后一个
                View preTargetView = manager.findViewByPosition(mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);

                // 如果targetView不在屏幕内,则为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                // 如果在屏幕内,则添加一个位移动画
                if (recyclerView.indexOfChild(preTargetView) >= 0) {
                    int targetX = preTargetView.getLeft();
                    int targetY = preTargetView.getTop();

                    int targetPosition = mMyChannelItems.size() - 1 + COUNT_PRE_OTHER_HEADER;

                    GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
                    int spanCount = gridLayoutManager.getSpanCount();
                    // target 在最后一行第一个
                    if ((targetPosition - COUNT_PRE_MY_HEADER) % spanCount == 0) {
                        View targetView = manager.findViewByPosition(targetPosition);
                        targetX = targetView.getLeft();
                        targetY = targetView.getTop();
                    } else {
                        targetX += preTargetView.getWidth();

                        // 最后一个item可见
                        if (gridLayoutManager.findLastVisibleItemPosition() == itemCount - 1) {
                            // 最后的item在最后一行第一个位置
                            if ((itemCount - 1 - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount == 0) {
                                // RecyclerView实际高度 > 屏幕高度 && RecyclerView实际高度 < 屏幕高度 + item.height
                                int firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
                                if (firstVisiblePosition == 0) {
                                    // FirstCompletelyVisibleItemPosition == 0 即 内容不满一屏幕 , targetY值不需要变化
                                    // // FirstCompletelyVisibleItemPosition != 0 即 内容满一屏幕 并且 可滑动 , targetY值 + firstItem.getTop
                                    if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                                        int offset = (-recyclerView.getChildAt(0).getTop()) - recyclerView.getPaddingTop();
                                        targetY += offset;
                                    }
                                } else { // 在这种情况下 并且 RecyclerView高度变化时(即可见第一个item的 position != 0),
                                    // 移动后, targetY值  + 一个item的高度
                                    targetY += preTargetView.getHeight();
                                }
                            }
                        } else {
                            System.out.println("current--No");
                        }
                    }

                    // 如果当前位置是otherChannel可见的最后一个
                    // 并且 当前位置不在grid的第一个位置
                    // 并且 目标位置不在grid的第一个位置

                    // 则 需要延迟250秒 notifyItemMove , 这是因为这种情况 , 并不触发ItemAnimator , 会直接刷新界面
                    // 导致我们的位移动画刚开始,就已经notify完毕,引起不同步问题
                    if (currentPosition == gridLayoutManager.findLastVisibleItemPosition()
                            && (currentPosition - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount != 0
                            && (targetPosition - COUNT_PRE_MY_HEADER) % spanCount != 0) {
                        mChannelController.moveOtherToMyWithDelay(this);
                    } else {
                        mChannelController.moveOtherToMy(this);
                    }
                    mChannelController.startAnimation(recyclerView, currentView, targetX, targetY);

                } else {
                    mChannelController.moveOtherToMy(this);
                }
                break;
            }
        }
    }
}

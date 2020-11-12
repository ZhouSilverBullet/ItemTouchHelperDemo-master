package me.yokeyword.itemtouchhelperdemo.demochannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.yokeyword.itemtouchhelperdemo.R;
import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.BaseArticleViewHolder;
import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.MyChannelHeaderViewHolder;
import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.MyTextViewHolder;
import me.yokeyword.itemtouchhelperdemo.demochannel.viewholder.OtherViewHolder;
import me.yokeyword.itemtouchhelperdemo.helper.OnItemMoveListener;

/**
 * 拖拽排序 + 增删
 * Created by YoKeyword on 15/12/28.
 */
public class ChannelAdapter extends RecyclerView.Adapter<BaseArticleViewHolder> implements OnItemMoveListener, IChannelController {
    // 我的频道 标题部分
    public static final int TYPE_MY_CHANNEL_HEADER = 0;
    // 我的频道
    public static final int TYPE_MY = 1;
    // 其他频道 标题部分
    public static final int TYPE_OTHER_CHANNEL_HEADER = 2;
    // 其他频道
    public static final int TYPE_OTHER = 3;

    // 我的频道之前的header数量  该demo中 即标题部分 为 1
    public static final int COUNT_PRE_MY_HEADER = 1;
    // 其他频道之前的header数量  该demo中 即标题部分 为 COUNT_PRE_MY_HEADER + 1
    public static final int COUNT_PRE_OTHER_HEADER = COUNT_PRE_MY_HEADER + 1;

    public static final long ANIM_TIME = 360L;

    // touch 点击开始时间
    private long startTime;
    // touch 间隔时间  用于分辨是否是 "点击"
    private static final long SPACE_TIME = 100;

    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;

    // 是否为 编辑 模式
    private boolean isEditMode;

    private List<ChannelEntity> mMyChannelItems, mOtherChannelItems;

    // 我的频道点击事件
    private OnMyChannelItemClickListener mChannelItemClickListener;
    private RecyclerView mRecyclerView;

    public ChannelAdapter(Context context, ItemTouchHelper helper, List<ChannelEntity> mMyChannelItems, List<ChannelEntity> mOtherChannelItems) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = helper;
        this.mMyChannelItems = mMyChannelItems;
        this.mOtherChannelItems = mOtherChannelItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {    // 我的频道 标题部分
            return TYPE_MY_CHANNEL_HEADER;
        } else if (position == mMyChannelItems.size() + 1) {    // 其他频道 标题部分
            return TYPE_OTHER_CHANNEL_HEADER;
        } else if (position > 0 && position < mMyChannelItems.size() + 1) {
            return TYPE_MY;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public BaseArticleViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case TYPE_MY_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_article_header_my_title, parent, false);
                final MyChannelHeaderViewHolder holder = new MyChannelHeaderViewHolder(view);
                holder.setChannelController(this);
                return holder;

            case TYPE_MY:
                view = mInflater.inflate(R.layout.item_article_header_my_text, parent, false);
                final MyTextViewHolder myHolder = new MyTextViewHolder(view);
                myHolder.setChannelController(this);
                return myHolder;

            default:
            case TYPE_OTHER_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_article_header_other_title, parent, false);
                return new BaseArticleViewHolder(view);

            case TYPE_OTHER:
                view = mInflater.inflate(R.layout.item_article_header_other_text, parent, false);
                final OtherViewHolder otherHolder = new OtherViewHolder(view);
                otherHolder.setChannelController(this);
                return otherHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseArticleViewHolder holder, int position) {
        if (holder instanceof MyTextViewHolder) {
            MyTextViewHolder myHolder = (MyTextViewHolder) holder;
            myHolder.fillView(mMyChannelItems.get(position - COUNT_PRE_MY_HEADER), position);
        } else if (holder instanceof OtherViewHolder) {
            holder.fillView(mOtherChannelItems.get(position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER), position);
        } else if (holder instanceof MyChannelHeaderViewHolder) {
            MyChannelHeaderViewHolder headerHolder = (MyChannelHeaderViewHolder) holder;
            headerHolder.fillView(null, position);
        }
    }

    @Override
    public int getItemCount() {
        // 我的频道  标题 + 我的频道.size + 其他频道 标题 + 其他频道.size
        return mMyChannelItems.size() + mOtherChannelItems.size() + COUNT_PRE_OTHER_HEADER;
    }

    /**
     * 开始增删动画
     */
    @Override
    public void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY) {
        final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
        final ImageView mirrorView = addMirrorView(viewGroup, recyclerView, currentView);

        Animation animation = getTranslateAnimator(
                targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.INVISIBLE);
        mirrorView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(mirrorView);
                if (currentView.getVisibility() == View.INVISIBLE) {
                    currentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 我的频道 移动到 其他频道
     *
     * @param myHolder
     */
    public void moveMyToOther(MyTextViewHolder myHolder) {
        int position = myHolder.getAdapterPosition();

        int startPosition = position - COUNT_PRE_MY_HEADER;
        if (startPosition > mMyChannelItems.size() - 1) {
            return;
        }
        ChannelEntity item = mMyChannelItems.get(startPosition);
        mMyChannelItems.remove(startPosition);
        mOtherChannelItems.add(0, item);

        notifyItemMoved(position, mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
    }

    /**
     * 其他频道 移动到 我的频道
     *
     * @param otherHolder
     */
    @Override
    public void moveOtherToMy(OtherViewHolder otherHolder) {
        int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
    }

    /**
     * 其他频道 移动到 我的频道 伴随延迟
     *
     * @param otherHolder
     */
    public void moveOtherToMyWithDelay(OtherViewHolder otherHolder) {
        final int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
            }
        }, ANIM_TIME);
    }

    private Handler delayHandler = new Handler();

    private int processItemRemoveAdd(OtherViewHolder otherHolder) {
        int position = otherHolder.getAdapterPosition();

        int startPosition = position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER;
        if (startPosition > mOtherChannelItems.size() - 1) {
            return -1;
        }
        ChannelEntity item = mOtherChannelItems.get(startPosition);
        mOtherChannelItems.remove(startPosition);
        mMyChannelItems.add(item);
        return position;
    }


    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {
        /**
         * 我们要获取cache首先要通过setDrawingCacheEnable方法开启cache，然后再调用getDrawingCache方法就可以获得view的cache图片了。
         buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，若果cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         若想更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         当调用setDrawingCacheEnabled方法设置为false, 系统也会自动把原来的cache销毁。
         */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);

        return mirrorView;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < 3 || toPosition < 3) {
            return;
        }
        ChannelEntity item = mMyChannelItems.get(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.remove(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.add(toPosition - COUNT_PRE_MY_HEADER, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 开启编辑模式
     */
    @Override
    public void startEditMode() {
        isEditMode = true;

        RecyclerView parent = mRecyclerView;
        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.iv_img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(View.VISIBLE);

                startViewAnim(view);
            } else {
                view.clearAnimation();
            }
        }
    }

    private void startViewAnim(View view) {
        RotateAnimation animation = new RotateAnimation(-1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }

    /**
     * 完成编辑模式
     */
    public void cancelEditMode() {
        isEditMode = false;

        RecyclerView parent = mRecyclerView;
        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.iv_img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(View.INVISIBLE);

                view.clearAnimation();
            }
        }
    }

    @Override
    public boolean isEditMode() {
        return isEditMode;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mChannelItemClickListener != null) {
            mChannelItemClickListener.onItemClick(view, position);
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public List<ChannelEntity> getMyChannelItems() {
        return mMyChannelItems;
    }

    public List<ChannelEntity> getOtherChannelItems() {
        return mOtherChannelItems;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return mItemTouchHelper;
    }

    /**
     * 获取位移动画
     */
    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    public interface OnMyChannelItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnMyChannelItemClickListener(OnMyChannelItemClickListener listener) {
        this.mChannelItemClickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

}

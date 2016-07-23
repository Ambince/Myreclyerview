package com.example.amence_a.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Amence_A on 2016/7/23.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    //头布局
    private View mHeaderView;
    //按下的y坐标
    private float downY;
    //移动后的y坐标
    private float moveY;
    //头布局的宽度
    private int mHeaderViewHeight;
    //下拉刷新
    private static final int PULL_TO_REFRESH = 0;
    //释放刷新
    private static final int RELEASE_REFRESH = 1;
    //释放中
    private static final int REFRESHING = 2;
    //当前刷新模式
    private int currentState = PULL_TO_REFRESH;
    //箭头向上动画
    private RotateAnimation rotateUpAnim;
    //箭头向下动画
    private RotateAnimation rotateDownAnim;
    //箭头布局
    private View mArrowView;
    //头布局标题
    private TextView mTitleText;
    //进度指示器
    private ProgressBar pb;
    //最后刷新时间
    private TextView mLastRefreshTime;
    //刷新监听
    private OnRefreshListener mListener;
    //脚布局
    private View mFooterView;
    //脚布局高度
    private int mFooterViewHeight;
    //是否正在加载更多
    private boolean isLoadingMore;

    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局，脚布局
     * 滚动监听
     */
    private void init() {
        initHeaderView();
        initAnimation();
        initFooterView();
        setOnScrollListener(this);

    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.layout_footer_list, null);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        //隐藏脚布局
        mFooterView.setPadding(0, mFooterViewHeight, 0, 0);
        addFooterView(mFooterView);


    }

    /**
     * 初始化头布局的动画
     */
    private void initAnimation() {
        //向上转，围绕自己的中心，逆时针旋转 0 - >-180
        rotateUpAnim = new RotateAnimation(0f, -180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnim.setDuration(300);
        //动画停留在结束位置
        rotateUpAnim.setFillAfter(true);

        rotateDownAnim = new RotateAnimation(-180f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnim.setDuration(300);
        rotateDownAnim.setFillAfter(true);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.layout_header_list, null);
        mArrowView = mHeaderView.findViewById(R.id.iv_arrow);
        pb = (ProgressBar) mHeaderView.findViewById(R.id.pb);
        mTitleText = (TextView) mHeaderView.findViewById(R.id.tv_title);
        mLastRefreshTime = (TextView) mHeaderView.findViewById(R.id.tv_desc_last_refresh);

        //提前手动测量宽高,按照设置的规则测量
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        //设置内边距，可以隐藏当前控件，
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
        //在设置适配之前添加布局
        addHeaderView(mHeaderView);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断滑动距离，给Header设置paddingTop
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取此时手指按下的位置高度
                downY = ev.getY();
                Log.v("Amence", "downY:" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                //获取移动的Y
                moveY = ev.getY();
                Log.v("Amence", "moveY:" + moveY);

                //如果正在刷新执行父类方法
                if (currentState == REFRESHING) {
                    return super.onTouchEvent(ev);
                }
                //获取偏移量
                float offset = moveY - downY;
                //当偏移量大于0，显示为添加的头条目的时候显示
                if (offset > 0 && getFirstVisiblePosition() == 0) {
                    //获取现在的paddingTop
                    int paddingTop = (int) (-mHeaderViewHeight + offset);
                    mHeaderView.setPadding(0, paddingTop, 0, 0);
                    if (paddingTop >= 0 && currentState != RELEASE_REFRESH) {
                        //切换刷新模式
                        currentState = RELEASE_REFRESH;
                        //更新头布局内容
                        updateHeader();
                    } else if (paddingTop < 0 && currentState != PULL_TO_REFRESH) {
                        currentState = PULL_TO_REFRESH;
                        updateHeader();

                    }
                    //当前事件被我们消费
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v("Amence", "ACTION_UP:" + currentState);
                //根据状态设置
                if (currentState == PULL_TO_REFRESH) {
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

                } else if (currentState == RELEASE_REFRESH) {
                    mHeaderView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    updateHeader();
                }
                break;
        }


        return super.onTouchEvent(ev);
    }

    /**
     * 根据状态更新头布局内容
     */
    private void updateHeader() {
        switch (currentState) {
            case PULL_TO_REFRESH://切换回下拉刷新
                mArrowView.startAnimation(rotateDownAnim);
                mTitleText.setText("下拉刷新");
                break;
            case RELEASE_REFRESH:
                //释放刷新
                mArrowView.startAnimation(rotateUpAnim);
                mTitleText.setText("释放刷新");
                break;
            case REFRESHING:
                //刷新中
                mArrowView.clearAnimation();
                mArrowView.setVisibility(View.INVISIBLE);
                pb.setVisibility(VISIBLE);
                mTitleText.setText("正在刷新中....");
                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;
        }

    }

    /**
     * 刷新结束，恢复界面效果
     */

    public void onRefreshComplete() {
        if (isLoadingMore) {
            //加载更多
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
            isLoadingMore = false;
        } else {
            //下拉刷新
            currentState = PULL_TO_REFRESH;
            mTitleText.setText("下拉刷新");
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
            pb.setVisibility(INVISIBLE);
            mArrowView.setVisibility(VISIBLE);
            String time = getTime();
            mLastRefreshTime.setText("最后刷新时间：" + time);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //状态更新调用
        if (isLoadingMore) {
            return;
        }
        if (scrollState == SCROLL_STATE_IDLE && getLastVisiblePosition() >= (getCount() - 1)) {
            isLoadingMore = true;
            System.out.println("scrollState: 开始加载更多");
            mFooterView.setPadding(0, 0, 0, 0);

            setSelection(getCount()); // 跳转到最后一条, 使其显示出加载更多.

            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public String getTime() {
        long curentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(curentTime);
    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    public void setRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }
}

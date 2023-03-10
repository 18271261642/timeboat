package com.scwang.smart.refresh.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshContent;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.DimensionStatus;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.kernel.R;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnStateChangedListener;
import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;
import com.scwang.smart.refresh.layout.simple.SimpleBoundaryDecider;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smart.refresh.layout.wrapper.RefreshContentWrapper;
import com.scwang.smart.refresh.layout.wrapper.RefreshFooterWrapper;
import com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import static android.view.MotionEvent.obtain;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.scwang.smart.refresh.layout.util.SmartUtil.dp2px;
import static com.scwang.smart.refresh.layout.util.SmartUtil.fling;
import static com.scwang.smart.refresh.layout.util.SmartUtil.isContentView;
import static java.lang.System.currentTimeMillis;

/**
 * ??????????????????
 * Intelligent RefreshLayout
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
@SuppressWarnings({"unused"})
public class SmartRefreshLayout extends ViewGroup implements RefreshLayout, NestedScrollingParent/*, NestedScrollingChild*/ {

    //<editor-fold desc="???????????? property and variable">

    //<editor-fold desc="????????????">
    protected int mTouchSlop;
    protected int mSpinner;//????????? Spinner ??????0????????????,?????????????????????
    protected int mLastSpinner;//???????????????Spinner
    protected int mTouchSpinner;//??????????????????Spinner
    protected int mFloorDuration = 300;//??????????????????
    protected int mReboundDuration = 300;//??????????????????
    protected int mScreenHeightPixels;//????????????
    protected float mTouchX;
    protected float mTouchY;
    protected float mLastTouchX;//????????????Header?????????????????????
    protected float mLastTouchY;//????????????????????????
    protected float mDragRate = .5f;
    protected char mDragDirection = 'n';//??????????????? none-n horizontal-h vertical-v
    protected boolean mIsBeingDragged;//??????????????????
    protected boolean mSuperDispatchTouchEvent;//??????????????????????????????
    protected boolean mEnableDisallowIntercept;//????????????????????????
    protected int mFixedHeaderViewId = View.NO_ID;//????????????????????????Id
    protected int mFixedFooterViewId = View.NO_ID;//????????????????????????Id
    protected int mHeaderTranslationViewId = View.NO_ID;//??????Header???????????????Id
    protected int mFooterTranslationViewId = View.NO_ID;//??????Footer???????????????Id

    protected int mMinimumVelocity;
    protected int mMaximumVelocity;
    protected int mCurrentVelocity;
    protected Scroller mScroller;
    protected VelocityTracker mVelocityTracker;
    protected Interpolator mReboundInterpolator;
    //</editor-fold>

    //<editor-fold desc="????????????">
    protected int[] mPrimaryColors;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadMore = false;
    protected boolean mEnableClipHeaderWhenFixedBehind = true;//??? Header FixedBehind ???????????????????????? Header
    protected boolean mEnableClipFooterWhenFixedBehind = true;//??? Footer FixedBehind ???????????????????????? Footer
    protected boolean mEnableHeaderTranslationContent = true;//????????????????????????????????????
    protected boolean mEnableFooterTranslationContent = true;//????????????????????????????????????
    protected boolean mEnableFooterFollowWhenNoMoreData = false;//?????????????????????????????????Footer???????????? 1.0.4-6
    protected boolean mEnablePreviewInEditMode = true;//??????????????????????????????????????????
    protected boolean mEnableOverScrollBounce = true;//????????????????????????
    protected boolean mEnableOverScrollDrag = false;//?????????????????????????????????????????????1.0.4-6
    protected boolean mEnableAutoLoadMore = true;//???????????????????????????????????????????????????
    protected boolean mEnablePureScrollMode = false;//???????????????????????????
    protected boolean mEnableScrollContentWhenLoaded = true;//????????????????????????????????????????????????????????????
    protected boolean mEnableScrollContentWhenRefreshed = true;//??????????????????????????????????????????????????????
    protected boolean mEnableLoadMoreWhenContentNotFull = true;//???????????????????????????????????????????????????????????????
    protected boolean mEnableNestedScrolling = true;//??????????????????????????????
    protected boolean mDisableContentWhenRefresh = false;//???????????????????????????????????????????????????
    protected boolean mDisableContentWhenLoading = false;//???????????????????????????????????????????????????
    protected boolean mFooterNoMoreData = false;//???????????????????????????????????????????????????????????????????????????
    protected boolean mFooterNoMoreDataEffective = false;//?????? NoMoreData ??????(?????? Footer ???????????????)

    protected boolean mManualLoadMore = false;//?????????????????????LoadMore?????????????????????
    protected boolean mManualHeaderTranslationContent = false;//?????????????????????????????????????????????
    protected boolean mManualFooterTranslationContent = false;//?????????????????????????????????????????????
    //</editor-fold>

    //<editor-fold desc="????????????">
    protected OnRefreshListener mRefreshListener;
    protected OnLoadMoreListener mLoadMoreListener;
    protected OnMultiListener mOnMultiListener;
    protected ScrollBoundaryDecider mScrollBoundaryDecider;
    //</editor-fold>

    //<editor-fold desc="????????????">
    protected int mTotalUnconsumed;
    protected boolean mNestedInProgress;
    protected int[] mParentOffsetInWindow = new int[2];
    protected NestedScrollingChildHelper mNestedChild = new NestedScrollingChildHelper(this);
    protected NestedScrollingParentHelper mNestedParent = new NestedScrollingParentHelper(this);
    //</editor-fold>

    //<editor-fold desc="????????????">

    protected int mHeaderHeight;        //???????????? ??? ??????????????????
    protected DimensionStatus mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
    protected int mFooterHeight;        //???????????? ??? ??????????????????
    protected DimensionStatus mFooterHeightStatus = DimensionStatus.DefaultUnNotify;

    protected int mHeaderInsetStart;    // Header ??????????????????
    protected int mFooterInsetStart;    // Footer ??????????????????

    protected float mHeaderMaxDragRate = 2.5f;  //??????????????????(????????????/Header??????)
    protected float mFooterMaxDragRate = 2.5f;  //??????????????????(????????????/Footer??????)
    protected float mHeaderTriggerRate = 1.0f;  //?????????????????? ??? HeaderHeight ?????????
    protected float mFooterTriggerRate = 1.0f;  //?????????????????? ??? FooterHeight ?????????

    protected float mTwoLevelBottomPullUpToCloseRate = 1/6f;//??????????????????????????????????????????????????????????????????

    protected RefreshComponent mRefreshHeader;     //??????????????????
    protected RefreshComponent mRefreshFooter;     //??????????????????
    protected RefreshContent mRefreshContent;   //??????????????????
    //</editor-fold>

    protected Paint mPaint;
    protected Handler mHandler;
    protected RefreshKernel mKernel = new RefreshKernelImpl();

    /**
     * ??????????????????
     * ?????? SmartRefresh ?????????????????????
     */
    protected RefreshState mState = RefreshState.None;          //?????????
    /**
     * ??????????????????
     * ??????????????? mState ??? Refreshing ??? Loading ??????????????????
     * 1.mState=Refreshing|Loading ??? mViceState ???????????? mState ??????
     * 2.mState=None,?????????????????? ??? mViceState ???????????? mState ??????
     * 3.????????????????????????????????? mViceState=mState
     * 4.SmartRefresh ?????????????????? mViceState
     */
    protected RefreshState mViceState = RefreshState.None;      //???????????????????????????????????????????????????

    protected long mLastOpenTime = 0;                           //????????? ?????????????????? ??????

    protected int mHeaderBackgroundColor = 0;                   //???Header??????????????????
    protected int mFooterBackgroundColor = 0;

    protected boolean mHeaderNeedTouchEventWhenRefreshing;      //?????????Header??????????????????
    protected boolean mFooterNeedTouchEventWhenLoading;

    protected boolean mAttachedToWindow;                        //???????????????Window

    protected boolean mFooterLocked = false;//Footer ??????loading ????????????????????? ????????????????????????

    //???????????? Footer ?????????
    protected static DefaultRefreshFooterCreator sFooterCreator = null;
    //???????????? Header ?????????
    protected static DefaultRefreshHeaderCreator sHeaderCreator = null;
    //???????????? ?????? ????????????
    protected static DefaultRefreshInitializer sRefreshInitializer = null;
    //???????????? ?????? Margin
    protected static MarginLayoutParams sDefaultMarginLP = new MarginLayoutParams(-1,-1);
    //</editor-fold>

    //<editor-fold desc="???????????? construction methods">
    public SmartRefreshLayout(Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        ViewConfiguration configuration = ViewConfiguration.get(context);

        mHandler = new Handler(Looper.getMainLooper());
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReboundInterpolator = new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mFooterHeight = SmartUtil.dp2px(60);
        mHeaderHeight = SmartUtil.dp2px(100);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);

        if (!ta.hasValue(R.styleable.SmartRefreshLayout_android_clipToPadding)) {
            super.setClipToPadding(false);
        }
        if (!ta.hasValue(R.styleable.SmartRefreshLayout_android_clipChildren)) {
            super.setClipChildren(false);
        }

        if (sRefreshInitializer != null) {
            sRefreshInitializer.initialize(context, this);//?????????????????????
        }

        mDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, mDragRate);
        mHeaderMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, mHeaderMaxDragRate);
        mFooterMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, mFooterMaxDragRate);
        mHeaderTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderTriggerRate, mHeaderTriggerRate);
        mFooterTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterTriggerRate, mFooterTriggerRate);
        mEnableRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, mEnableRefresh);
        mReboundDuration = ta.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, mReboundDuration);
        mEnableLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, mEnableLoadMore);
        mHeaderHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, mHeaderHeight);
        mFooterHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, mFooterHeight);
        mHeaderInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderInsetStart, mHeaderInsetStart);
        mFooterInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterInsetStart, mFooterInsetStart);
        mDisableContentWhenRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, mDisableContentWhenRefresh);
        mDisableContentWhenLoading = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, mDisableContentWhenLoading);
        mEnableHeaderTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, mEnableHeaderTranslationContent);
        mEnableFooterTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, mEnableFooterTranslationContent);
        mEnablePreviewInEditMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, mEnablePreviewInEditMode);
        mEnableAutoLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadMore, mEnableAutoLoadMore);
        mEnableOverScrollBounce = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollBounce, mEnableOverScrollBounce);
        mEnablePureScrollMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, mEnablePureScrollMode);
        mEnableScrollContentWhenLoaded = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenLoaded, mEnableScrollContentWhenLoaded);
        mEnableScrollContentWhenRefreshed = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenRefreshed, mEnableScrollContentWhenRefreshed);
        mEnableLoadMoreWhenContentNotFull = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMoreWhenContentNotFull, mEnableLoadMoreWhenContentNotFull);
        mEnableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenLoadFinished, mEnableFooterFollowWhenNoMoreData);
        mEnableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenNoMoreData, mEnableFooterFollowWhenNoMoreData);
        mEnableClipHeaderWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipHeaderWhenFixedBehind, mEnableClipHeaderWhenFixedBehind);
        mEnableClipFooterWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipFooterWhenFixedBehind, mEnableClipFooterWhenFixedBehind);
        mEnableOverScrollDrag = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, mEnableOverScrollDrag);
        mFixedHeaderViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, mFixedHeaderViewId);
        mFixedFooterViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, mFixedFooterViewId);
        mHeaderTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlHeaderTranslationViewId, mHeaderTranslationViewId);
        mFooterTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFooterTranslationViewId, mFooterTranslationViewId);
        mEnableNestedScrolling = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, mEnableNestedScrolling);
        mNestedChild.setNestedScrollingEnabled(mEnableNestedScrolling);

        mManualLoadMore = mManualLoadMore || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableLoadMore);
        mManualHeaderTranslationContent = mManualHeaderTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent);
        mManualFooterTranslationContent = mManualFooterTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent);
        mHeaderHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight) ? DimensionStatus.XmlLayoutUnNotify : mHeaderHeightStatus;
        mFooterHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight) ? DimensionStatus.XmlLayoutUnNotify : mFooterHeightStatus;

        int accentColor = ta.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int primaryColor = ta.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                mPrimaryColors = new int[]{primaryColor, accentColor};
            } else {
                mPrimaryColors = new int[]{primaryColor};
            }
        } else if (accentColor != 0) {
            mPrimaryColors = new int[]{0, accentColor};
        }

        if (mEnablePureScrollMode && !mManualLoadMore && !mEnableLoadMore) {
            mEnableLoadMore = true;
        }

        ta.recycle();
    }
    //</editor-fold>

    //<editor-fold desc="???????????? life cycle">

    /**
     * ?????? onFinishInflate ????????? smart ???????????????
     * 1.???????????? Xml ???????????? Content???Header???Footer
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = super.getChildCount();
        if (count > 3) {
            throw new RuntimeException("???????????????3??????View???Most only support three sub view");
        }

        int contentLevel = 0;
        int indexContent = -1;
        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (isContentView(view) && (contentLevel < 2 || i == 1)) {
                indexContent = i;
                contentLevel = 2;
            } else if (!(view instanceof RefreshComponent) && contentLevel < 1) {
                indexContent = i;
                contentLevel = i > 0 ? 1 : 0;
            }
        }

        int indexHeader = -1;
        int indexFooter = -1;
        if (indexContent >= 0) {
            mRefreshContent = new RefreshContentWrapper(super.getChildAt(indexContent));
            if (indexContent == 1) {
                indexHeader = 0;
                if (count == 3) {
                    indexFooter = 2;
                }
            } else if (count == 2) {
                indexFooter = 1;
            }
        }

        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (i == indexHeader || (i != indexFooter && indexHeader == -1 && mRefreshHeader == null && view instanceof RefreshHeader)) {
                mRefreshHeader = (view instanceof RefreshHeader) ? (RefreshHeader) view : new RefreshHeaderWrapper(view);
            } else if (i == indexFooter || (indexFooter == -1 && view instanceof RefreshFooter)) {
                mEnableLoadMore = (mEnableLoadMore || !mManualLoadMore);
                mRefreshFooter = (view instanceof RefreshFooter) ? (RefreshFooter) view : new RefreshFooterWrapper(view);
            }
        }

    }

    /**
     * ?????? onAttachedToWindow ????????? smart ???????????????
     * 1.????????????????????????????????? Header ??? Footer ???????????????????????????
     * 2.??? Content ???????????? TextView ??????
     * 3.???????????? ???????????? NestedScrollingEnabled
     * 4.????????? ???????????? ??? ?????? Header Footer Content ???????????????
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;

        final View thisView = this;
        if (!thisView.isInEditMode()) {

            if (mRefreshHeader == null) {
                if (sHeaderCreator != null) {
                    RefreshHeader header = sHeaderCreator.createRefreshHeader(thisView.getContext(), this);
                    //noinspection ConstantConditions
                    if (header == null) {
                        throw new RuntimeException("DefaultRefreshHeaderCreator can not return null");
                    }
                    setRefreshHeader(header);
                }
            }
            if (mRefreshFooter == null) {
                if (sFooterCreator != null) {
                    RefreshFooter footer = sFooterCreator.createRefreshFooter(thisView.getContext(), this);
                    //noinspection ConstantConditions
                    if (footer == null) {
                        throw new RuntimeException("DefaultRefreshFooterCreator can not return null");
                    }
                    setRefreshFooter(footer);
                }
            } else {
                mEnableLoadMore = mEnableLoadMore || !mManualLoadMore;
            }

            if (mRefreshContent == null) {
                for (int i = 0, len = getChildCount(); i < len; i++) {
                    View view = getChildAt(i);
                    if ((mRefreshHeader == null || view != mRefreshHeader.getView()) &&
                            (mRefreshFooter == null || view != mRefreshFooter.getView())) {
                        mRefreshContent = new RefreshContentWrapper(view);
                    }
                }
            }
            if (mRefreshContent == null) {
                final int padding = SmartUtil.dp2px(20);
                final TextView errorView = new TextView(thisView.getContext());
                errorView.setTextColor(0xffff6600);
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(20);
                errorView.setText(R.string.srl_content_empty);
                super.addView(errorView, 0, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
                mRefreshContent = new RefreshContentWrapper(errorView);
                mRefreshContent.getView().setPadding(padding, padding, padding, padding);
            }

            View fixedHeaderView = thisView.findViewById(mFixedHeaderViewId);
            View fixedFooterView = thisView.findViewById(mFixedFooterViewId);

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);

            if (mSpinner != 0) {
                notifyStateChanged(RefreshState.None);
                mRefreshContent.moveSpinner(mSpinner = 0, mHeaderTranslationViewId, mFooterTranslationViewId);
            }

        }

        if (mPrimaryColors != null) {
            if (mRefreshHeader != null) {
                mRefreshHeader.setPrimaryColors(mPrimaryColors);
            }
            if (mRefreshFooter != null) {
                mRefreshFooter.setPrimaryColors(mPrimaryColors);
            }
        }

        //????????????
        if (mRefreshContent != null) {
            super.bringChildToFront(mRefreshContent.getView());
        }
        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle().front) {
            super.bringChildToFront(mRefreshHeader.getView());
        }
        if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle().front) {
            super.bringChildToFront(mRefreshFooter.getView());
        }

    }

    /**
     * ?????? Header Footer Content
     * 1.?????????????????????????????????????????? Header Footer ??????????????????????????? {@link SpinnerStyle}??????????????????????????????????????????
     * 2.???????????????????????????????????? XML ????????????????????? ???isInEditMode???
     * 3.?????????????????????????????? mLastTouchX ???????????????
     * @param widthMeasureSpec ??????????????????
     * @param heightMeasureSpec ??????????????????
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec,final int heightMeasureSpec) {
        int minimumWidth = 0;
        int minimumHeight = 0;
        final View thisView = this;
        final boolean needPreview = thisView.isInEditMode() && mEnablePreviewInEditMode;

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (child.getVisibility() == GONE || "GONE".equals(child.getTag(R.id.srl_tag))) {
                continue;
            }

            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                final View headerView = mRefreshHeader.getView();
                final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, mlp.leftMargin + mlp.rightMargin, lp.width);
                int height = mHeaderHeight;

                if (mHeaderHeightStatus.ordinal < DimensionStatus.XmlLayoutUnNotify.ordinal) {
                    if (lp.height > 0) {
                        height =  lp.height + mlp.bottomMargin + mlp.topMargin;
                        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                            mHeaderHeight = lp.height + mlp.bottomMargin + mlp.topMargin;
                            mHeaderHeightStatus = DimensionStatus.XmlExactUnNotify;
                        }
                    } else if (lp.height == WRAP_CONTENT && (mRefreshHeader.getSpinnerStyle() != SpinnerStyle.MatchLayout || !mHeaderHeightStatus.notified)) {
                        final int maxHeight = Math.max(getSize(heightMeasureSpec) - mlp.bottomMargin - mlp.topMargin, 0);
                        headerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        final int measuredHeight = headerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                                mHeaderHeight = measuredHeight + mlp.bottomMargin + mlp.topMargin;
                                mHeaderHeightStatus = DimensionStatus.XmlWrapUnNotify;
                            }
                        }
                    }
                }

                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    height = getSize(heightMeasureSpec);
                } else if (mRefreshHeader.getSpinnerStyle().scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(mEnableRefresh) ? mSpinner : 0);
                }

                if (height != -1) {
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(height - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                }

                if (!mHeaderHeightStatus.notified) {
                    final float maxDragHeight = mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate;
                    mHeaderHeightStatus = mHeaderHeightStatus.notified();
                    mRefreshHeader.onInitialized(mKernel, mHeaderHeight, (int) maxDragHeight);
                }

                if (needPreview && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                    minimumWidth += headerView.getMeasuredWidth();
                    minimumHeight += headerView.getMeasuredHeight();
                }
            }

            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final View footerView = mRefreshFooter.getView();
                final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, mlp.leftMargin + mlp.rightMargin, lp.width);
                int height = mFooterHeight;

                if (mFooterHeightStatus.ordinal < DimensionStatus.XmlLayoutUnNotify.ordinal) {
                    if (lp.height > 0) {
                        height = lp.height + mlp.topMargin + mlp.bottomMargin;
                        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                            mFooterHeight = lp.height + mlp.topMargin + mlp.bottomMargin;
                            mFooterHeightStatus = DimensionStatus.XmlExactUnNotify;
                        }
                    } else if (lp.height == WRAP_CONTENT && (mRefreshFooter.getSpinnerStyle() != SpinnerStyle.MatchLayout || !mFooterHeightStatus.notified)) {
                        int maxHeight = Math.max(getSize(heightMeasureSpec) - mlp.bottomMargin - mlp.topMargin, 0);
                        footerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        int measuredHeight = footerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                                mFooterHeight = measuredHeight + mlp.topMargin + mlp.bottomMargin;
                                mFooterHeightStatus = DimensionStatus.XmlWrapUnNotify;
                            }
                        }
                    }
                }

                if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    height = getSize(heightMeasureSpec);
                } else if (mRefreshFooter.getSpinnerStyle().scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(mEnableLoadMore) ? -mSpinner : 0);
                }

                if (height != -1) {
                    footerView.measure(widthSpec, makeMeasureSpec(Math.max(height - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                }

                if (!mFooterHeightStatus.notified) {
                    final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
                    mFooterHeightStatus = mFooterHeightStatus.notified();
                    mRefreshFooter.onInitialized(mKernel, mFooterHeight, (int) maxDragHeight);
                }

                if (needPreview && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                    minimumWidth += footerView.getMeasuredWidth();
                    minimumHeight += footerView.getMeasuredHeight();
                }
            }

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                final View contentView = mRefreshContent.getView();
                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final boolean showHeader = (mRefreshHeader != null && isEnableRefreshOrLoadMore(mEnableRefresh) && isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader));
                final boolean showFooter = (mRefreshFooter != null && isEnableRefreshOrLoadMore(mEnableLoadMore) && isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter));
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        thisView.getPaddingLeft() + thisView.getPaddingRight() +  mlp.leftMargin + mlp.rightMargin, lp.width);
                final int heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        thisView.getPaddingTop() + thisView.getPaddingBottom() + mlp.topMargin + mlp.bottomMargin +
                                ((needPreview && showHeader) ? mHeaderHeight : 0) +
                                ((needPreview && showFooter) ? mFooterHeight : 0), lp.height);
                contentView.measure(widthSpec, heightSpec);
                minimumWidth += contentView.getMeasuredWidth() +  mlp.leftMargin + mlp.rightMargin;
                minimumHeight += contentView.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;
            }
        }
        minimumWidth += thisView.getPaddingLeft() + thisView.getPaddingRight();
        minimumHeight += thisView.getPaddingTop() + thisView.getPaddingBottom();
        super.setMeasuredDimension(
                View.resolveSize(Math.max(minimumWidth, super.getSuggestedMinimumWidth()), widthMeasureSpec),
                View.resolveSize(Math.max(minimumHeight, super.getSuggestedMinimumHeight()), heightMeasureSpec));

        mLastTouchX = thisView.getMeasuredWidth() / 2f;
    }

    /**
     * ?????? Header Footer Content
     * 1.????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param changed ????????????
     * @param l ???
     * @param t ???
     * @param r ???
     * @param b ???
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingTop = thisView.getPaddingTop();
        final int paddingBottom = thisView.getPaddingBottom();

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (child.getVisibility() == GONE || "GONE".equals(child.getTag(R.id.srl_tag))) {
                continue;
            }

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh) && mRefreshHeader != null;
                final View contentView = mRefreshContent.getView();
                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                int left = paddingLeft + mlp.leftMargin;
                int top = paddingTop + mlp.topMargin;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                if (isPreviewMode && (isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader))) {
                    top = top + mHeaderHeight;
                    bottom = bottom + mHeaderHeight;
                }

                contentView.layout(left, top, right, bottom);
            }
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh);
                final View headerView = mRefreshHeader.getView();
                final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                int left = mlp.leftMargin;
                int top = mlp.topMargin + mHeaderInsetStart;
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                if (!isPreviewMode) {
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = top - mHeaderHeight;
                        bottom = bottom - mHeaderHeight;
                        /*
                         * SpinnerStyle.Scale  headerView.getMeasuredHeight() ??????????????????
                         **/
//                    } else if (mRefreshHeader.getSpinnerStyle().scale && mSpinner > 0) {
//                        bottom = top + Math.max(Math.max(0, isEnableRefreshOrLoadMore(mEnableRefresh) ? mSpinner : 0) - lp.bottomMargin - lp.topMargin, 0);
                    }
                }
                headerView.layout(left, top, right, bottom);
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableLoadMore);
                final View footerView = mRefreshFooter.getView();
                final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
                int left = mlp.leftMargin;
                int top = mlp.topMargin + thisView.getMeasuredHeight() - mFooterInsetStart;
                if (mFooterNoMoreData && mFooterNoMoreDataEffective && mEnableFooterFollowWhenNoMoreData && mRefreshContent != null
                        && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate
                        && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                    final View contentView = mRefreshContent.getView();
                    final ViewGroup.LayoutParams clp = contentView.getLayoutParams();
                    final int topMargin = clp instanceof MarginLayoutParams ? ((MarginLayoutParams)clp).topMargin : 0;
                    top = paddingTop + paddingTop + topMargin + contentView.getMeasuredHeight();
                }

                if (style == SpinnerStyle.MatchLayout) {
                    top = mlp.topMargin - mFooterInsetStart;
                } else if (isPreviewMode
                        || style == SpinnerStyle.FixedFront
                        || style == SpinnerStyle.FixedBehind) {
                    top = top - mFooterHeight;
                } else if (style.scale && mSpinner < 0) {
                    top = top - Math.max(isEnableRefreshOrLoadMore(mEnableLoadMore) ? -mSpinner : 0, 0);
                }

                int right = left + footerView.getMeasuredWidth();
                int bottom = top + footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
        }
    }

    /**
     * ?????? onDetachedFromWindow ????????? smart ???????????????
     * 1.??????????????????
     * 2.?????????????????? ????????????????????????
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        mManualLoadMore = true;
        animationRunnable = null;
        if (reboundAnimator != null) {
            Animator animator = reboundAnimator;
            animator.removeAllListeners();
            reboundAnimator.removeAllUpdateListeners();
            reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
            reboundAnimator.cancel();//????????? cancel ??? end ??????
            reboundAnimator = null;
        }
        /*
         * 2020-5-27
         * https://github.com/scwang90/SmartRefreshLayout/issues/1166
         * ?????? Fragment ??????????????????????????????????????????????????????????????????
         * Smart ?????????????????????????????????????????????mHandler????????????????????????????????? APP ????????????
         */
        if (mRefreshHeader != null && mState == RefreshState.Refreshing) {
            mRefreshHeader.onFinish(this, false);
        }
        if (mRefreshFooter != null && mState == RefreshState.Loading) {
            mRefreshFooter.onFinish(this, false);
        }
        if (mSpinner != 0) {
            mKernel.moveSpinner(0, true);
        }
        if (mState != RefreshState.None) {
            notifyStateChanged(RefreshState.None);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        /*
         * https://github.com/scwang90/SmartRefreshLayout/issues/716
         * ????????????????????????????????????????????????????????????
         * ?????? onDetachedFromWindow ??? finishLoadMore ??? Runnable ????????????????????????
         * ???????????? mFooterLocked ????????? true??????????????????????????????
         * ????????? onDetachedFromWindow ??????????????? mFooterLocked = false
         */
        mFooterLocked = false;
    }

    /**
     * ?????? drawChild ????????? smart ???????????????
     * 1.??? Header ??? Footer ???????????? ??????????????????????????????
     * 2.??? Header ??? Footer ??? FixedBehind ??????????????????????????? ???mEnableClipHeaderWhenFixedBehind=true ?????????
     * @param canvas ????????????
     * @param child ??????????????????View
     * @param drawingTime ????????????
     */
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final View thisView = this;
        final View contentView = mRefreshContent != null ? mRefreshContent.getView() : null;
        if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
            if (!isEnableRefreshOrLoadMore(mEnableRefresh) || (!mEnablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int bottom = Math.max(contentView.getTop() + contentView.getPaddingTop() + mSpinner, child.getTop());
                if (mHeaderBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mHeaderBackgroundColor);
                    if (mRefreshHeader.getSpinnerStyle().scale) {
                        bottom = child.getBottom();
                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        bottom = child.getBottom() + mSpinner;
                    }
                    canvas.drawRect(0, child.getTop(), thisView.getWidth(), bottom, mPaint);
                }
                /*
                 * 2019-12-24
                 * ?????? ?????????????????????????????????????????????
                 * ??????????????? 1.1.0 ???????????? Smart ????????? Scale ??????????????? FixedBehind ??????
                 * ?????????????????? child ???????????????????????????????????????????????? dispatchDraw ????????????
                 */
                if ((mEnableClipHeaderWhenFixedBehind && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) || mRefreshHeader.getSpinnerStyle().scale) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), bottom);
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }
        }
        if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
            if (!isEnableRefreshOrLoadMore(mEnableLoadMore) || (!mEnablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int top = Math.min(contentView.getBottom() - contentView.getPaddingBottom() + mSpinner, child.getBottom());
                if (mFooterBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mFooterBackgroundColor);
                    if (mRefreshFooter.getSpinnerStyle().scale) {
                        top = child.getTop();
                    } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = child.getTop() + mSpinner;
                    }
                    canvas.drawRect(0, top, thisView.getWidth(), child.getBottom(), mPaint);
                }
                /*
                 * 2019-12-24
                 * ?????? ?????????????????????????????????????????????
                 * ??????????????? 1.1.0 ???????????? Smart ????????? Scale ??????????????? FixedBehind ??????
                 * ?????????????????? child ???????????????????????????????????????????????? dispatchDraw ????????????
                 */
                if ((mEnableClipFooterWhenFixedBehind && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) || mRefreshFooter.getSpinnerStyle().scale) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), top, child.getRight(), child.getBottom());
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }

        }
        return super.drawChild(canvas, child, drawingTime);
    }


    //<editor-fold desc="????????????">
    protected boolean mVerticalPermit = false;                  //??????????????????????????????????????????????????????

    /**
     * ?????? computeScroll ????????? smart ???????????????
     * 1.????????????
     * 2.????????????
     */
    @Override
    public void computeScroll() {
        int lastCurY = mScroller.getCurrY();
        if (mScroller.computeScrollOffset()) {
            int finalY = mScroller.getFinalY();
            if ((finalY < 0 && (mEnableRefresh || mEnableOverScrollDrag) && mRefreshContent.canRefresh())
                    || (finalY > 0 && (mEnableLoadMore || mEnableOverScrollDrag) && mRefreshContent.canLoadMore())) {
                if(mVerticalPermit) {
                    float velocity;
                    velocity = finalY > 0 ? -mScroller.getCurrVelocity() : mScroller.getCurrVelocity();
                    animSpinnerBounce(velocity);
                }
                mScroller.forceFinished(true);
            } else {
                mVerticalPermit = true;//?????????????????????
                final View thisView = this;
                thisView.invalidate();
            }
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="???????????? judgement of slide">
    protected MotionEvent mFalsifyEvent = null;

    /**
     * ???????????? ??????????????????
     * 1.????????????
     * 2.????????????????????????
     * @param e ??????
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        //<editor-fold desc="????????????????????????">
        //---------------------------------------------------------------------------
        //????????????????????????
        //---------------------------------------------------------------------------
        final int action = e.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? e.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
        final int count = e.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += e.getX(i);
            sumY += e.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float touchX = sumX / div;
        final float touchY = sumY / div;
        if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN)
                && mIsBeingDragged) {
            mTouchY += touchY - mLastTouchY;
        }
        mLastTouchX = touchX;
        mLastTouchY = touchY;
        //---------------------------------------------------------------------------
        //</editor-fold>


        //---------------------------------------------------------------------------
        //????????????????????????
        //---------------------------------------------------------------------------
        final View thisView = this;
        if (mNestedInProgress) {//??????????????????????????????????????????????????????????????????????????????????????? onHorizontalDrag
            int totalUnconsumed = mTotalUnconsumed;
            boolean ret = super.dispatchTouchEvent(e);
            if (action == MotionEvent.ACTION_MOVE) {
                if (totalUnconsumed == mTotalUnconsumed) {
                    final int offsetX = (int) mLastTouchX;
                    final int offsetMax = thisView.getWidth();
                    final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                    if (isEnableRefreshOrLoadMore(mEnableRefresh) && mSpinner > 0 && mRefreshHeader != null && mRefreshHeader.isSupportHorizontalDrag()) {
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                    } else if (isEnableRefreshOrLoadMore(mEnableLoadMore) && mSpinner < 0 && mRefreshFooter != null && mRefreshFooter.isSupportHorizontalDrag()) {
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }
            }
            return ret;
        } else if (!thisView.isEnabled()
                || (!mEnableRefresh && !mEnableLoadMore && !mEnableOverScrollDrag)
                || (mHeaderNeedTouchEventWhenRefreshing && ((mState.isOpening || mState.isFinishing) && mState.isHeader))
                || (mFooterNeedTouchEventWhenLoading && ((mState.isOpening || mState.isFinishing) && mState.isFooter))) {
            return super.dispatchTouchEvent(e);
        }

        if (interceptAnimatorByAction(action) || mState.isFinishing
                || (mState == RefreshState.Loading && mDisableContentWhenLoading)
                || (mState == RefreshState.Refreshing && mDisableContentWhenRefresh)) {
            return false;
        }

        //-------------------------------------------------------------------------//

        //---------------------------------------------------------------------------
        //??????????????????
        //---------------------------------------------------------------------------
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*----------------------------------------------------*/
                /*                   ?????????????????????                    */
                /*----------------------------------------------------*/
                mCurrentVelocity = 0;
                mVelocityTracker.addMovement(e);
                mScroller.forceFinished(true);
                /*----------------------------------------------------*/
                /*                   ?????????????????????                    */
                /*----------------------------------------------------*/
                mTouchX = touchX;
                mTouchY = touchY;
                mLastSpinner = 0;
                mTouchSpinner = mSpinner;
                mIsBeingDragged = false;
                mEnableDisallowIntercept = false;
                /*----------------------------------------------------*/
                mSuperDispatchTouchEvent = super.dispatchTouchEvent(e);
                if (mState == RefreshState.TwoLevel && mTouchY < thisView.getMeasuredHeight() * (1 - mTwoLevelBottomPullUpToCloseRate)) {
                    mDragDirection = 'h';//?????????????????????????????????????????????
                    return mSuperDispatchTouchEvent;
                }
                if (mRefreshContent != null) {
                    //??? RefreshContent ????????????????????????????????????????????????????????????????????????View??????????????????????????????
                    mRefreshContent.onActionDown(e);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = touchX - mTouchX;
                float dy = touchY - mTouchY;
                mVelocityTracker.addMovement(e);//????????????
                if (!mIsBeingDragged && !mEnableDisallowIntercept && mDragDirection != 'h' && mRefreshContent != null) {//???????????????????????????  canRefresh canLoadMore ???????????????
                    if (mDragDirection == 'v' || (Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy))) {//???????????????????????????45???
                        mDragDirection = 'v';
                        if (dy > 0 && (mSpinner < 0 || ((mEnableOverScrollDrag || mEnableRefresh) && mRefreshContent.canRefresh()))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY - mTouchSlop;//?????? mTouchSlop ??????
                        } else if (dy < 0 && (mSpinner > 0 || ((mEnableOverScrollDrag || mEnableLoadMore) && ((mState==RefreshState.Loading&&mFooterLocked)||mRefreshContent.canLoadMore())))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY + mTouchSlop;//?????? mTouchSlop ??????
                        }
                        if (mIsBeingDragged) {
                            dy = touchY - mTouchY;//?????? mTouchSlop ?????? ???????????? dy
                            if (mSuperDispatchTouchEvent) {//????????????????????????????????????????????????????????????
                                e.setAction(MotionEvent.ACTION_CANCEL);
                                super.dispatchTouchEvent(e);
                            }
                            mKernel.setState((mSpinner > 0 || (mSpinner == 0 && dy > 0)) ? RefreshState.PullDownToRefresh : RefreshState.PullUpToLoad);
                            final ViewParent parent = thisView.getParent();
                            if (parent instanceof ViewGroup) {
                                //???????????? https://github.com/scwang90/SmartRefreshLayout/issues/580
                                //noinspection RedundantCast
                                ((ViewGroup)parent).requestDisallowInterceptTouchEvent(true);//?????????????????????????????????
                            }
                        }
                    } else if (Math.abs(dx) >= mTouchSlop && Math.abs(dx) > Math.abs(dy) && mDragDirection != 'v') {
                        mDragDirection = 'h';//????????????????????????????????????????????? ???????????? ????????????
                    }
                }
                if (mIsBeingDragged) {
                    int spinner = (int) dy + mTouchSpinner;
                    if ((mViceState.isHeader && (spinner < 0 || mLastSpinner < 0)) || (mViceState.isFooter && (spinner > 0 || mLastSpinner > 0))) {
                        mLastSpinner = spinner;
                        long time = e.getEventTime();
                        if (mFalsifyEvent == null) {
                            mFalsifyEvent = obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mTouchY, 0);
                            super.dispatchTouchEvent(mFalsifyEvent);
                        }
                        MotionEvent em = obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mTouchY + spinner, 0);
                        super.dispatchTouchEvent(em);
                        if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                            mFooterLocked = false;//????????????????????? ??????Footer ?????????
                        }
                        if (spinner > 0 && ((mEnableOverScrollDrag || mEnableRefresh) && mRefreshContent.canRefresh())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullDownToRefresh);
                        } else if (spinner < 0 && ((mEnableOverScrollDrag || mEnableLoadMore) && mRefreshContent.canLoadMore())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullUpToLoad);
                        }
                        if ((mViceState.isHeader && spinner < 0) || (mViceState.isFooter && spinner > 0)) {
                            if (mSpinner != 0) {
                                moveSpinnerInfinitely(0);
                            }
                            return true;
                        } else if (mFalsifyEvent != null) {
                            mFalsifyEvent = null;
                            em.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(em);
                        }
                        em.recycle();
                    }
                    moveSpinnerInfinitely(spinner);
                    return true;
                } else if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                    mFooterLocked = false;//????????????????????? ??????Footer ?????????
                }
                break;
            case MotionEvent.ACTION_UP://?????????????????????????????????
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                mCurrentVelocity = (int) mVelocityTracker.getYVelocity();
                startFlingIfNeed(0);
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();//?????????????????????
                mDragDirection = 'n';//??????????????????
                if (mFalsifyEvent != null) {
                    mFalsifyEvent.recycle();
                    mFalsifyEvent = null;
                    long time = e.getEventTime();
                    MotionEvent ec = obtain(time, time, action, mTouchX, touchY, 0);
                    super.dispatchTouchEvent(ec);
                    ec.recycle();
                }
                overSpinner();
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;//??????????????????
                    return true;
                }
                break;
        }
        //-------------------------------------------------------------------------//
        return super.dispatchTouchEvent(e);
    }

    /**
     * ????????????????????????????????? SwipeRefreshLayout
     * ?????????????????????????????? ListView ?????????????????? ????????????????????? requestDisallowInterceptTouchEvent
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        View target = mRefreshContent.getScrollableView();
        if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
                && (/*target == null || */ViewCompat.isNestedScrollingEnabled(target))) {
            mEnableDisallowIntercept = disallowIntercept;
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /**
     * ?????????????????? ?????? Fling ??????
     * @param flingVelocity ??????
     * @return true ???????????? ??????????????? Fling
     */
    protected boolean startFlingIfNeed(float flingVelocity) {
        float velocity = flingVelocity == 0 ? mCurrentVelocity : flingVelocity;
//        //????????????????????????????????????????????????
//        if (Build.VERSION.SDK_INT > 27 && mRefreshContent != null) {
//            /*
//             * ?????? API 27 ???????????????????????????????????????????????????bug
//             */
//            float scaleY = getScaleY();
//            final View thisView = this;
//            final View contentView = mRefreshContent.getView();
//            if (thisView.getScaleY() == -1 && contentView.getScaleY() == -1) {
//                velocity = -velocity;
//            }
//        }
        if (Math.abs(velocity) > mMinimumVelocity) {
            if (velocity * mSpinner < 0) {
                /*
                 * ??????????????????????????????????????????????????????
                 * velocity * mSpinner < 0 ??????????????????????????????????????? mSpinner ?????????
                 * ????????? mState.isOpening?????????????????? ?????? ??? noMoreData ?????? ??? mSpinner ??????????????????
                 * ???????????? FlingRunnable ????????? mSpinner ?????????????????????????????? fling ??????
                 */
                if (mState == RefreshState.Refreshing || mState == RefreshState.Loading || (mSpinner < 0 && mFooterNoMoreData)) {
                    animationRunnable = new FlingRunnable(velocity).start();
                    return true;
                } else if (mState.isReleaseToOpening) {
                    return true;//??????????????????????????????????????????????????? Fling
                }
            }
            if ((velocity < 0 && ((mEnableOverScrollBounce && (mEnableLoadMore || mEnableOverScrollDrag)) || (mState == RefreshState.Loading && mSpinner >= 0) || (mEnableAutoLoadMore&&isEnableRefreshOrLoadMore(mEnableLoadMore))))
                    || (velocity > 0 && ((mEnableOverScrollBounce && mEnableRefresh || mEnableOverScrollDrag) || (mState == RefreshState.Refreshing && mSpinner <= 0)))) {
                /*
                 * ???????????????????????????Refreshing???Loading???noMoreData ???????????????
                 * ??????????????? mScroller.fling ????????????????????????????????? AbsListView ??? ScrollView ??????????????????????????????????????? mScroller.fling???
                 *      ?????? mScroller.fling ???????????? ?????????????????????????????????????????? fling ???????????? ??? ???????????????
                 *      ?????? computeScroll ????????????????????????????????????????????????????????????????????? fling ??????
                 *      ?????? ??????????????????????????? ?????????????????????????????????????????????Refreshing???Loading???noMoreData
                 */
                mVerticalPermit = false;//?????????????????????
                mScroller.fling(0, 0, 0, (int) -velocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                mScroller.computeScrollOffset();
                final View thisView = this;
                thisView.invalidate();
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param action MotionEvent
     * @return ??????????????????
     */
    protected boolean interceptAnimatorByAction(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            if (reboundAnimator != null) {
                if (mState.isFinishing || mState == RefreshState.TwoLevelReleased || mState == RefreshState.RefreshReleased || mState == RefreshState.LoadReleased) {
                    return true;//??????????????????????????????????????????
                }
                if (mState == RefreshState.PullDownCanceled) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                } else if (mState == RefreshState.PullUpCanceled) {
                    mKernel.setState(RefreshState.PullUpToLoad);
                }
                reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                reboundAnimator.cancel();//????????? cancel ??? end ??????
                reboundAnimator = null;
            }
            animationRunnable = null;
        }
        return reboundAnimator != null;
    }
    //</editor-fold>

    //<editor-fold desc="???????????? state changes">

    /**
     * ??????????????????????????? ???setState???
     * @param state ??????
     */
    protected void notifyStateChanged(RefreshState state) {
        final RefreshState oldState = mState;
        if (oldState != state) {
            mState = state;
            mViceState = state;
            final OnStateChangedListener refreshHeader = mRefreshHeader;
            final OnStateChangedListener refreshFooter = mRefreshFooter;
            final OnStateChangedListener refreshListener = mOnMultiListener;
            if (refreshHeader != null) {
                refreshHeader.onStateChanged(this, oldState, state);
            }
            if (refreshFooter != null) {
                refreshFooter.onStateChanged(this, oldState, state);
            }
            if (refreshListener != null) {
                refreshListener.onStateChanged(this, oldState, state);
            }
            if (state == RefreshState.LoadFinish) {
                mFooterLocked = false;
            }
        } else if (mViceState != mState) {
            /*
             * notifyStateChanged???mViceState ????????? ????????? ??????
             */
            mViceState = mState;
        }
    }

    /**
     * ???????????????????????? Loading ????????????
     * @param triggerLoadMoreEvent ????????????????????????
     */
    protected void setStateDirectLoading(boolean triggerLoadMoreEvent) {
        if (mState != RefreshState.Loading) {
            mLastOpenTime = currentTimeMillis();
//            if (mState != RefreshState.LoadReleased) {
//                if (mState != RefreshState.ReleaseToLoad) {
//                    if (mState != RefreshState.PullUpToLoad) {
//                        mKernel.setState(RefreshState.PullUpToLoad);
//                    }
//                    mKernel.setState(RefreshState.ReleaseToLoad);
//                }
//                notifyStateChanged(RefreshState.LoadReleased);
//                if (mRefreshFooter != null) {
//                    mRefreshFooter.onReleased(this, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
//                }
//            }
            mFooterLocked = true;//Footer ??????loading ????????????????????? ????????????????????????
            notifyStateChanged(RefreshState.Loading);
            if (mLoadMoreListener != null) {
                if (triggerLoadMoreEvent) {
                    mLoadMoreListener.onLoadMore(this);
                }
            } else if (mOnMultiListener == null) {
                finishLoadMore(2000);//????????????????????????????????????????????????????????????
            }
            if (mRefreshFooter != null) {
                final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
                mRefreshFooter.onStartAnimator(this, mFooterHeight, (int) maxDragHeight);
            }
            if (mOnMultiListener != null && mRefreshFooter instanceof RefreshFooter) {
                final OnLoadMoreListener listener = mOnMultiListener;
                if (triggerLoadMoreEvent) {
                    listener.onLoadMore(this);
                }
                final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
                mOnMultiListener.onFooterStartAnimator((RefreshFooter) mRefreshFooter, mFooterHeight, (int) maxDragHeight);
            }
        }
    }

    /**
     * ??????????????? Loading ????????????
     * @param notify ????????????????????????
     */
    protected void setStateLoading(final boolean notify) {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation != null && animation.getDuration() == 0) {
                    return;//0 ???????????????
                }
                setStateDirectLoading(notify);
            }
        };
        notifyStateChanged(RefreshState.LoadReleased);
        ValueAnimator animator = mKernel.animSpinner(-mFooterHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshFooter != null) {
            //onReleased ????????????????????? animSpinner ?????? onAnimationEnd ??????
            // ?????? onReleased ?????? ???????????? ??? ?????? animSpinner ????????? ??????
            final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
            mRefreshFooter.onReleased(this, mFooterHeight, (int) maxDragHeight);
        }
        if (mOnMultiListener != null && mRefreshFooter instanceof RefreshFooter) {
            //??? mRefreshFooter.onReleased ??????
            final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
            mOnMultiListener.onFooterReleased((RefreshFooter) mRefreshFooter, mFooterHeight, (int) maxDragHeight);
        }
        if (animator == null) {
            //onAnimationEnd ?????????????????? loading ????????? onReleased ????????????
            listener.onAnimationEnd(null);
        }
    }

    /**
     * ??????????????? Refreshing ????????????
     * @param notify ????????????????????????
     */
    protected void setStateRefreshing(final boolean notify) {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation != null && animation.getDuration() == 0) {
                    return;//0 ???????????????
                }
                mLastOpenTime = currentTimeMillis();
                notifyStateChanged(RefreshState.Refreshing);
                if (mRefreshListener != null) {
                    if(notify) {
                        mRefreshListener.onRefresh(SmartRefreshLayout.this);
                    }
                } else if (mOnMultiListener == null) {
                    finishRefresh(3000);
                }
                if (mRefreshHeader != null) {
                    final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
                    mRefreshHeader.onStartAnimator(SmartRefreshLayout.this, mHeaderHeight,  maxDragHeight);
                }
                if (mOnMultiListener != null && mRefreshHeader instanceof RefreshHeader) {
                    if (notify) {
                        mOnMultiListener.onRefresh(SmartRefreshLayout.this);
                    }
                    final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
                    mOnMultiListener.onHeaderStartAnimator((RefreshHeader) mRefreshHeader, mHeaderHeight,  maxDragHeight);
                }
            }
        };
        notifyStateChanged(RefreshState.RefreshReleased);
        ValueAnimator animator = mKernel.animSpinner(mHeaderHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshHeader != null) {
            //onReleased ????????????????????? animSpinner ?????? onAnimationEnd ??????
            // ?????? onRefreshReleased?????? ???????????? ??? ?????? animSpinner ????????? ??????
            final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
            mRefreshHeader.onReleased(this, mHeaderHeight,  maxDragHeight);
        }
        if (mOnMultiListener != null && mRefreshHeader instanceof RefreshHeader) {
            //??? mRefreshHeader.onReleased ??????
            final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
            mOnMultiListener.onHeaderReleased((RefreshHeader)mRefreshHeader, mHeaderHeight,  maxDragHeight);
        }
        if (animator == null) {
            //onAnimationEnd ?????????????????? Refreshing ????????? onReleased ????????????
            listener.onAnimationEnd(null);
        }
    }

//    /**
//     * ????????????
//     */
//    protected void resetStatus() {
//        if (mState != RefreshState.None) {
//            if (mSpinner == 0) {
//                notifyStateChanged(RefreshState.None);
//            }
//        }
//        if (mSpinner != 0) {
//            mKernel.animSpinner(0);
//        }
//    }

    /**
     * ?????? ?????????
     * @param state ??????
     */
    protected void setViceState(RefreshState state) {
        if (mState.isDragging && mState.isHeader != state.isHeader) {
            notifyStateChanged(RefreshState.None);
        }
        if (mViceState != state) {
            mViceState = state;
        }
    }

    /**
     * ???????????? ??????????????? ?????? ????????????
     * @param enable mEnableHeaderTranslationContent or mEnableFooterTranslationContent
     * @param internal mRefreshHeader or mRefreshFooter
     * @return enable
     */
    protected boolean isEnableTranslationContent(boolean enable, @Nullable RefreshComponent internal) {
        /*
         * 2019-12-25 ?????? 2.0 ????????????????????? Header Footer ?????????????????????????????? ?????? @Nullable
         */
        return enable || mEnablePureScrollMode || internal == null || internal.getSpinnerStyle() == SpinnerStyle.FixedBehind;
    }

    /**
     * ??????????????? ?????????????????????????????? ???????????? ??????????????????????????????
     * ?????????????????? ?????? ?????? ????????????????????????Header???Footer ???????????????
     * @param enable mEnableRefresh or mEnableLoadMore
     * @return enable
     */
    protected boolean isEnableRefreshOrLoadMore(boolean enable) {
        return enable && !mEnablePureScrollMode;
    }
    //</editor-fold>

    //<editor-fold desc="???????????? displacement">

    //<editor-fold desc="???????????? Animator Listener">
    protected Runnable animationRunnable;
    protected ValueAnimator reboundAnimator;
    protected class FlingRunnable implements Runnable {
        int mOffset;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mVelocity;
        float mDamping = 0.98f;//?????????????????????
        long mStartTime = 0;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        FlingRunnable(float velocity) {
            mVelocity = velocity;
            mOffset = mSpinner;
        }

        public Runnable start() {
            if (mState.isFinishing) {
                return null;
            }
            if (mSpinner != 0 && (!(mState.isOpening || (mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(mEnableLoadMore)))
                    || ((mState == RefreshState.Loading || (mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(mEnableLoadMore))) && mSpinner < -mFooterHeight)
                    || (mState == RefreshState.Refreshing && mSpinner > mHeaderHeight))) {
                int frame = 0;
                int offset = mSpinner;
                int spinner = mSpinner;
                float velocity = mVelocity;
                while (spinner * offset > 0) {
                    velocity *= Math.pow(mDamping, (++frame) * mFrameDelay / 10f);
                    float velocityFrame = (velocity * (1f * mFrameDelay / 1000));
                    if (Math.abs(velocityFrame) < 1) {
                        if (!mState.isOpening
                                || (mState == RefreshState.Refreshing && offset > mHeaderHeight)
                                || (mState != RefreshState.Refreshing && offset < -mFooterHeight)) {
                            return null;
                        }
                        break;
                    }
                    offset += velocityFrame;
                }
            }
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mHandler.postDelayed(this, mFrameDelay);
            return this;
        }

        @Override
        public void run() {
            if (animationRunnable == this && !mState.isFinishing) {
//                mVelocity *= Math.pow(mDamping, ++mFrame);
                long now = AnimationUtils.currentAnimationTimeMillis();
                long span = now - mLastTime;
                mVelocity *= Math.pow(mDamping, (now - mStartTime) / (1000f / mFrameDelay));
                float velocity = (mVelocity * (1f * span / 1000));
                if (Math.abs(velocity) > 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    if (mSpinner * mOffset > 0) {
                        mKernel.moveSpinner(mOffset, true);
                        mHandler.postDelayed(this, mFrameDelay);
                    } else {
                        animationRunnable = null;
                        mKernel.moveSpinner(0, true);
                        fling(mRefreshContent.getScrollableView(), (int) -mVelocity);
                        if (mFooterLocked && velocity > 0) {
                            mFooterLocked = false;
                        }
                    }
                } else {
                    animationRunnable = null;
                }
            }
        }
    }
    protected class BounceRunnable implements Runnable {
        int mFrame = 0;
        int mFrameDelay = 10;
        int mSmoothDistance;
        long mLastTime;
        float mOffset = 0;
        float mVelocity;
        BounceRunnable(float velocity, int smoothDistance){
            mVelocity = velocity;
            mSmoothDistance = smoothDistance;
            mLastTime = AnimationUtils.currentAnimationTimeMillis();
            mHandler.postDelayed(this, mFrameDelay);
            if (velocity > 0) {
                mKernel.setState(RefreshState.PullDownToRefresh);
            } else {
                mKernel.setState(RefreshState.PullUpToLoad);
            }
        }
        @Override
        public void run() {
            if (animationRunnable == this && !mState.isFinishing) {
                if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                    if (mSmoothDistance != 0) {
                        mVelocity *= Math.pow(0.45f, ++mFrame * 2);//??????????????????????????????????????????
                    } else {
                        mVelocity *= Math.pow(0.85f, ++mFrame * 2);//????????????????????????
                    }
                } else {
                    mVelocity *= Math.pow(0.95f, ++mFrame * 2);//????????????????????????
                }
                long now = AnimationUtils.currentAnimationTimeMillis();
                float t = 1f * (now - mLastTime) / 1000;
                float velocity = mVelocity * t;
                if (Math.abs(velocity) >= 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    moveSpinnerInfinitely(mOffset);
                    mHandler.postDelayed(this, mFrameDelay);
                } else {
                    if (mViceState.isDragging && mViceState.isHeader) {
                        mKernel.setState(RefreshState.PullDownCanceled);
                    } else if (mViceState.isDragging && mViceState.isFooter) {
                        mKernel.setState(RefreshState.PullUpCanceled);
                    }
                    animationRunnable = null;
                    if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                        int duration = 10 * Math.min(Math.max((int) SmartUtil.px2dp(Math.abs(mSpinner-mSmoothDistance)), 30), 100);
                        animSpinner(mSmoothDistance, 0, mReboundInterpolator, duration);
                    }
                }
            }
        }
    }
    //</editor-fold>

    /**
     * ??????????????????
     * @param endSpinner ?????????
     * @param startDelay ????????????
     * @param interpolator ?????????
     * @param duration ??????
     * @return ValueAnimator or null
     */
    protected ValueAnimator animSpinner(int endSpinner, int startDelay, Interpolator interpolator, int duration) {
        if (mSpinner != endSpinner) {
            if (reboundAnimator != null) {
                reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                reboundAnimator.cancel();//????????? cancel ??? end ??????
                reboundAnimator = null;
            }
            animationRunnable = null;
            reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner);
            reboundAnimator.setDuration(duration);
            reboundAnimator.setInterpolator(interpolator);
            reboundAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation != null && animation.getDuration() == 0) {
                        /*
                         * 2020-3-15 ??????
                         * onAnimationEnd ?????? cancel ?????????, ???????????? onAnimationEnd ?????????????????????
                         * ????????????????????? reboundAnimator.setDuration(0) ????????????????????????
                         */
                        return;
                    }
                    reboundAnimator = null;
                    if (mSpinner == 0 && mState != RefreshState.None && !mState.isOpening && !mState.isDragging) {
                        notifyStateChanged(RefreshState.None);
                    } else if (mState != mViceState) {
                        // ???????????????  ViceState ????????????????????????????????????????????? mViceState=mState
                        // ?????????
                        // ??? mState=Refreshing ??????????????????????????????setViceState = ReleaseToRefresh
                        // ???????????????????????????????????? HeaderHeight ??????
                        // ??????????????? mViceState ??????????????? Refreshing???????????????????????????????????????
                        setViceState(mState);
                    }
                }
            });
            reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mKernel.moveSpinner((int) animation.getAnimatedValue(), false);
                }
            });
            reboundAnimator.setStartDelay(startDelay);
//            reboundAnimator.setDuration(20000);
            reboundAnimator.start();
            return reboundAnimator;
        }
        return null;
    }

    /**
     * ??????????????????
     * @param velocity ??????
     */
    protected void animSpinnerBounce(final float velocity) {
        if (reboundAnimator == null) {
            if (velocity > 0 && (mState == RefreshState.Refreshing || mState == RefreshState.TwoLevel)) {
                animationRunnable = new BounceRunnable(velocity, mHeaderHeight);
            } else if (velocity < 0 && (mState == RefreshState.Loading
                    || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(mEnableLoadMore))
                    || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore) && mState != RefreshState.Refreshing))) {
                animationRunnable = new BounceRunnable(velocity, -mFooterHeight);
            } else if (mSpinner == 0 && mEnableOverScrollBounce) {
                animationRunnable = new BounceRunnable(velocity, 0);
            }
        }
    }

    /**
     * ??????????????????
     * ????????????????????????
     */
    protected void overSpinner() {
        if (mState == RefreshState.TwoLevel) {
            final View thisView = this;
            if (mCurrentVelocity > -1000 && mSpinner > thisView.getHeight() / 2) {
                ValueAnimator animator = mKernel.animSpinner(thisView.getHeight());
                if (animator != null) {
                    animator.setDuration(mFloorDuration);
                }
            } else if (mIsBeingDragged) {
                mKernel.finishTwoLevel();
            }
        } else if (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && mFooterNoMoreDataEffective && mSpinner < 0 && isEnableRefreshOrLoadMore(mEnableLoadMore))) {
            if (mSpinner < -mFooterHeight) {
                mKernel.animSpinner(-mFooterHeight);
            } else if (mSpinner > 0) {
                mKernel.animSpinner(0);
            }
        } else if (mState == RefreshState.Refreshing) {
            if (mSpinner > mHeaderHeight) {
                mKernel.animSpinner(mHeaderHeight);
            } else if (mSpinner < 0) {
                mKernel.animSpinner(0);
            }
        } else if (mState == RefreshState.PullDownToRefresh) {
            mKernel.setState(RefreshState.PullDownCanceled);
        } else if (mState == RefreshState.PullUpToLoad) {
            mKernel.setState(RefreshState.PullUpCanceled);
        } else if (mState == RefreshState.ReleaseToRefresh) {
            mKernel.setState(RefreshState.Refreshing);
        } else if (mState == RefreshState.ReleaseToLoad) {
            mKernel.setState(RefreshState.Loading);
        } else if (mState == RefreshState.ReleaseToTwoLevel) {
            mKernel.setState(RefreshState.TwoLevelReleased);
        } else if (mState == RefreshState.RefreshReleased) {
            if (reboundAnimator == null) {
                mKernel.animSpinner(mHeaderHeight);
            }
        } else if (mState == RefreshState.LoadReleased) {
            if (reboundAnimator == null) {
                mKernel.animSpinner(-mFooterHeight);
            }
        } else if (mState == RefreshState.LoadFinish) {
            /*
             * 2020-5-26 ?????? finishLoadMore ??????
             * ???????????? ???????????? ???????????? ?????? NoMoreData Footer ??????????????????????????????
             * overSpinner ??? LoadFinish ???????????????????????????
             */
        } else if (mSpinner != 0) {
            mKernel.animSpinner(0);
        }
    }

    /**
     * ???????????? spinner
     * @param spinner ?????????
     */
    protected void moveSpinnerInfinitely(float spinner) {
        final View thisView = this;
        if (mNestedInProgress && !mEnableLoadMoreWhenContentNotFull && spinner < 0) {
            if (!mRefreshContent.canLoadMore()) {
                /*
                 * 2019-1-22 ?????? ????????????????????? mEnableLoadMoreWhenContentNotFull=false ?????????bug
                 */
                spinner = 0;
            }
        }
        /*
         * ???????????????????????????APP???????????????????????????????????????
         *
         * 1.????????????????????????
         *         SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
         *             @Override
         *             public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
         *                 layout.getLayout().setTag("close egg");
         *             }
         *         });
         *
         * 2.XML??????
         *          <com.scwang.smart.refresh.layout.SmartRefreshLayout
         *              android:layout_width="match_parent"
         *              android:layout_height="match_parent"
         *              android:tag="close egg"/>
         *
         * 3.????????????
         *          ?????????????????????????????????4????????????
         */
        if (spinner > mScreenHeightPixels * 5 && (thisView.getTag() == null && thisView.getTag(R.id.srl_tag) == null) && mLastTouchY < mScreenHeightPixels / 6f && mLastTouchX < mScreenHeightPixels / 16f) {
            String egg = "???????????????????????????????????????";
            Toast.makeText(thisView.getContext(), egg, Toast.LENGTH_SHORT).show();
            thisView.setTag(R.id.srl_tag, egg);
        }
        if (mState == RefreshState.TwoLevel && spinner > 0) {
            mKernel.moveSpinner(Math.min((int) spinner, thisView.getMeasuredHeight()), true);
        } else if (mState == RefreshState.Refreshing && spinner >= 0) {
            if (spinner < mHeaderHeight) {
                mKernel.moveSpinner((int) spinner, true);
            } else {
                final float maxDragHeight = mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate;
                final double M = maxDragHeight - mHeaderHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mHeaderHeight;
                final double x = Math.max(0, (spinner - mHeaderHeight) * mDragRate);
                final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// ?????? y = M(1-100^(-x/H))
                mKernel.moveSpinner((int) y + mHeaderHeight, true);
            }
        } else if (spinner < 0 && (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(mEnableLoadMore))
                || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore)))) {
            if (spinner > -mFooterHeight) {
                mKernel.moveSpinner((int) spinner, true);
            } else {
                final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
                final double M = maxDragHeight - mFooterHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mFooterHeight;
                final double x = -Math.min(0, (spinner + mFooterHeight) * mDragRate);
                final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// ?????? y = M(1-100^(-x/H))
                mKernel.moveSpinner((int) y - mFooterHeight, true);
            }
        } else if (spinner >= 0) {
            final double M = mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate;
            final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
            final double x = Math.max(0, spinner * mDragRate);
            final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// ?????? y = M(1-100^(-x/H))
            mKernel.moveSpinner((int) y, true);
        } else {
            final double M = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
            final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
            final double x = -Math.min(0, spinner * mDragRate);
            final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// ?????? y = M(1-100^(-x/H))
            mKernel.moveSpinner((int) y, true);
        }
        if (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore) && spinner < 0
                && mState != RefreshState.Refreshing
                && mState != RefreshState.Loading
                && mState != RefreshState.LoadFinish) {
            if (mDisableContentWhenLoading) {
                animationRunnable = null;
                mKernel.animSpinner(-mFooterHeight);
            }
            setStateDirectLoading(false);
            /*
             * ???????????????????????????????????? onLoadMore ???mReboundDuration ???????????????????????????
             */
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore(SmartRefreshLayout.this);
                    } else if (mOnMultiListener == null) {
                        finishLoadMore(2000);//????????????????????????????????????????????????????????????
                    }
                    final OnLoadMoreListener listener = mOnMultiListener;
                    if (listener != null) {
                        listener.onLoadMore(SmartRefreshLayout.this);
                    }
                }
            }, mReboundDuration);
        }
    }
    //</editor-fold>

    //<editor-fold desc="???????????? LayoutParams">
//    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
//        return p instanceof LayoutParams;
//    }
//
//    @Override
//    protected LayoutParams generateDefaultLayoutParams() {
//        return new LayoutParams(MATCH_PARENT, MATCH_PARENT);
//    }
//
//    @Override
//    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
//        return new LayoutParams(p);
//    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        final View thisView = this;
        return new LayoutParams(thisView.getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout_Layout);
            backgroundColor = ta.getColor(R.styleable.SmartRefreshLayout_Layout_layout_srlBackgroundColor, backgroundColor);
            if (ta.hasValue(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle)) {
                spinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle, SpinnerStyle.Translate.ordinal)];
            }
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

//        public LayoutParams(MarginLayoutParams source) {
//            super(source);
//        }
//
//        public LayoutParams(ViewGroup.LayoutParams source) {
//            super(source);
//        }

        public int backgroundColor = 0;
        public SpinnerStyle spinnerStyle = null;
    }
    //</editor-fold>

    //<editor-fold desc="???????????? NestedScrolling">

    //<editor-fold desc="NestedScrollingParent">
    @Override
    public int getNestedScrollAxes() {
        return mNestedParent.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        final View thisView = this;
        boolean accepted = thisView.isEnabled() && isNestedScrollingEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (mEnableOverScrollDrag || mEnableRefresh || mEnableLoadMore);
        return accepted;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedParent.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        mNestedChild.startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);

        mTotalUnconsumed = mSpinner;//0;
        mNestedInProgress = true;

        interceptAnimatorByAction(MotionEvent.ACTION_DOWN);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        int consumedY = 0;

        // dy * mTotalUnconsumed > 0 ?????? mSpinner ???????????????????????????????????????
        // mTotalUnconsumed ???????????? dy ????????? ??????????????? mSpinner
        if (dy * mTotalUnconsumed > 0) {
            if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                consumedY = mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                consumedY = dy;
                mTotalUnconsumed -= dy;
            }
            moveSpinnerInfinitely(mTotalUnconsumed);
        } else if (dy > 0 && mFooterLocked) {
            consumedY = dy;
            mTotalUnconsumed -= dy;
            moveSpinnerInfinitely(mTotalUnconsumed);
        }

        // Now let our nested parent consume the leftovers
        mNestedChild.dispatchNestedPreScroll(dx, dy - consumedY, consumed, null);
        consumed[1] += consumedY;

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // Dispatch up to the nested parent first
        boolean scrolled = mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if ((dy < 0 && (mEnableRefresh || mEnableOverScrollDrag) && (mTotalUnconsumed != 0 || mScrollBoundaryDecider == null || mScrollBoundaryDecider.canRefresh(mRefreshContent.getView())))
                || (dy > 0 && (mEnableLoadMore || mEnableOverScrollDrag) && (mTotalUnconsumed != 0 || mScrollBoundaryDecider == null || mScrollBoundaryDecider.canLoadMore(mRefreshContent.getView())))) {
            if (mViceState == RefreshState.None || mViceState.isOpening) {
                /*
                 * ???????????????????????????????????????????????????????????????????????????????????????
                 * mViceState.isOpening ??????????????????????????? mViceState ???????????? mState ???????????? isOpening
                 */
                mKernel.setState(dy > 0 ? RefreshState.PullUpToLoad : RefreshState.PullDownToRefresh);
                if (!scrolled) {
                    final View thisView = this;
                    final ViewParent parent = thisView.getParent();
                    if (parent != null) {
                        //???????????? https://github.com/scwang90/SmartRefreshLayout/issues/580
                        parent.requestDisallowInterceptTouchEvent(true);//?????????????????????????????????
                    }
                }
            }
            moveSpinnerInfinitely(mTotalUnconsumed -= dy);
        }

        if (mFooterLocked && dyConsumed < 0) {
            mFooterLocked = false;//????????????????????? ??????Footer ?????????
        }

    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return (mFooterLocked && velocityY > 0) || startFlingIfNeed(-velocityY) || mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mNestedParent.onStopNestedScroll(target);
        mNestedInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        mTotalUnconsumed = 0;
        overSpinner();
        // Dispatch up our nested parent
        mNestedChild.stopNestedScroll();
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingChild">
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mEnableNestedScrolling = enabled;
//        mManualNestedScrolling = true;
        mNestedChild.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        /*
         * && ?????????????????????????????? https://github.com/scwang90/SmartRefreshLayout/issues/961 ??????
         */
        return mEnableNestedScrolling && (mEnableOverScrollDrag || mEnableRefresh || mEnableLoadMore);
//        return mNestedChild.isNestedScrollingEnabled();
    }

//    @Override
//    public boolean canScrollVertically(int direction) {
//        View target = mRefreshContent.getScrollableView();
//        if (direction < 0) {
//            return mEnableRefresh || ScrollBoundaryUtil.canScrollUp(target);
//        } else if (direction > 0) {
//            return mEnableLoadMore || ScrollBoundaryUtil.canScrollDown(target);
//        }
//        return true;
//    }

    //    @Override
//    @Deprecated
//    public boolean startNestedScroll(int axes) {
//        return mNestedChild.startNestedScroll(axes);
//    }
//
//    @Override
//    @Deprecated
//    public void stopNestedScroll() {
//        mNestedChild.stopNestedScroll();
//    }
//
//    @Override
//    @Deprecated
//    public boolean hasNestedScrollingParent() {
//        return mNestedChild.hasNestedScrollingParent();
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
//                                        int dyUnconsumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed,
//                dxUnconsumed, dyUnconsumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedPreScroll(
//                dx, dy, consumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
//    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="???????????? open interface">

    /**
     * Set the Header's height.
     * ?????? Header ??????
     * @param heightDp Density-independent Pixels ???????????????px????????????px2dp?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderHeight(float heightDp) {
        return setHeaderHeightPx(dp2px(heightDp));
    }

    /**
     * ?????? Header ??????
     * @param height ??????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderHeightPx(int height) {
        if (height == mHeaderHeight) {
            return this;
        }
        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mHeaderHeight = height;
            if (mRefreshHeader != null && mAttachedToWindow && mHeaderHeightStatus.notified) {
                SpinnerStyle style = mRefreshHeader.getSpinnerStyle();
                if (style != SpinnerStyle.MatchLayout && !style.scale) {
                    /*
                     * ?????? MotionLayout 2019-6-18
                     * ??? MotionLayout ?????? requestLayout ??????
                     * ?????? ???????????? layout ??????
                     * https://github.com/scwang90/SmartRefreshLayout/issues/944
                     */
//                  mRefreshHeader.getView().requestLayout();
                    View headerView = mRefreshHeader.getView();
                    final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                    final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams) lp : sDefaultMarginLP;
                    final int widthSpec = makeMeasureSpec(headerView.getMeasuredWidth(), EXACTLY);
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(mHeaderHeight - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                    final int left = mlp.leftMargin;
                    int top = mlp.topMargin + mHeaderInsetStart - ((style == SpinnerStyle.Translate) ? mHeaderHeight : 0);
                    headerView.layout(left, top, left + headerView.getMeasuredWidth(), top + headerView.getMeasuredHeight());
                }
                final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
                mHeaderHeightStatus = DimensionStatus.CodeExact;
                mRefreshHeader.onInitialized(mKernel, mHeaderHeight, maxDragHeight);
            } else {
                mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }

    /**
     * Set the Footer's height.
     * ?????? Footer ?????????
     * @param heightDp Density-independent Pixels ???????????????px????????????px2dp?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterHeight(float heightDp) {
        return setFooterHeightPx(dp2px(heightDp));
    }

    /**
     * ?????? Footer ??????
     * @param height ??????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterHeightPx(int height) {
        if (height == mFooterHeight) {
            return this;
        }
        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mFooterHeight = height;
            if (mRefreshFooter != null && mAttachedToWindow && mFooterHeightStatus.notified) {
                SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
                if (style != SpinnerStyle.MatchLayout && !style.scale) {
                    /*
                     * ?????? MotionLayout 2019-6-18
                     * ??? MotionLayout ?????? requestLayout ??????
                     * ?????? ???????????? layout ??????
                     * https://github.com/scwang90/SmartRefreshLayout/issues/944
                     */
//                  mRefreshFooter.getView().requestLayout();
                    View thisView = this;
                    View footerView = mRefreshFooter.getView();
                    final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                    final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                    final int widthSpec = makeMeasureSpec(footerView.getMeasuredWidth(), EXACTLY);
                    footerView.measure(widthSpec, makeMeasureSpec(Math.max(mFooterHeight - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                    final int left = mlp.leftMargin;
                    final int top = mlp.topMargin + thisView.getMeasuredHeight() - mFooterInsetStart - ((style != SpinnerStyle.Translate) ? mFooterHeight : 0);
                    footerView.layout(left, top, left + footerView.getMeasuredWidth(), top + footerView.getMeasuredHeight());
                }
                final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
                mFooterHeightStatus = DimensionStatus.CodeExact;
                mRefreshFooter.onInitialized(mKernel, mFooterHeight, (int) maxDragHeight);
            } else {
                mFooterHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }

    /**
     * Set the Header's start offset???see srlHeaderInsetStart in the RepastPracticeActivity XML in demo-app for the practical application???.
     * ?????? Header ??????????????????????????????????????? demo-app ?????? RepastPracticeActivity xml ?????? srlHeaderInsetStart???
     * @param insetDp Density-independent Pixels ???????????????px????????????px2dp?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderInsetStart(float insetDp) {
        mHeaderInsetStart = dp2px(insetDp);
        return this;
    }

    /**
     * Set the Header's start offset???see srlHeaderInsetStart in the RepastPracticeActivity XML in demo-app for the practical application???.
     * ?????? Header ???????????????????????????????????? demo-app ?????? RepastPracticeActivity xml ?????? srlHeaderInsetStart???
     * @param insetPx ??????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderInsetStartPx(int insetPx) {
        mHeaderInsetStart = insetPx;
        return this;
    }

    /**
     * Set the Footer's start offset.
     * ?????? Footer ??????????????????????????? setHeaderInsetStart ?????????
     * @see RefreshLayout#setHeaderInsetStart(float)
     * @param insetDp Density-independent Pixels ???????????????px????????????px2dp?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterInsetStart(float insetDp) {
        mFooterInsetStart = dp2px(insetDp);
        return this;
    }

    /**
     * Set the Footer's start offset.
     * ?????? Footer ??????????????????????????? setHeaderInsetStartPx ?????????
     * @param insetPx ??????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterInsetStartPx(int insetPx) {
        mFooterInsetStart = insetPx;
        return this;
    }

    /**
     * Set the damping effect.
     * ??????????????????/?????????????????? ???????????????0.5??????????????????
     * @param rate ratio = (The drag height of the view)/(The actual drag height of the finger)
     *             ?????? = ?????????????????? / ??????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setDragRate(float rate) {
        this.mDragRate = rate;
        return this;
    }

    /**
     * Set the ratio of the maximum height to drag header.
     * ???????????????????????????Header????????????????????????????????????????????????????????????
     * @param rate ratio = (the maximum height to drag header)/(the height of header)
     *             ?????? = ?????????????????? / Header?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderMaxDragRate(float rate) {
        this.mHeaderMaxDragRate = rate;
        if (mRefreshHeader != null && mAttachedToWindow) {
            final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
            mRefreshHeader.onInitialized(mKernel, mHeaderHeight,  maxDragHeight);
        } else {
            mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * Set the ratio of the maximum height to drag footer.
     * ???????????????????????????Footer????????????????????????????????????????????????????????????
     * @param rate ratio = (the maximum height to drag footer)/(the height of footer)
     *             ?????? = ?????????????????? / Footer?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterMaxDragRate(float rate) {
        this.mFooterMaxDragRate = rate;
        if (mRefreshFooter != null && mAttachedToWindow) {
            final float maxDragHeight = mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate;
            mRefreshFooter.onInitialized(mKernel, mFooterHeight, (int) maxDragHeight);
        } else {
            mFooterHeightStatus = mFooterHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * Set the ratio at which the refresh is triggered.
     * ?????? ?????????????????? ??? HeaderHeight ?????????
     * @param rate ?????????????????? ??? HeaderHeight ?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderTriggerRate(float rate) {
        this.mHeaderTriggerRate = rate;
        return this;
    }

    /**
     * Set the ratio at which the load more is triggered.
     * ?????? ?????????????????? ??? FooterHeight ?????????
     * @param rate ?????????????????? ??? FooterHeight ?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterTriggerRate(float rate) {
        this.mFooterTriggerRate = rate;
        return this;
    }

    /**
     * Set the rebound interpolator.
     * ??????????????????????????? [?????????????????????,?????????????????????]
     * @param interpolator ???????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator) {
        this.mReboundInterpolator = interpolator;
        return this;
    }

    /**
     * Set the duration of the rebound animation.
     * ???????????????????????? [?????????????????????,?????????????????????]
     * @param duration ??????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setReboundDuration(int duration) {
        this.mReboundDuration = duration;
        return this;
    }

    /**
     * Set whether to enable pull-up loading more (enabled by default).
     * ??????????????????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableLoadMore(boolean enabled) {
        this.mManualLoadMore = true;
        this.mEnableLoadMore = enabled;
        return this;
    }

    /**
     * ??????????????????????????????????????????
     * @param enabled ????????????
     * @return SmartRefreshLayout
     */
    @Override
    public RefreshLayout setEnableRefresh(boolean enabled) {
        this.mEnableRefresh = enabled;
        return this;
    }

    /**
     * Whether to enable pull-down refresh (enabled by default).
     * ??????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableHeaderTranslationContent(boolean enabled) {
        this.mEnableHeaderTranslationContent = enabled;
        this.mManualHeaderTranslationContent = true;
        return this;
    }

    /**
     * Set whether to pull up the content while pulling up the header.
     * ???????????????????????? Footer ?????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableFooterTranslationContent(boolean enabled) {
        this.mEnableFooterTranslationContent = enabled;
        this.mManualFooterTranslationContent = true;
        return this;
    }

    /**
     * Sets whether to listen for the list to trigger a load event when scrolling to the bottom (default true).
     * ????????????????????????????????????????????????????????????????????????true???
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableAutoLoadMore(boolean enabled) {
        this.mEnableAutoLoadMore = enabled;
        return this;
    }

    /**
     * Set whether to enable cross-border rebound function.
     * ??????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableOverScrollBounce(boolean enabled) {
        this.mEnableOverScrollBounce = enabled;
        return this;
    }

    /**
     * Set whether to enable the pure scroll mode.
     * ?????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnablePureScrollMode(boolean enabled) {
        this.mEnablePureScrollMode = enabled;
        return this;
    }

    /**
     * Set whether to scroll the content to display new data after loading more complete.
     * ??????????????????????????????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableScrollContentWhenLoaded(boolean enabled) {
        this.mEnableScrollContentWhenLoaded = enabled;
        return this;
    }

    /**
     * Set whether to scroll the content to display new data after the refresh is complete.
     * ??????????????????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled) {
        this.mEnableScrollContentWhenRefreshed = enabled;
        return this;
    }

    /**
     * Set whether to pull up and load more when the content is not full of one page.
     * ?????????????????????????????????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled) {
        this.mEnableLoadMoreWhenContentNotFull = enabled;
        if (mRefreshContent != null) {
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(enabled);
        }
        return this;
    }

    /**
     * Set whether to enable cross-border drag (imitation iphone effect).
     * ???????????????????????????????????????????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableOverScrollDrag(boolean enabled) {
        this.mEnableOverScrollDrag = enabled;
        return this;
    }

    /**
     * Set whether or not Footer follows the content after there is no more data.
     * ??????????????????????????????????????? Footer ????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableFooterFollowWhenNoMoreData(boolean enabled) {
        this.mEnableFooterFollowWhenNoMoreData = enabled;
        return this;
    }

    /**
     * Set whether to clip header when the Header is in the FixedBehind state.
     * ?????????????????? Header ?????? FixedBehind ??????????????????????????? Header
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled) {
        this.mEnableClipHeaderWhenFixedBehind = enabled;
        return this;
    }

    /**
     * Set whether to clip footer when the Footer is in the FixedBehind state.
     * ?????????????????? Footer ?????? FixedBehind ??????????????????????????? Footer
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled) {
        this.mEnableClipFooterWhenFixedBehind = enabled;
        return this;
    }

    /**
     * Setting whether nesting scrolling is enabled (default off + smart on).
     * ??????????????????????????????????????????????????????+???????????????
     * @param enabled ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableNestedScroll(boolean enabled) {
        setNestedScrollingEnabled(enabled);
        return this;
    }
    /**
     * ??????????????? Header ???????????????Id???????????? Footer ??????????????????????????????????????????
     * @param id ????????????????????????Id
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFixedHeaderViewId(int id) {
        this.mFixedHeaderViewId = id;
        return this;
    }
    /**
     * ??????????????? Footer ???????????????Id???????????? Header ??????????????????????????????????????????
     * @param id ????????????????????????Id
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFixedFooterViewId(int id) {
        this.mFixedFooterViewId = id;
        return this;
    }
    /**
     * ????????? Header ?????????????????????????????????????????????Id???????????????????????????
     * @param id ????????????????????????Id
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setHeaderTranslationViewId(int id) {
        this.mHeaderTranslationViewId = id;
        return this;
    }
    /**
     * ????????? Footer ?????????????????????????????????????????????Id???????????????????????????
     * @param id ????????????????????????Id
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setFooterTranslationViewId(int id) {
        this.mFooterTranslationViewId = id;
        return this;
    }

//    /**
//     * Sets whether to enable pure nested scrolling mode
//     * Smart scrolling supports both [nested scrolling] and [traditional scrolling] modes
//     * With nested scrolling enabled, traditional mode also works when necessary
//     * However, sometimes interference and conflict can occur. If you find this conflict, you can try to turn on [pure nested scrolling] mode and [traditional mode] off
//     * ?????????????????????????????????????????????
//     * Smart ??????????????? ?????????????????? + ?????????????????? ????????????
//     * ????????? ?????????????????? ?????????????????????????????????????????????????????????????????????
//     * ???????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????????????????????????????????????????
//     * @param enabled ????????????
//     * @return RefreshLayout
//     */
//    @Override
//    public RefreshLayout setEnableNestedScrollOnly(boolean enabled) {
//        if (enabled && !mNestedChild.isNestedScrollingEnabled()) {
//            mNestedChild.setNestedScrollingEnabled(true);
//        }
//        mEnableNestedScrollingOnly = enabled;
//        return this;
//    }

    /**
     * Set whether to enable the action content view when refreshing.
     * ?????????????????????????????????????????????????????????
     * @param disable ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setDisableContentWhenRefresh(boolean disable) {
        this.mDisableContentWhenRefresh = disable;
        return this;
    }

    /**
     * Set whether to enable the action content view when loading.
     * ?????????????????????????????????????????????????????????
     * @param disable ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setDisableContentWhenLoading(boolean disable) {
        this.mDisableContentWhenLoading = disable;
        return this;
    }

    /**
     * Set the header of RefreshLayout.
     * ??????????????? Header
     * @param header RefreshHeader ?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header) {
        return setRefreshHeader(header, 0, 0);
    }

    /**
     * Set the header of RefreshLayout.
     * ??????????????? Header
     * @param header RefreshHeader ?????????
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height) {
        if (mRefreshHeader != null) {
            super.removeView(mRefreshHeader.getView());
        }
        this.mRefreshHeader = header;
        this.mHeaderBackgroundColor = 0;
        this.mHeaderNeedTouchEventWhenRefreshing = false;
        this.mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;//2020-5-23 ??????????????????????????????????????????????????????
        /*
         * 2020-3-16 ?????? header ????????? LayoutParams ????????????
         */
        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? WRAP_CONTENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = header.getView().getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }
        if (mRefreshHeader.getSpinnerStyle().front) {
            final ViewGroup thisGroup = this;
            super.addView(mRefreshHeader.getView(), thisGroup.getChildCount(), lp);
        } else {
            super.addView(mRefreshHeader.getView(), 0, lp);
        }
        if (mPrimaryColors != null && mRefreshHeader != null) {
            mRefreshHeader.setPrimaryColors(mPrimaryColors);
        }
        return this;
    }

    /**
     * Set the footer of RefreshLayout.
     * ??????????????? Footer
     * @param footer RefreshFooter ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer) {
        return setRefreshFooter(footer, 0, 0);
    }

    /**
     * Set the footer of RefreshLayout.
     * ??????????????? Footer
     * @param footer RefreshFooter ????????????
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height) {
        if (mRefreshFooter != null) {
            super.removeView(mRefreshFooter.getView());
        }
        this.mRefreshFooter = footer;
        this.mFooterLocked = false;
        this.mFooterBackgroundColor = 0;
        this.mFooterNoMoreDataEffective = false;
        this.mFooterNeedTouchEventWhenLoading = false;
        this.mFooterHeightStatus = DimensionStatus.DefaultUnNotify;//2020-5-23 ??????????????????????????????????????????????????????
        this.mEnableLoadMore = !mManualLoadMore || mEnableLoadMore;
        /*
         * 2020-3-16 ?????? header ????????? LayoutParams ????????????
         */
        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? WRAP_CONTENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = footer.getView().getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }
        if (mRefreshFooter.getSpinnerStyle().front) {
            final ViewGroup thisGroup = this;
            super.addView(mRefreshFooter.getView(), thisGroup.getChildCount(), lp);
        } else {
            super.addView(mRefreshFooter.getView(), 0, lp);
        }
        if (mPrimaryColors != null && mRefreshFooter != null) {
            mRefreshFooter.setPrimaryColors(mPrimaryColors);
        }
        return this;
    }

    /**
     * Set the content of RefreshLayout???Suitable for non-XML pages, not suitable for replacing empty layouts??????
     * ??????????????? Content???????????????XML???????????????????????????????????????
     * @param content View ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshContent(@NonNull View content) {
        return setRefreshContent(content, 0, 0);
    }

    /**
     * Set the content of RefreshLayout???Suitable for non-XML pages, not suitable for replacing empty layouts???.
     * ??????????????? Content???????????????XML???????????????????????????????????????
     * @param content View ????????????
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               ?????? ???????????? MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshContent(@NonNull View content, int width, int height) {
        final View thisView = this;
        if (mRefreshContent != null) {
            super.removeView(mRefreshContent.getView());
        }
        final ViewGroup thisGroup = this;

        /*
         * 2020-3-16 ?????? content ????????? LayoutParams ????????????
         */
        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? MATCH_PARENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = content.getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }

        super.addView(content, thisGroup.getChildCount(), lp);

        mRefreshContent = new RefreshContentWrapper(content);
        if (mAttachedToWindow) {
            View fixedHeaderView = thisView.findViewById(mFixedHeaderViewId);
            View fixedFooterView = thisView.findViewById(mFixedFooterViewId);

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);
        }

        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle().front) {
            super.bringChildToFront(mRefreshHeader.getView());
        }
        if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle().front) {
            super.bringChildToFront(mRefreshFooter.getView());
        }
        return this;
    }

    /**
     * Get footer of RefreshLayout
     * ???????????? Footer
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshFooter getRefreshFooter() {
        return mRefreshFooter instanceof RefreshFooter ? (RefreshFooter) mRefreshFooter : null;
    }

    /**
     * Get header of RefreshLayout
     * ???????????? Header
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshHeader getRefreshHeader() {
        return mRefreshHeader instanceof RefreshHeader ? (RefreshHeader) mRefreshHeader : null;
    }

    /**
     * Get the current state of RefreshLayout
     * ??????????????????
     * @return RefreshLayout
     */
    @NonNull
    @Override
    public RefreshState getState() {
        return mState;
    }

    /**
     * Get the ViewGroup of RefreshLayout
     * ????????????????????????
     * @return ViewGroup
     */
    @NonNull
    @Override
    public ViewGroup getLayout() {
        return this;
    }

    /**
     * Set refresh listener separately.
     * ???????????????????????????
     * @param listener OnRefreshListener ???????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
        return this;
    }

    /**
     * Set load more listener separately.
     * ???????????????????????????
     * @param listener OnLoadMoreListener ???????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * Set refresh and load listeners at the same time.
     * ????????????????????????????????????
     * @param listener OnRefreshLoadMoreListener ?????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        this.mRefreshListener = listener;
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * Set up a multi-function listener.
     * Recommended {@link SimpleBoundaryDecider}
     * ???????????????????????????
     * ???????????? {@link SimpleBoundaryDecider}
     * @param listener OnMultiListener ??????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnMultiListener(OnMultiListener listener) {
        this.mOnMultiListener = listener;
        return this;
    }

    /**
     * Set theme color int (primaryColor and accentColor).
     * ??????????????????
     * @param primaryColors ColorInt ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setPrimaryColors(@ColorInt int... primaryColors) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setPrimaryColors(primaryColors);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.setPrimaryColors(primaryColors);
        }
        mPrimaryColors = primaryColors;
        return this;
    }

    /**
     * Set theme color id (primaryColor and accentColor).
     * ??????????????????
     * @param primaryColorId ColorRes ????????????ID
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId) {
        final View thisView = this;
        final int[] colors = new int[primaryColorId.length];
        for (int i = 0; i < primaryColorId.length; i++) {
            colors[i] = ContextCompat.getColor(thisView.getContext(), primaryColorId[i]);
        }
        setPrimaryColors(colors);
        return this;
    }

    /**
     * Set the scroll boundary Decider, Can customize when you can refresh.
     * Recommended {@link SimpleBoundaryDecider}
     * ???????????????????????????
     * ???????????? {@link SimpleBoundaryDecider}
     * @param boundary ScrollBoundaryDecider ?????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary) {
        mScrollBoundaryDecider = boundary;
        if (mRefreshContent != null) {
            mRefreshContent.setScrollBoundaryDecider(boundary);
        }
        return this;
    }

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * ???????????????????????????????????????
     * @param noMoreData ?????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setNoMoreData(boolean noMoreData) {
        if (mState == RefreshState.Refreshing && noMoreData) {
            finishRefreshWithNoMoreData();
        } else if (mState == RefreshState.Loading && noMoreData) {
            finishLoadMoreWithNoMoreData();
        } else if (mFooterNoMoreData != noMoreData) {
            mFooterNoMoreData = noMoreData;
            if (mRefreshFooter instanceof RefreshFooter) {
                if (((RefreshFooter) mRefreshFooter).setNoMoreData(noMoreData)) {
                    mFooterNoMoreDataEffective = true;
                    if (mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mSpinner > 0
                            && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate
                            && isEnableRefreshOrLoadMore(mEnableLoadMore)
                            && isEnableTranslationContent(mEnableRefresh, mRefreshHeader)) {
                        mRefreshFooter.getView().setTranslationY(mSpinner);
                    }
                } else {
                    mFooterNoMoreDataEffective = false;
                    String msg = "Footer:" + mRefreshFooter + " NoMoreData is not supported.(?????????NoMoreData????????????[ClassicsFooter]??????[?????????Footer?????????setNoMoreData???????????????true])";
                    Throwable e = new RuntimeException(msg);
                    e.printStackTrace();
                }
            }

        }
        return this;
    }

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * ???????????????????????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout resetNoMoreData() {
        return setNoMoreData(false);
    }

    /**
     * finish refresh.
     * ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishRefresh() {
        return finishRefresh(true);
    }

    /**
     * finish load more.
     * ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishLoadMore() {
        return finishLoadMore(true);
    }

    /**
     * finish refresh.
     * ????????????
     * @param delayed ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishRefresh(int delayed) {
        return finishRefresh(delayed, true, Boolean.FALSE);
    }

    /**
     * finish refresh.
     * ????????????
     * @param success ???????????????????????? ?????????????????????????????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishRefresh(boolean success) {
        if (success) {
            long passTime = System.currentTimeMillis() - mLastOpenTime;
            int delayed = (Math.min(Math.max(0, 300 - (int) passTime), 300) << 16);//?????????????????????300???????????????
            return finishRefresh(delayed, true, Boolean.FALSE);
        } else {
            return finishRefresh(0, false, null);
        }
    }

    /**
     * finish refresh.
     * ????????????
     *
     * @param delayed ????????????
     * @param success ???????????????????????? ?????????????????????????????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishRefresh(final int delayed, final boolean success, final Boolean noMoreData) {
        final int more = delayed >> 16;//??????????????????
        int delay = delayed << 16 >> 16;//??????????????????
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count == 0) {
                    if (mState == RefreshState.None && mViceState == RefreshState.Refreshing) {
                        //autoRefresh ???????????????????????????
                        mViceState = RefreshState.None;
                    } else if (reboundAnimator != null && mState.isHeader && (mState.isDragging || mState == RefreshState.RefreshReleased)) {
                        //autoRefresh ???????????????????????????
                        //mViceState = RefreshState.None;
                        /*
                         * 2020-3-15 BUG??????
                         * https://github.com/scwang90/SmartRefreshLayout/issues/1019
                         * ?????? autoRefresh ?????? cancel ?????? end ?????? ?????? ????????????????????????
                         */
                        reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                        reboundAnimator.cancel();//????????? cancel ??? end ??????
                        reboundAnimator = null;
                        /*
                         * 2020-1-4 BUG??????
                         * https://github.com/scwang90/SmartRefreshLayout/issues/1104
                         * ????????????????????? PullDownToRefresh ?????? mSpinner != 0
                         * mKernel.setState(RefreshState.None); ??????????????? animSpinner(0); ????????????
                         * ?????? PullDownToRefresh ?????? isDragging ?????????animSpinner(0); ???????????? None ??????
                         * ???????????? PullDownToRefresh ??????????????????????????? overSpinner(); ????????????
                         */
                        if (mKernel.animSpinner(0) == null) {
                            notifyStateChanged(RefreshState.None);
                        } else {
                            notifyStateChanged(RefreshState.PullDownCanceled);
                        }
//                      mKernel.setState(RefreshState.None);
                    } else if (mState == RefreshState.Refreshing && mRefreshHeader != null && mRefreshContent != null) {
                        count++;
                        mHandler.postDelayed(this, more);
                        //???????????? ????????? RefreshFinish ?????? postDelayed ?????? finishRefresh ????????????????????? state ?????? Refreshing
                        notifyStateChanged(RefreshState.RefreshFinish);
                        if (noMoreData == Boolean.FALSE) {
                            setNoMoreData(false);//????????????????????????????????????????????? noMoreData
                        }
                    }
                    if (noMoreData == Boolean.TRUE) {
                        setNoMoreData(true);
                    }
                } else {
                    int startDelay = mRefreshHeader.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiListener != null && mRefreshHeader instanceof RefreshHeader) {
                        mOnMultiListener.onHeaderFinish((RefreshHeader) mRefreshHeader, success);
                    }
                    //startDelay < Integer.MAX_VALUE ?????? ?????? startDelay ?????????????????????????????????
                    if (startDelay < Integer.MAX_VALUE) {
                        //??????????????????????????????????????????????????? ???????????????????????????????????????????????????????????????????????????
                        if (mIsBeingDragged || mNestedInProgress) {
                            long time = System.currentTimeMillis();
                            if (mIsBeingDragged) {
                                mTouchY = mLastTouchY;
                                mTouchSpinner = 0;
                                mIsBeingDragged = false;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mLastTouchY + mSpinner - mTouchSlop * 2, 0));
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mLastTouchY + mSpinner, 0));
                            }
                            if (mNestedInProgress) {
                                mTotalUnconsumed = 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_UP, mLastTouchX, mLastTouchY, 0));
                                mNestedInProgress = false;
                                mTouchSpinner = 0;
                            }
                        }
                        if (mSpinner > 0) {
                            AnimatorUpdateListener updateListener = null;
                            ValueAnimator valueAnimator = animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                            if (mEnableScrollContentWhenRefreshed) {
                                updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                            }
                            if (valueAnimator != null && updateListener != null) {
                                valueAnimator.addUpdateListener(updateListener);
                            }
                        } else if (mSpinner < 0) {
                            animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                        } else {
                            mKernel.moveSpinner(0, false);
//                            resetStatus();
                            mKernel.setState(RefreshState.None);
                        }
                    }
                }
            }
        };
        if (delay > 0) {
            mHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
        return this;
    }

    /**
     * finish load more with no more data.
     * ???????????????????????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishRefreshWithNoMoreData() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishRefresh((Math.min(Math.max(0, 300 - (int) passTime), 300) << 16), true, Boolean.TRUE);
    }

    /**
     * finish load more.
     * ????????????
     * @param delayed ????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishLoadMore(int delayed) {
        return finishLoadMore(delayed, true, false);
    }

    /**
     * finish load more.
     * ????????????
     * @param success ??????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishLoadMore(boolean success) {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(success ? (Math.min(Math.max(0, 300 - (int) passTime), 300) << 16) : 0, success, false);
    }

    /**
     * finish load more.
     * ????????????
     * @param delayed ????????????
     * @param success ??????????????????
     * @param noMoreData ?????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishLoadMore(final int delayed, final boolean success, final boolean noMoreData) {
        final int more = delayed >> 16;//??????????????????
        int delay = delayed << 16 >> 16;//??????????????????
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count == 0) {
                    if (mState == RefreshState.None && mViceState == RefreshState.Loading) {
                        //autoLoadMore ???????????????????????????
                        mViceState = RefreshState.None;
                    } else if (reboundAnimator != null && (mState.isDragging || mState == RefreshState.LoadReleased) && mState.isFooter) {
                        //autoLoadMore ???????????????????????????
                        /*
                         * 2020-3-15 BUG??????
                         * https://github.com/scwang90/SmartRefreshLayout/issues/1019
                         * ?????? autoRefresh ?????? cancel ?????? end ?????? ?????? ????????????????????????
                         */
                        reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                        reboundAnimator.cancel();//????????? cancel ??? end ??????
                        reboundAnimator = null;
                        /*
                         * 2020-1-4 BUG??????
                         * https://github.com/scwang90/SmartRefreshLayout/issues/1104
                         * ????????????????????? PullDownToRefresh ?????? mSpinner != 0
                         * mKernel.setState(RefreshState.None); ??????????????? animSpinner(0); ????????????
                         * ?????? PullDownToRefresh ?????? isDragging ?????????animSpinner(0); ???????????? None ??????
                         * ???????????? PullDownToRefresh ??????????????????????????? overSpinner(); ????????????
                         */
                        if (mKernel.animSpinner(0) == null) {
                            notifyStateChanged(RefreshState.None);
                        } else {
                            notifyStateChanged(RefreshState.PullUpCanceled);
                        }
                        //mKernel.setState(RefreshState.None);
                    } else if (mState == RefreshState.Loading && mRefreshFooter != null && mRefreshContent != null) {
                        count++;
                        mHandler.postDelayed(this, more);
                        //???????????? ????????? LoadFinish ?????? postDelayed ?????? finishLoadMore ????????????????????? state ?????? Loading
                        notifyStateChanged(RefreshState.LoadFinish);
                        return;
                    }
                    if (noMoreData) {
                        setNoMoreData(true);
                    }
                } else {
                    final int startDelay = mRefreshFooter.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiListener != null && mRefreshFooter instanceof RefreshFooter) {
                        mOnMultiListener.onFooterFinish((RefreshFooter) mRefreshFooter, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        //????????????????????????????????????
                        final boolean needHoldFooter = noMoreData && mEnableFooterFollowWhenNoMoreData && mSpinner < 0 && mRefreshContent.canLoadMore();
                        final int offset = mSpinner - (needHoldFooter ? Math.max(mSpinner,-mFooterHeight) : 0);
                        //???????????????????????????????????????????????????
                        if (mIsBeingDragged || mNestedInProgress) {
                            final long time = System.currentTimeMillis();
                            if (mIsBeingDragged) {
                                mTouchY = mLastTouchY;
                                mTouchSpinner = mSpinner - offset;
                                mIsBeingDragged = false;
                                int offsetY = mEnableFooterTranslationContent ? offset : 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mLastTouchY + offsetY + mTouchSlop * 2, 0));
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mLastTouchY + offsetY, 0));
                            }
                            if (mNestedInProgress) {
                                mTotalUnconsumed = 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_UP, mLastTouchX, mLastTouchY, 0));
                                mNestedInProgress = false;
                                mTouchSpinner = 0;
                            }
                        }
                        //??????????????????????????????
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUpdateListener updateListener = null;
                                if (mEnableScrollContentWhenLoaded && offset < 0) {
                                    updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                                    if (updateListener != null) {//???????????????????????????????????????
                                        updateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0));//????????????, Footer ?????????
                                    }
                                }
                                ValueAnimator animator = null;//?????????????????????????????????
                                AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation != null && animation.getDuration() == 0) {
                                            return;//0 ???????????????
                                        }
                                        mFooterLocked = false;
                                        if (noMoreData) {
                                            setNoMoreData(true);
                                        }
                                        if (mState == RefreshState.LoadFinish) {
                                            notifyStateChanged(RefreshState.None);
                                        }
                                    }
                                };
                                if (mSpinner > 0) { //??????0????????????, ?????? Header ??????, Footer ?????????
                                    animator = mKernel.animSpinner(0);//?????? Header ??????????????????
                                } else if (updateListener != null || mSpinner == 0) {//?????? Header ??? Footer ???????????? ???????????????????????????????????????
                                    if (reboundAnimator != null) {
                                        reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                                        reboundAnimator.cancel();//????????? cancel ??? end ??????
                                        reboundAnimator = null;//???????????????????????????
                                    }
                                    //???????????? Header ?????? Header ???????????????
                                    mKernel.moveSpinner(0, false);
                                    mKernel.setState(RefreshState.None);
                                } else {//???????????????????????????Footer
                                    if (noMoreData && mEnableFooterFollowWhenNoMoreData) {//????????????????????????????????????
                                        if (mSpinner >= -mFooterHeight) {//?????? Footer ???????????????????????????
                                            notifyStateChanged(RefreshState.None);//????????????????????????,????????? Footer
                                        } else {//?????? Footer ??????????????? Footer ???????????? (?????????????????????????????????, ?????????????????? Footer ?????????????????????????????????)
                                            animator = mKernel.animSpinner(-mFooterHeight);//??????????????? Footer ???????????????????????????
                                        }
                                    } else {
                                        animator = mKernel.animSpinner(0);//?????????????????? Footer
                                    }
                                }
                                if (animator != null) {
                                    animator.addListener(listenerAdapter);//????????????????????????,????????????????????????
                                } else {
                                    listenerAdapter.onAnimationEnd(null);//??????????????????,????????????????????????(????????????)
                                }
                            }
                        }, mSpinner < 0 ? startDelay : 0);
                    }
                }
            }
        };
        if (delay > 0) {
            mHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
        return this;
    }

    /**
     * finish load more with no more data.
     * ???????????????????????????????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout finishLoadMoreWithNoMoreData() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore((Math.min(Math.max(0, 300 - (int) passTime), 300) << 16), true, true);
    }

    /**
     * Close the Header or Footer, can't replace finishRefresh and finishLoadMore.
     * ?????? Header ?????? Footer
     * ?????????
     * 1.closeHeaderOrFooter ????????????????????????????????????  header ??? footer
     * 2.finishRefresh ??? finishLoadMore ????????? ?????? ?????? ?????? ???????????????
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout closeHeaderOrFooter() {
        if (mState == RefreshState.None && (mViceState == RefreshState.Refreshing || mViceState == RefreshState.Loading)) {
            //autoRefresh autoLoadMore ???????????????????????????
            mViceState = RefreshState.None;
        }
        if (mState == RefreshState.Refreshing) {
            finishRefresh();
        } else if (mState == RefreshState.Loading) {
            finishLoadMore();
        } else {
            /*
             * 2020-3-15 closeHeaderOrFooter ??????????????????
             * ?????? FalsifyHeader ????????????
             * ?????? FalsifyFooter ????????????
             */
            if (mKernel.animSpinner(0) == null) {
                notifyStateChanged(RefreshState.None);
            } else {
                if (mState.isHeader) {
                    notifyStateChanged(RefreshState.PullDownCanceled);
                } else {
                    notifyStateChanged(RefreshState.PullUpCanceled);
                }
            }
        }
        return this;
    }

    /**
     * Display refresh animation and trigger refresh event.
     * ??????????????????????????????????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoRefresh() {
        return autoRefresh(mAttachedToWindow ? 0 : 400, mReboundDuration, (mHeaderMaxDragRate + mHeaderTriggerRate) / 2, false);
    }

    /**
     * Display refresh animation and trigger refresh event, Delayed start.
     * ?????????????????????????????????????????????????????????
     * @param delayed ????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoRefresh(int delayed) {
        return autoRefresh(delayed, mReboundDuration, (mHeaderMaxDragRate + mHeaderTriggerRate) / 2, false);
    }


    /**
     * Display refresh animation without triggering events.
     * ????????????????????????????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoRefreshAnimationOnly() {
        return autoRefresh(mAttachedToWindow ? 0 : 400, mReboundDuration, (mHeaderMaxDragRate + mHeaderTriggerRate) / 2, true);
    }

    /**
     * Display refresh animation, Multifunction.
     * ??????????????????????????????????????????
     * @param delayed ????????????
     * @param duration ????????????????????????
     * @param dragRate ?????????????????????
     * @param animationOnly animation only ????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoRefresh(int delayed, final int duration, final float dragRate,final boolean animationOnly) {
        if (mState == RefreshState.None && isEnableRefreshOrLoadMore(mEnableRefresh)) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mViceState != RefreshState.Refreshing) return;
                    if (reboundAnimator != null) {
                        reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                        reboundAnimator.cancel();//????????? cancel ??? end ??????
                        reboundAnimator = null;
                    }

                    final View thisView = SmartRefreshLayout.this;
                    mLastTouchX = thisView.getMeasuredWidth() / 2f;
                    mKernel.setState(RefreshState.PullDownToRefresh);

                    final float height = mHeaderHeight == 0 ? mHeaderTriggerRate : mHeaderHeight;
                    final float dragHeight = dragRate < 10 ? height * dragRate : dragRate;
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, (int) (dragHeight));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID));
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (reboundAnimator != null && mRefreshHeader != null) {
                                mKernel.moveSpinner((int) animation.getAnimatedValue(), true);
                            }
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (animation != null && animation.getDuration() == 0) {
                                return;//0 ???????????????
                            }
                            reboundAnimator = null;
                            if (mRefreshHeader != null) {
                                if (mState != RefreshState.ReleaseToRefresh) {
                                    mKernel.setState(RefreshState.ReleaseToRefresh);
                                }
                                setStateRefreshing(!animationOnly);
                            } else {
                                /*
                                 * 2019-12-24 ?????? mRefreshHeader=null ?????????????????????
                                 */
                                mKernel.setState(RefreshState.None);
                            }
                        }
                    });
                    reboundAnimator.start();
                }
            };
            setViceState(RefreshState.Refreshing);
            if (delayed > 0) {
                mHandler.postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Display load more animation and trigger load more event.
     * ??????????????????????????????????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoLoadMore() {
        return autoLoadMore(0, mReboundDuration, (mFooterMaxDragRate + mFooterTriggerRate) / 2, false);
    }

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * ??????????????????????????????????????????, ????????????
     * @param delayed ????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoLoadMore(int delayed) {
        return autoLoadMore(delayed, mReboundDuration, (mFooterMaxDragRate + mFooterTriggerRate) / 2, false);
    }

    /**
     * Display load more animation without triggering events.
     * ????????????????????????????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoLoadMoreAnimationOnly() {
        return autoLoadMore(0, mReboundDuration, (mFooterMaxDragRate + mFooterTriggerRate) / 2, true);
    }

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * ??????????????????, ???????????????
     * @param delayed ????????????
     * @param duration ????????????????????????
     * @param dragRate ?????????????????????
     * @return true or false, Status non-compliance will fail.
     *         ??????????????????????????????????????????
     */
    @Override
    public boolean autoLoadMore(int delayed, final int duration, final float dragRate, final boolean animationOnly) {
        if (mState == RefreshState.None && (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mFooterNoMoreData)) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mViceState != RefreshState.Loading)return;
                    if (reboundAnimator != null) {
                        reboundAnimator.setDuration(0);//cancel?????????End?????????????????????0??????????????????cancel
                        reboundAnimator.cancel();//????????? cancel ??? end ??????
                        reboundAnimator = null;
                    }

                    final View thisView = SmartRefreshLayout.this;
                    mLastTouchX = thisView.getMeasuredWidth() / 2f;
                    mKernel.setState(RefreshState.PullUpToLoad);

                    final float height = mFooterHeight == 0 ? mFooterTriggerRate : mFooterHeight;
                    final float dragHeight = dragRate < 10 ? dragRate * height : dragRate;
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, - (int) (dragHeight));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID));
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (reboundAnimator != null && mRefreshFooter != null) {
                                mKernel.moveSpinner((int) animation.getAnimatedValue(), true);
                            }
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (animation != null && animation.getDuration() == 0) {
                                return;//0 ???????????????
                            }
                            reboundAnimator = null;
                            if (mRefreshFooter != null) {
                                if (mState != RefreshState.ReleaseToLoad) {
                                    mKernel.setState(RefreshState.ReleaseToLoad);
                                }
                                setStateLoading(!animationOnly);
                            } else {
                                /*
                                 * 2019-12-24 ?????? mRefreshFooter=null ?????????????????????
                                 */
                                mKernel.setState(RefreshState.None);
                            }
                        }
                    });
                    reboundAnimator.start();
                }
            };
            setViceState(RefreshState.Loading);
            if (delayed > 0) {
                mHandler.postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * ???????????? Header ?????????
     * @param creator Header?????????
     */
    public static void setDefaultRefreshHeaderCreator(@NonNull DefaultRefreshHeaderCreator creator) {
        sHeaderCreator = creator;
    }

    /**
     * ???????????? Footer ?????????
     * @param creator Footer?????????
     */
    public static void setDefaultRefreshFooterCreator(@NonNull DefaultRefreshFooterCreator creator) {
        sFooterCreator = creator;
    }

    /**
     * ???????????? Refresh ????????????
     * @param initializer ??????????????????
     */
    public static void setDefaultRefreshInitializer(@NonNull DefaultRefreshInitializer initializer) {
        sRefreshInitializer = initializer;
    }

    //<editor-fold desc="?????????API">

    /**
     * ??????????????????
     * @return ??????????????????
     */
    @Override
    public boolean isRefreshing() {
        return mState == RefreshState.Refreshing;
    }

    /**
     * ??????????????????
     * @return ??????????????????
     */
    @Override
    public boolean isLoading() {
        return mState == RefreshState.Loading;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="???????????? RefreshKernel">

    /**
     * ??????????????????????????????
     * ?????????????????? Header ?????? Footer ???????????????
     */
    public class RefreshKernelImpl implements RefreshKernel {

        @NonNull
        @Override
        public RefreshLayout getRefreshLayout() {
            return SmartRefreshLayout.this;
        }

        @NonNull
        @Override
        public RefreshContent getRefreshContent() {
            return mRefreshContent;
        }

        //<editor-fold desc="???????????? state changes">
        @Override
        public RefreshKernel setState(@NonNull RefreshState state) {
            switch (state) {
                case None:
                    if (mState != RefreshState.None && mSpinner == 0) {
                        notifyStateChanged(RefreshState.None);
                    } else if (mSpinner != 0) {
                        animSpinner(0);
                    }
                    break;
                case PullDownToRefresh:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.PullDownToRefresh);
                    } else {
                        setViceState(RefreshState.PullDownToRefresh);
                    }
                    break;
                case PullUpToLoad:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !mState.isFinishing && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mFooterNoMoreDataEffective)) {
                        notifyStateChanged(RefreshState.PullUpToLoad);
                    } else {
                        setViceState(RefreshState.PullUpToLoad);
                    }
                    break;
                case PullDownCanceled:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.PullDownCanceled);
//                        resetStatus();
                        setState(RefreshState.None);
                    } else {
                        setViceState(RefreshState.PullDownCanceled);
                    }
                    break;
                case PullUpCanceled:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mFooterNoMoreDataEffective)) {
                        notifyStateChanged(RefreshState.PullUpCanceled);
//                        resetStatus();
                        setState(RefreshState.None);
                    } else {
                        setViceState(RefreshState.PullUpCanceled);
                    }
                    break;
                case ReleaseToRefresh:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.ReleaseToRefresh);
                    } else {
                        setViceState(RefreshState.ReleaseToRefresh);
                    }
                    break;
                case ReleaseToLoad:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !mState.isFinishing && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && mFooterNoMoreDataEffective)) {
                        notifyStateChanged(RefreshState.ReleaseToLoad);
                    } else {
                        setViceState(RefreshState.ReleaseToLoad);
                    }
                    break;
                case ReleaseToTwoLevel: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.ReleaseToTwoLevel);
                    } else {
                        setViceState(RefreshState.ReleaseToTwoLevel);
                    }
                    break;
                }
                case RefreshReleased: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.RefreshReleased);
                    } else {
                        setViceState(RefreshState.RefreshReleased);
                    }
                    break;
                }
                case LoadReleased: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                        notifyStateChanged(RefreshState.LoadReleased);
                    } else {
                        setViceState(RefreshState.LoadReleased);
                    }
                    break;
                }
                case Refreshing:
                    setStateRefreshing(true);
                    break;
                case Loading:
                    setStateLoading(true);
                    break;
                default:
                    notifyStateChanged(state);
                    break;
//                case RefreshFinish: {
//                    if (mState == RefreshState.Refreshing) {
//                        notifyStateChanged(RefreshState.RefreshFinish);
//                    }
//                    break;
//                }
//                case LoadFinish:{
//                    if (mState == RefreshState.Loading) {
//                        notifyStateChanged(RefreshState.LoadFinish);
//                    }
//                    break;
//                }
//                case TwoLevelReleased:
//                    notifyStateChanged(RefreshState.TwoLevelReleased);
//                    break;
//                case TwoLevelFinish:
//                    notifyStateChanged(RefreshState.TwoLevelFinish);
//                    break;
//                case TwoLevel:
//                    notifyStateChanged(RefreshState.TwoLevel);
//                    break;
            }
            return null;
        }

        @Override
        public RefreshKernel startTwoLevel(boolean open) {
            if (open) {
                AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation != null && animation.getDuration() == 0) {
                            return;//0 ???????????????
                        }
                        mKernel.setState(RefreshState.TwoLevel);
                    }
                };
                final View thisView = SmartRefreshLayout.this;
                ValueAnimator animator = animSpinner(thisView.getMeasuredHeight());
                if (animator != null && animator == reboundAnimator) {
                    animator.setDuration(mFloorDuration);
                    animator.addListener(listener);
                } else {
                    listener.onAnimationEnd(null);
                }
            } else {
                if (animSpinner(0) == null) {
                    notifyStateChanged(RefreshState.None);
                }
            }
            return this;
        }

        @Override
        public RefreshKernel finishTwoLevel() {
            if (mState == RefreshState.TwoLevel) {
                mKernel.setState(RefreshState.TwoLevelFinish);
                if (mSpinner == 0) {
                    moveSpinner(0, false);
                    notifyStateChanged(RefreshState.None);
                } else {
                    animSpinner(0).setDuration(mFloorDuration);
                }
            }
            return this;
        }
        //</editor-fold>

        //<editor-fold desc="???????????? Spinner">

        /**
         * ???????????? Scroll
         * moveSpinner ??????????????? ??????????????? { android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
         * moveSpinner The name comes from { android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
         * @param spinner ?????? spinner
         * @param isDragging ??????????????????????????????
         *                   ?????????finishRefresh???finishLoadMore???overSpinner ???????????????????????? false
         *                   dispatchTouchEvent , nestScroll ????????? true
         *                   autoRefresh???autoLoadMore?????????????????????????????? true
         */
        public RefreshKernel moveSpinner(final int spinner, final boolean isDragging) {
            if (mSpinner == spinner
                    && (mRefreshHeader == null || !mRefreshHeader.isSupportHorizontalDrag())
                    && (mRefreshFooter == null || !mRefreshFooter.isSupportHorizontalDrag())) {
                return this;
            }
            final View thisView = SmartRefreshLayout.this;
            final int oldSpinner = mSpinner;
            mSpinner = spinner;
            // ?????? mViceState.isDragging ????????????????????? isDragging ????????????????????????????????? autoRefresh ??????
            //
            if (isDragging && (mViceState.isDragging || mViceState.isOpening)) {
                if (mSpinner > (mHeaderTriggerRate < 10 ? mHeaderHeight * mHeaderTriggerRate : mHeaderTriggerRate)) {
                    if (mState != RefreshState.ReleaseToTwoLevel) {
                        mKernel.setState(RefreshState.ReleaseToRefresh);
                    }
                } else if (-mSpinner > (mFooterTriggerRate < 10 ? mFooterHeight * mFooterTriggerRate : mFooterTriggerRate) && !mFooterNoMoreData) {
                    mKernel.setState(RefreshState.ReleaseToLoad);
                } else if (mSpinner < 0 && !mFooterNoMoreData) {
                    mKernel.setState(RefreshState.PullUpToLoad);
                } else if (mSpinner > 0) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                }
            }
            if (mRefreshContent != null) {
                int tSpinner = 0;
                boolean changed = false;
                /*
                 * 2019-12-25 ?????? 2.0 ????????????????????? Header Footer ??????????????????????????????
                 */
                if (spinner >= 0 /*&& mRefreshHeader != null*/) {
                    if (isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader)) {
                        changed = true;
                        tSpinner = spinner;
                    } else if (oldSpinner < 0) {
                        changed = true;
                        tSpinner = 0;
                    }
                }
                /*
                 * 2019-12-25 ?????? 2.0 ????????????????????? Header Footer ??????????????????????????????
                 */
                if (spinner <= 0 /*&& mRefreshFooter != null*/) {
                    if (isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter)) {
                        changed = true;
                        tSpinner = spinner;
                    } else if (oldSpinner > 0) {
                        changed = true;
                        tSpinner = 0;
                    }
                }
                if (changed) {
                    mRefreshContent.moveSpinner(tSpinner, mHeaderTranslationViewId, mFooterTranslationViewId);
                    if (mFooterNoMoreData && mFooterNoMoreDataEffective && mEnableFooterFollowWhenNoMoreData
                            && mRefreshFooter instanceof RefreshFooter && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate
                            && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                        mRefreshFooter.getView().setTranslationY(Math.max(0, tSpinner));
                    }
                    boolean header = mEnableClipHeaderWhenFixedBehind && mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                    header = header || mHeaderBackgroundColor != 0;
                    boolean footer = mEnableClipFooterWhenFixedBehind && mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                    footer = footer || mFooterBackgroundColor != 0;
                    if ((header && (tSpinner >= 0 || oldSpinner > 0)) || (footer && (tSpinner <= 0 || oldSpinner < 0))) {
                        thisView.invalidate();
                    }
                }
            }
            if ((spinner >= 0 || oldSpinner > 0) && mRefreshHeader != null) {

                final int offset = Math.max(spinner, 0);
                final int headerHeight = mHeaderHeight;
                final int maxDragHeight = (int)(mHeaderMaxDragRate < 10 ? mHeaderHeight * mHeaderMaxDragRate : mHeaderMaxDragRate);
                final float percent = 1f * offset / (mHeaderTriggerRate < 10 ? mHeaderTriggerRate * mHeaderHeight : mHeaderTriggerRate);
                //????????????????????? finish ??????????????? enable=false ??????????????????????????? state ???????????????
                if (isEnableRefreshOrLoadMore(mEnableRefresh) || (mState == RefreshState.RefreshFinish && !isDragging)) {
                    if (oldSpinner != mSpinner) {
                        if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                            mRefreshHeader.getView().setTranslationY(mSpinner);
                            if (mHeaderBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(mEnableHeaderTranslationContent,mRefreshHeader)) {
                                thisView.invalidate();
                            }
                        } else if (mRefreshHeader.getSpinnerStyle().scale){
                            /*
                             * ?????? MotionLayout 2019-6-18
                             * ??? MotionLayout ?????? requestLayout ??????
                             * ?????? ???????????? layout ??????
                             * https://github.com/scwang90/SmartRefreshLayout/issues/944
                             */
//                            mRefreshHeader.getView().requestLayout();
                            View headerView = mRefreshHeader.getView();
                            final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                            final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                            final int widthSpec = makeMeasureSpec(headerView.getMeasuredWidth(), EXACTLY);
                            headerView.measure(widthSpec, makeMeasureSpec(Math.max(mSpinner - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                            final int left = mlp.leftMargin;
                            final int top = mlp.topMargin + mHeaderInsetStart;
                            headerView.layout(left, top, left + headerView.getMeasuredWidth(), top + headerView.getMeasuredHeight());
                        }
                        mRefreshHeader.onMoving(isDragging, percent, offset, headerHeight, maxDragHeight);
                    }
                    if (isDragging && mRefreshHeader.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != mSpinner && mOnMultiListener != null && mRefreshHeader instanceof RefreshHeader) {
                    mOnMultiListener.onHeaderMoving((RefreshHeader) mRefreshHeader, isDragging, percent, offset, headerHeight, maxDragHeight);
                }

            }
            if ((spinner <= 0 || oldSpinner < 0) && mRefreshFooter != null) {

                final int offset = -Math.min(spinner, 0);
                final int footerHeight = mFooterHeight;
                final int maxDragHeight = (int) (mFooterMaxDragRate < 10 ? mFooterHeight * mFooterMaxDragRate : mFooterMaxDragRate);
                final float percent = offset * 1f / (mFooterTriggerRate < 10 ? mFooterTriggerRate * mFooterHeight : mFooterTriggerRate);

                if (isEnableRefreshOrLoadMore(mEnableLoadMore) || (mState == RefreshState.LoadFinish && !isDragging)) {
                    if (oldSpinner != mSpinner) {
                        if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                            mRefreshFooter.getView().setTranslationY(mSpinner);
                            if (mFooterBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter)) {
                                thisView.invalidate();
                            }
                        } else if (mRefreshFooter.getSpinnerStyle().scale){
                            /*
                             * ?????? MotionLayout 2019-6-18
                             * ??? MotionLayout ?????? requestLayout ??????
                             * ?????? ???????????? layout ??????
                             * https://github.com/scwang90/SmartRefreshLayout/issues/944
                             */
//                            mRefreshFooter.getView().requestLayout();
                            View footerView = mRefreshFooter.getView();
                            final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                            final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                            final int widthSpec = makeMeasureSpec(footerView.getMeasuredWidth(), EXACTLY);
                            footerView.measure(widthSpec, makeMeasureSpec(Math.max(-mSpinner - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                            final int left = mlp.leftMargin;
                            final int bottom = mlp.topMargin + thisView.getMeasuredHeight() - mFooterInsetStart;
                            footerView.layout(left, bottom - footerView.getMeasuredHeight(), left + footerView.getMeasuredWidth(), bottom);
                        }
                        mRefreshFooter.onMoving(isDragging, percent, offset, footerHeight, maxDragHeight);
                    }
                    if (isDragging && mRefreshFooter.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != mSpinner && mOnMultiListener != null && mRefreshFooter instanceof RefreshFooter) {
                    mOnMultiListener.onFooterMoving((RefreshFooter)mRefreshFooter, isDragging, percent, offset, footerHeight, maxDragHeight);
                }
            }
            return this;
        }

        public ValueAnimator animSpinner(int endSpinner) {
            return SmartRefreshLayout.this.animSpinner(endSpinner, 0, mReboundInterpolator, mReboundDuration);
        }
        //</editor-fold>

        //<editor-fold desc="????????????">

        @Override
        public RefreshKernel requestDrawBackgroundFor(@NonNull RefreshComponent internal, int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            if (internal.equals(mRefreshHeader)) {
                mHeaderBackgroundColor = backgroundColor;
            } else if (internal.equals(mRefreshFooter)) {
                mFooterBackgroundColor = backgroundColor;
            }
            return this;
        }

        @Override
        public RefreshKernel requestNeedTouchEventFor(@NonNull RefreshComponent internal, boolean request) {
            if (internal.equals(mRefreshHeader)) {
                mHeaderNeedTouchEventWhenRefreshing = request;
            } else if (internal.equals(mRefreshFooter)) {
                mFooterNeedTouchEventWhenLoading = request;
            }
            return this;
        }

        @Override
        public RefreshKernel requestDefaultTranslationContentFor(@NonNull RefreshComponent internal, boolean translation) {
            if (internal.equals(mRefreshHeader)) {
                if (!mManualHeaderTranslationContent) {
                    mManualHeaderTranslationContent = true;
                    mEnableHeaderTranslationContent = translation;
                }
            } else if (internal.equals(mRefreshFooter)) {
                if (!mManualFooterTranslationContent) {
                    mManualFooterTranslationContent = true;
                    mEnableFooterTranslationContent = translation;
                }
            }
            return this;
        }
        @Override
        public RefreshKernel requestRemeasureHeightFor(@NonNull RefreshComponent internal) {
            if (internal.equals(mRefreshHeader)) {
                if (mHeaderHeightStatus.notified) {
                    mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
                }
            } else if (internal.equals(mRefreshFooter)) {
                if (mFooterHeightStatus.notified) {
                    mFooterHeightStatus = mFooterHeightStatus.unNotify();
                }
            }
            return this;
        }
        @Override
        public RefreshKernel requestFloorDuration(int duration) {
            mFloorDuration = duration;
            return this;
        }

        @Override
        public RefreshKernel requestFloorBottomPullUpToCloseRate(float rate) {
            mTwoLevelBottomPullUpToCloseRate = rate;
            return this;
        }
        //</editor-fold>
    }
    //</editor-fold>

}

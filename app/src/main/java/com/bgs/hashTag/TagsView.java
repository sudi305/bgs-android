package com.bgs.hashTag;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bgs.dheket.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TagsView extends RelativeLayout implements TagsEditText.InputConnectionWrapperInterface {

    private static final String TAG = "TagsView";

    private static final int Tags_HEIGHT = 33;
    private static final int TEXT_EXTRA_TOP_MARGIN = 4;
    public static final int Tags_BOTTOM_PADDING = 1;

    // RES --------------------------------------------------

    private int mTagsBgRes = com.bgs.dheket.R.drawable.chip_background;

    // ------------------------------------------------------

    private int mTagsColor;
    private int mTagsColorClicked;
    private int mTagsColorErrorClicked;
    private int mTagsBgColor;
    private int mTagsBgColorClicked;
    private int mTagsBgColorErrorClicked;
    private int mTagsTextColor;
    private int mTagsTextColorClicked;
    private int mTagsTextColorErrorClicked;
    private int mTagsPlaceholderResId;
    private int mTagsDeleteResId;

    private String mTagsDialogTitle;
    private String mTagsDialogPlaceholder;
    private String mTagsDialogConfirm;
    private String mTagsDialogCancel;
    private String mTagsDialogErrorMsg;

    // ------------------------------------------------------

    private float mDensity;

    private TagsListener mTagsListener;

    private TagsEditText mEditText;
    private TagsVerticalLinearLayout mRootTagsLayout;

    private EditTextListener mEditTextListener;

    public List<Tags> mTagsList = new ArrayList<>();
    public List<Tags> removeTagsList = new ArrayList<>();

    private Object mCurrentEditTextSpan;

    private TagsValidator mTagsValidator;

    public boolean canEdit = true;

    public TagsView(Context context) {
        super(context);
        init();
    }

    public TagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    public void setAllowEdit(boolean mAllowEdit){
        //this.allowEdit = allowEdit;
        Log.e("allowEdit", "" + mAllowEdit);
        //if (mAllowEdit==false) mEditText.setFocusableInTouchMode(false);
        //canEdit = mAllowEdit;
        //mEditText.setHint("Add Tag");
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                com.bgs.dheket.R.styleable.ChipsView,
                0, 0);
        try {
            mTagsColor = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_color,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.base30));
            mTagsColorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_color_clicked,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.colorPrimaryDark));
            mTagsColorErrorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_color_error_clicked,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.color_error));

            mTagsBgColor = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_bg_color,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.base10));
            mTagsBgColorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_bg_color_clicked,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.blue));

            mTagsBgColorErrorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_bg_color_clicked,
                    ContextCompat.getColor(context, com.bgs.dheket.R.color.color_error));

            mTagsTextColor = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_text_color,
                    Color.BLACK);
            mTagsTextColorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_text_color_clicked,
                    Color.WHITE);
            mTagsTextColorErrorClicked = a.getColor(com.bgs.dheket.R.styleable.ChipsView_cv_text_color_clicked,
                    Color.WHITE);

            mTagsPlaceholderResId = a.getResourceId(com.bgs.dheket.R.styleable.ChipsView_cv_icon_placeholder,
                    com.bgs.dheket.R.drawable.ic_done_24dp);
            mTagsDeleteResId = a.getResourceId(com.bgs.dheket.R.styleable.ChipsView_cv_icon_delete,
                    com.bgs.dheket.R.drawable.ic_close_24dp);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;

        // Dummy item to prevent AutoCompleteTextView from receiving focus
        LinearLayout linearLayout = new LinearLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
        linearLayout.setLayoutParams(params);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setId(R.id.hashtag_view);

        addView(linearLayout);

        mEditText = new TagsEditText(getContext(), this);
        mEditText.setId(R.id.editText_hashtagEditText);
        mEditText.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_UNSPECIFIED);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //if (!allowEdit)mEditText.setFocusableInTouchMode(false);
        //mEditText.setHint(R.string.name_or_email_address);

        addView(mEditText);

        mRootTagsLayout = new TagsVerticalLinearLayout(getContext());
        mRootTagsLayout.setOrientation(LinearLayout.VERTICAL);
        mRootTagsLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mRootTagsLayout);

        initListener();
    }

    private void initListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
            }
        });

        mEditTextListener = new EditTextListener();
        mEditText.addTextChangedListener(mEditTextListener);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    unSelectTagssExcept(null);
                }
            }
        });
    }

    public void addTags(String displayName, String avatarUrl, HashTag hashTag) {
        addTags(displayName, Uri.parse(avatarUrl), hashTag);
    }

    public void addTags(String displayName, Uri avatarUrl, HashTag hashTag) {
        addTags(displayName, avatarUrl, hashTag, false);
        mEditText.setText("");
        addLeadingMarginSpan();
    }

    public void addTags(String displayName, Uri avatarUrl, HashTag hashTag, boolean isIndelible) {
        Tags tags = new Tags(displayName, avatarUrl, hashTag, isIndelible);
        mTagsList.add(tags);
        if (mTagsListener != null) {
            mTagsListener.onTagsAdded(tags);
        }

        onTagsChanged(true);
    }

    public boolean removeTagsBy(HashTag hashTag) {
        for (int i = 0; i < mTagsList.size(); i++) {
            if (mTagsList.get(i).mHashTag != null && mTagsList.get(i).mHashTag.equals(hashTag)) {
                mTagsList.remove(i);
                onTagsChanged(true);
                return true;
            }
        }
        return false;
    }

    /*public HashTag tryToRecognizeAddress() {
        String text = mEditText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            if (Common.isValidEmail(text)) {
                return new HashTag(text, "", null, text, null);
            }
        }
        return null;
    }*/

    /**
     * rebuild all Tags and place them right
     */
    private void onTagsChanged(final boolean moveCursor) {
        TagsVerticalLinearLayout.TextLineParams textLineParams = mRootTagsLayout.onTagsChanged(mTagsList);

        // if null then run another layout pass
        if (textLineParams == null) {
            post(new Runnable() {
                @Override
                public void run() {
                    onTagsChanged(moveCursor);
                }
            });
            return;
        }

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (textLineParams.row * Tags_HEIGHT * mDensity + TEXT_EXTRA_TOP_MARGIN * mDensity);
        mEditText.setLayoutParams(params);
        addLeadingMarginSpan(textLineParams.lineMargin);
        if (moveCursor) {
            mEditText.setSelection(mEditText.length());
        }
    }

    private void addLeadingMarginSpan(int margin) {
        Spannable spannable = mEditText.getText();
        if (mCurrentEditTextSpan != null) {
            spannable.removeSpan(mCurrentEditTextSpan);
        }
        mCurrentEditTextSpan = new android.text.style.LeadingMarginSpan.LeadingMarginSpan2.Standard(margin, 0);
        spannable.setSpan(mCurrentEditTextSpan, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mEditText.setText(spannable);
    }

    private void addLeadingMarginSpan() {
        Spannable spannable = mEditText.getText();
        if (mCurrentEditTextSpan != null) {
            spannable.removeSpan(mCurrentEditTextSpan);
        }
        spannable.setSpan(mCurrentEditTextSpan, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mEditText.setText(spannable);
    }

    private void onEnterPressed(String text) {
        if (text != null && text.length() > 0) {

            /*if (Common.isValidEmail(text)) {
                onEmailRecognized(text);
            } else {
                onNonEmailRecognized(text);
            }*/
            mEditText.setSelection(0);
        }
    }

    /*private void onNonEmailRecognized(String text) {
        try {
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();

            Bundle bundle = new Bundle();
            bundle.putString(TagsEmailDialogFragment.EXTRA_STRING_TEXT, text);
            bundle.putString(TagsEmailDialogFragment.EXTRA_STRING_TITLE, mTagssDialogTitle);
            bundle.putString(ChipsEmailDialogFragment.EXTRA_STRING_PLACEHOLDER, mChipsDialogPlaceholder);
            bundle.putString(ChipsEmailDialogFragment.EXTRA_STRING_CONFIRM, mChipsDialogConfirm);
            bundle.putString(ChipsEmailDialogFragment.EXTRA_STRING_CANCEL, mChipsDialogCancel);
            bundle.putString(ChipsEmailDialogFragment.EXTRA_STRING_ERROR_MSG, mChipsDialogErrorMsg);

            ChipsEmailDialogFragment chipsEmailDialogFragment = new ChipsEmailDialogFragment();
            chipsEmailDialogFragment.setArguments(bundle);
            chipsEmailDialogFragment.setEmailListener(this);
            chipsEmailDialogFragment.show(fragmentManager, ChipsEmailDialogFragment.class.getSimpleName());
        } catch (ClassCastException e) {
            Log.e(TAG, "Error ClassCast", e);
        }
    }*/

    private void selectOrDeleteLastTags() {
        if (mTagsList.size() > 0) {
            onTagsInteraction(mTagsList.size() - 1);
        }
    }

    private void onTagsInteraction(int position) {
        try {
            Tags tags = mTagsList.get(position);
            if (tags != null) {
                onTagsInteraction(tags, true);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Out of bounds", e);
        }
    }

    private void onTagsInteraction(Tags tags, boolean nameClicked) {
        unSelectTagssExcept(tags);
        if (tags.isSelected()) {
            Log.e("hapus",tags.getHashTag().getHashTagName());
            removeTagsList.add(tags);
            mTagsList.remove(tags);
            if (mTagsListener != null) {
                mTagsListener.onTagsDeleted(tags);
            }
            onTagsChanged(true);
            if (nameClicked) {
                if (canEdit) {
                    mEditText.setText(tags.getHashTag().getHashTagName());
                    addLeadingMarginSpan();
                    mEditText.requestFocus();
                    mEditText.setSelection(mEditText.length());
                }
            }
        } else {
            tags.setSelected(true);
            onTagsChanged(false);
        }
    }

    private void unSelectTagssExcept(Tags rootTags) {
        for (Tags tags : mTagsList) {
            if (tags != rootTags) {
                tags.setSelected(false);
            }
        }
        onTagsChanged(false);
    }

    @Override
    public InputConnection getInputConnection(InputConnection target) {
        return new KeyInterceptingInputConnection(target);
    }

    public void setTagsListener(TagsListener TagsListener) {
        this.mTagsListener = TagsListener;
    }

    /*@Override
    public void onDialogEmailEntered(String email, String initialText) {
        onEmailRecognized(new HashTag(initialText, "", initialText, email, null));
    }*/

    /**
     * sets the TagsValidator.
     */
    public void setTagsValidator(TagsValidator mTagsValidator) {
        this.mTagsValidator = mTagsValidator;
    }

    private class EditTextListener implements TextWatcher {

        private boolean mIsPasteTextChange = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 1) {
                mIsPasteTextChange = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mIsPasteTextChange) {
                mIsPasteTextChange = false;
                // todo handle copy/paste text here

            } else {
                // no paste text change
                if (s.toString().contains("\n")) {
                    String text = s.toString();
                    text = text.replace("\n", "");
                    while (text.contains("  ")) {
                        text = text.replace("  ", " ");
                    }
                    s.clear();
                    if (text.length() > 1) {
                        onEnterPressed(text);
                    } else {
                        s.append(text);
                    }
                }
            }
            if (mTagsListener != null) {
                mTagsListener.onTextChanged(s);
            }
        }
    }

    private class KeyInterceptingInputConnection extends InputConnectionWrapper {

        public KeyInterceptingInputConnection(InputConnection target) {
            super(target, true);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (mEditText.length() == 0) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        selectOrDeleteLastTags();
                        return true;
                    }
                }
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                mEditText.append("\n");
                return true;
            }

            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (mEditText.length() == 0 && beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public class Tags implements OnClickListener {

        private static final int MAX_LABEL_LENGTH = 30;

        private String mLabel;
        private final Uri mPhotoUri;
        private final HashTag mHashTag;
        private final boolean mIsIndelible;

        private LinearLayout mView,mViewL;
        private View mIconWrapper;
        private TextView mTextView;

        private ImageView mAvatarView;
        private ImageView mPersonIcon;
        private ImageView mCloseIcon;

        private ImageView mErrorIcon;

        private boolean mIsSelected = false;

        public Tags(String label, Uri photoUri, HashTag hashTag) {
            this(label, photoUri, hashTag, false);
        }

        public Tags(String label, Uri photoUri, HashTag hashTag, boolean isIndelible) {
            this.mLabel = label;
            this.mPhotoUri = photoUri;
            this.mHashTag = hashTag;
            this.mIsIndelible = isIndelible;

            if (mLabel == null) {
                mLabel = hashTag.getHashTagName();
            }

            if (mLabel.length() > MAX_LABEL_LENGTH) {
                mLabel = mLabel.substring(0, MAX_LABEL_LENGTH) + "...";
            }
        }

        public View getView() {
            if (mView == null) {
                mView = (LinearLayout) inflate(getContext(), com.bgs.dheket.R.layout.tags_view, null);
                mView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (32 * mDensity)));
                mAvatarView = (ImageView) mView.findViewById(com.bgs.dheket.R.id.ri_ch_avatar);
                mViewL = (LinearLayout)mView.findViewById(com.bgs.dheket.R.id.tag_view);
                mIconWrapper = mView.findViewById(com.bgs.dheket.R.id.rl_ch_avatar);
                mTextView = (TextView) mView.findViewById(com.bgs.dheket.R.id.tv_ch_name);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10f);
                mPersonIcon = (ImageView) mView.findViewById(com.bgs.dheket.R.id.iv_ch_person);
                mCloseIcon = (ImageView) mView.findViewById(com.bgs.dheket.R.id.iv_ch_close);

                mErrorIcon = (ImageView) mView.findViewById(com.bgs.dheket.R.id.iv_ch_error);

                // set inital res & attrs
                //mView.setBackgroundResource(mTagsBgRes);
                mViewL.setBackgroundResource(mTagsBgRes);
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewL.getBackground().setColorFilter(mTagsBgColor, PorterDuff.Mode.SRC_ATOP);
                        //mView.getBackground().setColorFilter(mTagsBgColor, PorterDuff.Mode.SRC_ATOP);
                    }
                });
                mIconWrapper.setBackgroundResource(com.bgs.dheket.R.drawable.circle);
                mTextView.setTextColor(mTagsTextColor);

                // set icon resources
                mPersonIcon.setBackgroundResource(mTagsPlaceholderResId);
                mCloseIcon.setBackgroundResource(mTagsDeleteResId);


                mView.setOnClickListener(this);
                mIconWrapper.setOnClickListener(this);
            }
            updateViews();
            return mView;
        }

        private void updateViews() {
            mTextView.setText(mLabel);
            if (mPhotoUri != null) {
                Picasso.with(getContext())
                        .load(mPhotoUri)
                        .noPlaceholder()
                        .into(mAvatarView, new Callback() {
                            @Override
                            public void onSuccess() {
                                mPersonIcon.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
            if (isSelected()) {
                if (mTagsValidator != null && !mTagsValidator.isValid(mHashTag)) {
                    // not valid & show error
                    //mView.getBackground().setColorFilter(mTagsBgColorErrorClicked, PorterDuff.Mode.SRC_ATOP);
                    mViewL.getBackground().setColorFilter(mTagsBgColorErrorClicked, PorterDuff.Mode.SRC_ATOP);
                    mTextView.setTextColor(mTagsTextColorErrorClicked);
                    mIconWrapper.getBackground().setColorFilter(mTagsColorErrorClicked, PorterDuff.Mode.SRC_ATOP);
                    mErrorIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                } else {
                    //mView.getBackground().setColorFilter(mTagsBgColorClicked, PorterDuff.Mode.SRC_ATOP);
                    mViewL.getBackground().setColorFilter(mTagsBgColorClicked, PorterDuff.Mode.SRC_ATOP);
                    mTextView.setTextColor(mTagsTextColorClicked);
                    mIconWrapper.getBackground().setColorFilter(mTagsColorClicked, PorterDuff.Mode.SRC_ATOP);
                }
                mPersonIcon.animate().alpha(0.0f).setDuration(200).start();
                mAvatarView.animate().alpha(0.0f).setDuration(200).start();
                mCloseIcon.animate().alpha(1f).setDuration(200).setStartDelay(100).start();

            } else {
                if (mTagsValidator != null && !mTagsValidator.isValid(mHashTag)) {
                    // not valid & show error
                    mErrorIcon.setVisibility(View.VISIBLE);
                    mErrorIcon.setColorFilter(null);
                } else {
                    mErrorIcon.setVisibility(View.GONE);
                }
                //mView.getBackground().setColorFilter(mTagsBgColor, PorterDuff.Mode.SRC_ATOP);
                mViewL.getBackground().setColorFilter(mTagsBgColor, PorterDuff.Mode.SRC_ATOP);
                mTextView.setTextColor(mTagsTextColor);
                mIconWrapper.getBackground().setColorFilter(mTagsColor, PorterDuff.Mode.SRC_ATOP);

                mPersonIcon.animate().alpha(0.3f).setDuration(200).setStartDelay(100).start();
                mAvatarView.animate().alpha(1f).setDuration(200).setStartDelay(100).start();
                mCloseIcon.animate().alpha(0.0f).setDuration(200).start();
            }
        }

        @Override
        public void onClick(View v) {
            mEditText.clearFocus();
            if (v.getId() == mView.getId()) {
                onTagsInteraction(this, true);
            } else {
                onTagsInteraction(this, false);
            }
        }

        public boolean isSelected() {
            return mIsSelected;
        }

        public void setSelected(boolean isSelected) {
            if (mIsIndelible) {
                return;
            }
            this.mIsSelected = isSelected;
        }

        public HashTag getHashTag() {
            return mHashTag;
        }

        @Override
        public boolean equals(Object o) {
            if (mHashTag != null && o instanceof HashTag) {
                return mHashTag.equals(o);
            }
            return super.equals(o);
        }
    }

    public interface TagsListener {
        void onTagsAdded(Tags tags);

        void onTagsDeleted(Tags tags);

        void onTextChanged(CharSequence text);
    }

    public static abstract class TagsValidator {
        public abstract boolean isValid(HashTag hashTag);
    }

    public List<Tags> getListData(){
        return mTagsList;
    }
}

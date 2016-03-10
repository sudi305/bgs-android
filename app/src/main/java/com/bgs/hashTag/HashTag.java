package com.bgs.hashTag;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;

public class HashTag implements Comparable<HashTag>, Serializable {
    //idHashtag hashtagName hashtagUri categoryId 

    @NonNull
    private final String mIdHashTag;

    @NonNull
    private final String mCategoryId;

    @NonNull
    private final String mHashTagName;

    @Nullable
    private transient final Uri mHashTagUri;

    @NonNull
    private final String mInitials;



    public HashTag(@NonNull String idHashTag, @NonNull String categoryId, @NonNull String hashTagName, @Nullable Uri hashTagUri) {
        mIdHashTag = idHashTag;
        mCategoryId = categoryId;
        mHashTagUri = hashTagUri;
        mHashTagName = hashTagName;

        /*if (!TextUtils.isEmpty(displayName)) {
            mDisplayName = displayName;
        } else if (TextUtils.isEmpty(mIdHashTag)) {
            if (TextUtils.isEmpty(mCategoryId)) {
                mDisplayName = mHashTagName;
            } else {
                mDisplayName = mCategoryId;
            }
        } else if (TextUtils.isEmpty(mCategoryId)) {
            mDisplayName = mIdHashTag;
        } else {
            mDisplayName = mIdHashTag + " " + mCategoryId;
        }*/

        StringBuilder initialsBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(mHashTagName)) {
            initialsBuilder.append(Character.toUpperCase(mHashTagName.charAt(0)));
        }
        /*if (!TextUtils.isEmpty(mCategoryId)) {
            initialsBuilder.append(Character.toUpperCase(mCategoryId.charAt(0)));
        }*/
        mInitials = initialsBuilder.toString();
    }

    @NonNull
    public String getIdHashTag() {
        return mIdHashTag;
    }

    @NonNull
    public String getCategoryId() {
        return mCategoryId;
    }

    @NonNull
    public String getHashTagName() {
        return mHashTagName;
    }

    @Nullable
    public Uri getHashTagUri() {
        return mHashTagUri;
    }

    /*@NonNull
    public String getDisplayName() {
        return mDisplayName;
    }*/

    @NonNull
    public String getInitials() {
        return mInitials;
    }

    @Override
    public int compareTo(final HashTag another) {

        if (another == null) {
            return 1;
        }

        // compare whatever is the first visible component of the name
        String myString;
        if (mHashTagName != null) {
            myString = mHashTagName;
        } else if (mIdHashTag != null) {
            myString = mIdHashTag;
        } else {
            myString = mCategoryId;
        }

        String otherString;
        if (another.mHashTagName != null) {
            otherString = another.mHashTagName;
        } else if (another.mIdHashTag != null) {
            otherString = another.mIdHashTag;
        } else {
            otherString = another.mCategoryId;
        }

        int diff = compare(myString, otherString);
        if (diff != 0) {
            return diff;
        }

        if (another.mIdHashTag == null && mIdHashTag != null) {
            return 1;
        }
        if (another.mIdHashTag != null && mIdHashTag == null) {
            return -1;
        }

        if (another.mIdHashTag != null && mIdHashTag != null) {
            // both have first names, so we didn't yet compare last names
            diff = compare(mCategoryId, another.mCategoryId);
            if (diff != 0) {
                return diff;
            }
        }

        return mHashTagName.compareTo(another.mHashTagName);
    }

    private int compare(String myString, String otherString) {
        boolean isMineBlank = TextUtils.isEmpty(myString);
        boolean isOtherBlank = TextUtils.isEmpty(otherString);
        if (isMineBlank && isOtherBlank) {
            return 0;
        }
        if (isMineBlank) {
            return 1;
        }
        if (isOtherBlank) {
            return -1;
        }
        return myString.toLowerCase().compareTo(otherString.toLowerCase());
    }

    public boolean matches(CharSequence searchString) {
        String lowerCaseSearchString = searchString.toString().toLowerCase();
        return (mIdHashTag != null && mIdHashTag.toLowerCase().contains(lowerCaseSearchString)) ||
                (mCategoryId != null && mCategoryId.toLowerCase().contains(lowerCaseSearchString)) ||
                mHashTagName.toLowerCase().contains(lowerCaseSearchString);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashTag hashTag = (HashTag) o;

        if (!mHashTagName.equals(hashTag.mHashTagName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mHashTagName.hashCode();
    }

    @Override
    public String toString() {
        return "HashTag{" +
                "mIdHashTag='" + mIdHashTag + '\'' +
                ", mCategoryId='" + mCategoryId + '\'' +
                ", mHashTagName='" + mHashTagName + '\'' +
                ", mHashTagUri=" + mHashTagUri +
                ", mInitials='" + mInitials + '\'' +
                '}';
    }
}

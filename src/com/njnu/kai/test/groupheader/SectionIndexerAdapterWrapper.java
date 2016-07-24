package com.njnu.kai.test.groupheader;

import android.content.Context;
import android.widget.SectionIndexer;

class SectionIndexerAdapterWrapper extends
        AdapterWrapper implements SectionIndexer {

    final SectionIndexer mSectionIndexerDelegate;

    SectionIndexerAdapterWrapper(Context context,
                                 StickyGroupHeaderAdapter delegate) {
        super(context, delegate);
        mSectionIndexerDelegate = (SectionIndexer)delegate;
    }

    @Override
    public int getPositionForSection(int section) {
        return mSectionIndexerDelegate.getPositionForSection(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return mSectionIndexerDelegate.getSectionForPosition(position);
    }

    @Override
    public Object[] getSections() {
        return mSectionIndexerDelegate.getSections();
    }

}

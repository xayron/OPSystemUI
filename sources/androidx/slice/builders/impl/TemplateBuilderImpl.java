package androidx.slice.builders.impl;

import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.Slice.Builder;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;

public abstract class TemplateBuilderImpl {
    private Clock mClock;
    private Builder mSliceBuilder;
    private final SliceSpec mSpec;

    public abstract void apply(Builder builder);

    protected TemplateBuilderImpl(Builder builder, SliceSpec sliceSpec) {
        this(builder, sliceSpec, new SystemClock());
    }

    protected TemplateBuilderImpl(Builder builder, SliceSpec sliceSpec, Clock clock) {
        this.mSliceBuilder = builder;
        this.mSpec = sliceSpec;
        this.mClock = clock;
    }

    /* access modifiers changed from: protected */
    public void setBuilder(Builder builder) {
        this.mSliceBuilder = builder;
    }

    public Slice build() {
        this.mSliceBuilder.setSpec(this.mSpec);
        apply(this.mSliceBuilder);
        return this.mSliceBuilder.build();
    }

    public Builder getBuilder() {
        return this.mSliceBuilder;
    }

    public Builder createChildBuilder() {
        return new Builder(this.mSliceBuilder);
    }

    public Clock getClock() {
        return this.mClock;
    }
}

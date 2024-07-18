package com.model.formatter.pdf.renders;

import com.itextpdf.layout.properties.Transform;

public class CustomTransform extends Transform {
    private SingleTransform lastSingleTransform;

    /**
     * Creates a new {@link Transform} instance.
     *
     * @param length the amount of {@link SingleTransform} instances that this {@link Transform}
     *               instant shall contain and be able to process
     */
    public CustomTransform(int length) {
        super(length);
    }

    @Override
    public void addSingleTransform(SingleTransform singleTransform) {
        this.lastSingleTransform = singleTransform;
        super.addSingleTransform(singleTransform);
    }

    public SingleTransform getLastSingleTransform() {
        return lastSingleTransform;
    }
}

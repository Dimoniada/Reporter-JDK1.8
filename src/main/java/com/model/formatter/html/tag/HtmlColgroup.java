package com.model.formatter.html.tag;

/**
 * Needs <table style="border-collapse: collapse;">
 * There is only a very limited selection of
 * CSS properties that are allowed to be used in the colgroup:
 * width
 * visibility
 * background
 * border
 * The style will be applied to the whole column
 * (you can't apply per cell style here,
 * for instance, double borders style for each cell)
 */
public class HtmlColgroup extends HtmlTag {
    public static final String TAG_NAME = "colgroup";

    @Override
    public String getTagName() {
        return TAG_NAME;
    }
}

package com.model.formatter.html.tag;

/**
 * Util <div/> class for complex rendering geometry-styled objects
 */
public class HtmlDiv extends HtmlTag {
    public static final String TAG_NAME = "div";

    private Class<?> realDomainClazz;

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    public Class<?> getRealDomainClazz() {
        return realDomainClazz;
    }

    public HtmlDiv setRealDomainClazz(Class<?> realDomainClazz) {
        this.realDomainClazz = realDomainClazz;
        return this;
    }
}

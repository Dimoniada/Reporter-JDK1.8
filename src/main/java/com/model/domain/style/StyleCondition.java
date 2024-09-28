package com.model.domain.style;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

/**
 * Style's applicability condition for the domain object,
 * used by {@link StyleService} in logic: if condition predicate
 * is executed successfully on the object and the class of the object is {@link StyleCondition#clazz},
 * then the style is transferred/applied to the object.
 */
public class StyleCondition {
    private static final Logger log = LoggerFactory.getLogger(StyleCondition.class);

    /**
     * Object class
     */
    protected Class<?> clazz;
    /**
     * Object styling condition
     */
    protected Predicate<?> predicate;

    public static StyleCondition create(Class<?> clazz, Predicate<?> condition) {
        return new StyleCondition().setPredicate(condition).setClazz(clazz);
    }

    public static StyleCondition create(Class<?> clazz) {
        return new StyleCondition().setClazz(clazz);
    }

    /**
     * Checks the condition of object to be styled (t),
     * the check should not change the condition itself or other styles.
     *
     * @param t   styled DocumentItem object
     * @param <T> object type, T extends DocumentItem
     * @return true if the condition is not set, or it is true on the object; otherwise - false
     */
    @SuppressWarnings("unchecked")
    public <T> boolean test(T t) {
        log.trace("Check predicate:{} on {}", predicate, t);

        boolean result = true;

        if (predicate != null) {
            log.trace("Condition contains predicate:{}.", predicate);

            result = ((Predicate<T>) predicate).test(t);

            log.trace("Predicate check is:{}", result);
        } else {
            log.trace("No predicate found. Use true.");
        }

        log.trace("Return check result:{}", result);

        return result;
    }

    public StyleCondition negate() {
        return StyleCondition.create(clazz, predicate.negate());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public StyleCondition setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public Predicate<?> getPredicate() {
        return predicate;
    }

    public StyleCondition setPredicate(Predicate<?> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("clazz", clazz)
                .add("predicate", predicate)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final StyleCondition that = (StyleCondition) o;

        return
            Objects.equal(this.clazz, that.clazz) &&
                Objects.equal(this.predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clazz, predicate);
    }
}

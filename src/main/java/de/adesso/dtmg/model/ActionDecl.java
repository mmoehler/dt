package de.adesso.dtmg.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by mohler ofList 16.01.16.
 */
public class ActionDecl extends Declaration {
    public ActionDecl() {
        super();
    }

    protected ActionDecl(@Nonnull List<String> data) {
        super(data);
    }

    public ActionDecl(String lfdNr, String expression, String possibleIndicators, String documentation) {
        super(lfdNr, expression, possibleIndicators, documentation);
    }

    private ActionDecl(Builder builder) {
        setExpression(builder.expression);
        setDocumentation(builder.documentation);
        setLfdNr(builder.lfdNr);
        setPossibleIndicators(builder.possibleIndicators);
    }

    public static ActionDecl of(@Nonnull List<String> data) {
        return new ActionDecl(data);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull ActionDecl copy) {
        Builder builder = new Builder();
        builder.expression = copy.expression;
        builder.documentation = copy.documentation;
        builder.lfdNr = copy.lfdNr;
        builder.possibleIndicators = copy.possibleIndicators;
        return builder;
    }

    @Override
    public String getLfdNr() {
        return super.getLfdNr();
    }

    @Override
    public void setLfdNr(String lfdNr) {
        super.setLfdNr(lfdNr);
    }

    @Override
    public String getExpression() {
        return super.getExpression();
    }

    @Override
    public void setExpression(String expression) {
        super.setExpression(expression);
    }

    @Override
    public String getPossibleIndicators() {
        return super.getPossibleIndicators();
    }

    @Override
    public void setPossibleIndicators(String possibleIndicators) {
        super.setPossibleIndicators(possibleIndicators);
    }

    @Override
    public int getPossibleIndicatorSize() {
        return super.getPossibleIndicatorSize();
    }

    @Override
    public String getDocumentation() {
        return super.getDocumentation();
    }

    @Override
    public void setDocumentation(String documentation) {
        super.setDocumentation(documentation);
    }

    @Override
    public String toString() {
        return super.toString();
    }


    /**
     * {@code ActionDecl} builder static inner class.
     */
    public static final class Builder {
        private String expression;
        private String documentation;
        private String lfdNr;
        private String possibleIndicators;

        private Builder() {
        }

        /**
         * Sets the {@code expression} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code expression} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withExpression(@Nonnull String val) {
            expression = val;
            return this;
        }

        /**
         * Sets the {@code lfdNr} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code lfdNr} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withLfdNr(@Nonnull String val) {
            lfdNr = val;
            return this;
        }

        @Nonnull
        public Builder withDocumentation(@Nonnull String val) {
            documentation = val;
            return this;
        }

        /**
         * Sets the {@code possibleIndicators} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code possibleIndicators} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withPossibleIndicators(@Nonnull String val) {
            possibleIndicators = val;
            return this;
        }

        /**
         * Returns a {@code ActionDecl} built from the parameters previously set.
         *
         * @return a {@code ActionDecl} built with parameters of this {@code ActionDecl.Builder}
         */
        @Nonnull
        public ActionDecl build() {
            return new ActionDecl(this);
        }
    }
}

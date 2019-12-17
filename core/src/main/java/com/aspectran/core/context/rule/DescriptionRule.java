package com.aspectran.core.context.rule;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.context.expr.TokenEvaluator;
import com.aspectran.core.context.expr.TokenExpression;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.expr.token.TokenParser;
import com.aspectran.core.context.rule.type.TextStyleType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created: 2019/12/17</p>
 */
public class DescriptionRule {

    private String profile;

    private TextStyleType contentStyle;

    private String content;

    private String formattedContent;

    private List<DescriptionRule> candidates;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public TextStyleType getContentStyle() {
        return contentStyle;
    }

    public void setContentStyle(TextStyleType contentStyle) {
        this.contentStyle = contentStyle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedContent() {
        return (formattedContent != null ? formattedContent : content);
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public List<DescriptionRule> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<DescriptionRule> candidates) {
        this.candidates = candidates;
    }

    public List<DescriptionRule> addCandidate(DescriptionRule descriptionRule) {
        if (candidates == null) {
            candidates = new ArrayList<>();
        }
        candidates.add(descriptionRule);
        return candidates;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("profile", profile);
        tsb.append("style", contentStyle);
        tsb.append("content", content);
        return tsb.toString();
    }

    public static String render(DescriptionRule descriptionRule, Activity activity) {
        if (!StringUtils.hasText(descriptionRule.getFormattedContent())) {
            return null;
        }
        if (activity == null) {
            return descriptionRule.getFormattedContent();
        }

        Token[] contentTokens = TokenParser.makeTokens(descriptionRule.getFormattedContent(), true);
        for (Token token : contentTokens) {
            Token.resolveAlternativeValue(token, activity.getActivityContext().getApplicationAdapter().getClassLoader());
        }
        TokenEvaluator evaluator = new TokenExpression(activity);
        return evaluator.evaluateAsString(contentTokens);
    }

    public static DescriptionRule newInstance(String style, String profile)
            throws IllegalRuleException {
        TextStyleType contentStyle = TextStyleType.resolve(style);
        if (style != null && contentStyle == null) {
            throw new IllegalRuleException("No text style type for '" + style + "'");
        }

        DescriptionRule descriptionRule = new DescriptionRule();
        descriptionRule.setContentStyle(contentStyle);
        if (profile != null && !profile.isEmpty()) {
            descriptionRule.setProfile(profile);
        }
        return descriptionRule;
    }

}

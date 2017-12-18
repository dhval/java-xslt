package com.dhval.diff;

import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DifferenceEvaluator;

public class SmartXMLDiffEvaluator implements DifferenceEvaluator {
    @Override
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
        if(outcome == ComparisonResult.EQUAL)
            return outcome;
        if(comparison.getType() != ComparisonType.CHILD_LOOKUP)
            return ComparisonResult.SIMILAR;
        if(comparison.getControlDetails().getXPath() == null)
            return ComparisonResult.SIMILAR;
        if(comparison.getTestDetails().getXPath() == null
                && comparison.getControlDetails().getXPath() != null)
            return ComparisonResult.DIFFERENT;
        return ComparisonResult.SIMILAR;
    }
}

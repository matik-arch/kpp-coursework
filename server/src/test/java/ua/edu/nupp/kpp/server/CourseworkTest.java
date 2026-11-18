package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseworkTest {

    @Test
    void normalizeRatioReturnsZeroWhenCompletedIsZero() {
        assertEquals(0.0, QualityCalculator.normalizeRatio(0, 20));
    }

    @Test
    void normalizeRatioCapsAt100WhenCompletedExceedsTotal() {
        assertEquals(100.0, QualityCalculator.normalizeRatio(25, 20));
    }

    @Test
    void normalizeRatioForDemoDataIs85() {
        assertEquals(85.0, QualityCalculator.normalizeRatio(17, 20));
    }

    @Test
    void qualityIndexForDemoDataIs838() {
        assertEquals(83.8, QualityCalculator.calculate(85.0, 88.0, 76.0));
    }

    @Test
    void qualityIndexRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> QualityCalculator.calculate(101.0, 50.0, 50.0));
    }

    @Test
    void qualityIndexRejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class,
                () -> QualityCalculator.calculate(-1.0, 50.0, 50.0));
    }

    @Test
    void draftToSubmittedAllowed() {
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.SUBMITTED));
    }

    @Test
    void submittedToInReviewAllowed() {
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.SUBMITTED, SubmissionState.IN_REVIEW));
    }

    @Test
    void inReviewToAcceptedAllowed() {
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.IN_REVIEW, SubmissionState.ACCEPTED));
    }

    @Test
    void draftToAcceptedRejected() {
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.ACCEPTED));
    }

    @Test
    void studentValidationRejectsBlankEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> StudentService.validate(new Student(null, "ST-201-05", "Варламов Ілля", "201-ТН", "")));
    }

    @Test
    void projectValidationRejectsNonPositiveRequirements() {
        assertThrows(IllegalArgumentException.class,
                () -> ProjectService.validate(new Project(null, 1L, "PRJ-01", "Проєкт", "Java 21", 0, 10)));
    }
}

package com.kolmir.fitness_tracker.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkoutSpecificationsTest {

    @Mock
    private Root<Workout> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    @Mock
    private jakarta.persistence.criteria.Path<Object> anyPath;

    @Mock
    private Join<Object, Object> join;

    @Test
    void withFilter_BuildsCompositePredicate() {
        WorkoutFilter filter = new WorkoutFilter();
        filter.setOwnerId(1L);
        filter.setCategoryName("cardio");
        filter.setDateFrom(LocalDateTime.now().minusDays(1));
        filter.setDateTo(LocalDateTime.now());
        filter.setDurationMinutesFrom(10);
        filter.setDurationMinutesTo(60);

        when(root.get("owner")).thenReturn(anyPath);
        when(anyPath.get("id")).thenReturn(anyPath);
        when(cb.equal(any(), any())).thenReturn(predicate);

        when(root.join("category")).thenReturn(join);
        when(join.get("name")).thenReturn(anyPath);

        when(cb.greaterThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.ge(any(), eq(10))).thenReturn(predicate);
        when(cb.le(any(), eq(60))).thenReturn(predicate);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        var spec = WorkoutSpecifications.withFilter(filter);
        Predicate result = spec.toPredicate(root, null, cb);

        assertNotNull(result);
        verify(cb).equal(any(), eq(1L));
        verify(cb).ge(any(), eq(10));
    }
}

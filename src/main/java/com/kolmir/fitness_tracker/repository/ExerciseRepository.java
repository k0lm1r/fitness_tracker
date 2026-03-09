package com.kolmir.fitness_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>, 
                JpaSpecificationExecutor<Exercise> {
    
    @Query("""
        select case when count(e) > 0 then true else false end 
        from Exercise e left join e.category c
        where e.id = :id and
        c.owner.id = :ownerId
    """)
    public boolean existsByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}

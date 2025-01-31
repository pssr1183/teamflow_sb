package com.example.demo.repository;

import com.example.demo.entity.Task;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t join t.assignments a where a.user.id = :userId")
    public List<Task> findTasksByUserId(@Param("userId") Long userId);
}

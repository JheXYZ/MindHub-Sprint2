package com.mindhub.todolist.repositories;

import com.mindhub.todolist.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser_id(Long userId);

    List<Task> findByUser_email(String userEmail);

    boolean existsByIdAndUser_email(Long id, String userEmail);
}

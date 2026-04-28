package com.todo.plus.backend.todo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
  List<TodoEntity> findByUsernameOrderByCreatedAtDesc(String username);
}

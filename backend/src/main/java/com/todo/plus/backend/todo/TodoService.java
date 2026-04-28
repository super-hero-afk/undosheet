package com.todo.plus.backend.todo;

import com.todo.plus.backend.todo.TodoDto.CreateRequest;
import com.todo.plus.backend.todo.TodoDto.Item;
import com.todo.plus.backend.todo.TodoDto.UpdateRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
  private final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  public List<Item> list(String username, TodoCategory category, String keyword) {
    String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
    return todoRepository.findByUsernameOrderByCreatedAtDesc(username).stream()
        .filter(todo -> category == null || todo.getCategory() == category)
        .filter(todo -> normalizedKeyword.isEmpty() || todo.getTitle().toLowerCase().contains(normalizedKeyword))
        .map(this::toItem)
        .toList();
  }

  public Item create(String username, CreateRequest request) {
    TodoEntity entity = new TodoEntity();
    entity.setTitle(request.title().trim());
    entity.setCategory(request.category());
    entity.setCompleted(false);
    entity.setUsername(username);
    TodoEntity saved = todoRepository.save(entity);
    return toItem(saved);
  }

  public Item update(String username, Long id, UpdateRequest request) {
    TodoEntity entity = findOwned(username, id);
    entity.setTitle(request.title().trim());
    entity.setCategory(request.category());
    TodoEntity saved = todoRepository.save(entity);
    return toItem(saved);
  }

  public Item toggle(String username, Long id) {
    TodoEntity entity = findOwned(username, id);
    entity.setCompleted(!entity.isCompleted());
    TodoEntity saved = todoRepository.save(entity);
    return toItem(saved);
  }

  public void delete(String username, Long id) {
    TodoEntity entity = findOwned(username, id);
    todoRepository.delete(entity);
  }

  private TodoEntity findOwned(String username, Long id) {
    return todoRepository.findById(id)
        .filter(todo -> todo.getUsername().equals(username))
        .orElseThrow(() -> new IllegalArgumentException("待办不存在"));
  }

  private Item toItem(TodoEntity entity) {
    return new Item(
        entity.getId(),
        entity.getTitle(),
        entity.getCategory(),
        entity.isCompleted(),
        entity.getUsername(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}

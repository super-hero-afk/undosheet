package com.todo.plus.backend.todo;

import com.todo.plus.backend.todo.TodoDto.CreateRequest;
import com.todo.plus.backend.todo.TodoDto.Item;
import com.todo.plus.backend.todo.TodoDto.UpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping
  public ResponseEntity<List<Item>> list(
      @RequestHeader("X-User") String username,
      @RequestParam(required = false) TodoCategory category,
      @RequestParam(required = false) String keyword) {
    return ResponseEntity.ok(todoService.list(username, category, keyword));
  }

  @PostMapping
  public ResponseEntity<Item> create(
      @RequestHeader("X-User") String username,
      @Valid @RequestBody CreateRequest request) {
    return ResponseEntity.ok(todoService.create(username, request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Item> update(
      @RequestHeader("X-User") String username,
      @PathVariable Long id,
      @Valid @RequestBody UpdateRequest request) {
    return ResponseEntity.ok(todoService.update(username, id, request));
  }

  @PatchMapping("/{id}/toggle")
  public ResponseEntity<Item> toggle(
      @RequestHeader("X-User") String username,
      @PathVariable Long id) {
    return ResponseEntity.ok(todoService.toggle(username, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @RequestHeader("X-User") String username,
      @PathVariable Long id) {
    todoService.delete(username, id);
    return ResponseEntity.noContent().build();
  }
}

package com.todo.plus.backend.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class TodoDto {
  public record CreateRequest(
      @NotBlank(message = "标题不能为空") String title,
      @NotNull(message = "分类不能为空") TodoCategory category) {
  }

  public record UpdateRequest(
      @NotBlank(message = "标题不能为空") String title,
      @NotNull(message = "分类不能为空") TodoCategory category) {
  }

  public record Item(
      Long id,
      String title,
      TodoCategory category,
      boolean completed,
      String username,
      Instant createdAt,
      Instant updatedAt) {
  }
}

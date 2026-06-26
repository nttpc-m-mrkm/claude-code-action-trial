package com.example.taskmanager.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * タスク管理のRESTコントローラ。
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /api/tasks
     * タスクを新規作成する。
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    /**
     * GET /api/tasks/{id}
     * 指定IDのタスクを取得する。
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTask(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/tasks
     * 全タスクを一覧取得する。
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /**
     * PATCH /api/tasks/{id}/status
     * タスクのステータスを更新する。
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        Task task = taskService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(task);
    }

    /**
     * DELETE /api/tasks/{id}
     * タスクを削除する。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}

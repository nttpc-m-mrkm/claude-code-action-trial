package com.example.taskmanager.service;

import java.util.List;
import java.util.Optional;

/**
 * タスク管理サービス。
 * タスクのCRUD操作を提供する。
 */
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * 新しいタスクを作成する。
     * ステータスは「未着手」で初期化される。
     */
    public Task createTask(String title, String description) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.TODO);
        return taskRepository.save(task);
    }

    /**
     * 指定IDのタスクを取得する。
     */
    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * 全タスクを一覧取得する。
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * タスクのステータスを更新する。
     * TODO → IN_PROGRESS → DONE の順でのみ遷移可能。
     */
    public Task updateStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (!isValidTransition(task.getStatus(), newStatus)) {
            throw new InvalidStatusTransitionException(task.getStatus(), newStatus);
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    /**
     * タスクを削除する。
     */
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * ステータス遷移が有効かどうかを判定する。
     * TODO → IN_PROGRESS → DONE の一方向のみ許可。
     */
    private boolean isValidTransition(TaskStatus current, TaskStatus next) {
        return switch (current) {
            case TODO -> next == TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TaskStatus.DONE;
            case DONE -> false;
        };
    }
}

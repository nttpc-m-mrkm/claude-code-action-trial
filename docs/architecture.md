# アーキテクチャ概要

## 1. 全体構成

本アプリケーションはSpring Bootをベースとした3層アーキテクチャを採用している。

```
┌──────────────┐
│   クライアント   │  ブラウザ / APIクライアント
└──────┬───────┘
       │ HTTP (REST)
┌──────▼───────┐
│  Controller層  │  TaskController — リクエストの受付・レスポンスの返却
└──────┬───────┘
       │
┌──────▼───────┐
│  Service層     │  TaskService — ビジネスロジック（ステータス遷移の検証等）
└──────┬───────┘
       │
┌──────▼───────┐
│  Repository層  │  TaskRepository — データの永続化（H2 Database）
└──────────────┘
```

各層は上から下への一方向の依存関係を持ち、下位層が上位層に依存することはない。

## 2. クラス図

```mermaid
classDiagram
    class Task {
        -Long id
        -String title
        -String description
        -TaskStatus status
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() Long
        +getTitle() String
        +setTitle(String)
        +getDescription() String
        +setDescription(String)
        +getStatus() TaskStatus
        +setStatus(TaskStatus)
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
    }

    class TaskStatus {
        <<enumeration>>
        TODO
        IN_PROGRESS
        DONE
    }

    class TaskService {
        -TaskRepository taskRepository
        +createTask(String, String) Task
        +getTask(Long) Optional~Task~
        +getAllTasks() List~Task~
        +updateStatus(Long, TaskStatus) Task
        +deleteTask(Long)
        -isValidTransition(TaskStatus, TaskStatus) boolean
    }

    class TaskController {
        -TaskService taskService
        +createTask(CreateTaskRequest) ResponseEntity
        +getTask(Long) ResponseEntity
        +getAllTasks() ResponseEntity
        +updateStatus(Long, UpdateStatusRequest) ResponseEntity
        +deleteTask(Long) ResponseEntity
    }

    class TaskRepository {
        <<interface>>
        +save(Task) Task
        +findById(Long) Optional~Task~
        +findAll() List~Task~
        +existsById(Long) boolean
        +deleteById(Long)
    }

    Task --> TaskStatus
    TaskService --> TaskRepository
    TaskService --> Task
    TaskController --> TaskService
```

## 3. シーケンス図

### タスク作成の流れ

```mermaid
sequenceDiagram
    actor Client as クライアント
    participant C as TaskController
    participant S as TaskService
    participant R as TaskRepository

    Client->>C: POST /api/tasks {title, description}
    C->>S: createTask(title, description)
    S->>S: new Task()
    S->>S: task.setStatus(TODO)
    S->>R: save(task)
    R-->>S: 保存されたTask（IDが採番済み）
    S-->>C: Task
    C-->>Client: 201 Created + Task
```

### ステータス更新の流れ

```mermaid
sequenceDiagram
    actor Client as クライアント
    participant C as TaskController
    participant S as TaskService
    participant R as TaskRepository

    Client->>C: PATCH /api/tasks/{id}/status {status}
    C->>S: updateStatus(id, newStatus)
    S->>R: findById(id)
    R-->>S: Task
    S->>S: isValidTransition(currentStatus, newStatus)
    alt 遷移が有効
        S->>S: task.setStatus(newStatus)
        S->>R: save(task)
        R-->>S: 更新されたTask
        S-->>C: Task
        C-->>Client: 200 OK + Task
    else 遷移が不正
        S-->>C: InvalidStatusTransitionException
        C-->>Client: 400 Bad Request
    end
```

## 4. エラーハンドリング方針

アプリケーションでは以下の例外クラスを使用してエラーを管理する。

| 例外クラス | HTTPステータス | 発生条件 |
|---|---|---|
| TaskNotFoundException | 404 Not Found | 指定されたIDのタスクが存在しない |
| InvalidStatusTransitionException | 400 Bad Request | 許可されていないステータス遷移を試みた |

例外はControllerAdviceで捕捉し、統一的なエラーレスポンス形式で返却する。

## 5. 設計上の判断

- **ステータス遷移の検証をService層に集約**: Controller層ではステータス値の受け渡しのみ行い、遷移ルールの判定はServiceに任せる。これにより、バッチ処理や別のエントリーポイントからタスクを操作する場合も同じルールが適用される。
- **updatedAtの自動更新**: Setter内でupdatedAtを更新する方式を採用。フィールドの変更漏れを防ぐ反面、単純な参照時にもSetterが呼ばれないよう注意が必要。
- **Repositoryのインターフェース化**: 現在はH2 Databaseを使用しているが、Repository層をインターフェースとして定義することで、将来的にPostgreSQL等への切り替えを容易にしている。

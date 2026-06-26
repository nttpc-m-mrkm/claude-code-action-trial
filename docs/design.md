# タスク管理機能 内部設計書

## 1. 概要

タスクの作成・取得・更新・削除を行うREST APIを提供する。

## 2. データモデル

### Task エンティティ

| フィールド | 型 | 説明 |
|---|---|---|
| id | Long | タスクID（自動採番） |
| title | String | タスクのタイトル |
| description | String | タスクの説明 |
| status | TaskStatus | タスクのステータス |
| createdAt | LocalDateTime | 作成日時 |
| updatedAt | LocalDateTime | 更新日時 |

### TaskStatus（ステータス）

| 値 | 意味 |
|---|---|
| TODO | 未着手 |
| IN_PROGRESS | 作業中 |
| DONE | 完了 |

## 3. ステータス遷移ルール

ステータスは以下の一方向にのみ遷移可能。逆方向や飛ばしは不可。

```
TODO → IN_PROGRESS → DONE
```

不正な遷移を行おうとした場合は `InvalidStatusTransitionException` がスローされる。

## 4. API仕様

### POST /api/tasks — タスク作成

- リクエスト: `{ "title": "...", "description": "..." }`
- レスポンス: 201 Created + 作成されたTaskオブジェクト
- ステータスは `TODO` で初期化される

### GET /api/tasks/{id} — タスク取得

- レスポンス: 200 OK + Taskオブジェクト
- 存在しない場合: 404 Not Found

### GET /api/tasks — タスク一覧

- レスポンス: 200 OK + Task配列

### PATCH /api/tasks/{id}/status — ステータス更新

- リクエスト: `{ "status": "IN_PROGRESS" }`
- レスポンス: 200 OK + 更新されたTaskオブジェクト
- 不正な遷移の場合: 400 Bad Request

### DELETE /api/tasks/{id} — タスク削除

- レスポンス: 204 No Content
- 存在しない場合: 404 Not Found

## 5. クラス構成

| クラス | 役割 |
|---|---|
| TaskController | REST APIのエンドポイント定義 |
| TaskService | ビジネスロジック（ステータス遷移の検証等） |
| Task | エンティティ（データモデル） |
| TaskStatus | ステータスのEnum定義 |
| TaskRepository | データアクセス層（インターフェース） |

# テーブル定義書

## 1. テーブル一覧

| テーブル名 | 論理名 | 概要 |
|---|---|---|
| tasks | タスク | タスクの情報を管理する |

## 2. tasks テーブル

タスクの基本情報を格納するテーブル。1レコードが1つのタスクに対応する。

### カラム定義

| カラム名 | 論理名 | データ型 | NOT NULL | デフォルト値 | 説明 |
|---|---|---|---|---|---|
| id | タスクID | BIGINT | ○ | AUTO_INCREMENT | 主キー。自動採番 |
| title | タイトル | VARCHAR(100) | ○ | — | タスクのタイトル。1〜100文字 |
| description | 説明 | VARCHAR(500) | × | NULL | タスクの詳細説明。最大500文字 |
| status | ステータス | VARCHAR(20) | ○ | 'TODO' | タスクの現在の状態。TODO / IN_PROGRESS / DONE のいずれか |
| created_at | 作成日時 | TIMESTAMP | ○ | CURRENT_TIMESTAMP | レコード作成日時 |
| updated_at | 更新日時 | TIMESTAMP | ○ | CURRENT_TIMESTAMP | 最終更新日時。フィールド変更時に自動更新 |

### 主キー

| 制約名 | カラム |
|---|---|
| PK_TASKS | id |

### インデックス

| インデックス名 | カラム | 種別 | 用途 |
|---|---|---|---|
| PK_TASKS | id | PRIMARY | 主キー検索 |
| IDX_TASKS_STATUS | status | NORMAL | ステータスによる絞り込み検索 |
| IDX_TASKS_CREATED_AT | created_at | NORMAL | 作成日時によるソート |

### DDL

```sql
CREATE TABLE tasks (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100)  NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)   NOT NULL DEFAULT 'TODO',
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT CHK_STATUS CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);

CREATE INDEX IDX_TASKS_STATUS ON tasks (status);
CREATE INDEX IDX_TASKS_CREATED_AT ON tasks (created_at);
```

## 3. ステータス値の定義

tasksテーブルのstatusカラムに格納される値の一覧。アプリケーション側では `TaskStatus` Enumで管理している。

| 値 | 意味 | 遷移先 |
|---|---|---|
| TODO | 未着手 | IN_PROGRESS |
| IN_PROGRESS | 作業中 | DONE |
| DONE | 完了 | なし（最終状態） |

遷移ルールはアプリケーション層（TaskService）で制御しており、データベース側には遷移順序の制約は設けていない。CHECK制約はstatusカラムの値域のみを制限する。

## 4. 初期データ

開発環境ではH2 Databaseを使用し、アプリケーション起動時に以下のテストデータを投入する。

```sql
INSERT INTO tasks (title, description, status) VALUES
    ('設計書を書く', 'タスク管理機能の内部設計書を作成する', 'TODO'),
    ('APIを実装する', 'REST APIのエンドポイントを実装する', 'IN_PROGRESS'),
    ('テストを書く', 'ユニットテストと結合テストを作成する', 'TODO');
```

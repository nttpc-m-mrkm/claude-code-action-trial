# タスク管理アプリケーション

チームのタスクを管理するためのWebアプリケーション。REST APIでタスクの作成・取得・更新・削除ができる。

## 機能

- タスクの新規作成（タイトルと説明を入力）
- タスク一覧の表示
- タスクの詳細表示
- ステータスの更新（TODO → IN_PROGRESS → DONE の順序で進行）
- タスクの削除
- タスクの優先度設定（LOW・MEDIUM・HIGH）

## 技術スタック

- Java 17
- Spring Boot 3.x
- Spring Web（REST API）
- H2 Database（開発環境）

## セットアップ

```bash
# リポジトリをクローン
git clone https://github.com/nttpc-m-mrkm/claude-code-action-trial.git
cd claude-code-action-trial

# ビルドと起動
./gradlew bootRun

アプリケーションは `http://localhost:8080` で起動する。

## API一覧

| メソッド | パス | 説明 |
|---|---|---|
| POST | /api/tasks | タスクを新規作成 |
| GET | /api/tasks | タスク一覧を取得 |
| GET | /api/tasks/{id} | 指定IDのタスクを取得 |
| PATCH | /api/tasks/{id}/status | ステータスを更新 |
| DELETE | /api/tasks/{id} | タスクを削除 |

詳細は [docs/design.md](docs/design.md) を参照。

## ディレクトリ構成

claude-code-action-trial/
├── src/
│   ├── Task.java            # タスクエンティティ
│   ├── TaskStatus.java      # ステータスEnum（TODO/IN_PROGRESS/DONE）
│   ├── Priority.java        # 優先度Enum（LOW/MEDIUM/HIGH）
│   ├── TaskService.java     # ビジネスロジック
│   └── TaskController.java  # RESTコントローラ
└── docs/
    ├── design.md            # 内部設計書（API仕様）
    ├── architecture.md      # アーキテクチャ概要
    ├── screen-spec.md       # 画面仕様書
    └── database.md          # テーブル定義書

## ステータスの遷移ルール

タスクのステータスは一方向にのみ遷移できる。逆方向やスキップはできない。

TODO → IN_PROGRESS → DONE

不正な遷移を試みた場合は 400 Bad Request が返る。

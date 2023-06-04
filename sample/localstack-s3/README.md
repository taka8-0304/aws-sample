# 事前準備

## [aws cliのインストール](https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/getting-started-install.html)

## aws configureの実行

localstack用の設定はaccesskeyとsecretkeyは任意の文字列(dummy等)で問題ない。

## awslocalコマンドの登録

```
doskey awslocal=aws --endpoint-uri=http://localhost:4566 $*
```

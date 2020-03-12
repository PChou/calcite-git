# calcite-git

通过git命令获得git仓库log和统计信息，并借助[calcite]框架提供jdbc和sql查询能力。

# 快速开始

```
cd calcite-git
mvn package
```

运行`quick-sqlline`

```
# quick-sqlline连接当前calcite-git的log信息
./quick-sqlline
0: jdbc:calcite:model=quick-model.yml> select * from CALCITE_GIT limit 5;
+---------+-----------------------+------------------------+-----------------------+------------------------+-----------------+-----+--------+
|  HASH   |      AUTHOR_TIME      |         AUTHOR         |    COMMITTER_TIME     |       COMMITTER        |      FILE       | ADD | REMOVE |
+---------+-----------------------+------------------------+-----------------------+------------------------+-----------------+-----+--------+
| eebdbc2 | 2020-03-12 00:20:34.0 | parker.zhou@eoitek.com | 2020-03-12 00:20:34.0 | parker.zhou@eoitek.com | README.md       | 111 | 0      |
| 788734f | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | .gitignore      | 19  | 0      |
| 788734f | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | pom.xml         | 81  | 0      |
| 788734f | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | quick-model.yml | 10  | 0      |
| 788734f | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | 2020-03-12 00:04:56.0 | parker.zhou@eoitek.com | quick-sqlline   | 38  | 0      |
+---------+-----------------------+------------------------+-----------------------+------------------------+-----------------+-----+--------+

# 统计所有贡献者的代码总量
0: jdbc:calcite:model=quick-model.yml> select COMMITTER, sum(ADD)-sum(REMOVE) as TOTAL from CALCITE_GIT group by COMMITTER;
+------------------------+-------+
|       COMMITTER        | TOTAL |
+------------------------+-------+
| parker.zhou@eoitek.com | 403   |
+------------------------+-------+

# 统计所有贡献者的java代码总量
0: jdbc:calcite:model=quick-model.yml> select COMMITTER, sum(ADD)-sum(REMOVE) as TOTAL from CALCITE_GIT where FILE like '%.java' group by COMMITTER;
+------------------------+-------+
|       COMMITTER        | TOTAL |
+------------------------+-------+
| parker.zhou@eoitek.com | 202   |
+------------------------+-------+

0: jdbc:calcite:model=quick-model.yml> !tables
+-----------+-------------+-------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+
| TABLE_CAT | TABLE_SCHEM | TABLE_NAME  |  TABLE_TYPE  | REMARKS | TYPE_CAT | TYPE_SCHEM | TYPE_NAME | SELF_REFERENCING_COL_NAME | REF_GENERATION |
+-----------+-------------+-------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+
|           | GIT         | CALCITE_GIT | TABLE        |         |          |            |           |                           |                |
|           | metadata    | COLUMNS     | SYSTEM TABLE |         |          |            |           |                           |                |
|           | metadata    | TABLES      | SYSTEM TABLE |         |          |            |           |                           |                |
+-----------+-------------+-------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+

0: jdbc:calcite:model=quick-model.yml> !describe CALCITE_GIT
+-----------+-------------+-------------+----------------+-----------+-----------------------+-------------+---------------+----------------+----------------+-----------+
| TABLE_CAT | TABLE_SCHEM | TABLE_NAME  |  COLUMN_NAME   | DATA_TYPE |       TYPE_NAME       | COLUMN_SIZE | BUFFER_LENGTH | DECIMAL_DIGITS | NUM_PREC_RADIX | NULLABLE  |
+-----------+-------------+-------------+----------------+-----------+-----------------------+-------------+---------------+----------------+----------------+-----------+
|           | GIT         | CALCITE_GIT | HASH           | 12        | VARCHAR NOT NULL      | -1          | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | AUTHOR_TIME    | 93        | TIMESTAMP(0) NOT NULL | 0           | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | AUTHOR         | 12        | VARCHAR NOT NULL      | -1          | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | COMMITTER_TIME | 93        | TIMESTAMP(0) NOT NULL | 0           | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | COMMITTER      | 12        | VARCHAR NOT NULL      | -1          | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | FILE           | 12        | VARCHAR NOT NULL      | -1          | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | ADD            | 4         | INTEGER NOT NULL      | -1          | null          | null           | 10             | 0         |
|           | GIT         | CALCITE_GIT | REMOVE         | 4         | INTEGER NOT NULL      | -1          | null          | null           | 10             | 0         |
+-----------+-------------+-------------+----------------+-----------+-----------------------+-------------+---------------+----------------+----------------+-----------+
```

# 自定义git仓库

参考`src/test/resources/git-model.yml`，可以同时创建多个自定义表

```yaml
version: 1.0
defaultSchema: GIT
schemas:
  - name: GIT
    tables:
      - name: JAX
        type: custom
        factory: com.eoi.dc.calcite.git.GitLogTableFactory
        operand:
          file: /Users/pchou/Projects/java/jax/.git
      - name: CALCITE
        type: custom
        factory: com.eoi.dc.calcite.git.GitLogTableFactory
        operand:
          file: /Users/pchou/Projects/java/calcite/.git
```

`file`需要指向实际的本地git仓库中的`.git`目录。你可以同时配置多个表

运行`sqlline`脚本

```
./sqlline
# 通过指向model文件连接到虚拟的数据库，用户名密码随意
sqlline> !connect jdbc:calcite:model=src/test/resources/git-model.yml admin admin
# 查看数据库和表
0: jdbc:calcite:model=src/test/resources/git-> !tables
+-----------+-------------+------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+
| TABLE_CAT | TABLE_SCHEM | TABLE_NAME |  TABLE_TYPE  | REMARKS | TYPE_CAT | TYPE_SCHEM | TYPE_NAME | SELF_REFERENCING_COL_NAME | REF_GENERATION |
+-----------+-------------+------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+
|           | GIT         | CALCITE    | TABLE        |         |          |            |           |                           |                |
|           | GIT         | JAX        | TABLE        |         |          |            |           |                           |                |
|           | metadata    | COLUMNS    | SYSTEM TABLE |         |          |            |           |                           |                |
|           | metadata    | TABLES     | SYSTEM TABLE |         |          |            |           |                           |                |
+-----------+-------------+------------+--------------+---------+----------+------------+-----------+---------------------------+----------------+
```


# git命令

实现原理是运行这个git命令

```
git log --format=%h,%at,%ae,%ct,%ce --numstat 
```

# calcite和sqlline

- [calcite](https://github.com/apache/calcite)提供了SQL层和JDBC驱动层
- [sqlline](https://github.com/julianhyde/sqlline)提供了一个命令行交互式的jdbc操作界面
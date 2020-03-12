package com.eoi.dc.calcite.git;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.util.Arrays;
import java.util.List;

public class GitLogTable extends GitAbstractTable implements ScannableTable {

    // the .git directory path
    private String repositoryDir;

    public GitLogTable(String repositoryDir) {
        this.repositoryDir = repositoryDir;
    }

    @Override
    public Enumerable<Object[]> scan(DataContext dataContext) {
        try {
            Process process = Runtime.getRuntime().exec(
                    String.format("git --git-dir=%s log --format=%%h,%%at,%%ae,%%ct,%%ce --numstat",
                            this.repositoryDir)
            );
            return new GitLogTableEnumerator(process.getInputStream(), process.getErrorStream());
        } catch (Exception ex)  {
            return null;
        }
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        JavaTypeFactory factory = (JavaTypeFactory) relDataTypeFactory;
        List<String> columnNames = Arrays.asList("HASH", "AUTHOR_TIME", "AUTHOR", "COMMITTER_TIME", "COMMITTER", "FILE", "ADD", "REMOVE");
        List<RelDataType> columnTypes = Arrays.asList(
                factory.createSqlType(SqlTypeName.VARCHAR),
                factory.createSqlType(SqlTypeName.TIMESTAMP),
                factory.createSqlType(SqlTypeName.VARCHAR),
                factory.createSqlType(SqlTypeName.TIMESTAMP),
                factory.createSqlType(SqlTypeName.VARCHAR),
                factory.createSqlType(SqlTypeName.VARCHAR),
                factory.createSqlType(SqlTypeName.INTEGER),
                factory.createSqlType(SqlTypeName.INTEGER)
        );
        return factory.createStructType(Pair.zip(columnNames, columnTypes));
    }
}

package com.eoi.dc.calcite.git;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TableFactory;

import java.util.Map;

public class GitLogTableFactory implements TableFactory<GitLogTable> {
    @Override
    public GitLogTable create(
            SchemaPlus schemaPlus,
            String name,
            Map<String, Object> operand,
            RelDataType relDataType) {
        final String directory = (String) operand.get("file");
        if ( directory == null || directory.isEmpty() ) {
            throw new RuntimeException("file must be supplied as the path of .git directory");
        }
        return new GitLogTable(directory);
    }
}

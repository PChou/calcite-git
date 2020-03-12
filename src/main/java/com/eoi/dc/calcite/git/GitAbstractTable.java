package com.eoi.dc.calcite.git;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;

public abstract class GitAbstractTable implements Table {

    @Override
    public Statistic getStatistic() {
        return Statistics.UNKNOWN;
    }

    @Override
    public Schema.TableType getJdbcTableType() {
        return Schema.TableType.TABLE;
    }

    @Override
    public boolean isRolledUp(String s) {
        return false;
    }

    @Override
    public boolean rolledUpColumnValidInsideAgg(
            String s, SqlCall sqlCall, SqlNode sqlNode, CalciteConnectionConfig calciteConnectionConfig) {
        return true;
    }
}

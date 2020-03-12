package com.eoi.dc.calcite.git;

import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitLogTableTest {
    @Test
    public void testCommitLineTableScan() throws Exception {
        String pwd = System.getenv("PWD");
        GitLogTable gitLogTable =
                new GitLogTable(String.format("%s/.git",pwd));
        Enumerable<Object[]> et = gitLogTable.scan(null);
        Enumerator<Object[]> it =  et.enumerator();
        while (it.moveNext()) {
            Object[] row = it.current();
            System.out.println(
                    String.format("%s,%d,%s,%d,%s,%s,%d,%d", row[0],
                            (Long) row[1], row[2], (Long) row[3], row[4], row[5], (Integer) row[6], (Integer) row[7]));
        }
    }

}
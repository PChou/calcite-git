package com.eoi.dc.calcite.git;

import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitLogTableEnumerator extends AbstractEnumerable<Object[]>
        implements Enumerator<Object[]> {

    private Object[] current;
    private InputStream stdout;
    private InputStream stderr;
    private BufferedReader stdoutReader;

    private String lastHash;
    private Long lastTimestamp;
    private String lastCommitter;
    // header line like: 39bc26f,1583890041,di.wang@eoitek.com
    private Pattern blockHeaderPattern = Pattern.compile("^([0-9a-f]+),(\\d+),(.+)$");
    private Pattern diffLinePattern = Pattern.compile("^(\\d+)\\s+(\\d+)\\s+(.+)$");

    public GitLogTableEnumerator(InputStream stdout, InputStream stderr) {
        this.current = null;
        this.stdout = stdout;
        this.stderr = stderr;
        this.stdoutReader = new BufferedReader(new InputStreamReader(stdout));
    }

    @Override public Enumerator<Object[]> enumerator() {
        return this;
    }

    @Override public Object[] current() {
        return current;
    }

    /**
     * read a block
     * 39bc26f,1583890041,di.wang@eoitek.com
     *
     * 1       2       jax-web/src/main/java/com/eoi/jax/web/schedule/service/....java
     * 6       3       jax-web/src/main/java/com/eoi/jax/web/service/PipelineService.java
     * @return
     */
    @Override public boolean moveNext() {
        try {
            String line = "";
            while (true) {
                line = this.stdoutReader.readLine();
                Matcher matcher = blockHeaderPattern.matcher(line);
                if (matcher.find()) {
                    lastHash = matcher.group(1);
                    lastTimestamp = Long.valueOf(matcher.group(2)) * 1000;
                    lastCommitter = matcher.group(3);
                } else {
                    Matcher matcher2 = diffLinePattern.matcher(line);
                    if (matcher2.find()) {
                        // produce a row
                        List<Object> row = new ArrayList<>();
                        row.add(lastHash);
                        row.add(lastTimestamp);
                        row.add(lastCommitter);
                        row.add(matcher2.group(3));
                        row.add(Integer.valueOf(matcher2.group(1)));
                        row.add(Integer.valueOf(matcher2.group(2)));
                        current = row.toArray();
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            // read end of stream
            return false;
        }
    }

    @Override public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override public void close() {
        try {
            this.stderr.close();
            this.stdout.close();
        } catch (Exception ex) {
            throw new RuntimeException("Error closing process stdout or stderr", ex);
        }
    }
}

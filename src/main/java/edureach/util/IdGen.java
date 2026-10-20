package edureach.util;

import java.util.concurrent.atomic.AtomicInteger;

/** Tiny monotonic id generator for the file-backed phase. */
public final class IdGen {

    private static final AtomicInteger SEQ = new AtomicInteger(1000);

    private IdGen() {}

    public static String next(String prefix) {
        return prefix + "-" + SEQ.incrementAndGet();
    }
}

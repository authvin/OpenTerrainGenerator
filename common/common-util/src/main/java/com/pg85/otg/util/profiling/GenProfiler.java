package com.pg85.otg.util.profiling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.LongAdder;

/**
 * Lightweight always-on profiler for world generation. Aggregates wall-clock timings and
 * counters per named section across all generation threads (worldgen thread + shadowgen
 * workers), so hotspots can be identified without attaching an external profiler.
 *
 * Usage:
 * <pre>
 *   long t = GenProfiler.start();
 *   ... work ...
 *   GenProfiler.stop("terrain.populateNoise", t);
 * </pre>
 *
 * Timings keep count/total/min/max plus a log2 histogram for approximate percentiles.
 * Counters are plain tallies for events too cheap to time individually (cache hits,
 * per-block density samples).
 *
 * Overhead per stop() is two nanoTime calls plus a few atomic adds; per count() one atomic
 * add. Do not time per-block work — use counters there instead.
 */
public final class GenProfiler {
    private static volatile boolean enabled = true;
    private static final ConcurrentHashMap<String, SectionStats> SECTIONS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LongAdder> COUNTERS = new ConcurrentHashMap<>();
    private static final AtomicLong CHUNKS = new AtomicLong();
    private static volatile long resetTimeMillis = System.currentTimeMillis();

    private GenProfiler() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    /** Start a timing; returns 0 when disabled so the matching stop() is a no-op. */
    public static long start() {
        return enabled ? System.nanoTime() : 0L;
    }

    public static void stop(String section, long startNanos) {
        if (startNanos != 0L) {
            record(section, System.nanoTime() - startNanos);
        }
    }

    public static void record(String section, long elapsedNanos) {
        if (!enabled) {
            return;
        }
        SECTIONS.computeIfAbsent(section, k -> new SectionStats()).record(elapsedNanos);
    }

    public static void count(String counter) {
        count(counter, 1L);
    }

    public static void count(String counter, long delta) {
        if (!enabled) {
            return;
        }
        COUNTERS.computeIfAbsent(counter, k -> new LongAdder()).add(delta);
    }

    /**
     * Resolve a counter once into a reusable handle. Use on hot paths (per-sample,
     * per-block) where a string map lookup per call would dominate the work measured.
     * Store the result in a {@code static final} field.
     */
    public static Counter counter(String name) {
        return new Counter(COUNTERS.computeIfAbsent(name, k -> new LongAdder()));
    }

    /** Cached counter handle. The map lookup is paid once at construction, not per call. */
    public static final class Counter {
        private final LongAdder adder;

        private Counter(LongAdder adder) {
            this.adder = adder;
        }

        public void increment() {
            if (enabled) {
                this.adder.add(1L);
            }
        }

        public void add(long delta) {
            if (enabled) {
                this.adder.add(delta);
            }
        }
    }

    /** Tally a completed chunk; returns the running total (used for periodic auto-reports). */
    public static long chunkCompleted() {
        if (!enabled) {
            return -1L;
        }
        count("chunks.completed");
        return CHUNKS.incrementAndGet();
    }

    public static void reset() {
        SECTIONS.clear();
        // Zero rather than clear: cached Counter handles keep their LongAdder reference.
        COUNTERS.values().forEach(LongAdder::reset);
        CHUNKS.set(0);
        resetTimeMillis = System.currentTimeMillis();
    }

    public static boolean hasData() {
        return !SECTIONS.isEmpty() || !COUNTERS.isEmpty();
    }

    /** Full report: all sections sorted by total time, then all counters. */
    public static String report() {
        return report(Integer.MAX_VALUE);
    }

    /** Report limited to the topN sections by total time (counters always included). */
    public static String report(int topN) {
        StringBuilder sb = new StringBuilder();
        long uptimeMs = System.currentTimeMillis() - resetTimeMillis;
        sb.append(String.format(
                "OTG generation profile — %.1fs since reset, %d chunks completed%s%n",
                uptimeMs / 1000.0, CHUNKS.get(), enabled ? "" : " (collection disabled)"
        ));

        List<Map.Entry<String, SectionStats>> entries = new ArrayList<>(SECTIONS.entrySet());
        entries.sort(Comparator.comparingLong((Map.Entry<String, SectionStats> e) -> e.getValue().totalNanos.sum()).reversed());

        if (entries.isEmpty()) {
            sb.append("No timing data collected yet.").append(System.lineSeparator());
        } else {
            sb.append(String.format(
                    "%-44s %9s %10s %9s %9s %9s %9s%n",
                    "SECTION", "COUNT", "TOTAL", "AVG", "~P50", "~P95", "MAX"
            ));
            int shown = 0;
            for (Map.Entry<String, SectionStats> e : entries) {
                if (shown++ >= topN) {
                    sb.append(String.format("... %d more sections%n", entries.size() - topN));
                    break;
                }
                SectionStats s = e.getValue();
                long count = s.count.sum();
                long total = s.totalNanos.sum();
                sb.append(String.format(
                        "%-44s %9d %10s %9s %9s %9s %9s%n",
                        e.getKey(), count,
                        formatNanos(total),
                        formatNanos(count == 0 ? 0 : total / count),
                        formatNanos(s.percentile(0.50)),
                        formatNanos(s.percentile(0.95)),
                        formatNanos(s.max.get())
                ));
            }
        }

        if (!COUNTERS.isEmpty()) {
            sb.append(String.format("%-44s %9s%n", "COUNTER", "COUNT"));
            COUNTERS.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> sb.append(String.format("%-44s %9d%n", e.getKey(), e.getValue().sum())));
        }
        return sb.toString();
    }

    private static String formatNanos(long nanos) {
        if (nanos < 1_000L) {
            return nanos + "ns";
        }
        if (nanos < 1_000_000L) {
            return String.format("%.1fus", nanos / 1_000.0);
        }
        if (nanos < 1_000_000_000L) {
            return String.format("%.1fms", nanos / 1_000_000.0);
        }
        return String.format("%.2fs", nanos / 1_000_000_000.0);
    }

    private static final class SectionStats {
        private static final int BUCKETS = 64;

        private final LongAdder count = new LongAdder();
        private final LongAdder totalNanos = new LongAdder();
        private final AtomicLong min = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong max = new AtomicLong();
        // Bucket i holds samples in [2^i, 2^(i+1)) nanoseconds.
        private final AtomicLongArray histogram = new AtomicLongArray(BUCKETS);

        private void record(long nanos) {
            if (nanos < 0) {
                nanos = 0;
            }
            this.count.increment();
            this.totalNanos.add(nanos);
            this.min.accumulateAndGet(nanos, Math::min);
            this.max.accumulateAndGet(nanos, Math::max);
            int bucket = nanos == 0 ? 0 : 63 - Long.numberOfLeadingZeros(nanos);
            this.histogram.incrementAndGet(bucket);
        }

        /** Approximate percentile from the log2 histogram (geometric bucket midpoint). */
        private long percentile(double q) {
            long total = this.count.sum();
            if (total == 0) {
                return 0;
            }
            long threshold = (long) Math.ceil(total * q);
            long cumulative = 0;
            for (int i = 0; i < BUCKETS; i++) {
                cumulative += this.histogram.get(i);
                if (cumulative >= threshold) {
                    // ~sqrt(2) * 2^i: geometric midpoint of [2^i, 2^(i+1))
                    return (long) ((1L << i) * 1.5);
                }
            }
            return this.max.get();
        }
    }
}

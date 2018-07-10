package de.digitalcollections.commons.springboot.metrics;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

  protected Map<String, Long> counters = new HashMap<>();
  protected Map<String,Set<Tag>> counterTags = new HashMap<>();
  protected Map<String, Timer> timers = new HashMap<>();

  private final MeterRegistry meterRegistry;

  public MetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  /**
   * Sets the value of a gauge
   * @param name Name of the gauge, postfixed with <tt>.amount</tt>
   * @param value Value of the gauge
   */
  public void setGauge(String name, long value) {
    handleCounter(name, null,null, null, value, null, false);
  }

  /**
   * Sets the value of a gauge
   * @param name Name of the gauge, postfixed with <tt>.amount</tt>
   * @param tag Name of the tag (key is <tt>type</tt>)
   * @param value Value of the gauge
   */
  public void setGauge(String name, String tag, long value) {
    handleCounter(name, "type", tag, null, value, null, false);
  }


  /**
   * Sets the value of a gauge
   * @param name Name of the gauge, postfixed with <tt>.amount</tt>
   * @param tagKey Name of the tag
   * @param tagValue Value of the tag
   * @param value Value of the gauge
   */
  public void setGauge(String name, String tagKey, String tagValue, long value) {
    handleCounter(name, tagKey, tagValue, null, value, null, false);
  }

  /**
   * Increases the gauge counter by one
   * @param name Name of the gauge, postfixed with <tt>.amount</tt>
   * @param tag Name of the tag
   */
  public void increaseCounter(String name, String tag) {
    handleCounter(name, "type", tag, 1L,  null, null, false);
  }

  /**
   * Increases the gauge counter with a custom increment
   * @param name Name of the gauge, postfixed with <tt>.amount</tt>
   * @param tag Name of the tag
   * @param increment Increment value
   */
  public void increaseCounter(String name, String tag, long increment) {
    handleCounter(name, "type", tag, increment, null,null, false);
  }

  /**
   * Increases the gauge counter and logs its accompanied duration
   * @param name Name of the gauge, postfixed with <tt>.amount</tt> and name of the timer, postfixed with <tt>.duration</tt>
   * @param tag Name of the tag
   * @param durationMillis Duration in milliseconds
   */
  public void increaseCounterWithDuration(String name, String tag, Long durationMillis) {
    handleCounter(name, "type", tag, 1L, null, durationMillis, false);
  }

  /**
   * Increases the gauge counter and logs its accompanied duration including percentiles (0.5 and 0.95) and histogram
   * @param name Name of the gauge, postfixed with <tt>.amount</tt> and name of the timer, postfixed with <tt>.duration</tt>
   * @param tag Name of the tag (must not be null)
   * @param durationMillis Duration in milliseconds
   */
  public void increaseCounterWithDurationAndPercentiles(String name, String tag, Long durationMillis) {
    handleCounter(name, "type", tag, 1L, null, durationMillis, true);
  }

  private void handleCounter(String name, String tagKey, String tagValue, Long increment, Long absoluteValue, Long durationMillis, Boolean publishPercentiles) {
    String key = name + ( ( tagKey != null && tagValue != null ) ? "." + tagValue : "");

    if ( increment != null ) {
      // Increase counter value
      counters.put(key, counters.getOrDefault(key, 0L) + increment);
    } else {
      counters.put(key, absoluteValue);
    }

    // Register counter, if it doesn't exist yet
    if (counterTags.get(key) == null) {
      counterTags.put(key, new HashSet<>());
      if ( tagKey != null && tagValue != null ) {
        counterTags.get(key).add(new ImmutableTag(tagKey, tagValue));
        meterRegistry.gauge(name + ".amount", counterTags.get(key), key, counters::get);
      } else {
        meterRegistry.gauge(name + ".amount", counters.get(key));
      }
    }

    if (durationMillis != null && tagKey != null && tagValue != null) {
      // Register Timer
      if (timers.get(key) == null) {
        Timer.Builder timerBuilder = Timer.builder(name + ".duration")
                .tag(tagKey, tagValue);

        if ( publishPercentiles ) {
          timerBuilder = timerBuilder.publishPercentiles(0.5, 0.95)
              .publishPercentileHistogram();
        }

        Timer timer = timerBuilder
          .register(meterRegistry);

        timers.put(key, timer);
      }
      // Record time
      timers.get(key).record(durationMillis, TimeUnit.MILLISECONDS);
    }
  }

  // ------------------------------- only for tests
  protected Map<String, Long> getCounters() {
    return counters;
  }

  protected Map<String, Set<Tag>> getCounterTags() {
    return counterTags;
  }

  protected Map<String, Timer> getTimers() {
    return timers;
  }
}

package de.digitalcollections.commons.springboot.metrics;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MeterRegistry.Config;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricsServiceTest {

  @Mock
  MeterRegistry meterRegistry;

  MetricsService metricsService;

  @BeforeEach
  public void beforeAll() {
    MockitoAnnotations.initMocks(this);
    Config config = mock(Config.class);
    when(meterRegistry.config()).thenReturn(config);
    PauseDetector pauseDetector = mock(PauseDetector.class);
    when(config.pauseDetector()).thenReturn(pauseDetector);
    metricsService = new MetricsService(meterRegistry);
  }


  @Test
  @DisplayName("Initial call registers the counter")
  @SuppressWarnings("unchecked")
  void initialCallRegistersCounter() {
    metricsService.increaseCounter("foo", "bar");
    Set<Tag> expectedTags = new HashSet<>();
    expectedTags.add(new ImmutableTag("type", "bar"));
    verify(meterRegistry, times(1)).gauge(eq("foo.amount"), eq(expectedTags), eq("foo.bar"), any(ToDoubleFunction.class));
  }

  @Test
  @DisplayName("Second call does not register the counter but increments")
  @SuppressWarnings("unchecked")
  void secondCallDoesNotRegisterCounterTwice() {
    metricsService.increaseCounter("foo", "bar");
    Set<Tag> expectedTags = new HashSet<>();
    expectedTags.add(new ImmutableTag("type", "bar"));
    verify(meterRegistry, times(1)).gauge(eq("foo.amount"), eq(expectedTags), eq("foo.bar"), any(ToDoubleFunction.class));
    reset(meterRegistry);
    metricsService.increaseCounter("foo", "bar");
    verify(meterRegistry, never()).gauge(any(String.class), any(Set.class), any(String.class), any(ToDoubleFunction.class));
    assertThat(metricsService.getCounters().get("foo.bar")).isEqualTo(2);
  }

  @Test
  @DisplayName("Increments can be different to 1")
  void differentIncrements() {
    metricsService.increaseCounter("foo", "bar");    // Start with 1
    metricsService.increaseCounter("foo", "bar", 2);    // add 2
    metricsService.increaseCounter("foo", "bar", 20); // add 20
    assertThat(metricsService.getCounters().get("foo.bar")).isEqualTo(23);
  }

  @Test
  @DisplayName("Multiple values of a gauge")
  void multipleValuesOfAGauge() {
    metricsService.setGauge("foo", "bar", 2);
    assertThat(metricsService.getCounters().get("foo.bar")).isEqualTo(2);
    metricsService.setGauge("foo", "bar", 42);
    assertThat(metricsService.getCounters().get("foo.bar")).isEqualTo(42);
  }

  @Test
  @DisplayName("Gauges with different tag keys")
  void gaugesWithDifferentTagKeys() {
    metricsService.setGauge("foo", "bar", "blub", 2);
    assertThat(metricsService.getCounters().get("foo.blub")).isEqualTo(2);
    metricsService.setGauge("foo", "two", 42);
    assertThat(metricsService.getCounters().get("foo.two")).isEqualTo(42);
  }

  @Test
  @DisplayName("Gauges without tag")
  void gaugeWithoutTag() {
    metricsService.setGauge("foo", 2);
    assertThat(metricsService.getCounters().get("foo")).isEqualTo(2);
    metricsService.setGauge("foo", 42);
    assertThat(metricsService.getCounters().get("foo")).isEqualTo(42);
    metricsService.setGauge("bar", 4);
    assertThat(metricsService.getCounters().get("bar")).isEqualTo(4);
    assertThat(metricsService.getCounters().get("foo")).isEqualTo(42);
    verify(meterRegistry, times(1)).gauge(eq("foo.amount"), any());
    verify(meterRegistry, times(1)).gauge(eq("bar.amount"), any());
  }

  @Test
  @DisplayName("Multiple calls for counter and duration only register once")
  @Disabled("Duration registration is not mockable and so not testable with reasonable effort")
  @SuppressWarnings("unchecked")
  void multipleCallsForCounterAndDuration() {
    Set<Tag> expectedTags = new HashSet<>();
    expectedTags.add(new ImmutableTag("type", "bar"));

    metricsService.increaseCounterWithDuration("foo","bar", 123L);
    metricsService.increaseCounterWithDuration("foo","bar", 234L);

    verify(meterRegistry, times(1)).gauge(eq("foo.amount"), eq(expectedTags), eq("foo.bar"), any(ToDoubleFunction.class));
    verify(meterRegistry, times(1)).timer(any(), any(), any());
  }

}
package com.simon.kafka;

import org.junit.jupiter.api.Test;

public class MySourceConnectorConfigTest {
  @Test
  public void doc() {
    System.out.println(MySourceConnectorConfig.conf().toRst());
  }
}
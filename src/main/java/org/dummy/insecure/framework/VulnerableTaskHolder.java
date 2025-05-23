package org.dummy.insecure.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// TODO move back to lesson
public class VulnerableTaskHolder implements Serializable {

  private static final long serialVersionUID = 2;

  private String taskName;
  private String taskAction;
  private LocalDateTime requestedExecutionTime;

  public VulnerableTaskHolder(String taskName, String taskAction) {
    super();
    this.taskName = taskName;
    this.taskAction = taskAction;
    this.requestedExecutionTime = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return "VulnerableTaskHolder [taskName="
        + taskName
        + ", taskAction="
        + taskAction
        + ", requestedExecutionTime="
        + requestedExecutionTime
        + "]";
  }

  /**
   * Execute a task when de-serializing a saved or received object.
   *
   * @author stupid develop
   */
  private void readObject(ObjectInputStream stream) throws Exception {
    // unserialize data so taskName and taskAction are available
    stream.defaultReadObject();

    // do something with the data
    log.info("restoring task: {}", taskName);
    log.info("restoring time: {}", requestedExecutionTime);

    if (requestedExecutionTime != null
        && (requestedExecutionTime.isBefore(LocalDateTime.now().minusMinutes(10))
            || requestedExecutionTime.isAfter(LocalDateTime.now()))) {
      // do nothing is the time is not within 10 minutes after the object has been created
      log.debug(this.toString());
      throw new IllegalArgumentException("outdated");
    }

    // Avoid executing arbitrary commands during deserialization
    if ((taskAction.startsWith("sleep") || taskAction.startsWith("ping"))
        && taskAction.length() < 22) {
      log.info("about to execute: {}", taskAction);
      // Command execution disabled for safety
    }
  }
}

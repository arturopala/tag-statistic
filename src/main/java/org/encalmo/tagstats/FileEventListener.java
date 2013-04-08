package org.encalmo.tagstats;

import java.nio.file.Path;


public interface FileEventListener {
  void fileCreated(Path path);

  void fileModified(Path path);

  void fileDeleted(Path path);

  boolean initialFilesAlreadyProcessed() throws Exception;
}

abstract class SimpleFileEventListener implements FileEventListener {
  @Override
  public void fileCreated(Path path) {
  }

  @Override
  public void fileModified(Path path) {
  }

  @Override
  public void fileDeleted(Path path) {
  }

  @Override
  public boolean initialFilesAlreadyProcessed() throws Exception {
    return true;
  }
}

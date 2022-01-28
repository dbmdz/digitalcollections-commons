package de.digitalcollections.commons.file.backend;

import de.digitalcollections.model.exception.ResourceIOException;

/**
 * A subtype of ResourceIOException to indicate that an honest-to-god actual IOException (as in
 * harddisk broken, network share unavailable or a cosmic ray hit the CPU in just the right way for
 * the syscall to fail.)
 */
public class FileSystemResourceIOException extends ResourceIOException {

  public FileSystemResourceIOException(Throwable ex) {
    super(ex);
  }
}

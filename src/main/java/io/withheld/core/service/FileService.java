/* Licensed under Apache-2.0 */
package io.withheld.core.service;

import io.withheld.core.domain.Project;
import reactor.core.publisher.Mono;

/**
 * Handles file operations on the host. Events such as scanning for new files and managing last
 * modified time.
 *
 * @author Chris Turner
 */
public interface FileService {

  /**
   * Given a path and a filter, find all files that match and return their paths.
   *
   * @param directory base directory to recursively scan
   * @return a flux of all files found
   */
  Mono<Project> importFiles(final Project directory);
}

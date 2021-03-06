/* Licensed under Apache-2.0 */
package io.withheld.core.service;

import io.withheld.core.domain.FirebaseUser;
import io.withheld.core.domain.Project;
import io.withheld.core.repositories.CodeFileRepository;
import io.withheld.core.repositories.ProjectRepository;
import io.withheld.core.utils.AuthUtils;
import io.withheld.core.utils.ProjectUtils;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** @author Chris Turner (chris@forloop.space) */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  private final CodeFileRepository codeFileRepository;

  private final FirestoreService firestoreService;

  private final GitService gitService;

  @Override
  public Mono<Project> create(final Project project)
      throws ExecutionException, InterruptedException {

    final List<FirebaseUser> reviewGroups =
        firestoreService.findAllWithReviewGroups(project.getReviewGroups());

    project.setReviewers(reviewGroups);

    return gitService.clone(project);
  }

  @Override
  public Mono<Project> patch(final Project project) {

    return projectRepository.save(project);
  }

  @Override
  public Mono<Project> findById(final String projectId) {

    return projectRepository.findById(projectId).flatMap(ProjectUtils::setReviewStatus);
  }

  @Override
  public Flux<Project> findAll() {

    return AuthUtils.getAuthentication()
        .flux()
        .flatMap(
            authentication ->
                projectRepository.findAllReviewersByUid(authentication.getPrincipal().toString()))
        .flatMap(project -> ProjectUtils.setReviewStatus(project).flux());
  }

  @Override
  public Mono<Void> delete(final String projectId) {

    return codeFileRepository
        .deleteAllByProjectId(projectId)
        .then(projectRepository.deleteById(projectId));
  }
}

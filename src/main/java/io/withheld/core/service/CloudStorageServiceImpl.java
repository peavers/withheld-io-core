/* Licensed under Apache-2.0 */
package io.withheld.core.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** @author Chris Turner (chris@forloop.space) */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudStorageServiceImpl implements CloudStorageService {

  private final Storage storage;

  @Override
  public String uploadFile(final File file, final String name, final String bucketName)
      throws IOException {

    final BlobInfo blobInfo =
        BlobInfo.newBuilder(bucketName, name)
            .setAcl(
                new ArrayList<>(Collections.singletonList(Acl.of(User.ofAllUsers(), Role.READER))))
            .build();

    return storage.create(blobInfo, Files.readAllBytes(file.toPath())).getMediaLink();
  }
}

package org.example.gridgestagram.service.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.FileUploadInfo;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.files.FilesRepository;
import org.example.gridgestagram.repository.files.entity.Files;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;

    public void saveFiles(Feed feed, List<FileUploadInfo> fileInfos) {
        try {
            List<Files> filesToSave = fileInfos.stream()
                .map(fileInfo -> {
                    Files file = Files.create(feed, fileInfo.getUrl(), fileInfo.getOrder());
                    feed.addFile(file);
                    return file;
                })
                .toList();

            filesRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_SAVE_FAILED);
        }
    }

}

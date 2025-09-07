package com.eventty.eventtynextgen.asset.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartConvertHelper {

    private MultipartConvertHelper() {
    }

    public static MockMultipartFile convertMultipartFile(String filePath, String contentType) throws Exception {
        return convertMultipartFile(filePath, contentType, "file");
    }

    public static List<MockMultipartFile> convertMultipartFiles(List<MultipartFileInfo> fileInfoList) throws Exception {
        return fileInfoList.stream()
            .map(fileInfo -> {
                try {
                    return convertMultipartFile(fileInfo.filePath, fileInfo.contentType, "files");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();
    }

    private static MockMultipartFile convertMultipartFile(String filePath, String contentType, String name) throws Exception{
        File file = new File(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return new MockMultipartFile(
                name,
                file.getName(),
                contentType,
                fileInputStream);
        }
    }

    // TODO: convertMultipartFile 메서드와 무엇이 좋은지 비교
    public static MockMultipartFile convertMultipartFileUsingFiles(String filePath, String contentType) throws Exception {
        Path path = java.nio.file.Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);

        return new MockMultipartFile(
            "file_name",
            path.getFileName().toString(),
            contentType,
            fileContent
        );
    }

    public record MultipartFileInfo (String filePath, String contentType) {}
}

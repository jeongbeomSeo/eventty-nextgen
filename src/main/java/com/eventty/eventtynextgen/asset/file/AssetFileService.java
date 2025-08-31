package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import jakarta.servlet.ServletInputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AssetFileService {

    AssetUploadAssetFile uploadMultipartFile(MultipartFile file, String context);

    List<AssetUploadAssetFile> uploadMultipartFiles(List<MultipartFile> files, String context);

    AssetUploadAssetFile uploadStreaming(ServletInputStream inputStream, String context);
}

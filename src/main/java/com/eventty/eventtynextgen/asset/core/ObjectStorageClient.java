package com.eventty.eventtynextgen.asset.core;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageClient {

    UploadFileResult uploadMultipartFile(MultipartFile file, Context context);

    UploadFileResult uploadStreaming(InputStream inputStream, Context context, @Nullable String contentType);

    boolean deleteFile(String fileName, Context context);

    String findFileDownloadLink(String fileName, Context context);

    String findFileUrl(String fileName, Context context);

    FindFileUrlResult findFileUrls(List<String> fileNames, Context context);

    boolean existsFile(String fileName, Context context);

    enum Context {
        FILE,
        LARGE_FILE,
        EVENT_IMAGE,
        EVENT_VIDEO;

        public static Optional<Context> getContext(String context) {
            for (Context ctx : Context.values()) {
                if (ctx.name().equalsIgnoreCase(context)) {
                    return Optional.of(ctx);
                }
            }
            return Optional.empty();
        }
    }

    record FindFileUrlResult(List<String> fileUrls, List<String> failedFileNames) {}

    record UploadFileResult(String fileName, long contentLength, String contentType) {}
}

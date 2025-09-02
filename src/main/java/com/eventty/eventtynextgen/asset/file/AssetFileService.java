package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import jakarta.servlet.ServletInputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AssetFileService {

    AssetUploadAssetFile uploadMultipartFile(MultipartFile file, String context);

    List<AssetUploadAssetFile> uploadMultipartFiles(List<MultipartFile> files, String context);

    /**
     * ServletInputStream을 통해 원시 바이트 스트림을 업로드합니다.
     *
     * <p>역할: 요청 본문에서 전달된 입력 스트림을 읽어 파일로 저장하고,
     * 업로드 컨텍스트에 맞는 메타데이터를 생성합니다.</p>
     *
     * <p>제약 사항:</p>
     * <ul>
     *   <li>파일 크기: 스트림을 모두 읽기 전에는 총 크기를 알 수 없음</li>
     *   <li>Content-Type: HTTP 헤더 또는 인자로 신뢰해야 하며 검증이 어려움</li>
     *   <li>파일 확장자: 파일명이 없어 확장자 추출 불가</li>
     * </ul>
     *
     * @param inputStream 업로드할 파일의 입력 스트림
     * @param contentType 헤더로부터 받아온 컨텐츠 타입
     * @param context 업로드 컨텍스트 식별자
     * @return 업로드된 파일 정보
     * @deprecated 제약 사항으로 인해 사용이 권장되지 않습니다. 대신 {@link #uploadMultipartFile(MultipartFile, String)}을 사용하세요.
     */
    @Deprecated
    AssetUploadAssetFile uploadStreaming(ServletInputStream inputStream, String contentType, String context);
}

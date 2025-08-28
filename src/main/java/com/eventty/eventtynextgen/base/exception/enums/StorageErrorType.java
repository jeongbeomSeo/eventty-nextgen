package com.eventty.eventtynextgen.base.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageErrorType implements ErrorType {

    NOT_FOUND_BUCKET_NAME("NOT_FOUND_BUCKET_NAME", "Bucket Name을 찾을 수 없습니다. 파일 업로드 목적과 매칭되는 버킷이 존재하는지 다시 한번 확인해주세요."),
    NOT_FOUND_FILES("NOT_FOUND_FILES", "모든 파일을 찾을 수 없습니다.");

    private final String code;
    private final String msg;
}

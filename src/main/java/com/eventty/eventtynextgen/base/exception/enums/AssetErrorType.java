package com.eventty.eventtynextgen.base.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetErrorType implements ErrorType {

    // File
    INVALID_FILE_SIZE("INVALID_FILE_SIZE", "파일의 크기가 허용치를 초과했습니다."),
    INVALID_FILE_CONTENT_TYPE("INVALID_FILE_CONTENT_TYPE", "파일의 Content Type이 허용되지 않습니다."),
    INVALID_FILE_EXTENSION("INVALID_FILE_EXTENSION", "파일의 확장자가 허용되지 않습니다."),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드에 실패하였습니다"),
    FILE_ALREADY_EXISTS("FILE_ALREADY_EXISTS", "동일한 파일명이 이미 존재합니다."),

    // Storage
    NOT_FOUND_BUCKET_NAME("NOT_FOUND_BUCKET_NAME", "Bucket Name을 찾을 수 없습니다. 파일 업로드 목적과 매칭되는 버킷이 존재하는지 다시 한번 확인해주세요."),
    NOT_FOUND_FILES("NOT_FOUND_FILES", "모든 파일을 찾을 수 없습니다."),

    // Common
    ILLEGAL_ARGUMENT_FILE_CONTEXT("ILLEGAL_ARGUMENT_FILE_CONTEXT", "허용하지 않은 File Context 인자가 들어왔습니다.");

    private final String code;
    private final String msg;
}

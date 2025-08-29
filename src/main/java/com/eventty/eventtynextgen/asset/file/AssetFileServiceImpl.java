package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetFileServiceImpl implements AssetFileService {

    private final ObjectStorageClient objectStorageClient;


}

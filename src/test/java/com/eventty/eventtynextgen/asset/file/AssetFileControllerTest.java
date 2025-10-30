package com.eventty.eventtynextgen.asset.file;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.fixture.FileMetadataFixture;
import com.eventty.eventtynextgen.asset.file.repository.FileMetadataRepository;
import com.eventty.eventtynextgen.asset.file.request.AssetFileUploadMultipartFileRequestCommand;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFileResponseView;
import com.eventty.eventtynextgen.asset.utils.MultipartConvertHelper;
import com.eventty.eventtynextgen.asset.utils.MultipartConvertHelper.MultipartFileInfo;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.base.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.base.fixture.CertificationTokenFixture;
import com.eventty.eventtynextgen.base.fixture.SessionTokenFixture;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Asset File Controller 통합 테스트")
class AssetFileControllerTest {

    private static final String BASE_URL = "/api/v1/asset/file";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectStorageClient objectStorageClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("MultipartFile 파일 업로드 API 테스트")
    class UploadMultipartFile {

        @BeforeEach
        public void setup() {
            fileMetadataRepository.deleteAllInBatch();
        }

        private static final String URL = BASE_URL + "/multipart-file";

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("용량이 512KB인 MultipartFile 파일 업로드에 성공한다")
        void 용량이_512KB인_multipartFile_파일_업로드에_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");

            long sizeInBytes = 512 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.zip", "application/zip", content);

            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.contentType").value("application/zip"));

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            AssetUploadAssetFileResponseView assetUploadAssetFile = objectMapper.readValue(contentAsString, AssetUploadAssetFileResponseView.class);

            objectStorageClient.deleteFile(user.getId() + "/" + assetUploadAssetFile.fileName(), StorageContext.FILE);
        }

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("용량이 15MB인 MultipartFile 파일 업로드에 성공한다")
        void 용량이_15MB인_multipartFile_파일_업로드에_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 15 * 1024 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.yml", "application/yaml", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.contentType").value("application/yaml"));

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            AssetUploadAssetFileResponseView assetUploadAssetFile = objectMapper.readValue(contentAsString, AssetUploadAssetFileResponseView.class);

            objectStorageClient.deleteFile(user.getId() + "/" + assetUploadAssetFile.fileName(), StorageContext.FILE);
        }

        @Test
        @DisplayName("제한 용량(25MB)이 초과한 MultipartFile 파일 업로드에 실패한다")
        void 제한_용량이_초과한_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 26 * 1024 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.zip", "application/zip", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_SIZE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_SIZE.getMsg()));
        }

        @Test
        @DisplayName("이미지 컨텐츠 타입의 MultipartFile 파일 업로드에 실패한다")
        void 이미지_컨텐츠_타입의_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 512 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.jpg", "image/jpeg", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getMsg()));
        }

        @Test
        @DisplayName("동영상 컨텐츠 타입의 MultipartFile 파일 업로드에 실패한다")
        void 동영상_컨텐츠_타입의_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 1 * 1024 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.mp4", "video/mp4", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getMsg()));
        }

        @Test
        @DisplayName("이미지 확장자 타입의 MultipartFile 파일 업로드에 실패한다")
        void 이미지_확장자_타입의_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 1 * 1024 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.jpg", "multipart/form-data", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_EXTENSION.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_EXTENSION.getMsg()));
        }

        @Test
        @DisplayName("동영상 확장자 타입의 MultipartFile 파일 업로드에 실패한다")
        void 동영상_확장자_타입의_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            AssetFileUploadMultipartFileRequestCommand requestCommand = new AssetFileUploadMultipartFileRequestCommand("테스트용파일1");
            MockMultipartFile requestCommandJson = new MockMultipartFile("requestCommand", "", "application/json",
                objectMapper.writeValueAsString(requestCommand).getBytes());

            long sizeInBytes = 1 * 1024 * 1024;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile multipartFile = new MockMultipartFile("file", "file_name.mp4", "multipart/form-data", content);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL)
                .file(multipartFile)
                .file(requestCommandJson)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_EXTENSION.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_EXTENSION.getMsg()));
        }
    }

    @Disabled(value = "복합 파일 업로드 API는 deprecated 되었습니다.")
    @Nested
    @DisplayName("MultipartFile 파일 리스트 업로드 API 테스트")
    class uploadMultipartFiles {

        private static final String URL = BASE_URL + "/multipart-files";

        @Test
        @DisplayName("용량이 512KB인 MultipartFile 파일 3개를 업로드하는데 성공한다")
        void 용량이_512KB인_MultipartFile_파일_3개를_업로드하는데_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].fileName").isNotEmpty())
                .andExpect(jsonPath("$[*].contentType").isNotEmpty());

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            List<AssetUploadAssetFileResponseView> assetUploadAssetFiles = objectMapper.readValue(contentAsString, new TypeReference<>() {
            });
            assetUploadAssetFiles.forEach(assetFile -> objectStorageClient.deleteFile(assetFile.fileName(), StorageContext.FILE));
        }

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("용량이 15MB인 MultipartFile 파일 3개를 업로드하는데 성공한다")
        void 용량이_15MB인_MultipartFile_파일_3개를_업로드하는데_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/15MB.yml";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].fileName").isNotEmpty())
                .andExpect(jsonPath("$[*].contentType").isNotEmpty());

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            List<AssetUploadAssetFileResponseView> assetUploadAssetFiles = objectMapper.readValue(contentAsString, new TypeReference<>() {
            });
            assetUploadAssetFiles.forEach(assetFile -> objectStorageClient.deleteFile(assetFile.fileName(), StorageContext.FILE));
        }

        @Test
        @DisplayName("제한 용량이 초과한 MultipartFile이 1개 이상 포함되어 있을 경우 모든 파일의 업로드에 실패한다")
        void 제한_용량이_초과한_MultipartFile이_1개_이상_포함되어_있을_경우_모든_파일의_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/files/26MB.zip", contentType));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_SIZE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_SIZE.getMsg()));
        }

        @Test
        @DisplayName("이미지 컨텐츠 타입의 MultipartFile이 1개 이상 포함되어 있을 경우 모든 파일의 업로드에 실패한다")
        void 이미지_컨텐츠_타입의_MultipartFile이_1개_이상_포함되어_있을_경우_모든_파일의_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/images/512KB_size_image.jpg", "image/jpeg"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getMsg()));
        }

        @Test
        @DisplayName("동영상 컨텐츠 타입의 MultipartFile이 1개 이상 포함되어 있을 경우 모든 파일의 업로드에 실패한다")
        void 동영상_컨텐츠_타입의_MultipartFile이_1개_이상_포함되어_있을_경우_모든_파일의_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/videos/1MB.mp4", "video/mp4"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_CONTENT_TYPE.getMsg()));
        }

        @Test
        @DisplayName("이미지 확장자 타입의 MultipartFile이 1개 이상 포함되어 있을 경우 모든 파일의 업로드에 실패한다")
        void 이미지_확장자_타입의_MultipartFile이_1개_이상_포함되어_있을_경우_모든_파일의_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/images/512KB_size_image.jpg", "multipart/form-data"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_EXTENSION.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_EXTENSION.getMsg()));
        }

        @Test
        @DisplayName("동영상 확장자 타입의 MultipartFile이 1개 이상 포함되어 있을 경우 모든 파일의 업로드에 실패한다")
        void 동영상_확장자_타입의_MultipartFile이_1개_이상_포함되어_있을_경우_모든_파일의_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/videos/1MB.mp4", "multipart/form-data"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_EXTENSION.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_EXTENSION.getMsg()));
        }

        @Test
        @DisplayName("이미지 관련 Context가 요청 데이터로 들어온 경우 모든 파일 업로드에 실패한다")
        void 이미지_관련_Context가_요청_데이터로_들어온_경우_모든_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }

        @Test
        @DisplayName("동영상 관련 Context가 요청 데이터로 들어온 경우 모든 파일 업로드에 실패한다")
        void 동영상_관련_Context가_요청_데이터로_들어온_경우_모든_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }

        @Test
        @DisplayName("알 수 없는 Context가 요청 데이터로 들어온 경우 모든 파일 업로드에 실패한다")
        void 알_수_없는_Context가_요청_데이터로_들어온_경우_모든_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/512KB.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/")
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken());

            mockMultipartFiles.forEach(requestBuilder::file);

            ResultActions resultActions = mockMvc.perform(requestBuilder);

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }
    }

    @Disabled(value = "Deprecated된 API입니다.")
    @Nested
    @DisplayName("Streaming 방식의 파일 업로드 API 테스트")
    class UploadStreamFile {

        private static final String URL = BASE_URL + "/stream-file";

        @Test
        @DisplayName("용량이 15MB인 파일을 스트리밍 방식으로 업로드하는데 성공한다")
        void 용량이_15MB인_파일을_스트리밍_방식으로_업로드하는데_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            // 테스트용 파일 경로 및 파일 읽기
            String filePath = "src/test/resources/files/15MB.yml";
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // 파일 이름 및 컨텐츠 타입 설정
            String contentType = "application/yaml";

            // when
            ResultActions resultActions = mockMvc.perform(post(URL + "/")
                .content(fileContent)
                .contentType(contentType)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.contentType").value(contentType));

            // 업로드된 파일 삭제
            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            AssetUploadAssetFileResponseView assetUploadAssetFile = objectMapper.readValue(contentAsString, AssetUploadAssetFileResponseView.class);
            objectStorageClient.deleteFile(assetUploadAssetFile.fileName(), StorageContext.FILE);
        }
    }

    @Nested
    @DisplayName("사용자별 모든 파일 메타데이터 조회 API")
    class findFileMetadata {

        @BeforeEach
        public void setup() {
            fileMetadataRepository.deleteAllInBatch();
        }

        private static final String URL = BASE_URL + "/metadata";

        @Test
        @DisplayName("사용자별 모든 파일 메타데이터를 조회하는데 성공한다")
        void 사용자별_모든_파일_메타데이터를_조회하는데_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            FileMetadata fileMetadata1 = FileMetadataFixture.createFileMetadata(userFromDb.getId(), "테스트용파일1");
            fileMetadataRepository.save(fileMetadata1);
            FileMetadata fileMetadata2 = FileMetadataFixture.createFileMetadata(userFromDb.getId(), "테스트용파일2");
            fileMetadataRepository.save(fileMetadata2);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.fileMetadataList.size()").value(2));
        }

        @Test
        @DisplayName("해당 사용자가 업로드한 파일 중 삭제된 파일이 존재할 경우 해당 파일 메타데이터는 제외하고 응답한다")
        void 해당_사용자가_업로드한_파일_중_삭제된_파일이_존재할_경우_해당_파일_메타데이터는_제외하고_응답한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            FileMetadata fileMetadata1 = FileMetadataFixture.createFileMetadata(userFromDb.getId(), "테스트용파일1");
            fileMetadataRepository.save(fileMetadata1);
            FileMetadata fileMetadata2 = FileMetadataFixture.createFileMetadata(userFromDb.getId(), "테스트용파일2");
            fileMetadataRepository.save(fileMetadata2);
            FileMetadata fileMetadata3 = FileMetadataFixture.createDeletedFileMetadata(userFromDb.getId(), "테스트용파일3");
            fileMetadataRepository.save(fileMetadata3);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.fileMetadataList.size()").value(2));
        }

        @Test
        @DisplayName("해당 사용자가 업로드한 파일이 존재하지 않을 경우 모든 파일 메타데이터 조회 결과는 빈 리스트이다")
        void 해당_사용자가_업로드한_파일이_존재하지_않을_경우_모든_파일_메타데이터_조회_결과는_빈_리스트이다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.fileMetadataList.size()").value(0));

        }
    }

    @Nested
    @DisplayName("단일 파일 메타데이터 조회 API")
    class GetFileMetadata {

        @BeforeEach
        public void setup() {
            fileMetadataRepository.deleteAllInBatch();
        }

        private static final String URL = BASE_URL + "/metadata";

        @Test
        @DisplayName("파일 메타데이터 조회에 성공한다")
        void 파일_메타데이터_조회에_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            FileMetadata fileMetadata = FileMetadataFixture.createFileMetadata(userFromDb.getId(), "테스트용파일1");
            FileMetadata fileMetadataFromDb = fileMetadataRepository.save(fileMetadata);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL + "/" + fileMetadataFromDb.getId())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileMetadataId").value(fileMetadataFromDb.getId()))
                .andExpect(jsonPath("$.fileName").value(fileMetadataFromDb.getFileName()))
                .andExpect(jsonPath("$.contentType").value(fileMetadataFromDb.getContentType()))
                .andExpect(jsonPath("$.fileUrl").value(fileMetadataFromDb.getFileUrl()));
        }

        @Test
        @DisplayName("존재하지 않는 파일 메타데이터 조회에 실패한다")
        void 존재하지_않는_파일_메타데이터_조회에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.NOT_FOUND, AssetErrorType.NOT_FOUND_FILE_METADATA)
            );

            // when
            ResultActions resultActions = mockMvc.perform(get(URL + "/" + 1)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("삭제된 파일 메타데이터 조회에 실패한다")
        void 삭제된_파일_메타데이터_조회에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            FileMetadata deletedFileMetadata = FileMetadataFixture.createDeletedFileMetadata(userFromDb.getId(), "테스트용파일1");
            FileMetadata deletedFileMetadataFromDb = fileMetadataRepository.save(deletedFileMetadata);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.NOT_ALLOW_ACCESS_DELETED_FILE)
            );

            // when
            ResultActions resultActions = mockMvc.perform(get(URL + "/" + deletedFileMetadataFromDb.getId())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isForbidden())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("본인이 업로드한 파일이 아니라면 해당 파일 메타데이터 조회에 실패한다")
        void 본인이_업로드한_파일이_아니라면_해당_파일_메타데이터_조회에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            FileMetadata fileMetadata = FileMetadataFixture.createFileMetadata(userFromDb.getId() + 1, "테스트용파일1");
            FileMetadata fileMetadataFromDb = fileMetadataRepository.save(fileMetadata);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.UNAUTHORIZED_FILE_ACCESS)
            );

            // when
            ResultActions resultActions = mockMvc.perform(get(URL + "/" + fileMetadataFromDb.getId())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isForbidden())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }

    @Nested
    @DisplayName("파일 다운로드 API")
    class DownloadFile {

        @BeforeEach
        public void setup() {
            fileMetadataRepository.deleteAllInBatch();
        }

        private static final String URL = BASE_URL + "/download";

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("파일 다운로드 API 호출 권한 검증에 성공하고 유효한 파일 메타데이터 ID를 파라미터로 전달하여 파일 다운로드 링크를 반환한다")
        void 파일_다운로드_API_호출_권한_검증에_성공하고_유효한_파일_메타데이터_ID를_파라미터로_전달하여_파일_다운로드_링크를_반환한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String fileName = userFromDb.getId() + "/" + "file";
            FileMetadata fileMetadata = FileMetadataFixture.createFileMetadata(userFromDb.getId(), fileName);
            FileMetadata fileMetadataFromDb = fileMetadataRepository.save(fileMetadata);
            MockMultipartFile file = new MockMultipartFile(fileName, fileName + ".json", fileMetadata.getContentType(), "content_type: json".getBytes());
            objectStorageClient.uploadMultipartFile(file, fileName, StorageContext.FILE);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .param("fileMetadataId", fileMetadataFromDb.getId().toString()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition", "attachment; filename=\"" + fileMetadataFromDb.getFileName() + "\""))
                .andExpect(jsonPath("$.fileMetadataId").value(fileMetadataFromDb.getId()))
                .andExpect(jsonPath("$.fileName").value(fileMetadataFromDb.getFileName()))
                .andExpect(jsonPath("$.contentType").value(fileMetadataFromDb.getContentType()))
                .andExpect(jsonPath("$.downloadLink").isNotEmpty());

            objectStorageClient.deleteFile(fileName, StorageContext.FILE);
        }

        @Test
        @DisplayName("파일 다운로드 API 호출 권한 검증에 실패하면 예외 메시지를 전달한다")
        void 파일_다운로드_API_호출_권한_검증에_실패하면_예외_메시지를_전달한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledUser();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.AUTH_USER_NOT_AUTHORIZED));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .param("fileMetadataId", "1"));

            // then
            resultActions.andExpect(status().isForbidden())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("파일 다운로드 API 호출 권한 검증에 성공하나 존재하지 않는 파일 메타데이터 ID를 파라미터로 전달하면 예외 메시지를 전달한다")
        void 파일_다운로드_API_호출_권한_검증에_성공하나_존재하지_않는_파일_메타데이터_ID를_파라미터로_전달하면_예외_메시지를_전달한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.NOT_FOUND, AssetErrorType.NOT_FOUND_FILE_METADATA));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .param("fileMetadataId", "1"));

            // then
            resultActions.andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("파일 다운로드 API 호출 권한 검증에 성공하나 삭제된 파일 메타데이터를 조회한 경우 예외 메시지를 전달한다")
        void 파일_다운로드_API_호출_권한_검증에_성공하나_삭제된_파일_메타데이터를_조회한_경우_예외_메시지를_전달한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String fileName = userFromDb.getId() + "/" + "file";
            FileMetadata fileMetadata = FileMetadataFixture.createDeletedFileMetadata(userFromDb.getId(), fileName);
            FileMetadata fileMetadataFromDb = fileMetadataRepository.save(fileMetadata);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.NOT_ALLOW_ACCESS_DELETED_FILE));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .param("fileMetadataId", fileMetadataFromDb.getId().toString()));

            // then
            resultActions.andExpect(status().isForbidden())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("파일 다운로드 API 호출 권한 검증에 성공하나 본인이 업로드하지 않은 파일 메타데이터를 조회한 경우 예외 메시지를 전달한다")
        void 파일_다운로드_API_호출_권한_검증에_성공하나_본인이_업로드하지_않은_파일_메타데이터를_조회한_경우_예외_메시지를_전달한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String fileName = userFromDb.getId() + "/" + "file";
            FileMetadata fileMetadata = FileMetadataFixture.createDeletedFileMetadata(userFromDb.getId() + 1, fileName);
            FileMetadata fileMetadataFromDb = fileMetadataRepository.save(fileMetadata);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.UNAUTHORIZED_FILE_ACCESS));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .param("fileMetadataId", fileMetadataFromDb.getId().toString()));

            // then
            resultActions.andExpect(status().isForbidden())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}
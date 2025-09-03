package com.eventty.eventtynextgen.asset.file;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.asset.utils.MultipartConvertHelper;
import com.eventty.eventtynextgen.asset.utils.MultipartConvertHelper.MultipartFileInfo;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.fixture.CertificationTokenFixture;
import com.eventty.eventtynextgen.base.fixture.SessionTokenFixture;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

//@Tag("ExternalIntegration")
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

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("MultipartFile 파일 업로드 API 테스트")
    class UploadMultipartFile {

        private static final String URL = BASE_URL + "/multipart-file";

        @Test
        @DisplayName("용량이 512KB인 MultipartFile 파일 업로드에 성공한다")
        void 용량이_512KB인_multipartFile_파일_업로드에_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/512k.zip", "application/zip");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.contentType").value("application/zip"));

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            AssetUploadAssetFile assetUploadAssetFile = objectMapper.readValue(contentAsString, AssetUploadAssetFile.class);
            objectStorageClient.deleteFile(assetUploadAssetFile.fileName(), StorageContext.FILE);
        }

        @Test
        @DisplayName("용량이 15MB인 MultipartFile 파일 업로드에 성공한다")
        void 용량이_15MB인_multipartFile_파일_업로드에_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/15m.yml", "application/yaml");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.contentType").value("application/yaml"));

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            AssetUploadAssetFile assetUploadAssetFile = objectMapper.readValue(contentAsString, AssetUploadAssetFile.class);
            objectStorageClient.deleteFile(assetUploadAssetFile.fileName(), StorageContext.FILE);
        }

        @Test
        @DisplayName("제한 용량(25MB)이 초과한 MultipartFile 파일 업로드에 실패한다")
        void 제한_용량이_초과한_MultipartFile_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/26m.zip", "application/zip");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
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

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/images/512KB_size_image.jpg", "image/jpeg");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
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

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/videos/1m.mp4", "video/mp4");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
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

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/images/512KB_size_image.jpg",
                "multipart/form-data");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
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

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/videos/1m.mp4", "multipart/form-data");
            String context = "file";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.INVALID_FILE_EXTENSION.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.INVALID_FILE_EXTENSION.getMsg()));
        }

        @Test
        @DisplayName("이미지 관련 Context가 요청 데이터로 들어온 경우 파일 업로드에 실패한다")
        void 이미지_관련_Context가_요청_데이터로_들어온_경우_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/512k.zip", "application/zip");
            String context = "event_image";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }

        @Test
        @DisplayName("동영상 관련 Context가 요청 데이터로 들어온 경우 파일 업로드에 실패한다")
        void 동영상_관련_Context가_요청_데이터로_들어온_경우_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/512k.zip", "application/zip");
            String context = "event_video";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }

        @Test
        @DisplayName("알 수 없는 Context가 요청 데이터로 들어온 경우 파일 업로드에 실패한다")
        void 알_수_없는_Context가_요청_데이터로_들어온_경우_파일_업로드에_실패한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            MockMultipartFile multipartFile = MultipartConvertHelper.convertMultipartFile("src/test/resources/files/512k.zip", "application/zip");
            String context = "unknown_context";

            // when
            ResultActions resultActions = mockMvc.perform(multipart(URL + "/" + context)
                .file(multipartFile)
                .contentType(MULTIPART_FORM_DATA)
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken()));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getCode()))
                .andExpect(jsonPath("$.msg").value(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT.getMsg()));
        }
    }

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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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
            List<AssetUploadAssetFile> assetUploadAssetFiles = objectMapper.readValue(contentAsString, new TypeReference<>() {
            });
            assetUploadAssetFiles.forEach(assetFile -> objectStorageClient.deleteFile(assetFile.fileName(), StorageContext.FILE));
        }

        @Test
        @DisplayName("용량이 15MB인 MultipartFile 파일 3개를 업로드하는데 성공한다")
        void 용량이_15MB인_MultipartFile_파일_3개를_업로드하는데_성공한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();
            User user = UserFixture.createUserWithRoledHost();
            User userFromDb = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(userFromDb.getId());

            String filePath = "src/test/resources/files/15m.yml";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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
            List<AssetUploadAssetFile> assetUploadAssetFiles = objectMapper.readValue(contentAsString, new TypeReference<>() {
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/files/26m.zip", contentType));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/images/512KB_size_image.jpg", "image/jpeg"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/videos/1m.mp4", "video/mp4"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/images/512KB_size_image.jpg", "multipart/form-data"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = new ArrayList<>(IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList());
            fileInfoList.add(new MultipartFileInfo("src/test/resources/videos/1m.mp4", "multipart/form-data"));

            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "file";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "event_image";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "event_video";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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

            String filePath = "src/test/resources/files/512k.zip";
            String contentType = "application/zip";

            List<MultipartFileInfo> fileInfoList = IntStream.range(0, 3).mapToObj(i -> new MultipartFileInfo(filePath, contentType)).toList();
            List<MockMultipartFile> mockMultipartFiles = MultipartConvertHelper.convertMultipartFiles(fileInfoList);
            String context = "unknown_context";

            // when
            MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(URL + "/" + context)
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
}
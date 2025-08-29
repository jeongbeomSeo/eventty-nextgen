package com.eventty.eventtynextgen.events;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.base.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.base.fixture.CertificationTokenFixture;
import com.eventty.eventtynextgen.base.fixture.SessionTokenFixture;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import com.eventty.eventtynextgen.events.fixture.CreateEventRequestFixture;
import com.eventty.eventtynextgen.events.repository.EventBasicRepository;
import com.eventty.eventtynextgen.events.repository.EventDetailsRepository;
import com.eventty.eventtynextgen.events.request.EventsCreateEventRequestCommand;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Events Controller 통합 테스트")
class EventsControllerTest {

    private static final String BASE_URL = "/api/v1/events";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventBasicRepository eventBasicRepository;

    @Autowired
    private EventDetailsRepository eventDetailsRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        userRepository.deleteAllInBatch();
        eventBasicRepository.deleteAllInBatch();
        eventDetailsRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("행사 주최 테스트")
    class createEvent {

        @Test
        @DisplayName("행사 생성 요청 데이터에 이미지가 포함되어 있지 않더라도 201 성공 응답을 받는다")
        void 행사_생성_요청_데이터에_이미지가_포함되어_있지_않더라도_201_성공_응답을_받는다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createEmptyImageRequest();

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("행사 생성 요청 데이터 값이 null이라도 201 성공 응답을 받는다")
        void 행사_생성_요청_데이터_값이_null이라도__201_성공_응답을_받는다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createNullImageRequest();

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("참가 인원 정책을 '참여 인원 제한 없음'으로 설정한 경우 최대 참가 인원 필드는 무시되고 201 성공 응답을 받는다.")
        void 참가_인원_정책을_참여_인원_제한_없음_으로_설정한_경우_최대_참가_인원_필드는_무시되고_201_성공_응답을_받는다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createParticipantPolicyTypeIsUnlimitedAndZeroMaxParticipants();

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("행사 종료 시간이 행사 시작 시간보다 1초라도 늦는 경우 201 성공 응답을 받는다")
        void 행사_종료_시간이_행사_시작_시간보다_1초라도_늦는_경우_201_성공_응답을_받는다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createStartEndTimeIsLateOnlyOneSecond();

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("GCP 저장소에 저장한 이미지의 URL들을 요청 데이터에 포함하여 보내온 경우 201 성공 응답을 받는다")
        void GCP_저장소에_저장한_이미지의_URL들을_요청_데이터에_포함하여_보내온_경우_201_성공_응답을_받는다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            // TODO: 이미지 저장 로직 + List<String> 생성 후 Fixture 생성 로직 추가
            List<String> imageUrls = List.of();
            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createRequestWithGcsImageUrls(imageUrls);

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("행사 시작 날짜가 행사 종료 날짜 뒤에 있을 경우 400 예외가 발생한다")
        void 행사_시작_날짜가_행사_종료_날짜_뒤에_있을_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createStartTimeAfterEndTimeRequest();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_END_BEFORE_START,
                    "eventEndAt: " + request.eventEndAt() + " is before eventStartAt: " + request.eventStartAt()));

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("행사 시작 시간과 행사 종료 시간이 완전히 일치하는 경우 400 예외가 발생한다")
        void 행사_시작_시간과_행사_종료_시간이_완전히_일치하는_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createStartTimeEqualsEndTimeRequest();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_END_BEFORE_START,
                    "eventEndAt: " + request.eventEndAt() + " is before eventStartAt: " + request.eventStartAt()));


            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("요청 데이터의 이미지들 중 데이터 형식이 올바르지 않은 것이 1개라도 있을 경우 400 예외가 발생한다")
        void 요청_데이터의_이미지들_중_데이터_형식이_올바르지_않은_것이_1개라도_있을_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            // TODO: 이미지 저장 로직 + List<String> 생성 후 Fixture 생성 로직 추가
            List<String> validImageUrls = List.of("https://test.com/test.png", "https://test.com/test.jpg");
            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createRequestWithWrongFormatImageUrl(validImageUrls);

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Tag("ExternalIntegration")
        @Test
        @DisplayName("요청 데이터의 이미지들 중 GCP 저장소에 저장되어 있지 않은 것이 1개라도 있을 경우 400 예외가 발생한다")
        void 요청_데이터의_이미지들_중_GCP_저장소에_저장되어_있지_않은_것이_1개라도_있을_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            // TODO: 이미지 저장 로직 + List<String> 생성 후 Fixture 생성 로직 추가
            List<String> savedImageUrls = List.of("https://test.com/test.png", "https://test.com/test.jpg");
            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createRequestWithNotSavedImage(savedImageUrls);

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("참가 인원 제한 정책이 '참가 인원 제한 있음'으로 설정되어 있는데 최대 참가 인원 필드가 요청 데이터에 포함되어 있지 않은 경우 400 예외가 발생한다")
        void 참가_인원_제한_정책이_참가_인원_제한_있음_으로_설정되어_있는데_최대_참가_인원_필드가_요청_데이터에_포함되어_있지_않은_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createParticipantLimitPolicyTypeIsLimitedAndMaxParticipantsIsNull();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_MAX_PARTICIPANTS,
                    "maxParticipants can not be negative number or zero: " + request.maxParticipants()));

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("참가 인원 제한 정책이 '참가 인원 제한 있음'으로 설정되어 있는데 최대 참가 인원 필드 값이 null인 경우 400 예외가 발생한다")
        void 참가_인원_제한_정책이_참가_인원_제한_있음_으로_설정되어_있는데_최대_참가_인원_필드_값이_음수인_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createParticipantLimitPolicyTypeIsLimitedAndMaxParticipantsIsNull();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_MAX_PARTICIPANTS,
                    "maxParticipants can not be negative number or zero: " + request.maxParticipants()));

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("참가 인원 제한 정책이 '참가 인원 제한 있음'으로 설정되어 있는데 최대 참가 인원 필드 값이 0인 경우 400 예외가 발생한다")
        void 참가_인원_제한_정책이_참가_인원_제한_있음_으로_설정되어_있는데_최대_참가_인원_필드_값이_0인_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createParticipantLimitPolicyTypeIsLimitedAndMaxParticipantsIsZero();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_MAX_PARTICIPANTS,
                    "maxParticipants can not be negative number or zero: " + request.maxParticipants()));

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("예약 시작 날짜가 예약 종료 날짜 뒤에 있을 경우 400 예외가 발생한다")
        void 예약_시작_날짜가_예약_종료_날짜_뒤에_있을_경우_400_예외가_발생한다() throws Exception {
            // given
            CertificationTokenInfo certificationToken = CertificationTokenFixture.createFullAuthorizedCertificationToken();

            User user = UserFixture.createUserWithRoledHost();
            User savedUser = userRepository.save(user);
            String accessTokenHeaderValue = SessionTokenFixture.createAccessTokenHeaderValue(savedUser.getId());

            EventsCreateEventRequestCommand request = CreateEventRequestFixture.createApplyStartTimeAfterApplyEndTime();

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(EventsErrorType.ILLEGAL_APPLY_END_BEFORE_START,
                    "Apply end time should be after apply start time applyStartAt: " + request.applyStartAt() + " applyEndAt: " + request.applyEndAt()));

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .header(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken())
                .header(AUTHORIZATION_HEADER, accessTokenHeaderValue)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}
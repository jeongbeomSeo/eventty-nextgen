package com.eventty.eventtynextgen.base.constant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseConst {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String APP_NAME_KEY = "AppName";
    public static final String ADMIN_EMAIL_KEY = "AdminEmail";
    public static final String API_ALLOW_KEY = "API_Allow";
    public static final String JWT_TOKEN_TYPE = "Bearer";
    public static final String JWT_SECRET_KEY = "d172e90745bcc237af59f500a4d6acded461842227719b69f493cbf29c6a7acc0cfd00ae2117f3d5be5787427ab390988b23bf0968214595e68c2b0613118af3";
    public static final String JWT_CLAIM_USER_ID_KEY = "userId";
}

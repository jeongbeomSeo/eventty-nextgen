package com.eventty.eventtynextgen;

import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EventtyNextgenApplicationTests {

    @Test
    void contextLoads() {
    }

}
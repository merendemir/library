package com.application.library.controller;

import com.application.library.support.TestSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "test")
@DirtiesContext
public abstract class BaseRestControllerTest extends TestSupport {

}

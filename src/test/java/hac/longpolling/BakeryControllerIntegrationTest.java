package hac.longpolling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BakeryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bakeEndpointReturnsExpectedMessage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/bake/cookie"))
                .andExpect(request().asyncStarted())
                .andReturn();

        // The bake either completes (up to 10s) or times out (5s), so wait long enough for both paths.
        mvcResult.getAsyncResult(11_000);

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", anyOf(
                        startsWith("Bake for cookie complete"),
                        equalTo("the bakery is not responding in allowed time")
                )));
    }
}


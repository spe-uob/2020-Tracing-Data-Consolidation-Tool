package Group1.com.DataConsolidation.ControllerTest;

import Group1.com.DataConsolidation.Controller.DownloadController;
import Group1.com.DataConsolidation.Controller.UploadController;
import ch.qos.logback.core.status.StatusUtil;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DownloadController.class)
class DownloadControllerTest {

    private final String TEST_FILES_PATH = "src/test/resources/";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Group1.com.DataConsolidation.DataProcessing.Progress Progress;

    @Test
    void getFile() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/processed?jobId=0");
        try {
            MvcResult mvcResult = mvc.perform(request).andExpect(status().is(200)).andReturn();
            String contentlength = mvcResult.getResponse().getHeader("Content-Length");
            assertTrue(contentlength.equals("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
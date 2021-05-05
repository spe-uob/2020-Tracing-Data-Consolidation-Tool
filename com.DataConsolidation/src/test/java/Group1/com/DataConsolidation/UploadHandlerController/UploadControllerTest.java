package Group1.com.DataConsolidation.UploadHandlerController;

import Group1.com.DataConsolidation.DataProcessing.Progress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UploadController.class)
class UploadControllerTest {

//    @Test
//    void uploadData() {
//    }

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Group1.com.DataConsolidation.DataProcessing.Progress Progress;

    @Test
    void showdata() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/upload");
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals("",result.getResponse().getContentAsString());

    }
}
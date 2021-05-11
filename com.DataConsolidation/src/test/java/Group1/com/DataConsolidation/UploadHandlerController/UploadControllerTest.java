package Group1.com.DataConsolidation.UploadHandlerController;

import Group1.com.DataConsolidation.DataProcessing.Progress;
import org.h2.util.json.JSONByteArrayTarget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UploadController.class)
class UploadControllerTest {

    private final String TEST_FILES_PATH = "src/test/resources/";


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Group1.com.DataConsolidation.DataProcessing.Progress Progress;

    @Test
    void uploadData() throws Exception {
        File targetFile = new File(TEST_FILES_PATH + "arams_invalid.xlsx");
        InputStream inStream = new FileInputStream(targetFile);
        byte[] content = new byte[inStream.available()];
        inStream.read(content);
        inStream.close();

        //RequestBuilder request = MockMvcRequestBuilders.post("/upload");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file",content);
        //MvcResult result = mvc.perform(request).andReturn();
        //assertEquals("", result.getResponse().getContentAsString());


        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(mockMultipartFile)).andExpect(status().is(200)).andReturn();
        String responseJson = result.getResponse().getContentAsString();
//        String id = responseJson.substring(9,responseJson.lastIndexOf("}"));
//
//        System.out.println(id);


    }


}
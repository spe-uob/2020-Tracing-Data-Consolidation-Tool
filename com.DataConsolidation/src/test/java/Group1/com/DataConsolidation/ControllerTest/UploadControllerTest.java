package Group1.com.DataConsolidation.ControllerTest;

import Group1.com.DataConsolidation.Controller.UploadController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;

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
        File targetFile = new File(TEST_FILES_PATH + "BlankDoc.xlsx");
        File targetFile2 = new File(TEST_FILES_PATH + "Test_image.jpg");
        try{
            InputStream inStream = new FileInputStream(targetFile);
            InputStream inStream2 = new FileInputStream(targetFile2);

            byte[] content = new byte[inStream.available()];
            byte[] content2 = new byte[inStream2.available()];

            inStream.read(content);
            inStream2.read(content2);

            inStream.close();
            inStream2.close();

            File file = new File("src/main/resources/ProcessedFiles");
            file.mkdir();
            //RequestBuilder request = MockMvcRequestBuilders.post("/upload");
            MockMultipartFile mockMultipartFile = new MockMultipartFile("file",content);
            MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file",content2);
            //MvcResult result = mvc.perform(request).andReturn();
            //assertEquals("", result.getResponse().getContentAsString());


            MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/upload")
                    .file(mockMultipartFile).param("outbreakSource", "")).andExpect(status().is(200)).andReturn();
            mvc.perform(MockMvcRequestBuilders.multipart("/upload")
                    .file(mockMultipartFile2).param("outbreakSource", "")).andExpect(status().is(200));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
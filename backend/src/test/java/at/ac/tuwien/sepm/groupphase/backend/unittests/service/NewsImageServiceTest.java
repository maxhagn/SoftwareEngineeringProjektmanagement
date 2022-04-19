package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsImageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsImageService;
import at.ac.tuwien.sepm.groupphase.backend.utils.PictureHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class NewsImageServiceTest {

    @Autowired
    private NewsRepository newsRepository;



    @Autowired
    private NewsImageService newsImageService;



    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService realDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        realDataService.initData();
    }
    /*=============================POSITIVE-TESTS=============================*/

    /*-----------------------------PUT-----------------------------*/
    @Test
    public void givenValidNews_whenUpdatePicture_thenOk() throws IOException {
        News news = newsRepository.findAll().get(0);
        File file = new File("src/test/java/at/ac/tuwien/sepm/groupphase/backend/unittests/service/boom1.txt");
        FileReader fileReader = null;
        fileReader = new FileReader(file);
        String line;
        BufferedReader br = new BufferedReader(fileReader);
        line = br.readLine();
        byte[] decode = Base64.getDecoder().decode(line);

        NewsImage image = NewsImage.builder().pic(PictureHandler.compressBytes(decode)).news(news).original_name("picture.txt").build();
        NewsImage image1 = newsImageService.saveImage(image);
        news.addImage(image1);
        News news1 = newsRepository.saveAndFlush(news);


        assertAll(
            () -> assertNotNull(news),
            () -> assertNotNull(image1)
        );
    }

    @Test
    public void givenValidNews_whenGetPicture_thenOk() throws IOException {
        List<NewsImageDto> imagesForId = newsImageService.findImagesForId(newsRepository.findAll().get(0).getId());

        assertAll(
            () -> assertEquals(1,  imagesForId.size()),
            () -> assertNotNull(imagesForId)
        );
    }


    /*=============================NEGATIVE-TESTS=============================*/

    /*-----------------------------PUT-----------------------------*/
    @Test
    public void givenInValidNews_whenUpdatePicture_thenValidationException() {
        Exception exception = assertThrows(ValidationException.class, () -> {

            File file = new File("src/test/java/at/ac/tuwien/sepm/groupphase/backend/unittests/service/boom1.txt");
            FileReader fileReader = null;
            fileReader = new FileReader(file);
            String line;
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            byte[] decode = Base64.getDecoder().decode(line);

            NewsImage image = NewsImage.builder().pic(PictureHandler.compressBytes(decode)).news(null).original_name("picture.txt").build();
            NewsImage image1 = newsImageService.saveImage(image);


        });

    }



    @Test
    public void givenInValidPicture_whenUpdatePicture_thenNotFound() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News news = newsRepository.findAll().get(0);
            File file = new File("src/test/java/at/ac/tuwien/sepm/groupphase/backend/unittests/service/boom1.txt");
            FileReader fileReader = null;
            fileReader = new FileReader(file);
            String line;
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            byte[] decode = Base64.getDecoder().decode(line);

            NewsImage image1 = newsImageService.saveImage(null);


        });

    }

    @Test
    public void givenInValidNews_whenGetPicture_thenNotFoundException()  {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            List<NewsImageDto> imagesForId = newsImageService.findImagesForId(null);

        });
    }

    @Test
    public void givenInValidNews_whenGetPicture_thenNotFoudnException() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            List<NewsImageDto> imagesForId = newsImageService.findImagesForId(-1L);

        });
    }


}

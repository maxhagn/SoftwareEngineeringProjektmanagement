package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsImageDto;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.utils.PictureHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsImageEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsImageService newsImageService;
    private final NewsService newsService;

    @Autowired
    public NewsImageEndpoint(NewsImageService newsImageService, NewsService newsService) {

        this.newsImageService = newsImageService;
        this.newsService = newsService;


    }

    /*-----------------------------GET-----------------------------*/

    /**
     * This method updates news with the readers
     *
     * @return updated news
     */
    @PostMapping("/{id}/upload")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request")})
    @ApiOperation(value = "Update news entity")
    public NewsImage updateImages(@RequestParam("file") MultipartFile file, @PathVariable(value = "id") Long id) {
        NewsImage img = null;
        NewsImage save = null;
        try {
            LOGGER.info("Upload Image for news with id = " + id);
            News news = newsService.findById(id);

            img = NewsImage.builder().original_name(file.getOriginalFilename()).news(news).pic(
                PictureHandler.compressBytes(file.getBytes())).build();


            save = newsImageService.saveImage(img);
            LOGGER.info("Image saved for news entry with id =  {} ", id);
        } catch (IOException e) {
            LOGGER.error("Error during creating image for news entry with id = " + id);
            LOGGER.debug("Error during creating image for news entry with id = " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during creating image for news entry with id = " + id + " : " + e.getMessage(), e);

        }

        return save;
    }




    /*-----------------------------GET-----------------------------*/

    @GetMapping(value = "/{id}/pic")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Article not found")
    })
    @ApiOperation(value = "Get display picture for news entry with given id")

    public NewsImageDto findOnePictureForNewsId(@PathVariable Long id) {
        LOGGER.trace("Get display picture for news entry with id = " + id);
        NewsImageDto response = null;


        List<NewsImageDto> all = newsImageService.findImagesForId(id);
        NewsImageDto img = null;
        if (all != null && all.size() != 0) {
            img = all.get(0);
        }
        response = NewsImageDto.builder()
            .pic(img.getPic())
            .original_name(img.getOriginal_name())
            .build();

        LOGGER.info("Image for news with id = {} found", id);


        return response;
    }


    @GetMapping(value = "/{id}/pics")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Article not found")
    })
    @ApiOperation(value = "Get all news pictures for news article with given id")

    public List<NewsImageDto> findAllPicturesForNewsId(@PathVariable Long id) {
        LOGGER.trace("get all news entries");
        List<NewsImageDto> response = null;

        response = newsImageService.findImagesForId(id);
        LOGGER.info("Images for news entry with id = {} found", id);
        return response;
    }


}

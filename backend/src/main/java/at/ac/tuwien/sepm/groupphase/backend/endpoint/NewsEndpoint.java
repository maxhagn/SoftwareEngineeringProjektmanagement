package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsOutputPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.*;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

import java.util.LinkedList;
import java.util.List;

@RestController
@Api(tags = "News Endpoint")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsMapper newsMapper;
    private final NewsService newsService;
    private final UserService userService;


    @Autowired
    public NewsEndpoint(NewsMapper newsMapper, NewsService newsService, UserService userService) {
        this.newsMapper = newsMapper;
        this.newsService = newsService;
        this.userService = userService;
    }

    /*-----------------------------POST-----------------------------*/


    /**
     * This method creates a news entry
     *
     * @param news that is being created
     * @return the saved entry
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {@ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @ApiOperation(value = "Create new a new news article")
    public NewsOutputDto save(@RequestBody @Valid NewsInputDto news) {
        LOGGER.trace("Save{}", news);
        NewsOutputDto response = null;

        News news_entity = newsMapper.inputDtoToNews(news);

        news_entity = newsService.save(news_entity);

        LOGGER.info("News entry was saved successfully");

        response = newsMapper.outputNewsDto(news_entity);

        return response;

    }
    /*-----------------------------PUT-----------------------------*/


    /**
     * This method updates news with the readers
     *
     * @return updated news
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request"),
        @ApiResponse(code = 400, message = "Bad Request")})
    @ApiOperation(value = "Update readers of news entry with id with given id")
    public NewsOutputReadersDto updateReaders(@PathVariable Long id, Authentication authentication) {
        LOGGER.trace("Update readers of news entry with id = {}", id);
        NewsOutputReadersDto response = null;
        String email = authentication.getPrincipal().toString();

        User userByEmail = userService.findUserByEmail(email);
        List<User> readers = new LinkedList<>();
        readers.add(userByEmail);
        NewsInputReadersDto newsDto = NewsInputReadersDto.builder().readers(readers).id(id).build();
        News news = newsMapper.inputReaderDtoToNews(newsDto);
        News update = newsService.update(news.getId(), news.getReaders());
        response = newsMapper.newsToReaderDto(update);
        LOGGER.info("Updated readers of news entry with id = {} successfully", id);
        return response;
    }






    /*-----------------------------GET-----------------------------*/

    /**
     * This method gets a news entry by Id
     *
     * @param id of news entry to be found
     * @return found news entry
     */

    @Transactional
    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Article not found"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @ApiOperation(value = "Get detail information about a specific news article")
    public NewsOutputDetailDto find(@PathVariable Long id) {
        LOGGER.trace("Get news with id {}", id);
        NewsOutputDetailDto response = null;

        News byId = newsService.findById(id);
        response = newsMapper.newsToDetailDto(byId);
        LOGGER.info("News entry successfully found");


        return response;
    }


    /**
     * This method gets all news entries
     *
     * @return all news entries
     */

    @GetMapping("/read")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Article not found")
    })
    @ApiOperation(value = "Get read news with pagination")

    public Page<NewsOutputPreviewDto> findReadAllwithPagination(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "6") int size, Authentication authentication) {
        LOGGER.info("Get read news with pagination");
        Page<NewsOutputPreviewDto> response = null;
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("date").descending());
        String email = authentication.getPrincipal().toString();

        Page<News> pageResult = newsService.getReadNewsWithPagination(email, pageRequest);

        List<NewsOutputPreviewDto> dtos = new LinkedList<>();
        for (News n : pageResult.getContent()) {
            dtos.add(this.newsMapper.newsToPreviewDto(n));


        }


        response = new PageImpl<>(dtos, pageRequest, pageResult.getTotalElements());
        LOGGER.info("Read news with pagination successfully found");
        return response;

    }


    /**
     * This method gets all news entries
     *
     * @return all news entries
     */

    @GetMapping("/unread")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Article not found")
    })
    @ApiOperation(value = "Get unread news with pagination")

    public Page<NewsOutputPreviewDto> findUnreadAllwithPagination(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "7") int size, Authentication authentication) {
        LOGGER.info("Get unread news with pagination");
        Page<NewsOutputPreviewDto> response = null;
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("date").descending());

        String email = authentication.getPrincipal().toString();


        Page<News> pageResult = newsService.getUnreadNewsWithPagination(email, pageRequest);

        List<NewsOutputPreviewDto> dtos = new LinkedList<>();
        for (News n : pageResult.getContent()) {
            dtos.add(this.newsMapper.newsToPreviewDto(n));


        }

        response = new PageImpl<>(dtos, pageRequest, pageResult.getTotalElements());
        LOGGER.info("Unread news with pagination successfully found");

        return response;

    }

    /**
     * This method gets all read news
     *
     * @return list of read news
     */
    @GetMapping("/list/unread")
    public List<NewsOutputPreviewDto> listUnreadNews(Authentication authentication) {
        List<NewsOutputPreviewDto> response = new LinkedList<>();
        String email = authentication.getPrincipal().toString();

        List<News> readNews = newsService.getUnreadNews(email);
        if (readNews != null) {
            for (News news : readNews) {
                if (news != null) {


                    NewsOutputPreviewDto outputPreviewDto = newsMapper.newsToPreviewDto(news);
                    response.add(outputPreviewDto);

                }

            }
        }
        LOGGER.info("Read News entries successfully found");


        return response;


    }


}

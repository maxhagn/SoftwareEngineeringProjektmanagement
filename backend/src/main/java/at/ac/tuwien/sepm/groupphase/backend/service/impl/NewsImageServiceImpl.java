package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsImageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsImageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsImageService;
import at.ac.tuwien.sepm.groupphase.backend.utils.PictureHandler;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Service

public class NewsImageServiceImpl implements NewsImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final NewsImageRepository newsImageRepository;
    private final Validator validator;

    public NewsImageServiceImpl(NewsRepository newsRepository, NewsImageRepository newsImageRepository, Validator validator) {
        this.newsRepository = newsRepository;
        this.newsImageRepository = newsImageRepository;
        this.validator = validator;
    }


    @Override
    public List<NewsImageDto> findImagesForId(Long id) {
        LOGGER.trace("get news images for news enrty with id = {}", id);
        List<NewsImageDto> response;
        Set<NewsImageDto> tempSet = new HashSet<>();
        try {
            News news;
            List<NewsImage> allForId;
            if (id == null) {
                LOGGER.debug("No news entry for given id");
                LOGGER.error("No news entry for given id");
                throw new NotFoundException("No news entry for given id");
            }
            Optional<News> byId = newsRepository.findById(id);

            if (byId.isPresent()) {
                news = byId.get();

                if (news.getImages() != null) {
                    allForId = news.getImages();
                    for (NewsImage n : allForId) {
                        if (n != null) {
                            tempSet.add(NewsImageDto.builder()
                                .original_name(n.getOriginal_name())
                                .pic(PictureHandler.decompressBytes(n.getPic()))
                                .news_id(n.getNews().getId()).build());
                        }


                    }
                }

            } else {
                LOGGER.debug("No news entry for given id");
                LOGGER.error("No news entry for given id");
                throw new NotFoundException("No news entry for given id");
            }


            response = new LinkedList<>(tempSet);

            LOGGER.info("Image entries successfully found");

        } catch (NotFoundException e) {
            LOGGER.error("No news entry for given id");
            LOGGER.debug("No news entry for given id");
            throw new NotFoundException("No news entry for given id");
        } catch (PersistenceException e) {
            LOGGER.error("Error during getting news images for id = " + id);
            LOGGER.debug("Error during getting  news images for id =" + id);
            throw new ServiceException("Invalid input for news images for id =" + id);

        }
        return response;

    }

    @Override
    public NewsImage saveImage(NewsImage img) {
        Long id = 0L;
        NewsImage response = null;
        try {
            News news = null;
            if (img == null || img.getNews() == null) {
                LOGGER.debug("Invalid picture.");
                LOGGER.error("Invalid picture.");
                throw new ValidationException("Invalid picture.");
            }

            id = img.getNews().getId();
            LOGGER.trace("Update news entity with id {}", id);
            Optional<News> newsOptional = newsRepository.findById(img.getNews().getId());
            if (newsOptional.isPresent()) {
                news = newsOptional.get();
                LOGGER.info("News entry successfully found");
            }
            validator.validateNews(news);

            response = newsImageRepository.save(img);
            news.addImage(img);
            newsRepository.save(news);
            LOGGER.info("News entry updated");

        } catch (ValidationException e) {
            LOGGER.debug("Invalid picture.");
            LOGGER.error("Invalid picture.");
            throw new ValidationException("Invalid picture.");
        } catch (PersistenceException e) {
            LOGGER.error("Error during updating news entry");
            LOGGER.debug("Error during updatung  news entry");
            throw new ServiceException("Invalid input for news entry");

        }

        return response;

    }


}

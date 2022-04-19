package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsImageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface NewsImageService {


    /**
     * This method finds all picture for the news entry of given id
     * @param id of news entry
     * @return list of images that belong to the news entry
     * @throws ValidationException if id of news doesn't exist
     */
    List<NewsImageDto> findImagesForId(Long id);

    /**
     * This method saves a picture to news entry with given id
     * @param image that is being saved
     * @return saved image
     * @throws ValidationException if id of news doesn't exist
     */
    NewsImage saveImage(NewsImage image);
}

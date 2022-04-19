package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {

    /**
     * This method saves a news enty
     * @param news that are being saved
     * @return saved news
     * @throws ValidationException if there is invalid input
     * @throws ServiceException if there was an internal error
     */
    News save(News news);

    /**
     * This method gets the news entry with the given id
     * @param id that is given
     * @return news entry with given id
     * @throws NotFoundException if news entry with id does not exist
     * @throws ValidationException if id is invalid
     * @throws ServiceException if there was an internal error
     */
    News findById(Long id);


    /**
     * This method updates a news entity
     * @param id of news entity to be updated
     * @param readers that are being set
     * @return news entity with readers
     * @throws ValidationException if there is invalid input
     * @throws ServiceException if there was an internal error
     */
    News update(Long id, List<User> readers);


    /**
     * This gets all news that the user has already read
     * @param email of user
     * @param page  with page request
     * @return read news
     * @throws ValidationException if there is invalid input
     * @throws ServiceException if there was an internal error
     */
    Page<News> getReadNewsWithPagination(String email, Pageable page);

    /**
     * This gets all news that the user has not read yet
     * @param email of user
     * @param page with page request
     * @return page of unread news
     */
    Page<News> getUnreadNewsWithPagination(String email, Pageable page);

    /**
     * This method loads unread news
     * @param email  of user
     * @return list of unread news
     * @throws ValidationException if there is invalid input
     * @throws ServiceException if there was an internal error
     */
    List<News> getUnreadNews(String email);


}

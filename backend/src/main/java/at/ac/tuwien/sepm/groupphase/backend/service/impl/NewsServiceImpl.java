package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsImageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final NewsImageRepository imageRepository;
    private final Validator validator;

    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, EventRepository eventRepository, NewsImageRepository imageRepository, Validator validator) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.imageRepository = imageRepository;
        this.validator = validator;
    }
    /*-----------------------------POST-----------------------------*/

    /**
     * This method creates a news entry
     *
     * @param news that is being created
     * @return the saved entry
     */
    @Override
    public News save(News news) {
        News savedNewsEntry = null;
        LOGGER.debug("save news {}", news);
        try {
            validator.validateEvent(news.getEvent());
            Optional<Event> byId = eventRepository.findById(news.getEvent().getId());
            if (byId.isPresent()) {
                validator.validateNews(news);
                news.setEvent(byId.get());

                savedNewsEntry = newsRepository.save(news);
                LOGGER.info("News entry saved");


            } else {
                LOGGER.debug("Error event does not exist for news entry");
                LOGGER.error("Error event does not exist for news entry");
                throw new ValidationException("Event does not exist");
            }


        } catch (ValidationException e) {
            LOGGER.error("Invalid input for news entry: " + e.getMessage());
            LOGGER.debug("Invalid input for news entry: " + e.getMessage());
            throw new ValidationException("Invalid input for news entry");
        } catch (PersistenceException e) {
            LOGGER.error("Invalid input for news entry: " + e.getMessage());
            LOGGER.debug("Invalid input for news entry: " + e.getMessage());
            throw new ServiceException("Invalid input for news entry");

        }
        return savedNewsEntry;

    }
    /*-----------------------------PUT-----------------------------*/


    /**
     * This method updates news with the readers
     *
     * @return updated news
     */
    @Override
    public News update(Long id, List<User> readers) {
        LOGGER.trace("Update news entity with id {}", id);
        News response = null;
        try {
            News news = null;
            if (id == null) {
                LOGGER.error("Error news with id = " + id + " does not exist");
                LOGGER.debug("Error news with id = " + id + " does not exist");
                throw new NotFoundException("News with id = " + id + " does not exist.");
            }
            Optional<News> newsOptional = newsRepository.findById(id);
            if (newsOptional.isPresent()) {
                news = newsOptional.get();
                LOGGER.info("News entry successfully found");
            } else {
                LOGGER.error("Error news with id = " + id + " does not exist");
                LOGGER.debug("Error news with id = " + id + " does not exist");
                throw new NotFoundException("News with id = " + id + " does not exist.");
            }


            List<User> readersNew = new LinkedList<>();
            validator.validateReaders(readers);
            for (User r : readers) {
                validator.validateReader(r);
                User userByEmail = userRepository.findUserByEmailIgnoreCase(r.getEmail());
                if (userByEmail == null) {
                    LOGGER.error("Error user does not exist");
                    LOGGER.debug("Error user does not exist");
                    throw new NotFoundException("Error user does not exist");
                }
                readersNew.add(userByEmail);

            }
            news.setReaders(readersNew);
            response = newsRepository.save(news);
            LOGGER.info("News entry updated");
        } catch (NotFoundException e) {
            LOGGER.error("Error news with id = " + id + " does not exist");
            LOGGER.debug("Error news with id = " + id + " does not exist");
            throw new NotFoundException("Error news with id = " + id + " does not exist");
        } catch (ValidationException e) {
            LOGGER.error("Error invalid readers");
            LOGGER.debug("Error invalid readers");
            throw new ValidationException("Invalid input for readers");

        } catch (PersistenceException e) {
            LOGGER.error("Error during updating news entry");
            LOGGER.debug("Error during updatung  news entry");
            throw new ServiceException("Invalid input for news entry");

        }

        return response;
    }
    /*-----------------------------GET-----------------------------*/

    /**
     * This method gets a news entry by Id
     *
     * @param id of news entry to be found
     * @return found news entry
     */
    @Override
    public News findById(Long id) {
        LOGGER.trace("get news with id {}", id);
        News found = null;
        try {
            if (id < 0) {
                LOGGER.debug("Id of news is invalid");
                LOGGER.error("Id of news is invalid");
                throw new ValidationException("News with id = " + id + " does not exist");
            }
            Optional<News> news = newsRepository.findById(id);
            if (news.isPresent()) {
                found = news.get();

                LOGGER.info("News entry successfully found");
            } else {
                throw new NotFoundException("News with id = " + id + " does not exist");
            }


        } catch (NoSuchElementException | NotFoundException e) {
            LOGGER.error("No such id for news entry: ");
            LOGGER.debug("Nos id for news entry: ");
            throw new NotFoundException("Invalid input for news entry");
        } catch (ValidationException e) {
            LOGGER.error("Invalid id for news entry: ");
            LOGGER.debug("Invalid id for news entry: ");
            throw new ValidationException("Invalid input for news entry");
        } catch (PersistenceException e) {
            LOGGER.error("Error during getting news entry");
            LOGGER.debug("Error during getting  news entry");
            throw new ServiceException("Invalid input for news entry");

        }
        return found;
    }



    /**
     * This method gets all news entries
     *
     * @return all news entries
     */

    @Override
    public List<News> getUnreadNews(String email) {
        LOGGER.trace("Get read news for user with email = {}", email);
        List<News> response = new LinkedList<>();
        try {
            validator.validateReaderEmail(email);
            User userByEmail = userRepository.findUserByEmailIgnoreCase(email);
            if (userByEmail == null || userByEmail.getId() == null) {
                throw new NotFoundException("No user for given email.");
            }
            List<News> readNews = newsRepository.findReadNews(userByEmail.getId());
            List<News> all = newsRepository.findAllDesc();
            for (News n : all) {
                if (!readNews.contains(n)) {
                    response.add(n);
                }
            }
            LOGGER.info("Unread news successfully found");
        } catch (ValidationException e) {
            LOGGER.error("Error user with email = " + email + " does not exist");
            LOGGER.debug("Error user with email = " + email + " does not exist");
            throw new ValidationException("Error no user with email = " + email + " does not exist");
        } catch (NotFoundException e) {
            LOGGER.error("Error user with email = " + email + " does not exist");
            LOGGER.debug("Error user with email = " + email + " does not exist");
            throw new NotFoundException("Error no user with email = " + email + " does not exist");
        } catch (PersistenceException e) {
            LOGGER.error("Error during updating news entry");
            LOGGER.debug("Error during updatung  news entry");
            throw new ServiceException("Invalid input for news entry");

        }

        return response;
    }


    /**
     * This gets all news that the user has not read yet
     *
     * @param email of user
     * @param page  with page request
     * @return page of unread news
     */
    @Override
    public Page<News> getUnreadNewsWithPagination(String email, Pageable page) {

        Page<News> returnPage;
        try {
            validator.validateReaderEmail(email);

            User userByEmail = userRepository.findUserByEmailIgnoreCase(email);
            if (userByEmail == null || userByEmail.getId() == null) {
                throw new NotFoundException("No user for given email.");
            }


            List<News> readNews = newsRepository.findReadNews(userByEmail.getId());

            returnPage = newsRepository.findUnreadNewsWithPagination(readNews, page);


        } catch (ValidationException e) {
            LOGGER.debug("Invalid email for news");
            LOGGER.error("Invalid email for news");
            throw new ValidationException(e.getMessage());
        } catch (DataAccessException e) {
            LOGGER.debug("DataAccessException during getting unread news");
            LOGGER.error("DataAccessException during getting unread news");
            throw new RuntimeException(e.getMessage(), e);
        }
        return returnPage;

    }

    /**
     * This gets all news that the user has already read
     *
     * @param email of user
     * @param page  with page request
     * @return read news
     */
    @Override
    public Page<News> getReadNewsWithPagination(String email, Pageable page) {

        Page<News> returnPage;
        try {
            validator.validateReaderEmail(email);

            User userByEmail = userRepository.findUserByEmailIgnoreCase(email);
            if (userByEmail == null || userByEmail.getId() == null) {
                throw new NotFoundException("No user for given email.");
            }


            returnPage = newsRepository.findReadNewsWithPagination(userByEmail.getId(), page);

            if (returnPage.getTotalElements() < page.getPageSize()) {
                page = PageRequest.of(0, page.getPageSize());
                returnPage = newsRepository.findReadNewsWithPagination(userByEmail.getId(), page);
            }


            LOGGER.info("Read news successfully found");

        } catch (ValidationException e) {
            LOGGER.debug("Invalid email for news");
            LOGGER.error("Invalid email for news");
            throw new ValidationException(e.getMessage());
        } catch (DataAccessException e) {
            LOGGER.debug("DataAccessException during getting read news");
            LOGGER.error("DataAccessException during getting read news");
            throw new RuntimeException(e.getMessage(), e);
        }
        return returnPage;

    }


}

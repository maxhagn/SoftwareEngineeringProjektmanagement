package at.ac.tuwien.sepm.groupphase.backend.utils;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public void validateEvent(Event event) {
        if (event == null || event.getId() == null) {
            LOGGER.debug("Error event does not exist for news entry");
            throw new ValidationException("Event does not exist");
        }
    }

    public void validateCreateEvent(Event event) {
        if (event == null) throw new ValidationException("Event does not exist");
        if (event.getTitle().length() < 1) throw new ValidationException("Event must have title");
        if (event.getDuration() < 1) throw new ValidationException("Event must last longer then nothing");
        if (event.getDescription().length() < 1) throw new ValidationException("Event must have description");
        if (event.getArtist() == null) throw new ValidationException("Event must have artist");
        if (event.getArtist().getId() == null) throw new ValidationException("Event must have artist");
        if(event.getCategory() == null)throw new ValidationException("Event must have a category");
        checkIfOnlyWhitespaces(event.getTitle(),"title");
        checkIfOnlyWhitespaces(event.getDescription(),"description");

        for (Performance p : event.getPerformances()) {
            if (p.getHall() == null) throw new ValidationException("Performance must have hall");
            if (p.getDatetime() == null) throw new ValidationException("Performance must have datetime");
            if (p.getMin_price() < 0) throw new ValidationException("Minimum price must be positive or zero");
        }
    }

    public void validateSearchArtist(Artist artist) {
        if (artist.getFirstname() != null && artist.getFirstname().length() > 255) {
            LOGGER.debug("Error: firstname of artist is longer than 255 chars");
            LOGGER.error("Error: firstname of artist is longer than 255 chars");
            throw new ValidationException("Error: firstname of artist is longer than 255 chars");
        }
        if (artist.getSurname() != null && artist.getSurname().length() > 255) {
            LOGGER.debug("Error: surname of artist is longer than 255 chars");
            LOGGER.error("Error: surname of artist is longer than 255 chars");
            throw new ValidationException("Error: surname of artist is longer than 255 chars");
        }
    }

    public void validateSearchLocation(LocationSearchDto location) {
        if (location != null) {
            if (location.getName() != null && location.getName().length() > 255) {
                LOGGER.debug("Error: name of location is longer than 255 chars");
                LOGGER.error("Error: name of location is longer than 255 chars");
                throw new ValidationException("Error: name of location is longer than 255 chars");
            }
            if (location.getStreet() != null && location.getStreet().length() > 255) {
                LOGGER.debug("Error: street of location is longer than 255 chars");
                LOGGER.error("Error: street of location is longer than 255 chars");
                throw new ValidationException("Error: street of location is longer than 255 chars");
            }
            if (location.getCity() != null && location.getCity().length() > 255) {
                LOGGER.debug("Error: city of location is longer than 255 chars");
                LOGGER.error("Error: city of location is longer than 255 chars");
                throw new ValidationException("Error: city of location is longer than 255 chars");
            }
        }
    }

    public void validateSearchPerformance(PerformanceSearchDto performance) {
        if (performance != null) {
            if (performance.getEventTitle() != null && performance.getEventTitle().length() > 255) {
                LOGGER.debug("Error: name of events is longer than 255 chars");
                LOGGER.error("Error: name of events is longer than 255 chars");
                throw new ValidationException("Error: name of events is longer than 255 chars");
            }
            if (performance.getHallName() != null && performance.getHallName().length() > 255) {
                LOGGER.debug("Error: name of hall is longer than 255 chars");
                LOGGER.error("Error: name of hall is longer than 255 chars");
                throw new ValidationException("Error: name of hall is longer than 255 chars");
            }
        }
    }

    public void validatePage(int page) {
        if (page < 0) {
            LOGGER.debug("Error: page number cannot be negative");
            LOGGER.error("Error: page number cannot be negative");
            throw new ValidationException("Error: page number cannot be negative");
        }
    }

    public void validateId(Long id) {
        if (id < 0) {
            LOGGER.debug("Error: id cannot be negative");
            LOGGER.error("Error: id cannot be negative");
            throw new ValidationException("Error: id cannot be negative");
        }
    }

    public void validateNews(News news) {
        if (news.getDate() == null) {
            LOGGER.debug("Error" + "Invalid author date");
            LOGGER.debug("Error" + "Invalid author date");
            throw new ValidationException("Invalid author date");
        }

        checkIfOnlyWhitespaces(news.getTitle(), "title");
        checkIfOnlyWhitespaces(news.getAuthor(), "author");
        checkIfOnlyWhitespaces(news.getSummary(), "summary");
        checkIfOnlyWhitespaces(news.getText(), "text");
        checkIfCorrectLength(news.getTitle(), "title");
        checkIfCorrectLength(news.getAuthor(), "author");
        checkIfCorrectLength(news.getSummary(), "summary");
        checkIfCorrectLength(news.getText(), "text");


    }

    public void validateChangeUser(User user) {
        if (user.getPassword() == null && user.getSurname() == null && user.getFirstname() == null && user.getEmail() == null) {
            throw new ValidationException("You must change at least one field");
        }
        if (user.getEmail() != null) {
            Pattern mailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");//Java email validation by RFC 5322
            Matcher mailMatcher = mailPattern.matcher(user.getEmail());
            if (!mailMatcher.matches()) throw new ValidationException("Create Failed. Email has illegal format");
            if (user.getEmail().length() > 255)
                throw new ValidationException("Create Failed. Fields must be shorter than 256 characters");
        }
        if (user.getPassword() != null)
            if (user.getPassword().length() < 8 || user.getPassword().length() > 255)
                throw new ValidationException("Create Failed. Password must have 8 to 255 characters");//Password length
        //Field length max 255:
        if (user.getFirstname() != null) {
            checkIfOnlyWhitespaces(user.getFirstname(), "firstname");
            if (user.getFirstname().length() > 255)
                throw new ValidationException("Create Failed. Fields must be shorter than 256 characters");

        }
        if (user.getSurname() != null) {
            checkIfOnlyWhitespaces(user.getSurname(), "surname");
            if (user.getSurname().length() > 255)
                throw new ValidationException("Create Failed. Fields must be shorter than 256 characters");

        }
        if (user.getEmail() != null) {
            if (user.getEmail().length() > 255)
                throw new ValidationException("Create Failed. Fields must be shorter than 256 characters");

        }
    }

    public void validateCreateUser(User user) {
        if (user.getEmail() == null || user.getSurname() == null || user.getFirstname() == null || user.getPassword() == null)
            throw new ValidationException("Create Failed. All fields must be set.");
        Pattern mailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");//Java email validation by RFC 5322
        Matcher mailMatcher = mailPattern.matcher(user.getEmail());
        if (!mailMatcher.matches()) throw new ValidationException("Create Failed. Email has illegal format");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 255)
            throw new ValidationException("Create Failed. Password must have 8 to 255 characters");//Password length
        //Field length max 255:
        if (user.getFirstname().length() > 255 || user.getSurname().length() > 255 || user.getEmail().length() > 255)
            throw new ValidationException("Create Failed. Fields must be shorter than 256 characters");
        checkIfOnlyWhitespaces(user.getFirstname(), "firstname");
        checkIfOnlyWhitespaces(user.getSurname(), "surname");
    }

    private void checkIfCorrectLength(String string, String attribute) {
        String[] split = string.split("\\s");
        if (split.length == 1) {
            LOGGER.debug("Error" + "Invalid " + attribute);
            LOGGER.debug("Error" + "Invalid " + attribute);
            throw new ValidationException("Invalid " + attribute);
        }
    }

    private void checkIfOnlyWhitespaces(String string, String attribute) {
        if (string != null) {
            String[] split = string.split("\\s");
            if (string.equals("\\s") || string.isEmpty() || split.length == 0) {
                LOGGER.debug("Error" + "Invalid attribute: " + attribute +" must not only contain whitespaces");
                LOGGER.debug("Error" + "Invalid attribute: " + attribute +" must not only contain whitespaces");
                throw new ValidationException("Invalid attribute: " + attribute+" must not only contain whitespaces");
            }
        } else {
            LOGGER.debug("Error" + "Attribute " + attribute + " has to be set.");
            LOGGER.debug("Error" + "Attribute " + attribute + " has to be set.");
            throw new ValidationException("Attribute " + attribute + " has to be set.");
        }
    }

    public void validateNewsImage(Long id, News news) {
        if (id < 0) {
            LOGGER.error("Invalid id for picture");
            LOGGER.debug("Invalid id for picture");
            throw new ValidationException("Invalid id for picture");
        }
        if (news == null || news.getId() == null) {
            LOGGER.error("No news entry for picture");
            LOGGER.debug("No news enrty for picture");
            throw new ValidationException("No id for picture");
        }

    }

    public void validateReaders(List<User> readers) {
        if (readers == null || readers.isEmpty()) {
            LOGGER.error("Invalid readers are null");
            LOGGER.debug("Invalid readers are null");
            throw new ValidationException("Invalid readers are null");
        }
    }

    public void validateReader(User r) {
        if (r == null || r.getEmail() == null) {
            LOGGER.error("Invalid reader is null");
            LOGGER.debug("Invalid reader is null");
            throw new ValidationException("Invalid reader is null");
        }
    }

    public void validateReaderEmail(String email) {
        if (email == null) {
            LOGGER.error("Invalid email is null");
            LOGGER.debug("Invalid email is null");
            throw new ValidationException("Invalid email is null");
        }
    }

    public void validateCreateLocationDto(CreateLocationDto dto) {
        if(dto.getArea_code() == null)throw new ValidationException("You must set an area code");
        checkIfOnlyWhitespaces(dto.getArea_code(),"area code");
        if(dto.getCategories() == null)throw new ValidationException("You must set categories");;
        if(dto.getCategories().size()<1)throw new ValidationException("You must set at least one price category");
        dto.getCategories().forEach(
            createPriceCategory -> {
                if(createPriceCategory.getName() == null)throw new ValidationException("You must set a price category name");
                checkIfOnlyWhitespaces(createPriceCategory.getName(),"price category name");
                if(createPriceCategory.getPrice()<0)throw new ValidationException("Price must be greater 0");
            }
        );
        if(dto.getCity() == null)throw new ValidationException("You must set a city");
        checkIfOnlyWhitespaces(dto.getCity(),"city");
        if(dto.getStreet() == null)throw new ValidationException("You must set a street");
        checkIfOnlyWhitespaces(dto.getStreet(),"street");
        if(dto.getName() == null) throw new ValidationException("You must set a name");
        checkIfOnlyWhitespaces(dto.getName(),"name");
        if(dto.getCountry() == null)throw new ValidationException("You must set country");
        checkIfOnlyWhitespaces(dto.getCountry(),"country");
        if(dto.getHalls() == null)throw new ValidationException("You must create halls");
        if(dto.getHalls().size() < 1)throw new ValidationException("You must create at least one hall");
        dto.getHalls().forEach(
            createHall -> {
                if(createHall.getCols()<1)throw new ValidationException("You must set at least one column");
                if(createHall.getRows()<1)throw new ValidationException("You must set at least one row");
                if(createHall.getName() == null)throw new ValidationException("You must set a name");
                checkIfOnlyWhitespaces(createHall.getName(),"hall name");
                if(createHall.getAreas() == null)throw new ValidationException("You must create at least one Area");
                if(createHall.getAreas().size()<1)throw new ValidationException("You must create at least one Area");
                createHall.getAreas().forEach(
                    createArea -> {
                        if(createArea.getName() == null)throw new ValidationException("You must give an area name");
                        checkIfOnlyWhitespaces(createArea.getName(),"area name");
                        if(createArea.getType() == null)throw new ValidationException("You must set an area type");
                    }
                );
            }
        );
    }
}

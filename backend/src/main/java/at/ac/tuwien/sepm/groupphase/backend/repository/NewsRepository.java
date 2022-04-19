package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * finds all read news
     * @param id of reader
     * @return list of read news
     */
    @Query(value = "select news from News news " +
        "inner join news.readers reader " +
        "where reader.id =:id")
    List<News> findReadNews(@Param("id") Long id);

    /**
     * finds all
     * @return
     */
    @Query(value = "select news from News news order by  news.date DESC")
    List<News> findAllDesc();

    /**
     * finds read news with pagination
     * @param id of reader
     * @param pageable the pageable object
     * @return page with read news
     */
    @Query(value =
        "select news from News news " +
            "inner join news.readers reader " +
            "where reader.id  =:id "
    )
    Page<News> findReadNewsWithPagination(@Param("id") Long id, Pageable pageable);

    /**
     * finds unread news with pagination
     * @param read news
     * @param pageable the pageable object
     * @return page with unread news
     */
    @Query(value =
        "select news from News news " +
            "where news NOT IN" +
            "(:readNews)"
    )
    Page<News> findUnreadNewsWithPagination(@Param("readNews") List<News> read, Pageable pageable);

    /**
     * finds unread News
     * @param read news
     * @return list of unread news
     */
    @Query(value =
        "select news from News news " +
            "where news.id NOT IN" +
            "(:readNews)" +
            "order by news.date"
    )
    List<News> findUnreadNews(@Param("readNews") List<News> read);
}

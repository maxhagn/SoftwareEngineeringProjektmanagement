package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.PriceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceCategoryRepository extends JpaRepository<PriceCategory, Long> {

    /**
     * gives cheapest price in hall
     * @param hall_id filters by hall id
     * @param pageable filters first one
     * @return list of horses that satisfy the query

    @Query(value = "Select pricecategory from PriceCategory pricecategory" +
        " left join Area area on area.id=pricecategory.id " +
        " where area.hall = :hall_id " +
        " group by area.id " +
        " order by pricecategory.price"
    )
    PriceCategory searchCheapestPrice(@Param("hall_id") String hall_id,
                                      Pageable pageable
    );*/
}

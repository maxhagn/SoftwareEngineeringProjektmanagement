package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HallService;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.lang.invoke.MethodHandles;
import java.util.Optional;


@Service
public class HallServiceImpl implements HallService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallRepository hallRepository;
    private final PriceCategoryRepository priceCategoryRepository;
    private final Validator validator;

    @Autowired
    public HallServiceImpl(HallRepository hallRepository, PriceCategoryRepository priceCategoryRepository, Validator validator) {
        this.hallRepository = hallRepository;
        this.priceCategoryRepository = priceCategoryRepository;
        this.validator = validator;
    }

    @Override
    public Hall findOneById(Long id) throws NotFoundException{
        Optional<Hall> hall = hallRepository.findById(id);
        if(hall.isPresent()){
            return hall.get();
        }
        throw new NotFoundException("Hall not found");
    }
}

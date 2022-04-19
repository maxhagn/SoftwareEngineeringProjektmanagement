package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsOutputPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    /**
     * This method maps a input dto to a news entity
     * @param newsInputDto  that is being mapped
     * @return mapped entity
     */
    @Named("inputDtoToNews")
    News inputDtoToNews(NewsInputDto newsInputDto);
    /**
     * This method maps a news entity to a output dto
     * @param news_entity that is being mapped
     * @return mapped dto
     */
    NewsOutputDto outputNewsDto(News news_entity);

    /**
     * This method maps a news entity to a preview dto
     * @param news_entity that is being mapped
     * @return mapped dto
     */
    NewsOutputPreviewDto newsToPreviewDto(News news_entity);

    /**
     * This method maps a news entity to a detail dto
     * @param byId that is being mapped
     * @return mapped dto
     */
    NewsOutputDetailDto newsToDetailDto(News byId);

    /**
     * This method maps a news entity to a reader dto
     * @param news that is being mapped
     * @return mapped dto
     */
    NewsOutputReadersDto newsToReaderDto(News news);


    News inputReaderDtoToNews(NewsInputReadersDto news);


}

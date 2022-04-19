package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    PreviewArtistDto entityToDto(Artist artist);

    Artist dtoToEntity(PreviewArtistDto previewArtistDto);

}
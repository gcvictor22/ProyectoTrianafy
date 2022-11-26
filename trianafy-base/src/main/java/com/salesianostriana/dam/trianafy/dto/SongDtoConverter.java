package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongDtoConverter {

    private final ArtistDtoConverter dtoConverter;

    public Song createSongDtoToSong(CreateSongDto dto){
        return new Song(
                dto.getTitle(),
                dto.getAlbum(),
                dto.getYear()
        );
    }

    public GetSongDto songToGetSongDto(Song s){

        String aux = null;

        if (s.getArtist() != null) {
            aux = s.getArtist().getName();
        }

        return GetSongDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .album(s.getAlbum())
                .year(s.getYear())
                .artist(aux)
                .build();
    }

    public GetSongIdDto getSongToGetSongIdDto(Song s){

        Artist aux = new Artist();

        if (s.getArtist() != null) {
            aux = s.getArtist();
        }

        return GetSongIdDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .album(s.getAlbum())
                .year(s.getYear())
                .artist(dtoConverter.artistToArtistDto(aux))
                .build();

    }
}

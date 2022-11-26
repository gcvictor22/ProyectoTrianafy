package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

@Component
public class SongDtoConverter {

    public Song createSongDtoToSong(CreateSongDto dto){
        return new Song(
                dto.getTitle(),
                dto.getAlbum(),
                dto.getYear()
        );
    }

    public GetSongDto songToGetSongDto(Song s){
        return GetSongDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .album(s.getAlbum())
                .year(s.getYear())
                .artist(s.getArtist().getName())
                .build();
    }
}

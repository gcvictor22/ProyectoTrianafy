package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaylistDtoConverter {

    private final SongDtoConverter dtoConverter;

    public Playlist playlistDtoToPlaylist(CreatePlaylistDto dto){

        return Playlist.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

    }

    public GetCreatedPlaylistDto playlistDtoToGetCreatedPlaylistDto (Playlist p){
        return GetCreatedPlaylistDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    public GetPlaylistDto playlistToPlaylistDto(Playlist p){

        return GetPlaylistDto.builder()
                .id(p.getId())
                .name(p.getName())
                .numberOfSongs(p.getSongs().size())
                .build();
    }

    public GetAddSongToPlaylistDto playlistToGetAddSonToPlaylistDto(Playlist p){

        List<GetSongDto> aux = new ArrayList<GetSongDto>();

        p.getSongs().forEach(s ->{
            aux.add(dtoConverter.songToGetSongDto(s));
        });

        return GetAddSongToPlaylistDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .songs(aux)
                .build();

    }

}

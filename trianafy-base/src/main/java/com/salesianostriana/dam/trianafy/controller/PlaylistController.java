package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.CreatePlaylistDto;
import com.salesianostriana.dam.trianafy.dto.GetCreatedPlaylistDto;
import com.salesianostriana.dam.trianafy.dto.GetPlaylistDto;
import com.salesianostriana.dam.trianafy.dto.PlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistDtoConverter dtoConverter;

    @GetMapping("/list")
    public ResponseEntity<List<GetPlaylistDto>> findAll(){

        if(playlistService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }

        List<GetPlaylistDto> aux = new ArrayList<GetPlaylistDto>();

        playlistService.findAll().forEach(playlist -> {
            aux.add(dtoConverter.playlistToPlaylistDto(playlist));
        });

        return ResponseEntity.ok(aux);

    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Playlist> findOneById(@PathVariable Long id) {

        if(!playlistService.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(playlistService.findById(id).get());

    }

    @PostMapping("/list")
    public ResponseEntity<GetCreatedPlaylistDto> create(@RequestBody CreatePlaylistDto dto){
        if(dto.getName() == null || dto.getDescription()== null){
            return ResponseEntity.badRequest().build();
        }

        Playlist nueva = dtoConverter.playlistDtoToPlaylist(dto);
        playlistService.add(nueva);

        GetCreatedPlaylistDto aux = dtoConverter.playlistDtoToGetCreatedPlaylistDto(nueva);

        return ResponseEntity.status(HttpStatus.CREATED).body(aux);
    }

    @PutMapping("/list/{id}")
    public ResponseEntity<GetCreatedPlaylistDto> update(@PathVariable Long id, @RequestBody CreatePlaylistDto dto){

        if(dto.getName() == null || dto.getDescription()== null){
            return ResponseEntity.badRequest().build();
        } else if (!playlistService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }else{

            List<Song> getSongs = new ArrayList<Song>();

            getSongs.addAll(playlistService.findById(id).get().getSongs());

            return ResponseEntity.of(
                    playlistService.findById(id)
                            .map(p -> {
                                p.setId(id);
                                p.setName(dto.getName());
                                p.setDescription(dto.getDescription());
                                p.setSongs(getSongs);

                                playlistService.edit(p);
                                GetCreatedPlaylistDto aux = dtoConverter.playlistDtoToGetCreatedPlaylistDto(p);

                                return Optional.of(aux);
                            })
                            .orElse(Optional.empty())
            );

        }
    }

    @DeleteMapping("/list/{id}")
    public ResponseEntity<Song> delete(@PathVariable Long id){

        if (!playlistService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            playlistService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }



}

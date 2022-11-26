package com.salesianostriana.dam.trianafy.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.salesianostriana.dam.trianafy.dto.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController()
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final ArtistService artistService;
    private final SongDtoConverter dtoConverter;

    @GetMapping("/song/")
    public ResponseEntity<List<GetSongDto>> findAll(){
        if(songService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else {

            List<GetSongDto> aux = new ArrayList<GetSongDto>();

            for(Song auxSong : songService.findAll()){
                aux.add(dtoConverter.songToGetSongDto(auxSong));
            }

            return ResponseEntity.ok(aux);
        }
    }

    @GetMapping("/song/{id}")
    public ResponseEntity<Song> findOneById(@PathVariable Long id){
        if(songService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.of(songService.findById(id));
        }
    }

    @PostMapping("/song/")
    public ResponseEntity<GetSongDto> create(@RequestBody CreateSongDto dto){

        if(dto.getTitle().isEmpty() || dto.getAlbum().isEmpty()
        || dto.getYear().isEmpty() || dto.getArtistId() == null){
            return ResponseEntity.badRequest().build();
        }else{
            Song nueva = dtoConverter.createSongDtoToSong(dto);
            Artist artist = artistService.findById(dto.getArtistId()).get();

            nueva.setArtist(artist);

            nueva = songService.add(nueva);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoConverter.songToGetSongDto(nueva));
        }
    }

    @PutMapping("/song/{id}")
    public ResponseEntity<GetSongDto> update(@PathVariable Long id, @RequestBody CreateSongDto dto){

        if(artistService.findById(dto.getArtistId()).isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Artist artist = artistService.findById(dto.getArtistId()).get();

        return ResponseEntity.of(
                songService.findById(id)
                        .map(old -> {
                            old.setTitle(dto.getTitle());
                            old.setAlbum(dto.getAlbum());
                            old.setArtist(artist);
                            old.setYear(dto.getYear());
                            songService.edit(dtoConverter.createSongDtoToSong(dto));
                            return Optional.of(dtoConverter.songToGetSongDto(songService.edit(old)));
                        })
                        .orElse(Optional.empty())
                );
    }


    @DeleteMapping("/song/{id}")
    public ResponseEntity<Song> delete(@PathVariable Long id){
        if(songService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            songService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }



}

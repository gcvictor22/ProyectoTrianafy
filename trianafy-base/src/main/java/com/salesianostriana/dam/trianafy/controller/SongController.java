package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final ArtistService artistService;
    private final SongDtoConverter dtoConverter;

    @GetMapping("/song/")
    public ResponseEntity<List<Song>> findAll(){
        if(songService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok(songService.findAll());
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
    public ResponseEntity<Song> create(@RequestBody CreateSongDto dto){

        if(dto.getTitle().isEmpty() || dto.getAlbum().isEmpty()
        || dto.getYear().isEmpty() || dto.getArtistId() == null){
            return ResponseEntity.notFound().build();
        }else{
            Song nueva = dtoConverter.createSongDtoToSong(dto);
            Artist artist = artistService.findById(dto.getArtistId()).get();

            nueva.setArtist(artist);

            nueva = songService.add(nueva);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(nueva);
        }
    }

}

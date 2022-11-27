package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;

    @GetMapping("/artist/")
    public ResponseEntity<List<Artist>> findAll(){
        if(artistService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok().body(artistService.findAll());
        }
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> findOneById(@PathVariable Long id){
        if(artistService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.of(artistService.findById(id));
        }
    }

    @PostMapping("/artist/")
    public ResponseEntity<Artist> create(@RequestBody Artist a){
        if (a.getName() == null) {
            return ResponseEntity.badRequest().build();
        }else{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(artistService.add(a));
        }
    }

    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> update(@PathVariable Long id, @RequestBody Artist a){
        if(artistService.findById(id).isEmpty()){
            return ResponseEntity.badRequest().build();
        }else {

            return ResponseEntity.of(
                    artistService.findById(id)
                            .map(old -> {
                                old.setName(a.getName());
                                return Optional.of(artistService.add(old));
                            })
                            .orElse(Optional.empty())
            );
        }
    }

    @DeleteMapping("/artist/{id}")
    public ResponseEntity<Artist> delete(@PathVariable Long id) {
        if (artistService.findById(id).isPresent()) {

            songService.findAll().forEach(song -> {
                if(song.getArtist() != null){
                    if(song.getArtist().getId().equals(id)){
                        song.setArtist(null);
                        songService.add(song);
                    }
                }
            });
            artistService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

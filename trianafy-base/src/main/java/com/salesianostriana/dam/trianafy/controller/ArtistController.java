package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService service;

    @GetMapping("/artist/")
    public ResponseEntity<List<Artist>> findAll(){
        if(service.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok().body(service.findAll());
        }
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> findOneById(@PathVariable Long id){
        if(service.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.of(service.findById(id));
        }
    }

    @PostMapping("/artist/")
    public ResponseEntity<Artist> create(@RequestBody Artist a){
        if (a.getName() == null) {
            return ResponseEntity.badRequest().build();
        }else{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.add(a));
        }
    }

    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> update(@PathVariable Long id, @RequestBody Artist a){
        if(service.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else {

            return ResponseEntity.of(
                    service.findById(id)
                            .map(old -> {
                                old.setName(a.getName());
                                return Optional.of(service.add(old));
                            })
                            .orElse(Optional.empty())
            );
        }
    }

    @DeleteMapping("/artist/{id}")
    public ResponseEntity<Artist> delete(@PathVariable Long id){
        if(service.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}

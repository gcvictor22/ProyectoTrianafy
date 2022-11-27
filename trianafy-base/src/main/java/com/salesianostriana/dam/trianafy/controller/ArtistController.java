package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Controlador de artistas")
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;

    @Operation(summary = "Obtiene todos los artistas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado artistas",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Artist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                                {"id": 1, "name": "Jon Doe"},
                                                {"id": 2, "name": "Random name"}
                                            ]                                          
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ningun artista",
                    content = @Content),
    })
    @GetMapping("/artist/")
    public ResponseEntity<List<Artist>> findAll(){
        if(artistService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok().body(artistService.findAll());
        }
    }

    @Operation(summary = "Obtiene un artista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado el artista",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado el artista específico",
                    content = @Content),
    })
    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> findOneById(@Parameter(description = "ID del artista") @PathVariable Long id){
        if(artistService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.of(artistService.findById(id));
        }
    }

    @Operation(summary = "Obtiene un artista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado el artista",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado el artista específicado",
                    content = @Content),
    })
    @PostMapping("/artist/")
    public ResponseEntity<Artist> create(@RequestBody Artist a){
        if (a.getName() == null) {
            return ResponseEntity.badRequest().build();
        }else{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(artistService.add(a));
        }
    }

    @Operation(summary = "Edita un artista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado el artista",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado el artista específicado",
                    content = @Content),
    })
    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> update(@Parameter(description = "ID del artista") @PathVariable Long id, @RequestBody Artist a){
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

    @Operation(summary = "Elimina un artista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha eliminado el artista con éxito",
                    content = @Content),
    })
    @DeleteMapping("/artist/{id}")
    public ResponseEntity<Artist> delete(@Parameter(description = "ID del artista") @PathVariable Long id) {
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

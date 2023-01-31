package com.salesianostriana.dam.trianafy.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.salesianostriana.dam.trianafy.dto.*;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
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

@RestController()
@RequiredArgsConstructor
@Tag(name = "Song", description = "Controller de Song")
public class SongController {

    private final SongService songService;
    private final ArtistService artistService;
    private final SongDtoConverter dtoConverter;
    private final PlaylistService playlistService;
/*
    @Operation(summary = "Obtiene todas las canciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado todas las canciones",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetSongDto.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "title": "string",
                                                "artist": "string",
                                                "album": "string",
                                                "year": "string"
                                              },
                                              {
                                                "id": 2,
                                                "title": "string",
                                                "artist": "string",
                                                "album": "string",
                                                "year": "string"
                                              }
                                            ]                                          
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna canción",
                    content = @Content),
    })
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

    @Operation(summary = "Obtiene una canción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la canción",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongIdDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la canción específica",
                    content = @Content),
    })
    @GetMapping("/song/{id}")
    public ResponseEntity<GetSongIdDto> findOneById(@Parameter(description = "ID de la canción") @PathVariable Long id){
        if(songService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtoConverter.getSongToGetSongIdDto(songService.findById(id).get()));
    }

    @Operation(summary = "Crea una canción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado la canción correctamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "No se ha podido crear la canción",
                    content = @Content),
    })
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

    @Operation(summary = "Edita una canción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha editado la canción correctamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "No se ha podido crear la canción",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la canción específica",
                    content = @Content),
    })
    @PutMapping("/song/{id}")
    public ResponseEntity<GetSongDto> update(@Parameter(description = "ID de la canción") @PathVariable Long id, @RequestBody CreateSongDto dto){

        if(dto.getArtistId() == null || dto.getTitle() == null
        || dto.getAlbum() == null || dto.getYear() == null || !artistService.existsById(dto.getArtistId())){
            return ResponseEntity.badRequest().build();
        }

        if(songService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Artist artist = artistService.findById(dto.getArtistId()).orElse(null);

        return ResponseEntity.of(
                songService.findById(id)
                        .map(old -> {
                            old.setArtist(artist);
                            old.setTitle(dto.getTitle());
                            old.setAlbum(dto.getAlbum());
                            old.setYear(dto.getYear());
                            songService.edit(dtoConverter.createSongDtoToSong(dto));
                            return Optional.of(dtoConverter.songToGetSongDto(songService.edit(old)));
                        })
                        .orElse(Optional.empty())
                );
    }


    @Operation(summary = "Elimina una canción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha eliminado la canción correctamente",
                    content = @Content),
    })
    @DeleteMapping("/song/{id}")
    public ResponseEntity<Song> delete(@Parameter(description = "ID de la canción") @PathVariable Long id){
        if(songService.findById(id).isPresent()){

            List<Playlist> aux = playlistService.findAll().stream()
                    .filter(p -> p.getSongs().contains(songService.findById(id).get())).toList();

            aux.stream().forEach(p -> {
                while (p.getSongs().contains(songService.findById(id).get())){
                    p.deleteSong(songService.findById(id).get());
                }
                playlistService.edit(p);
            });

            songService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

*/

}

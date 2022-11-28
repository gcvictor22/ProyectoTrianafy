package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.*;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
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
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequiredArgsConstructor
@Tag(name = "Playlist", description = "Controller de Playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SongService songService;
    private final PlaylistDtoConverter dtoConverter;
    private final SongController songController;

    @Operation(summary = "Obtiene todas las playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado todas las playlist",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetPlaylistDto.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "name": "Random name",
                                                "numberOfSongs": 4
                                              },
                                              {
                                                "id": 2,
                                                "name": "another random name",
                                                "numberOfSongs": 78
                                              }
                                            ]                                       
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna playlist",
                    content = @Content),
    })
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

    @Operation(summary = "Obtiene una playlist específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la playlist",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetAddSongToPlaylistDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la playlist específicada",
                    content = @Content),
    })
    @GetMapping("/list/{id}")
    public ResponseEntity<GetAddSongToPlaylistDto> findOneById(@Parameter(description = "ID de la playlist") @PathVariable Long id) {

        if(!playlistService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(dtoConverter.playlistToGetAddSonToPlaylistDto(playlistService.findById(id).get()));

    }

    @Operation(summary = "Crea una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado la canción correctamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetCreatedPlaylistDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "No se ha podido crear la canción",
                    content = @Content),
    })
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

    @Operation(summary = "Edita una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha editado la playlist correctamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetCreatedPlaylistDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "No se ha podido editar la playlist",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la playlist específica",
                    content = @Content),
    })
    @PutMapping("/list/{id}")
    public ResponseEntity<GetCreatedPlaylistDto> update(@Parameter(description = "ID de la playlist") @PathVariable Long id, @RequestBody CreatePlaylistDto dto){

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

    @Operation(summary = "Elimina una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha eliminado la playlist correctamente",
                    content = @Content),
    })
    @DeleteMapping("/list/{id}")
    public ResponseEntity<Song> delete(@Parameter(description = "ID de la playlist") @PathVariable Long id){

        if (playlistService.existsById(id)) {
            playlistService.deleteById(id);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @Operation(summary = "Añade una canción a una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha añadido la canción correctamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetAddSongToPlaylistDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la canción específica",
                    content = @Content),
    })
    @PostMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<GetAddSongToPlaylistDto> addSong(@Parameter(description = "ID de la playlist") @PathVariable Long idP, @Parameter(description = "ID de la canción") @PathVariable Long idS){
        if(!playlistService.existsById(idP) || !songService.existsById(idS)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{

            Playlist aux = playlistService.findById(idP).get();
            aux.addSong(songService.findById(idS).get());

            ResponseEntity.of(
                    playlistService.findById(idP)
                            .map(p -> {
                                p.setSongs(aux.getSongs());
                                return Optional.of(playlistService.edit(p));
                            })
                            .orElse(Optional.empty())
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    dtoConverter.playlistToGetAddSonToPlaylistDto(playlistService.findById(idP).get())
            );
        }
    }

    @Operation(summary = "Obtiene las canciones de una playlist específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la playlist",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetAddSongToPlaylistDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la playlist específicada",
                    content = @Content),
    })
    @GetMapping("/list/{id}/song")
    public ResponseEntity<GetAddSongToPlaylistDto> getAllPlaylistSongs(@Parameter(description = "ID de la playlist") @PathVariable Long id){
        return findOneById(id);
    }

    @Operation(summary = "Obtiene una canción específica de una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la canción",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongIdDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado la canción específica, la playlist, y/o la" +
                            "canción no esta incluida en la playlist especificada",
                    content = @Content),
    })
    @GetMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<GetSongIdDto> findSongFromPlaylist(@Parameter(description = "ID de la playlist") @PathVariable Long idP, @Parameter(description = "ID de la canción") @PathVariable Long idS){

        if(!playlistService.existsById(idP) || !songService.existsById(idS)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
            List<Song> auxList = playlistService.findById(idP).get().getSongs();
            Song auxSong = songService.findById(idS).get();
            if(!auxList.contains(auxSong)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return songController.findOneById(idS);
            }

        }
    }

    @Operation(summary = "Elimina una/s canción/es de una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha/n eliminado la/s cancion/es de la playlist correctamente",
                    content = @Content),
    })
   @DeleteMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<Playlist> deleteSongFromPlaylist (@Parameter(description = "ID de la playlist") @PathVariable Long idP, @Parameter(description = "ID de la canción") @PathVariable Long idS){

       List<Song> auxList = playlistService.findById(idP).get().getSongs();
       Song auxSong = songService.findById(idS).get();

       if(!playlistService.existsById(idP) || !songService.existsById(idS) || !auxList.contains(auxSong)){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       } else {

           ResponseEntity.of(
                   playlistService.findById(idP)
                           .map(p -> {
                               while(auxList.contains(auxSong)){
                                   p.deleteSong(auxSong);
                               }
                               return Optional.of(playlistService.edit(p));
                           })
                           .orElse(Optional.empty())
           );

           return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

       }

   }

}
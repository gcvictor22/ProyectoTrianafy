package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.*;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
import com.salesianostriana.dam.trianafy.service.SongService;
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
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SongService songService;
    private final PlaylistDtoConverter dtoConverter;
    private final SongController songController;

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
    public ResponseEntity<GetAddSongToPlaylistDto> findOneById(@PathVariable Long id) {

        if(!playlistService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(dtoConverter.playlistToGetAddSonToPlaylistDto(playlistService.findById(id).get()));

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

        if (playlistService.existsById(id)) {
            playlistService.deleteById(id);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PostMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<GetAddSongToPlaylistDto> addSong(@PathVariable Long idP, @PathVariable Long idS){
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

    @GetMapping("/list/{id}/song")
    public ResponseEntity<GetAddSongToPlaylistDto> getAllPlaylistSongs(@PathVariable Long id){
        return findOneById(id);
    }

    @GetMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<GetSongIdDto> findSongFromPlaylist(@PathVariable Long idP, @PathVariable Long idS){

        List<Song> auxList = playlistService.findById(idP).get().getSongs();
        Song auxSong = songService.findById(idS).get();

        if(!playlistService.existsById(idP) || !songService.existsById(idS) || !auxList.contains(auxSong)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return songController.findOneById(idS);
        }
    }

   @DeleteMapping("/list/{idP}/song/{idS}")
    public ResponseEntity<Playlist> deleteSongFromPlaylist (@PathVariable Long idP, @PathVariable Long idS){

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
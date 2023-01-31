package com.salesianostriana.dam.trianafy.service;


import com.salesianostriana.dam.trianafy.exception.EmptyException;
import com.salesianostriana.dam.trianafy.exception.NotFoundException;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository repository;


    public Artist add(Artist artist) {
        return repository.save(artist);
    }

    public Artist findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public List<Artist> findAll() {

        List<Artist> list = repository.findAll();

        if (list.isEmpty())
                throw new EmptyException();

        return list;
    }

    public Artist edit(Artist artist) {
        return repository.save(artist);
    }

    public void delete(Artist artist) {
        repository.delete(artist);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsById(Long id){
        if(repository.existsById(id)){
            return true;
        }else{
            return false;
        }
    }

}

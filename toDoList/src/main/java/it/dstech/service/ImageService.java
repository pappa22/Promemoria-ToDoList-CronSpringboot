package it.dstech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.dstech.models.User;
import it.dstech.repository.ImageRepository;


@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public User getFile(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> null);
    }
}
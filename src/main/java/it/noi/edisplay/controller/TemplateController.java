package it.noi.edisplay.controller;

import it.noi.edisplay.dto.TemplateDto;
import it.noi.edisplay.model.Template;
import it.noi.edisplay.repositories.TemplateRepository;
import it.noi.edisplay.utils.MonochromeImageCreator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to create API for CRUD operations on Templates
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    ModelMapper modelMapper;

    @RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<TemplateDto> getTemplate(@PathVariable("uuid") String uuid) {
        Template template = templateRepository.findByUuid(uuid);

        if (template == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(modelMapper.map(template, TemplateDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getAllTemplates() {
        List<Template> list = templateRepository.findAll();
        ArrayList<TemplateDto> dtoList = new ArrayList<>();
        for (Template template : list)
            dtoList.add(modelMapper.map(template, TemplateDto.class));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity createTemplate(@RequestParam("name") String name, @RequestParam("image") MultipartFile image) {
        Template template = new Template();
        template.setName(name);

        try {
            InputStream in = new ByteArrayInputStream(image.getBytes());
            BufferedImage bImageFromConvert = ImageIO.read(in);
            template.setImage(MonochromeImageCreator.convertToMonochrome(bImageFromConvert));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(modelMapper.map(templateRepository.saveAndFlush(template), TemplateDto.class), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/delete/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity deleteTemplate(@PathVariable("uuid") String uuid) {
        Template template = templateRepository.findByUuid(uuid);

        if (template == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        try {
            templateRepository.delete(template);
        } catch (EmptyResultDataAccessException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity updateTemplate(@RequestBody TemplateDto templateDto) {
        Template template = templateRepository.findByUuid(templateDto.getUuid());
        if (template == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        template.setName(templateDto.getName());
        template.setImage(templateDto.getImage());
        templateRepository.save(template);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}


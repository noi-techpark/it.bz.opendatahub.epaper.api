package it.noi.edisplay.controller;


import it.noi.edisplay.dto.DisplayDto;
import it.noi.edisplay.model.Display;
import it.noi.edisplay.model.Template;
import it.noi.edisplay.repositories.DisplayRepository;
import it.noi.edisplay.repositories.TemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Controller class to create API for CRUD operations on Displays
 */
@RestController
@RequestMapping("/display")
public class DisplayController {


    @Autowired
    DisplayRepository displayRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    ModelMapper modelMapper;

    @RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<DisplayDto> getDisplay(@PathVariable("uuid") String uuid) {
        Display display = displayRepository.findByUuid(uuid);

        if (display == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(modelMapper.map(display, DisplayDto.class), HttpStatus.OK);
    }


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getAllDisplays() {
        List<Display> list = displayRepository.findAll();
        ArrayList<DisplayDto> dtoList = new ArrayList<>();
        for (Display display : list)
            dtoList.add(modelMapper.map(display, DisplayDto.class));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity createDisplay(@RequestParam("name") String name, @RequestParam("templateUuid") String templateUuid) {
        Display display = new Display();
        display.setName(name);
        display.setBatteryPercentage(new Random().nextInt(99));

        Template template = templateRepository.findByUuid(templateUuid);
        display.setImage(template.getImage());

        return new ResponseEntity<>(modelMapper.map(displayRepository.saveAndFlush(display), DisplayDto.class), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/delete/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity deleteDisplay(@PathVariable("uuid") String uuid) {
        Display display = displayRepository.findByUuid(uuid);

        if (display == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        try {
            displayRepository.delete(display);
        } catch (EmptyResultDataAccessException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity updateDisplay(@RequestBody DisplayDto displayDto) {
        Display display = displayRepository.findByUuid(displayDto.getUuid());
        if (display == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        display.setBatteryPercentage(displayDto.getBatteryPercentage());
        display.setImage(displayDto.getImage());
        display.setImage(displayDto.getImage());
        display.setName(displayDto.getName());
        display.setLastState(displayDto.getLastState());
        displayRepository.save(display);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

}

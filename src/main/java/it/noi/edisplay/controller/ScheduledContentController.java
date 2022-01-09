package it.noi.edisplay.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.noi.edisplay.dto.EventDto;
import it.noi.edisplay.dto.NOIPlaceData;
import it.noi.edisplay.dto.NOIPlaceDto;
import it.noi.edisplay.dto.ScheduledContentDto;
import it.noi.edisplay.model.Display;
import it.noi.edisplay.model.Location;
import it.noi.edisplay.model.ScheduledContent;
import it.noi.edisplay.repositories.DisplayRepository;
import it.noi.edisplay.repositories.ScheduledContentRepository;
import it.noi.edisplay.services.OpenDataRestService;

@RestController
@RequestMapping("/ScheduledContent")
public class ScheduledContentController {
    @Autowired
    ScheduledContentRepository scheduledContentRepository;

    @Autowired
    DisplayRepository displayRepository;

    @Autowired
    private OpenDataRestService openDataRestService;

    @Autowired
    ModelMapper modelMapper;

    Logger logger = LoggerFactory.getLogger(ScheduledContentController.class);

    @RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ScheduledContentDto> getScheduledContent(@PathVariable("uuid") String uuid) {
        ScheduledContent scheduledContent = scheduledContentRepository.findByUuid(uuid);

        if (scheduledContent == null) {
            logger.debug("Scheduled content with uuid: " + uuid + " not found.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.debug("Get scheduled content with uuid: " + uuid);
        return new ResponseEntity<>(modelMapper.map(scheduledContent, ScheduledContentDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getAllScheduledContents(@RequestParam String displayUuid) {
        Display display = displayRepository.findByUuid(displayUuid);

        if (display == null) {
            logger.debug("Display with uuid: " + displayUuid + " not found.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<ScheduledContent> list = scheduledContentRepository.findByDisplayId(display.getId());

        ArrayList<ScheduledContentDto> dtoList = new ArrayList<>();

        for (ScheduledContent scheduledContent : list)
            dtoList.add(modelMapper.map(scheduledContent, ScheduledContentDto.class));

        // Retrieve NOI events
        List<EventDto> events = openDataRestService.getEvents();

        // Retrieve NOI places for filtering out events
        NOIPlaceDto places = openDataRestService.getNOIPlaces();

        Location displayLocation = display.getLocation();

        if (displayLocation != null && displayLocation.getRoomCode() != null) {
            NOIPlaceData room = places.getData().stream()
                    .filter(item -> item.getScode().equals(displayLocation.getRoomCode())).findFirst().orElse(null);
            if (room != null) {
                List<EventDto> noiEvents = events.stream()
                        .filter(item -> item.getSpaceDesc().equals(room.getTodaynoibzit()))
                        .collect(Collectors.toList());
                for (EventDto noiEvent : noiEvents) {
                    // Look for modified events in eInk database
                    ScheduledContentDto scheduledContentDto = dtoList.stream().filter(
                            item -> item.getEventId() != null && item.getEventId().equals(noiEvent.getEventId()))
                            .findFirst().orElse(null);
                    if (scheduledContentDto == null) {
                        scheduledContentDto = new ScheduledContentDto();
                        scheduledContentDto.setStartDate(new Timestamp(noiEvent.getRoomStartDateUTC()));
                        scheduledContentDto.setEndDate(new Timestamp(noiEvent.getRoomEndDateUTC()));
                        scheduledContentDto.setEventDescription(noiEvent.getEventDescriptionEN());
                        scheduledContentDto.setEventId(noiEvent.getEventId());
                        scheduledContentDto.setDisplayUuid(displayUuid);
                        dtoList.add(scheduledContentDto);
                    }

                    scheduledContentDto.setOriginalStartDate(new Timestamp(noiEvent.getRoomStartDateUTC()));
                    scheduledContentDto.setOriginalEndDate(new Timestamp(noiEvent.getRoomEndDateUTC()));
                    scheduledContentDto.setOriginalEventDescription(noiEvent.getEventDescriptionEN());
                }
            }
        }

        logger.debug("All scheduled content requested");
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity createScheduledContent(@RequestBody ScheduledContentDto scheduledContentDto) {
        ScheduledContent scheduledContent = modelMapper.map(scheduledContentDto, ScheduledContent.class);
        scheduledContent.setDisplay(displayRepository.findByUuid(scheduledContentDto.getDisplayUuid()));
        scheduledContentRepository.saveAndFlush(scheduledContent);

        logger.debug("Scheduled content with uuid:" + scheduledContent.getUuid() + " created.");
        return new ResponseEntity<>(modelMapper.map(scheduledContent, ScheduledContentDto.class), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/delete/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity deleteScheduledContent(@PathVariable("uuid") String uuid) {
        ScheduledContent scheduledContent = scheduledContentRepository.findByUuid(uuid);
        if (scheduledContent == null) {
            logger.debug("Delete scheduled content with uuid:" + uuid + " failed.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        scheduledContentRepository.delete(scheduledContent);
        logger.debug("Deleted scheduled content with uuid:" + uuid);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity updateScheduledContent(@RequestBody ScheduledContentDto scheduledContentDto) {
        ScheduledContent scheduledContent;
        ScheduledContent existingScheduledContent;
        Display display = displayRepository.findByUuid(scheduledContentDto.getDisplayUuid());

        if (scheduledContentDto.getUuid() != null) {
            existingScheduledContent = scheduledContentRepository.findByUuid(scheduledContentDto.getUuid());
        } else {
            existingScheduledContent = scheduledContentRepository.findByDisplayIdAndEventId(display.getId(),
                    scheduledContentDto.getEventId());
        }

        if (existingScheduledContent == null) {
            scheduledContent = modelMapper.map(scheduledContentDto, ScheduledContent.class);
        } else {
            existingScheduledContent.setDisabled(scheduledContentDto.getDisabled());
            existingScheduledContent.setStartDate(scheduledContentDto.getStartDate());
            existingScheduledContent.setEndDate(scheduledContentDto.getEndDate());
            existingScheduledContent.setEventDescription(scheduledContentDto.getEventDescription());
            scheduledContent = existingScheduledContent;
        }

        scheduledContent.setDisplay(display);

        scheduledContentRepository.saveAndFlush(scheduledContent);
        logger.debug("Updated scheduled content with uuid:" + scheduledContent.getUuid());

        if (existingScheduledContent == null) {
            return new ResponseEntity<>(modelMapper.map(scheduledContent, ScheduledContentDto.class),
                    HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
    }
}

// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.noi.edisplay.dto;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;
import java.sql.Timestamp;

public class ScheduledContentDto {

    private Boolean disabled;

    private Integer eventId;

    private Timestamp startDate;

    private Timestamp endDate;

    private String displayUuid;

    private String eventDescription;

    private Timestamp originalStartDate;

    private Timestamp originalEndDate;

    private String originalEventDescription;

    private String spaceDesc;

    private DisplayContentDto displayContent;

    @ApiModelProperty(hidden = true)
    private String uuid;
    @ApiModelProperty(hidden = true)
    private Date created;
    @ApiModelProperty(hidden = true)
    private Date lastUpdate;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getDisplayUuid() {
        return displayUuid;
    }

    public void setDisplayUuid(String displayUuid) {
        this.displayUuid = displayUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Timestamp getOriginalStartDate() {
        return originalStartDate;
    }

    public void setOriginalStartDate(Timestamp originalStartDate) {
        this.originalStartDate = originalStartDate;
    }

    public Timestamp getOriginalEndDate() {
        return originalEndDate;
    }

    public void setOriginalEndDate(Timestamp originalEndDate) {
        this.originalEndDate = originalEndDate;
    }

    public String getOriginalEventDescription() {
        return originalEventDescription;
    }

    public void setOriginalEventDescription(String originalEventDescription) {
        this.originalEventDescription = originalEventDescription;
    }

    public DisplayContentDto getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(DisplayContentDto displayContent) {
        this.displayContent = displayContent;
    }

    public String getSpaceDesc() {
        return spaceDesc;
    }

    public void setSpaceDesc(String spaceDesc) {
        this.spaceDesc = spaceDesc;
    }

}
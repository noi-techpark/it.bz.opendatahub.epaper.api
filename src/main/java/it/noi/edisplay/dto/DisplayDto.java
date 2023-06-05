// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.noi.edisplay.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class DisplayDto {

	private String name;
	private byte[] image;
	private int batteryPercentage;
	private ResolutionDto resolution;
	private String mac;
	private String errorMessage;
	private String locationUuid;
    private DisplayContentDto displayContent;
    private boolean ignoreScheduledContent;
    private String warningMessage;
	private String uuid;
	private String currentImageHash;

	@ApiModelProperty(hidden = true)
	private Date created;

	@ApiModelProperty(hidden = true)
	private Date lastUpdate;

	@ApiModelProperty(hidden = true)
	private Date lastState;

	@ApiModelProperty(hidden = true)
	private Date lastRealDisplayState;

	public Date getLastRealDisplayState() {
		return lastRealDisplayState;
	}

	public void setLastRealDisplayState(Date lastRealDisplayState) {
		this.lastRealDisplayState = lastRealDisplayState;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Date getLastState() {
		return lastState;
	}

	public void setLastState(Date lastState) {
		this.lastState = lastState;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getBatteryPercentage() {
		return batteryPercentage;
	}

	public void setBatteryPercentage(int batteryPercentage) {
		this.batteryPercentage = batteryPercentage;
	}

	public ResolutionDto getResolution() {
		return resolution;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setResolution(ResolutionDto resolution) {
		this.resolution = resolution;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public DisplayContentDto getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(DisplayContentDto displayContent) {
        this.displayContent = displayContent;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public boolean getIgnoreScheduledContent() {
        return ignoreScheduledContent;
    }

    public void setIgnoreScheduledContent(boolean ignoreScheduledContent) {
        this.ignoreScheduledContent = ignoreScheduledContent;
    }

    public String getCurrentImageHash() {
        return currentImageHash;
    }

    public void setCurrentImageHash(String currentImageHash) {
        this.currentImageHash = currentImageHash;
    }
}

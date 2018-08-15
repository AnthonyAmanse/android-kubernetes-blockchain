package com.amanse.anthony.fitcoinandroid.Models;

public class EventModel {
    String eventId;
    String name;
    String description;
    String eventStatus;
    String approvalStatus;
    String owner;
    String link;

    public EventModel(String eventId, String name, String description, String eventStatus, String approvalStatus, String owner, String link) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.eventStatus = eventStatus;
        this.approvalStatus = approvalStatus;
        this.owner = owner;
        this.link = link;
    }

    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getOwner() {
        return owner;
    }

    public String getLink() {
        return link;
    }
}

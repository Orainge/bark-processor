package com.orainge.bark_processor.forward.client.bean;

import lombok.Data;

@Data
public class BarkMessage {
    private String isArchive;
    private String level;
    private String icon;
    private String title;
    private String body;
}

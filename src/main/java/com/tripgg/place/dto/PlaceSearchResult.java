package com.tripgg.place.dto;

import lombok.Data;

@Data
public class PlaceSearchResult {
    private String id;
    private String placeName;
    private String categoryName;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String phone;
    private String addressName;
    private String roadAddressName;
    private Double longitude;
    private Double latitude;
    private String placeUrl;
    private String distance;
    private String source; // "database" 또는 "kakao"
    private String description;
}

package com.novaflow.recommendation.domain.model.recommendation.valueobject;

/**
 * 位置信息值对象（本地存根）
 * 来自 video 服务的跨服务依赖
 */
public class Location {

    private final Double latitude;
    private final Double longitude;
    private final String address;
    private final String city;
    private final String province;

    private Location(Double latitude, Double longitude, String address, String city, String province) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.province = province;
    }

    public static Location of(Double latitude, Double longitude) {
        return new Location(latitude, longitude, null, null, null);
    }

    public static Location of(Double latitude, Double longitude, String address, String city, String province) {
        return new Location(latitude, longitude, address, city, province);
    }

    public static Location empty() {
        return new Location(null, null, null, null, null);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty();
    }
}

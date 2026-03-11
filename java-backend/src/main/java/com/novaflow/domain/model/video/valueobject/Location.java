package com.novaflow.domain.model.video.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 位置信息值对象
 * 表示GPS坐标和位置描述
 */
@Getter
@EqualsAndHashCode
public class Location implements ValueObject {

    private final Double latitude;
    private final Double longitude;
    private final String address;
    private final String city;
    private final String province;

    private Location(Double latitude, Double longitude, String address, String city, String province) {
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.province = province;
    }

    /**
     * 从坐标创建位置（最小信息）
     */
    public static Location of(Double latitude, Double longitude) {
        return new Location(latitude, longitude, null, null, null);
    }

    /**
     * 创建完整的位置信息
     */
    public static Location of(Double latitude, Double longitude, String address, String city, String province) {
        return new Location(latitude, longitude, address, city, province);
    }

    /**
     * 创建空位置
     */
    public static Location empty() {
        return new Location(null, null, null, null, null);
    }

    /**
     * 验证坐标是否有效
     */
    private static void validateCoordinates(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("纬度必须在-90到90之间");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("经度必须在-180到180之间");
        }
    }

    /**
     * 检查是否有位置信息
     */
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    /**
     * 检查是否有详细地址
     */
    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty();
    }

    /**
     * 计算与另一个位置的距离（公里）
     * 使用Haversine公式
     */
    public double distanceTo(Location other) {
        if (!this.hasLocation() || !other.hasLocation()) {
            throw new IllegalStateException("两个位置都必须有坐标信息");
        }

        final int EARTH_RADIUS = 6371; // 地球半径，单位：公里

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude))
                * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 检查是否在指定半径内（公里）
     */
    public boolean isWithinRadius(Location other, double radiusKm) {
        return distanceTo(other) <= radiusKm;
    }
}

package com.tripgg.place.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "places_godata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PlacesGodata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sigungu_name", nullable = false, length = 100)
    private String sigunguName; // 시군명

    @Column(name = "place_name", nullable = false, length = 255)
    private String placeName; // 관광정보명

    @Column(name = "phone_number", length = 50)
    private String phoneNumber; // 전화번호

    @Column(name = "data_reference_date")
    private LocalDate dataReferenceDate; // 데이터기준일자

    @Column(name = "road_address", length = 255)
    private String roadAddress; // 정제도로명주소

    @Column(name = "lot_address", length = 255)
    private String lotAddress; // 정제지번주소

    @Column(name = "postal_code", length = 10)
    private String postalCode; // 정제우편번호

    @Column(name = "latitude")
    private Double latitude; // 정제WGS84위도

    @Column(name = "longitude")
    private Double longitude; // 정제WGS84경도

    @Column(name = "source_info", columnDefinition = "TEXT")
    private String sourceInfo; // 원천데이터정보

    @Column(name = "source_url", columnDefinition = "VARCHAR(500)")
    private Double sourceUrl; // 원천데이터URL

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
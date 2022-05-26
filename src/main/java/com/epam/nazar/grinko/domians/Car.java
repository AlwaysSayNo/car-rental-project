package com.epam.nazar.grinko.domians;

import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.Color;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_brands_id")
    private CarBrand brand;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(name = "price_per_day", nullable = false)
    private Long pricePerDay;

    @Column(name = "segment", nullable = false)
    @Enumerated(EnumType.STRING)
    private CarSegment segment;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CarStatus status;

}

package com.epam.nazar.grinko.domians;

import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.CarColor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "cars")
@Accessors(chain = true)
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_brands_id")
    private CarBrand brand;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "segment", nullable = false)
    @Enumerated(EnumType.STRING)
    private CarSegment segment;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_color_id")
    private CarColor color;

    @Column(name = "price_per_day", nullable = false)
    private Long pricePerDay;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CarStatus status;


    @OneToMany(mappedBy = "car", fetch = FetchType.EAGER)
    private Collection<Order> orders;

}

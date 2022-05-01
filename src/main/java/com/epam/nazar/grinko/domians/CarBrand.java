package com.epam.nazar.grinko.domians;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "car_brands")
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "brand", nullable = false, unique = true)
    private String brand;

    @OneToMany(mappedBy = "brand", fetch = FetchType.EAGER)
    private Collection<Car> cars;
}

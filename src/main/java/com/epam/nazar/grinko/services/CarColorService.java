package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.repositories.CarColorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarColorService {

    private final CarColorRepository carColorRepository;

    public CarColor getColor(String color) {
        return carColorRepository.getCarColorByColor(color);
    }

    public void addNewColor(CarColor newColor){
        carColorRepository.save(newColor);
    }

    public List<CarColor> getAllCarColors(){
        return carColorRepository.findAll();
    }

    public boolean carColorExists(String color) {
        return carColorRepository.existsCarColorByColor(color);
    }

}

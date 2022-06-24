package com.epam.nazar.grinko.services.car;

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
        return carColorRepository.getCarColorByValue(color);
    }

    public void save(CarColor newColor){
        carColorRepository.save(newColor);
    }

    public List<CarColor> getAll(){
        return carColorRepository.findAll();
    }

    public boolean carColorExists(String color) {
        return carColorRepository.existsCarColorByValue(color);
    }

    public void addColorIfExists(String colorName){
        if(!carColorExists(colorName)) {
            CarColor color = new CarColor().setValue(colorName);
            save(color);
        }
    }

}

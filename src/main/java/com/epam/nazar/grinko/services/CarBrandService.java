package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.repositories.CarBrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    public CarBrand getBrand(String brand) {
        return carBrandRepository.getCarBrandByBrand(brand);
    }

    public void addNewBrand(CarBrand newBrand){
        carBrandRepository.save(newBrand);
    }

    public List<CarBrand> getAllCarBrands(){
        return carBrandRepository.findAll();
    }

    public boolean carBrandExists(String brand) {
        return carBrandRepository.existsCarBrandByBrand(brand);
    }

}

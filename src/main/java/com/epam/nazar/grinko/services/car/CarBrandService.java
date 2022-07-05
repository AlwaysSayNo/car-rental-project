package com.epam.nazar.grinko.services.car;

import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.repositories.CarBrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    public CarBrand getBrand(String brand) {
        return carBrandRepository.getCarBrandByValue(brand);
    }

    public void save(CarBrand newBrand){
        carBrandRepository.save(newBrand);
    }

    public List<CarBrand> getAll(){
        return carBrandRepository.findAll();
    }

    public boolean carBrandExists(String brand) {
        return carBrandRepository.existsCarBrandByValue(brand);
    }

    public void addBrandIfNotExists(String brandName){
        if(!carBrandExists(brandName)){
            CarBrand brand = new CarBrand().setValue(brandName);
            save(brand);
        }
    }

}

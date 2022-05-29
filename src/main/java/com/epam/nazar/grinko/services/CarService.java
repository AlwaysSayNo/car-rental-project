package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.repositories.CarRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;

    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(long id){
        return carRepository.findById(id);
    }

    public void addNewCar(Car newCar){
        carRepository.save(newCar);
    }

    public void deleteCarById(long id) {
        carRepository.deleteById(id);
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    // Конвертировать в dto в car можно в том случае, если все поля из бд уже существуют в базе данных
    public Car convertCarDtoToCar(CarDto carDto){
        CarBrand brand = carBrandService.getBrand(carDto.getBrand());
        CarColor color = carColorService.getColor(carDto.getColor());
        CarSegment segment = CarSegment.valueOf(carDto.getSegment());
        CarStatus status = CarStatus.NOT_RENTED;

        return new Car().setBrand(brand)
                .setColor(color)
                .setSegment(segment)
                .setStatus(status)
                .setName(carDto.getName())
                .setNumber(carDto.getNumber())
                .setPricePerDay(carDto.getPricePerDay());
    }
}

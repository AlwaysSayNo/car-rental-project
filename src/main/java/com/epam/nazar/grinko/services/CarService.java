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

    public Car getCarById(long id){
        return carRepository.getById(id);
    }

    public Optional<Car> getCarByNumber(String number){
        return carRepository.getByNumber(number);
    }

    public void addNewCar(Car newCar){
        carRepository.save(newCar);
    }

    public void deleteCarById(long id) {
        carRepository.deleteById(id);
    }

    public void updateCarById(Car car, long id){
        carRepository.updateCarById(car.getNumber(), car.getBrand(), car.getName(),
                car.getColor(), car.getPricePerDay(), car.getSegment(), car.getStatus(), id);
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    // Конвертировать в dto в car можно в том случае, если все поля из бд уже существуют в базе данных
    public Car convertCarDtoToCar(CarDto carDto){
        CarBrand brand = carBrandService.getBrand(carDto.getBrand());
        CarColor color = carColorService.getColor(carDto.getColor());

        return new Car().setBrand(brand)
                .setColor(color)
                .setSegment(carDto.getSegment())
                .setStatus(carDto.getStatus())
                .setName(carDto.getName())
                .setNumber(carDto.getNumber())
                .setPricePerDay(carDto.getPricePerDay());
    }

    public CarDto convertCarToCarDto(Car car){
        return new CarDto().setBrand(car.getBrand().getValue())
                .setName(car.getName())
                .setNumber(car.getNumber())
                .setSegment(car.getSegment())
                .setStatus(car.getStatus())
                .setColor(car.getColor().getValue())
                .setPricePerDay(car.getPricePerDay());
    }
}

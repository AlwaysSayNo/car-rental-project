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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;

    public List<Car> getAll(){
        return carRepository.findAll();
    }

    public List<Car> getByStatusIn(CarStatus... statuses){
        return carRepository.findAllByStatusIsIn(Arrays.asList(statuses));
    }

    public Car getById(long id){
        return carRepository.getById(id);
    }

    public Optional<Car> getByNumber(String number){
        return carRepository.getByNumber(number);
    }

    public void save(Car newCar){
        carRepository.save(newCar);
    }

    public void deleteById(long id) {
        carRepository.deleteById(id);
    }

    public void updateCarById(long id, Car car){
        carRepository.updateCarById(car.getNumber(), car.getBrand(), car.getName(),
                car.getColor(), car.getPricePerDay(), car.getSegment(), car.getStatus(), id);
    }

    public void updateCarStatusById(long id, CarStatus status){
        carRepository.updateCarStatusById(status, id);
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    // Конвертировать в dto в car можно в том случае, если все поля из бд уже существуют в базе данных
    public Car mapToObject(CarDto carDto){
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

    public CarDto mapToDto(Car car){
        return new CarDto().setBrand(car.getBrand().getValue())
                .setName(car.getName())
                .setNumber(car.getNumber())
                .setSegment(car.getSegment())
                .setStatus(car.getStatus())
                .setColor(car.getColor().getValue())
                .setPricePerDay(car.getPricePerDay());
    }
}

package com.epam.nazar.grinko.services.car;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.repositories.CarRepository;
import com.epam.nazar.grinko.utils.Utility;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandService brandService;
    private final CarColorService colorService;
    private final CarQueryManipulationService manipulationService;

    public Page<Car> getAll(PageRequest request, String filterBy, String filterValue){
        Map<String, List<String>> byAll = new HashMap<>();

        Utility.safetyAdd(byAll, filterBy, filterValue);

        return manipulationService.evaluateQuery(request, byAll);
    }

    public Page<Car> getByStatus(PageRequest request, CarStatus status, String filterBy, String filterValue){
        Map<String, List<String>> byStatus = new HashMap<>();

        Utility.safetyAdd(byStatus, "status", status.name());
        Utility.safetyAdd(byStatus, filterBy, filterValue);

        return manipulationService.evaluateQuery(request, byStatus);
    }

    public Car getById(Long id){
        return carRepository.getById(id);
    }

    public Optional<Car> getByNumber(String number){
        return carRepository.getByNumber(number);
    }

    public Long getIdByNumber(String number){
        return carRepository.getIdByNumber(number);
    }

    public void save(Car newCar){
        carRepository.save(newCar);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    public void updateCarById(Long id, Car car){
        carRepository.updateCarById(car.getNumber(), car.getBrand(), car.getName(),
                car.getColor(), car.getPricePerDay(), car.getSegment(), car.getStatus(), id);
    }

    public void updateCarStatusById(Long id, CarStatus status){
        carRepository.updateCarStatusById(status, id);
    }

    public Long updateCarStatusByIdIn(CarStatus status, List<Long> ids){
        return (long) carRepository.updateCarStatusByIdIn(status, ids);
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    public Car mapToObject(CarDto carDto){
        CarBrand brand = brandService.getBrand(carDto.getBrand());
        CarColor color = colorService.getColor(carDto.getColor());

        return new Car().setBrand(brand)
                .setColor(color)
                .setSegment(CarSegment.valueOf(carDto.getSegment()))
                .setStatus(CarStatus.valueOf(carDto.getStatus()))
                .setName(carDto.getName())
                .setNumber(carDto.getNumber())
                .setPricePerDay(carDto.getPricePerDay());
    }

    public CarDto mapToDto(Car car){
        return new CarDto().setBrand(car.getBrand().getValue())
                .setName(car.getName())
                .setNumber(car.getNumber())
                .setSegment(car.getSegment().name())
                .setStatus(car.getStatus().name())
                .setColor(car.getColor().getValue())
                .setPricePerDay(car.getPricePerDay());
    }

    public CarQueryManipulationService getManipulationService() {
        return manipulationService;
    }

}

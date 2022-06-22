package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.repositories.CarRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandService brandService;
    private final CarColorService colorService;

    public PageRequest createRequest(Integer page, Integer size, String sortBy, String direction){
        if(sortBy == null) return PageRequest.of(page, size);
        return PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortBy);
    }

    public Page<Car> getByFilter(PageRequest request, String filterBy, String filterValue){
        switch (filterBy){
            case "segment": return carRepository.getBySegment(CarSegment.valueOf(filterValue), request);
            case "brand": return carRepository.getByBrand(brandService.getBrand(filterValue), request);
            case "color": return carRepository.getByColor(colorService.getColor(filterValue), request);
            default: throw new IllegalPathVariableException();
        }
    }

    public Page<Car> getAll(PageRequest request){
        return carRepository.findAll(request);
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

    public void updateCarStatusById(Long id, CarStatus status){
        carRepository.updateCarStatusById(status, id);
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    public Car mapToObject(CarDto carDto){
        CarBrand brand = brandService.getBrand(carDto.getBrand());
        CarColor color = colorService.getColor(carDto.getColor());

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

    public Page<CarDto> mapToDto(Page<Car> cars){
        return cars.map(this::mapToDto);
    }

}

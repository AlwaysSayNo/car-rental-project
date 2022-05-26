package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.repositories.CarRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(long id){
        return carRepository.findById(id);
    }

    public boolean addNewCar(Car newCar){
        try{
            carRepository.save(newCar);
        } catch (DataAccessException exception){
          return false;
        }
        return true;
    }

    public boolean deleteCarById(long id) {
        try{
            carRepository.deleteById(id);
        } catch (DataAccessException exception){
            return false;
        }
        return true;
    }

    public boolean existsCarByNumber(String number){
        return carRepository.existsByNumber(number);
    }
}

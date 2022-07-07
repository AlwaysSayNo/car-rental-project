package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.engines.FilterPresetEngine;
import com.epam.nazar.grinko.engines.PaginationPresetEngine;
import com.epam.nazar.grinko.engines.SortPresetEngine;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin/cars")
@AllArgsConstructor
@Slf4j
public class AllCarsController {

    private final CarService carService;
    private final CarBrandService brandService;
    private final CarColorService colorService;

    @GetMapping()
    public String showAllCars(@RequestParam(value = "sortBy", required = false) String sortBy,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                  @RequestParam(value = "filterBy", required = false) String filterBy,
                                  @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        log.info("ADMIN showAllCars: sortBy={}, direction={}, filterBy={}, filterValue={}, page={}, size={}",
                sortBy, direction, filterBy, filterValue, page, size);

        PageRequest pageRequest = carService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Car> cars = carService.getAll(pageRequest, filterBy, filterValue);

        Page<CarDto> carsDto = cars.map(carService::mapToDto);
        List<Long> allId = cars.stream().map(Car::getId).collect(Collectors.toList());

        model.addAttribute("cars", carsDto);
        model.addAttribute("ids", allId);

        PaginationPresetEngine.updateModelForPagination(model, carsDto, page, size);
        SortPresetEngine.updateModelForSorting(model, sortBy, direction);
        FilterPresetEngine.updateModelForFiltering(model, filterBy, filterValue);

        return "admin/cars/all-cars";
    }

    @ModelAttribute("filtersMap")
    public Map<String, List<String>> getFiltersMap(){
        Map<String, List<String>> filtersMap = new HashMap<>();

        List<String> segments = Arrays.stream(CarSegment.values()).map(CarSegment::name).collect(Collectors.toList());
        filtersMap.put("segment", segments);

        List<String> brands = brandService.getAll().stream().map(CarBrand::getValue).collect(Collectors.toList());
        filtersMap.put("brand", brands);

        List<String> colors = colorService.getAll().stream().map(CarColor::getValue).collect(Collectors.toList());
        filtersMap.put("color", colors);

        List<String> statuses = Arrays.stream(CarStatus.values()).map(CarStatus::name).collect(Collectors.toList());
        filtersMap.put("status", statuses);

        return filtersMap;
    }

    @ModelAttribute("sortsMap")
    public Map<String, List<String>> getSortsMap(){
        Map<String, List<String>> sortsMap = new HashMap<>();
        List<String> directions = Arrays.asList("ASC", "DESC");

        sortsMap.put("pricePerDay", directions);
        sortsMap.put("name", directions);

        return sortsMap;
    }
}

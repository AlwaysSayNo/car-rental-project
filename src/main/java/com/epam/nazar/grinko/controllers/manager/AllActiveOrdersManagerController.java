package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.order.OrderService;
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
@RequestMapping("car-rental-service/manager/active-orders")
@AllArgsConstructor
@Slf4j
public class AllActiveOrdersManagerController {

    private final OrderService orderService;
    private final CarBrandService brandService;

    @GetMapping("/active-orders")
    public String showAllActiveOrders(@RequestParam(value = "sortBy", required = false) String sortBy,
                                   @RequestParam(value = "direction", required = false) String direction,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                   @RequestParam(value = "filterBy", required = false) String filterBy,
                                   @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        log.info("MANAGER showAllActiveOrders: sortBy={}, direction={}, filterBy={}, filterValue={}, page={}, size={}",
                sortBy, direction, filterBy, filterValue, page, size);

        List<OrderStatus> availableStatuses = Arrays.asList(OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT);

        PageRequest request = orderService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Order> orders = orderService.getOrdersWithStatusIn(request, availableStatuses, filterBy, filterValue);

        Page<OrderDto> ordersDto = orders.map(orderService::mapToDto);
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "manager/active-orders/all-active-orders";
    }

    @ModelAttribute("filtersMap")
    public Map<String, List<String>> getFiltersMap(){
        Map<String, List<String>> filtersMap = new HashMap<>();

        List<String> segments = Arrays.stream(CarSegment.values()).map(CarSegment::name).collect(Collectors.toList());
        filtersMap.put("segment", segments);

        List<String> brands = brandService.getAll().stream().map(CarBrand::getValue).collect(Collectors.toList());
        filtersMap.put("brand", brands);

        List<String> statuses = Arrays.stream(OrderStatus.values()).map(OrderStatus::name).collect(Collectors.toList());
        filtersMap.put("status", statuses);

        return filtersMap;
    }

    @ModelAttribute("sortsMap")
    public Map<String, List<String>> getSortsMap(){
        Map<String, List<String>> sortsMap = new HashMap<>();
        List<String> directions = Arrays.asList("ASC", "DESC");

        sortsMap.put("user_firstName", directions);
        sortsMap.put("user_email", directions);
        sortsMap.put("car_name", directions);

        return sortsMap;
    }

}

package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.engines.FilterPresetEngine;
import com.epam.nazar.grinko.engines.PaginationPresetEngine;
import com.epam.nazar.grinko.engines.SortPresetEngine;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BillService;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/car-rental-service/user/active-orders")
@AllArgsConstructor
@Slf4j
public class AllActiveOrdersUserController {

    private final UserService userService;
    private final OrderService orderService;
    private final BillService billService;
    private final CarBrandService brandService;
    private final CarColorService colorService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showActiveOrdersPage(@RequestParam(value = "sortBy", required = false) String sortBy,
                                       @RequestParam(value = "direction", required = false) String direction,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                       @RequestParam(value = "filterBy", required = false) String filterBy,
                                       @RequestParam(value = "filterValue", required = false) String filterValue,
                                       Model model, HttpServletRequest servletRequest){
        log.info("USER showAllCars: sortBy={}, direction={}, filterBy={}, filterValue={}, page={}, size={}",
                sortBy, direction, filterBy, filterValue, page, size);

        Long userId = userService.getUserIdByEmail(jwtTokenProvider.getUsername(
                jwtTokenProvider.resolveToken(servletRequest))
        ).orElseThrow(IllegalJwtContentException::new);


        List<OrderStatus> availableStatuses = Arrays.asList(OrderStatus.UNDER_CONSIDERATION,
                OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT);

        PageRequest request = orderService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Order> orders = orderService.getAllByUserIdAndStatus(request, userId, availableStatuses, filterBy, filterValue);

        Page<OrderDto> ordersDto = orders.map(orderService::mapToDto);
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<BillDto> billsDto = ids.stream()
                .map(billService::getBillsById)
                .map(billService::mapToDto)
                .collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);
        model.addAttribute("bills", billsDto);

        PaginationPresetEngine.updateModelForPagination(model, ordersDto, page, size);
        SortPresetEngine.updateModelForSorting(model, sortBy, direction);
        FilterPresetEngine.updateModelForFiltering(model, filterBy, filterValue);

        return "/user/active-orders/all-active-orders";
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

        return filtersMap;
    }

    @ModelAttribute("sortsMap")
    public Map<String, List<String>> getSortsMap(){
        Map<String, List<String>> sortsMap = new HashMap<>();
        List<String> directions = Arrays.asList("ASC", "DESC");

        sortsMap.put("car_name", directions);

        return sortsMap;
    }

}

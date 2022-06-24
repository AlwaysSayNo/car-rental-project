package com.epam.nazar.grinko.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;

public interface QueryManipulation<T> {

    PageRequest createRequest(Integer page, Integer size, String sortBy, String direction);
    Page<T> evaluateQuery(PageRequest request,  Map<String, String> filter);

}

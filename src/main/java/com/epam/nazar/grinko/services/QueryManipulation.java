package com.epam.nazar.grinko.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface QueryManipulation<T> {

    PageRequest createRequest(Integer page, Integer size, String sortBy, String direction);
    Page<T> evaluateQuery(PageRequest request,  Map<String, List<String>> filter);

}

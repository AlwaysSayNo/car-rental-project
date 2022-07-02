package com.epam.nazar.grinko.engines;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationPresetEngine {

    public static final Integer RECORDS_PER_PAGE = 8;
    public static final Integer CURRENT_PAGE = 1;

    public static <T> boolean updateModelForPagination(Model model, Page<T> page, Integer currentPage, Integer recordsPerPage) {
        model.addAttribute("pageNumbers", preparePageNumbers(page));
        model.addAttribute("itemStep", prepareItemStep(4, 8, 12, 16));
        Integer totalPages = preparePageNumbers(page).size();
        model.addAttribute("totalPages", preparePageNumbers(page).size());

        if(currentPage > totalPages) currentPage = totalPages;
        model.addAttribute("page", currentPage != null ? currentPage : CURRENT_PAGE);
        model.addAttribute("size", recordsPerPage != null ? recordsPerPage : RECORDS_PER_PAGE);

        return true;
    }

    private static List<Integer> prepareItemStep(Integer... values) {
        return Arrays.stream(values).collect(Collectors.toList());
    }

    private static  <T> List<Integer> preparePageNumbers(Page<T> page) {
        int totalPages = page.getTotalPages() > 0 ? page.getTotalPages() : 1;

        return IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
    }

}

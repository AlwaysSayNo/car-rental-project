package com.epam.nazar.grinko.engines;

import org.springframework.ui.Model;

public class FilterPresetEngine {

    public static final String NONE = "none";

    public static void updateModelForFiltering(Model model, String filterBy, String filterValue){
        if(filterBy != null) {
            model.addAttribute("filterBy", filterBy);
            model.addAttribute("filterValue", filterValue);
        }
        else {
            model.addAttribute("filterBy", NONE);
            model.addAttribute("filterValue", NONE);
        }
    }

}

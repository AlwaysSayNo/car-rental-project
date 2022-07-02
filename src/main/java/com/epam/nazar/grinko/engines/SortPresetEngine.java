package com.epam.nazar.grinko.engines;

import org.springframework.ui.Model;

public class SortPresetEngine {

    public static final String NONE = "none";

    public static void updateModelForSorting(Model model, String sortBy, String direction){
        if(sortBy != null) {
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
        }
        else {
            model.addAttribute("sortBy", NONE);
            model.addAttribute("direction", NONE);
        }
    }

}

package com.coderscampus.Assignment15.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View Controller for serving HTML templates.
 * Handles requests for the main application pages.
 */
@Controller
public class SelfCareViewController {

    /**
     * Serves the main index page (redirects to tracker).
     * 
     * @return the name of the template to render
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Serves the tracker page (track.html).
     * 
     * @return the name of the template to render
     */
    @GetMapping("/track")
    public String track() {
        return "track";
    }

    /**
     * Serves the progress page (progress.html).
     * 
     * @return the name of the template to render
     */
    @GetMapping("/progress")
    public String progress() {
        return "progress";
    }
}


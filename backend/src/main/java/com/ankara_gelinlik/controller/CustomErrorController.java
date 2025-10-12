package com.ankara_gelinlik.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = "Beklenmedik bir hata oluştu.";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            switch (statusCode) {
                case 403:
                    message = "Bu sayfaya erişim yetkiniz yok!";
                    break;
                case 404:
                    message = "Aradığınız sayfa bulunamadı!";
                    break;
                case 500:
                    message = "Sunucuda bir hata oluştu!";
                    break;
            }
        }

        model.addAttribute("message", message);
        return "error"; // error.html gösteriliyor
    }
}

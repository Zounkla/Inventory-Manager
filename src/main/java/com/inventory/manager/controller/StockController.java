package com.inventory.manager.controller;

import com.inventory.manager.model.Stock;
import com.inventory.manager.service.StockService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public String listStocks(Model model) {
        model.addAttribute("stocks", stockService.getAllStocks());
        return "stocks/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("stock", new Stock());
        return "stocks/form";
    }

    @PostMapping
    public String createStock(@Valid @ModelAttribute Stock stock, 
                            BindingResult result, 
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "stocks/form";
        }
        stockService.saveStock(stock);
        redirectAttributes.addFlashAttribute("success", "Stock created successfully!");
        return "redirect:/stocks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return stockService.getStockById(id)
                .map(stock -> {
                    model.addAttribute("stock", stock);
                    return "stocks/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Stock not found!");
                    return "redirect:/stocks";
                });
    }

    @PostMapping("/update/{id}")
    public String updateStock(@PathVariable Long id, 
                            @Valid @ModelAttribute Stock stock, 
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "stocks/form";
        }
        stock.setId(id);
        stockService.saveStock(stock);
        redirectAttributes.addFlashAttribute("success", "Stock updated successfully!");
        return "redirect:/stocks";
    }

    @GetMapping("/delete/{id}")
    public String deleteStock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (stockService.existsById(id)) {
            stockService.deleteStock(id);
            redirectAttributes.addFlashAttribute("success", "Stock deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Stock not found!");
        }
        return "redirect:/stocks";
    }
}

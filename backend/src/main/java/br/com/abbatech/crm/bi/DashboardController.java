package br.com.abbatech.crm.bi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bi")
public class DashboardController {
    private final MetricasService metricas;
    public DashboardController(MetricasService metricas) { this.metricas = metricas; }
    @GetMapping("/dashboard")
    public MetricasService.DashboardResumo dashboard() { return metricas.dashboard(); }
}


package br.com.abbatech.crm.comercial;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {}


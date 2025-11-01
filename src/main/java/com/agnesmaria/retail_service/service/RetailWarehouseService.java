package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.dto.RetailWarehouseRequest;
import com.agnesmaria.retail_service.model.RetailWarehouse;
import com.agnesmaria.retail_service.repository.RetailWarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetailWarehouseService {

    private final RetailWarehouseRepository warehouseRepository;

    public List<RetailWarehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public RetailWarehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retail warehouse not found"));
    }

    @Transactional
    public RetailWarehouse create(RetailWarehouseRequest req) {
        if (warehouseRepository.existsByCode(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Retail warehouse code already exists");
        }
        RetailWarehouse wh = RetailWarehouse.builder()
                .code(req.getCode())
                .name(req.getName())
                .address(req.getAddress())
                .active(req.getActive() == null ? true : req.getActive())
                .build();
        return warehouseRepository.save(wh);
    }

    @Transactional
    public RetailWarehouse update(Long id, RetailWarehouseRequest req) {
        RetailWarehouse wh = findById(id);
        // if code changed, ensure unique
        if (!wh.getCode().equals(req.getCode()) && warehouseRepository.existsByCode(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Retail warehouse code already exists");
        }
        wh.setCode(req.getCode());
        wh.setName(req.getName());
        wh.setAddress(req.getAddress());
        wh.setActive(req.getActive() == null ? wh.getActive() : req.getActive());
        return warehouseRepository.save(wh);
    }

    @Transactional
    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Retail warehouse not found");
        }
        warehouseRepository.deleteById(id);
    }
}

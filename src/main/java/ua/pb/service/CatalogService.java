package ua.pb.service;

import ua.pb.dto.view.CatalogItemViewDto;

import java.util.List;

public interface CatalogService {

    CatalogItemViewDto getCatalogItem(int currencyId);

    List<CatalogItemViewDto> getCatalog();
}

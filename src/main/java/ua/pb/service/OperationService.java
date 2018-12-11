package ua.pb.service;

import lombok.NonNull;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.dto.view.OperationViewDto;

import java.util.List;

public interface OperationService {

    OperationViewDto create(@NonNull OperationCreateDto operationCreateDto, int userId);

    OperationViewDto update(@NonNull OperationCreateDto operationCreateDto, int operationId);

    OperationViewDto updateDeletedState(int operationId, boolean isDeleted);

    OperationViewDto getById(int operationId);

    List<OperationViewDto> getAll();

    List<OperationViewDto> getAllByUserId(int userId);
}

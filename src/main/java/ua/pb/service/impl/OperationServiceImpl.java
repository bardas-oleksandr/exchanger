package ua.pb.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ua.pb.dao.OperationDao;
import ua.pb.dao.UserDao;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.dto.view.OperationViewDto;
import ua.pb.model.Operation;
import ua.pb.model.User;
import ua.pb.service.OperationService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("operationService")
public class OperationServiceImpl implements OperationService {

    @Autowired
    private OperationDao operationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public OperationViewDto create(@NonNull OperationCreateDto operationCreateDto, int userId) {
        Operation operation = conversionService.convert(operationCreateDto, Operation.class);
        User user = userDao.getById(userId);
        operation.setUser(user);
        operation.setDate(new Timestamp(System.currentTimeMillis()));
        operation.setDeleted(false);
        operationDao.create(operation);
        return conversionService.convert(operationDao.getById(operation.getId()), OperationViewDto.class);
    }

    @Override
    public OperationViewDto update(@NonNull OperationCreateDto operationCreateDto, int operationId) {
        Operation operation = conversionService.convert(operationCreateDto, Operation.class);
        operation.setId(operationId);
        operationDao.update(operation);
        return conversionService.convert(operationDao.getById(operation.getId()), OperationViewDto.class);
    }

    //Удаление записи об операции заключается в изменении статуса этой записи
    @Override
    public OperationViewDto updateDeletedState(int operationId, boolean isDeleted) {
        Operation operation = operationDao.getById(operationId);
        operation.setDeleted(isDeleted);
        operationDao.update(operation);
        return conversionService.convert(operation, OperationViewDto.class);
    }

    @Override
    public OperationViewDto getById(int operationId) {
        Operation operation = operationDao.getById(operationId);
        return conversionService.convert(operation, OperationViewDto.class);
    }

    @Override
    public List<OperationViewDto> getAll() {
        List<Operation> operations = operationDao.getAll();
        return getViewDtos(operations);
    }

    @Override
    public List<OperationViewDto> getAllByUserId(int userId) {
        List<Operation> operations = operationDao.getAllNotDeletedByUserId(userId);
        return getViewDtos(operations);
    }

    private List<OperationViewDto> getViewDtos(List<Operation> operationList) {
        return operationList.stream()
                .map((operation)->conversionService
                        .convert(operation, OperationViewDto.class))
                .collect(Collectors.toList());
    }
}

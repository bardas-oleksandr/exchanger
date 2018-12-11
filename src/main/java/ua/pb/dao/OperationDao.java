package ua.pb.dao;

import lombok.NonNull;
import ua.pb.model.Operation;

import java.util.List;

public interface OperationDao {
    void create(@NonNull Operation operation);

    void update(@NonNull Operation operation);

    void updateDeletedColumn(boolean deleted, int operationId);

    Operation getById(int id);

    List<Operation> getAll();

    List<Operation> getAllNotDeletedByUserId(int userId);
}

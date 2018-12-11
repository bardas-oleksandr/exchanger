package ua.pb.converter.fromdto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.model.NbuRate;
import ua.pb.model.Operation;
import ua.pb.model.Rate;

/**
 *
 */
@Component("operationCreateDtoConverter")
public class OperationCreateDtoConverter implements Converter<OperationCreateDto, Operation> {

    @Override
    public Operation convert(@NonNull OperationCreateDto source) {
        Operation operation = new Operation();
        Rate rate = new Rate();
        rate.setId(source.getRateId());
        operation.setRate(rate);
        NbuRate nbuRate = new NbuRate();
        nbuRate.setId(source.getNbuRateId());
        operation.setNbuRate(nbuRate);
        operation.setBuyOperation(source.getBuyOperation());
        operation.setSumHrn(source.getSumHrn());
        operation.setSumCurrency(source.getSumCurrency());
        return operation;
    }
}

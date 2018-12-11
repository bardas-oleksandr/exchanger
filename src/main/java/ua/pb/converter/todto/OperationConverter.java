package ua.pb.converter.todto;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.view.NbuRateViewDto;
import ua.pb.dto.view.OperationViewDto;
import ua.pb.dto.view.RateViewDto;
import ua.pb.dto.view.UserViewDto;
import ua.pb.model.Operation;

/**
 *
 */
@Component("operationConverter")
public class OperationConverter implements Converter<Operation, OperationViewDto> {

    @Autowired
    private RateConverter rateConverter;

    @Autowired
    private NbuRateConverter nbuRateConverter;

    @Autowired
    private UserConverter userConverter;

    @Override
    public OperationViewDto convert(@NonNull Operation source) {
        OperationViewDto operationViewDto = new OperationViewDto();
        operationViewDto.setId(source.getId());
        RateViewDto rateViewDto = rateConverter.convert(source.getRate());
        operationViewDto.setRateViewDto(rateViewDto);
        NbuRateViewDto nbuRateViewDto = nbuRateConverter.convert(source.getNbuRate());
        operationViewDto.setNbuRateViewDto(nbuRateViewDto);
        UserViewDto userViewDto = userConverter.convert(source.getUser());
        operationViewDto.setUserViewDto(userViewDto);
        operationViewDto.setBuyOperation(source.isBuyOperation());
        operationViewDto.setSumHrn(source.getSumHrn());
        operationViewDto.setSumCurrency(source.getSumCurrency());
        operationViewDto.setDate(source.getDate());
        operationViewDto.setDeleted(source.isDeleted());
        return operationViewDto;
    }
}

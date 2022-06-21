package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Cancellation;
import com.epam.nazar.grinko.dto.CancellationDto;
import com.epam.nazar.grinko.repositories.CancellationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CancellationService {

    private final CancellationRepository cancellationRepository;
    private final OrderService orderService;

    public void save(Cancellation cancellation){
        cancellationRepository.save(cancellation);
    }

    public CancellationDto mapToDto(Cancellation cancellation){
        return new CancellationDto()
                .setMessage(cancellation.getMessage())
                .setOrder(orderService.mapToDto(cancellation.getOrder()));
    }

    public Cancellation mapToObject(CancellationDto cancellationDto){
        return new Cancellation()
                .setMessage(cancellationDto.getMessage())
                .setOrder(orderService.mapToObject(cancellationDto.getOrder()));
    }


}

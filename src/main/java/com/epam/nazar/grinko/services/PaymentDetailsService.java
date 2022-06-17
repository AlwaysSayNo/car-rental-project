package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.PaymentDetails;
import com.epam.nazar.grinko.dto.PaymentDetailsDto;
import com.epam.nazar.grinko.repositories.PaymentDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class PaymentDetailsService {

    private PaymentDetailsRepository paymentDetailsRepository;

    public PaymentDetails mapToObject(PaymentDetailsDto paymentDetailsDto){
        return new PaymentDetails().setFirstName(paymentDetailsDto.getFirstName())
                .setLastName(paymentDetailsDto.getLastName())
                .setDateOfBirth(paymentDetailsDto.getDateOfBirth())
                .setPassportNumber(paymentDetailsDto.getPassportNumber());
    }

    public void addBreakdown(PaymentDetails paymentDetails, Breakdown breakdown){
        Set<Breakdown> breakdowns = paymentDetails.getBreakdowns();
        breakdowns.add(breakdown);
        paymentDetails.setBreakdowns(breakdowns);
    }

    public void saveIfNotExists(PaymentDetails paymentDetails){
        if(!existsByPassportNumber(paymentDetails.getPassportNumber()))
            paymentDetailsRepository.save(paymentDetails);
    }

    public boolean existsByPassportNumber(String passportNumber){
        return paymentDetailsRepository.existsByPassportNumber(passportNumber);
    }

}

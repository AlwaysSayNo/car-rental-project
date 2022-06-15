package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.PaymentDetails;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.PaymentDetailsDto;
import com.epam.nazar.grinko.repositories.PaymentDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentDetailsService {

    private PaymentDetailsRepository paymentDetailsRepository;

    public PaymentDetails convertPaymentDetailsDtoToPaymentDetails(PaymentDetailsDto paymentDetailsDto){
        return new PaymentDetails().setFirstName(paymentDetailsDto.getFirstName())
                .setLastName(paymentDetailsDto.getLastName())
                .setDateOfBirth(paymentDetailsDto.getDateOfBirth())
                .setPassportNumber(paymentDetailsDto.getPassportNumber());
    }


}

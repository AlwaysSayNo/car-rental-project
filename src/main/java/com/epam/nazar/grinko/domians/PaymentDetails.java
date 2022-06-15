package com.epam.nazar.grinko.domians;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "payment_details")
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "paymentDetails", fetch = FetchType.EAGER)
    private Set<Bill> bills;

    @OneToMany(mappedBy = "paymentDetails", fetch = FetchType.EAGER)
    private Set<Cancellation> cancellations;

    @Column(name = "passport_number", length = 9)
    private String passportNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Calendar dateOfBirth;

}

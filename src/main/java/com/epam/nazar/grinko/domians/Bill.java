package com.epam.nazar.grinko.domians;

import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.securities.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Calendar;

@Getter
@Setter
@Entity
@Table(name = "bills")
@Accessors(chain = true)
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_details_id")
    private PaymentDetails paymentDetails;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Calendar startDate;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Calendar expirationDate;

    @Column(name = "car_price", nullable = false)
    private Long carPrice;

    @Column(name = "with_driver", nullable = false)
    private Boolean withDriver;

    @Column(name = "driver_price", nullable = false)
    private Long driverPrice;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillStatus status;

}
